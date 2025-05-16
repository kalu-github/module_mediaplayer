#ifndef LOGGING_H
#define LOGGING_H

#include <jni.h>
#include <android/log.h>


#define LOG_TAG "MP_EXO_FFMPEG_JNI"

void setLevel(jboolean flag);

int getLevel();

// 动态日志打印宏
#define LOGV(...) \
    do { \
        if (getLevel() == 1) { \
            __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__); \
        } \
    } while (0)

#define LOGD(...) \
    do { \
        if (getLevel() == 1) { \
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__); \
        } \
    } while (0)

#define LOGI(...) \
    do { \
        if (getLevel() == 1) { \
            __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__); \
        } \
    } while (0)

#define LOGW(...) \
    do { \
        if (getLevel() == 1) { \
            __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__); \
        } \
    } while (0)

#define LOGE(...) \
    do { \
        if (getLevel() == 1) { \
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__); \
        } \
    } while (0)

#endif // LOGGING_H