#include <jni.h>
#include <android/log.h>
#include <mpv/client.h>
#include <mpv/stream_cb.h>
extern "C" {
#include <libavcodec/jni.h>
}
#include "file_slice.h"
#include <chrono>
#include <condition_variable>
#include <cstring>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <vector>

namespace {
JavaVM *vm = nullptr;
constexpr int PREPARED = 1, COMPLETE = 2, BUFFERING = 3, SEEK_COMPLETE = 4,
        VIDEO_SIZE = 5, TIMED_TEXT = 99, ERROR = 100, INFO = 200;

void fail(JNIEnv *env, const char *message, const char *type = "java/lang/IllegalStateException") {
    __android_log_print(ANDROID_LOG_ERROR, "MpvPlayer", "%s", message);
    if (env->ExceptionCheck()) return;
    jclass cls = env->FindClass(type);
    if (cls) { env->ThrowNew(cls, message); env->DeleteLocalRef(cls); }
}
bool check(JNIEnv *env, int code, const char *operation = "libmpv") {
    if (code >= 0) return true;
    std::string message = std::string(operation) + ": " + mpv_error_string(code);
    fail(env, message.c_str());
    return false;
}

// libmpv expects UTF-8, not JNI's Modified UTF-8 (notably for non-BMP filenames).
std::string string(JNIEnv *env, jstring value) {
    if (!value) { fail(env, "Null string", "java/lang/IllegalArgumentException"); return {}; }
    const jchar *chars = env->GetStringChars(value, nullptr);
    if (!chars) return {};
    jsize size = env->GetStringLength(value);
    std::string output;
    for (jsize i = 0; i < size; ++i) {
        uint32_t c = chars[i];
        if (c >= 0xd800 && c <= 0xdbff && i + 1 < size && chars[i + 1] >= 0xdc00 && chars[i + 1] <= 0xdfff) {
            c = 0x10000 + ((c - 0xd800) << 10) + chars[++i] - 0xdc00;
        } else if (c >= 0xd800 && c <= 0xdfff) { c = 0xfffd; }
        if (c < 0x80) output += static_cast<char>(c);
        else if (c < 0x800) { output += static_cast<char>(0xc0 | (c >> 6)); output += static_cast<char>(0x80 | (c & 63)); }
        else if (c < 0x10000) {
            output += static_cast<char>(0xe0 | (c >> 12)); output += static_cast<char>(0x80 | ((c >> 6) & 63)); output += static_cast<char>(0x80 | (c & 63));
        } else {
            output += static_cast<char>(0xf0 | (c >> 18)); output += static_cast<char>(0x80 | ((c >> 12) & 63));
            output += static_cast<char>(0x80 | ((c >> 6) & 63)); output += static_cast<char>(0x80 | (c & 63));
        }
    }
    env->ReleaseStringChars(value, chars);
    return output;
}

struct Player {
    mpv_handle *mpv = nullptr;
    jobject reference = nullptr, surface = nullptr;
    jclass callbackClass = nullptr;
    jmethodID callback = nullptr;
    std::atomic<int> generation{0};
    std::atomic<bool> quitting{false}, seekRequested{false}, seekInProgress{false};
    std::thread events;
    std::mutex prepareMutex, fileMutex, barrierMutex;
    std::condition_variable prepareCondition, barrierCondition;
    bool barrierComplete = false;
    bool preparing = false;
    int prepareResult = 0;
    // Owned by the event thread, reset for each media load (not each seek).
    bool renderingStarted = false;
    std::unique_ptr<FileSlice> file;

    void finishPrepare(int result) {
        std::lock_guard<std::mutex> lock(prepareMutex);
        if (preparing) { prepareResult = result; preparing = false; prepareCondition.notify_all(); }
    }
    void post(JNIEnv *env, int event, int arg1 = 0, int arg2 = 0, const char *text = nullptr) {
        jbyteArray bytes = nullptr;
        if (text) {
            auto size = static_cast<jsize>(strlen(text));
            bytes = env->NewByteArray(size);
            if (bytes) env->SetByteArrayRegion(bytes, 0, size, reinterpret_cast<const jbyte *>(text));
        }
        if (!env->ExceptionCheck())
            env->CallStaticVoidMethod(callbackClass, callback, reference, generation.load(), event, arg1, arg2, bytes);
        if (bytes) env->DeleteLocalRef(bytes);
        if (env->ExceptionCheck()) {
            __android_log_print(ANDROID_LOG_ERROR, "MpvPlayer", "Unable to deliver player event");
            env->ExceptionClear();
        }
    }
    double number(const char *name, double fallback = 0) {
        double value = fallback;
        if (mpv_get_property(mpv, name, MPV_FORMAT_DOUBLE, &value) < 0) return fallback;
        return value;
    }
    void eventLoop() {
        JNIEnv *env = nullptr;
        if (vm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            finishPrepare(MPV_ERROR_GENERIC);
            return;
        }
        while (!quitting) {
            mpv_event *event = mpv_wait_event(mpv, -1);
            if (quitting) break;
            switch (event->event_id) {
                case MPV_EVENT_START_FILE:
                    renderingStarted = false;
                    break;
                case MPV_EVENT_COMMAND_REPLY:
                    if (event->reply_userdata == 1) {
                        std::lock_guard<std::mutex> lock(barrierMutex);
                        barrierComplete = true;
                        barrierCondition.notify_all();
                    }
                    break;
                case MPV_EVENT_FILE_LOADED:
                    finishPrepare(0);
                    post(env, PREPARED);
                    break;
                case MPV_EVENT_END_FILE: {
                    auto *end = static_cast<mpv_event_end_file *>(event->data);
                    if (end->reason == MPV_END_FILE_REASON_ERROR) {
                        finishPrepare(end->error);
                        post(env, ERROR, end->error);
                    } else if (end->reason == MPV_END_FILE_REASON_EOF) {
                        finishPrepare(MPV_ERROR_LOADING_FAILED);
                        post(env, COMPLETE);
                    }
                    break;
                }
                case MPV_EVENT_SEEK:
                    if (seekRequested) seekInProgress = true;
                    break;
                case MPV_EVENT_PLAYBACK_RESTART:
                    if (seekInProgress.exchange(false) && seekRequested.exchange(false)) post(env, SEEK_COMPLETE);
                    if (!renderingStarted) {
                        int configured = 0;
                        mpv_get_property(mpv, "vo-configured", MPV_FORMAT_FLAG, &configured);
                        char *vo = mpv_get_property_string(mpv, "current-vo");
                        // Audio-only playback and the null VO must not report a video frame.
                        if (configured && vo && strcmp(vo, "gpu") == 0 && number("dwidth") > 0) {
                            renderingStarted = true;
                            post(env, INFO, 3); // MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
                        }
                        mpv_free(vo);
                    }
                    break;
                case MPV_EVENT_VIDEO_RECONFIG:
                    post(env, VIDEO_SIZE, static_cast<int>(number("dwidth")), static_cast<int>(number("dheight")));
                    break;
                case MPV_EVENT_PROPERTY_CHANGE: {
                    auto *property = static_cast<mpv_event_property *>(event->data);
                    if (strcmp(property->name, "sub-text") == 0) {
                        post(env, TIMED_TEXT, 0, 0, property->format == MPV_FORMAT_STRING && property->data ?
                             *static_cast<char **>(property->data) : nullptr);
                    } else if (property->data && property->format == MPV_FORMAT_FLAG) {
                        post(env, INFO, *static_cast<int *>(property->data) ? 701 : 702);
                    } else if (property->data && property->format == MPV_FORMAT_DOUBLE) {
                        double duration = number("duration");
                        double end = *static_cast<double *>(property->data);
                        if (duration > 0) post(env, BUFFERING, static_cast<int>(std::max(0.0, std::min(100.0, end * 100 / duration))));
                    }
                    break;
                }
                case MPV_EVENT_QUEUE_OVERFLOW:
                    finishPrepare(MPV_ERROR_EVENT_QUEUE_FULL);
                    post(env, ERROR, MPV_ERROR_EVENT_QUEUE_FULL);
                    break;
                case MPV_EVENT_SHUTDOWN:
                    finishPrepare(MPV_ERROR_UNINITIALIZED);
                    quitting = true;
                    break;
                case MPV_EVENT_LOG_MESSAGE: {
                    auto *log = static_cast<mpv_event_log_message *>(event->data);
                    __android_log_print(ANDROID_LOG_WARN, "MpvPlayer", "%s: %s", log->prefix, log->text);
                    break;
                }
                default: break;
            }
        }
        vm->DetachCurrentThread();
    }
    void destroy(JNIEnv *env) {
        quitting = true;
        finishPrepare(MPV_ERROR_UNINITIALIZED);
        if (mpv) mpv_wakeup(mpv);
        if (events.joinable()) events.join();
        // Keep Java Surface and stream userdata alive until all mpv threads stop.
        if (mpv) { mpv_terminate_destroy(mpv); mpv = nullptr; }
        if (surface) env->DeleteGlobalRef(surface);
        if (reference) env->DeleteGlobalRef(reference);
        if (callbackClass) env->DeleteGlobalRef(callbackClass);
    }
};

Player *player(JNIEnv *env, jlong handle) {
    if (!handle) { fail(env, "Player released"); return nullptr; }
    return reinterpret_cast<Player *>(handle);
}
int openFile(void *opaque, char *uri, mpv_stream_cb_info *info) {
    auto *p = static_cast<Player *>(opaque);
    std::lock_guard<std::mutex> lock(p->fileMutex);
    if (strcmp(uri, "androidfd://media") != 0 || !p->file) return MPV_ERROR_LOADING_FAILED;
    std::unique_ptr<FileSlice> stream(new FileSlice);
    if (!stream->open(p->file->fd, p->file->offset, p->file->length)) return MPV_ERROR_LOADING_FAILED;
    info->cookie = stream.release();
    info->read_fn = [](void *s, char *buf, uint64_t count) { return static_cast<FileSlice *>(s)->read(buf, count); };
    info->seek_fn = [](void *s, int64_t offset) { return static_cast<FileSlice *>(s)->seek(offset); };
    info->size_fn = [](void *s) { return static_cast<FileSlice *>(s)->length; };
    info->close_fn = [](void *s) { delete static_cast<FileSlice *>(s); };
    info->cancel_fn = [](void *s) { static_cast<FileSlice *>(s)->cancelled = true; };
    return 0;
}
} // namespace

#define JNI_METHOD(name) Java_lib_kalu_mpvplayer_MpvPlayer_##name
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *javaVM, void *) {
    vm = javaVM;
    if (av_jni_set_java_vm(vm, nullptr) < 0) return JNI_ERR;
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT jlong JNICALL JNI_METHOD(nativeCreate)(JNIEnv *env, jclass cls, jobject reference, jint generation) {
    std::unique_ptr<Player> p(new Player);
    p->generation = generation;
    p->reference = env->NewGlobalRef(reference);
    p->callbackClass = static_cast<jclass>(env->NewGlobalRef(cls));
    p->callback = env->GetStaticMethodID(cls, "postEventFromNative", "(Ljava/lang/Object;IIII[B)V");
    p->mpv = mpv_create();
    if (!p->reference || !p->callbackClass || !p->callback || !p->mpv) {
        p->destroy(env); fail(env, "Could not create libmpv instance"); return 0;
    }
    const char *options[][2] = {
        {"config", "no"}, {"terminal", "no"}, {"idle", "yes"}, {"pause", "yes"},
        {"keep-open", "no"}, {"vo", "null"}, {"gpu-context", "android"}, {"opengl-es", "yes"},
        {"hwdec", "mediacodec-copy"}, {"ao", "audiotrack,opensles"},
        {"input-default-bindings", "no"}, {"input-vo-keyboard", "no"},
        {"osc", "no"}, {"osd-level", "0"}, {"network-timeout", "30"}
    };
    for (const auto &option : options) {
        if (!check(env, mpv_set_option_string(p->mpv, option[0], option[1]), option[0])) { p->destroy(env); return 0; }
    }
    mpv_request_log_messages(p->mpv, "warn");
    if (!check(env, mpv_initialize(p->mpv), "mpv_initialize") ||
        !check(env, mpv_stream_cb_add_ro(p->mpv, "androidfd", p.get(), openFile)) ||
        !check(env, mpv_observe_property(p->mpv, 1, "paused-for-cache", MPV_FORMAT_FLAG)) ||
        !check(env, mpv_observe_property(p->mpv, 2, "demuxer-cache-time", MPV_FORMAT_DOUBLE)) ||
        !check(env, mpv_observe_property(p->mpv, 3, "sub-text", MPV_FORMAT_STRING))) {
        p->destroy(env); return 0;
    }
    try { p->events = std::thread(&Player::eventLoop, p.get()); }
    catch (const std::exception &e) { p->destroy(env); fail(env, e.what()); return 0; }
    return reinterpret_cast<jlong>(p.release());
}

extern "C" JNIEXPORT void JNICALL JNI_METHOD(nativeRelease)(JNIEnv *env, jclass, jlong handle) {
    if (!handle) return;
    std::unique_ptr<Player> p(reinterpret_cast<Player *>(handle));
    p->destroy(env);
}

extern "C" JNIEXPORT void JNICALL JNI_METHOD(nativePrepare)(JNIEnv *env, jclass, jlong handle, jstring source, jobjectArray headers, jboolean wait, jint generation) {
    auto *p = player(env, handle);
    if (!p) return;
    // Drain previously queued load/seek events before assigning a new Java token.
    // Event callbacks only post to Handler, so the Java monitor can stay held.
    {
        std::unique_lock<std::mutex> lock(p->barrierMutex);
        p->barrierComplete = false;
        const char *stop[] = {"stop", nullptr};
        if (!check(env, mpv_command_async(p->mpv, 1, stop))) return;
        if (!p->barrierCondition.wait_for(lock, std::chrono::seconds(5), [&] { return p->barrierComplete; })) {
            fail(env, "Timed out draining previous player events");
            return;
        }
        p->generation = generation;
    }
    std::string path = string(env, source);
    jsize count = env->GetArrayLength(headers);
    std::vector<std::string> strings;
    for (jsize i = 0; i < count; ++i) {
        auto header = static_cast<jstring>(env->GetObjectArrayElement(headers, i));
        strings.push_back(string(env, header));
        env->DeleteLocalRef(header);
    }
    if (env->ExceptionCheck()) return;
    std::vector<mpv_node> values(count);
    for (jsize i = 0; i < count; ++i) { values[i].format = MPV_FORMAT_STRING; values[i].u.string = &strings[i][0]; }
    mpv_node_list list{count, values.data(), nullptr};
    mpv_node node{}; node.format = MPV_FORMAT_NODE_ARRAY; node.u.list = &list;
    if (!check(env, mpv_set_property(p->mpv, "http-header-fields", MPV_FORMAT_NODE, &node)) ||
        !check(env, mpv_set_property_string(p->mpv, "pause", "yes"))) return;
    {
        std::lock_guard<std::mutex> lock(p->prepareMutex);
        p->preparing = true;
        p->prepareResult = 0;
    }
    p->seekRequested = false;
    p->seekInProgress = false;
    const char *args[] = {"loadfile", path.c_str(), "replace", nullptr};
    int result = mpv_command(p->mpv, args);
    if (result < 0) { p->finishPrepare(result); check(env, result); return; }
    if (wait) {
        std::unique_lock<std::mutex> lock(p->prepareMutex);
        if (!p->prepareCondition.wait_for(lock, std::chrono::seconds(30), [&] { return !p->preparing; })) {
            p->preparing = false;
            lock.unlock();
            const char *stop[] = {"stop", nullptr};
            mpv_command(p->mpv, stop);
            fail(env, "Timed out preparing media");
        } else { check(env, p->prepareResult); }
    }
}

extern "C" JNIEXPORT void JNICALL JNI_METHOD(nativeCommand)(JNIEnv *env, jclass, jlong handle, jobjectArray arguments) {
    auto *p = player(env, handle);
    if (!p) return;
    std::vector<std::string> strings;
    for (jsize i = 0; i < env->GetArrayLength(arguments); ++i) {
        auto value = static_cast<jstring>(env->GetObjectArrayElement(arguments, i));
        strings.push_back(string(env, value));
        env->DeleteLocalRef(value);
    }
    if (env->ExceptionCheck()) return;
    std::vector<const char *> args;
    for (const auto &s : strings) args.push_back(s.c_str());
    args.push_back(nullptr);
    check(env, mpv_command(p->mpv, args.data()));
}

extern "C" JNIEXPORT void JNICALL JNI_METHOD(nativeSeek)(JNIEnv *env, jclass, jlong handle, jdouble seconds) {
    auto *p = player(env, handle);
    if (!p) return;
    // Use a typed node so formatting never depends on the process locale.
    mpv_node values[3]{};
    values[0].format = MPV_FORMAT_STRING; values[0].u.string = const_cast<char *>("seek");
    values[1].format = MPV_FORMAT_DOUBLE; values[1].u.double_ = seconds;
    values[2].format = MPV_FORMAT_STRING; values[2].u.string = const_cast<char *>("absolute+exact");
    mpv_node_list list{3, values, nullptr};
    mpv_node node{}; node.format = MPV_FORMAT_NODE_ARRAY; node.u.list = &list;
    p->seekInProgress = false;
    p->seekRequested = true;
    if (!check(env, mpv_command_node(p->mpv, &node, nullptr))) p->seekRequested = false;
}

extern "C" JNIEXPORT void JNICALL JNI_METHOD(nativeSetProperty)(JNIEnv *env, jclass, jlong handle, jstring name, jstring value) {
    auto *p = player(env, handle);
    if (!p) return;
    auto key = string(env, name), val = string(env, value);
    if (!env->ExceptionCheck()) check(env, mpv_set_property_string(p->mpv, key.c_str(), val.c_str()));
}
extern "C" JNIEXPORT jdouble JNICALL JNI_METHOD(nativeGetNumber)(JNIEnv *env, jclass, jlong handle, jstring name, jdouble fallback) {
    auto *p = player(env, handle);
    if (!p) return fallback;
    auto key = string(env, name);
    return env->ExceptionCheck() ? fallback : p->number(key.c_str(), fallback);
}

extern "C" JNIEXPORT void JNICALL JNI_METHOD(nativeSetSurface)(JNIEnv *env, jclass, jlong handle, jobject surface) {
    auto *p = player(env, handle);
    if (!p || env->IsSameObject(p->surface, surface)) return;
    jobject next = surface ? env->NewGlobalRef(surface) : nullptr;
    if (surface && !next) return;
    // In mpv 0.41.0 UPDATE_VO synchronously uninitializes the previous VO.
    // Disable it before changing wid; destroy() likewise retains the ref until termination.
    if (!check(env, mpv_set_property_string(p->mpv, "vo", "null"))) {
        if (next) env->DeleteGlobalRef(next);
        return;
    }
    int64_t wid = reinterpret_cast<intptr_t>(next);
    if (!check(env, mpv_set_property(p->mpv, "wid", MPV_FORMAT_INT64, &wid))) {
        if (next) env->DeleteGlobalRef(next);
        return;
    }
    if (p->surface) env->DeleteGlobalRef(p->surface);
    p->surface = next;
    if (next) check(env, mpv_set_property_string(p->mpv, "vo", "gpu"));
}

extern "C" JNIEXPORT void JNICALL JNI_METHOD(nativeSetFileDescriptor)(JNIEnv *env, jclass, jlong handle, jint fd, jlong offset, jlong length) {
    auto *p = player(env, handle);
    if (!p) return;
    std::unique_ptr<FileSlice> file(new FileSlice);
    if (!file->open(fd, offset, length)) {
        fail(env, "Cannot duplicate file descriptor or invalid/non-seekable file range", "java/io/IOException");
        return;
    }
    std::lock_guard<std::mutex> lock(p->fileMutex);
    p->file = std::move(file);
}
