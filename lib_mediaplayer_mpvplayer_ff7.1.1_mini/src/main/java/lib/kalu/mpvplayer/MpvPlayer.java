package lib.kalu.mpvplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import lib.kalu.mpvplayer.inter.*;

/**
 * Per-instance libmpv player. Times are milliseconds; callbacks run on the main looper.
 * Call setSurface(null) before the surface is destroyed, and release() when finished.
 * Requires Android 6.0+. Native errors retain their libmpv error code in onError's extra.
 */
public class MpvPlayer implements AutoCloseable {
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    private static final int PREPARED = 1, COMPLETE = 2, BUFFERING = 3,
            SEEK_COMPLETE = 4, VIDEO_SIZE = 5, TIMED_TEXT = 99, ERROR = 100, INFO = 200;
    private enum State { IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED, ERROR, RELEASED }

    static { System.loadLibrary("mpvplayer"); }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private long nativeHandle;
    private int generation;
    private State state = State.IDLE;
    private String source;
    private Map<String, String> headers = Collections.emptyMap();
    private Surface surface;
    private SurfaceHolder holder;
    private boolean screenOn, looping, hardwareDecoding = true, restartOnPrepared;
    private float volume = 1f, speed = 1f;
    private OnPreparedListener onPrepared;
    private OnCompletionListener onCompletion;
    private OnBufferingUpdateListener onBuffering;
    private OnSeekCompleteListener onSeek;
    private OnVideoSizeChangedListener onSize;
    private OnErrorListener onError;
    private OnInfoListener onInfo;
    private OnTimedTextListener onText;

    public MpvPlayer() { nativeHandle = nativeCreate(new WeakReference<>(this), ++generation); }

    public synchronized void setDataSource(String path) { setDataSource(path, Collections.emptyMap()); }

    public synchronized void setDataSource(String path, Map<String, String> requestHeaders) {
        require(State.IDLE);
        if (path == null || path.isEmpty() || path.indexOf('\0') >= 0)
            throw new IllegalArgumentException("Empty or invalid media path");
        Map<String, String> copy = new LinkedHashMap<>();
        if (requestHeaders != null) {
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                String key = entry.getKey(), value = entry.getValue();
                if (key == null || !key.matches("[!#$%&'*+.^_`|~0-9A-Za-z-]+") ||
                        value == null || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\0') >= 0)
                    throw new IllegalArgumentException("Invalid HTTP header");
                copy.put(key, value);
            }
        }
        source = path;
        headers = copy;
        state = State.INITIALIZED;
    }

    public synchronized void setDataSource(Context context, Uri uri) throws IOException {
        setDataSource(context, uri, Collections.emptyMap());
    }

    public synchronized void setDataSource(Context context, Uri uri, Map<String, String> requestHeaders) throws IOException {
        require(State.IDLE);
        String scheme = uri.getScheme();
        if ("content".equals(scheme) || "android.resource".equals(scheme)) {
            try (AssetFileDescriptor afd = context.getContentResolver().openAssetFileDescriptor(uri, "r")) {
                if (afd == null) throw new IOException("Cannot open " + uri);
                setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            }
        } else {
            setDataSource("file".equals(scheme) ? uri.getPath() : uri.toString(), requestHeaders);
        }
    }

    public synchronized void setDataSource(FileDescriptor fd) throws IOException { setDataSource(fd, 0, -1); }

    /** Descriptor must refer to a seekable regular file. It may be closed after this call. */
    public synchronized void setDataSource(FileDescriptor fd, long offset, long length) throws IOException {
        require(State.IDLE);
        if (offset < 0 || length < -1) throw new IllegalArgumentException("Invalid file range");
        try (ParcelFileDescriptor duplicate = ParcelFileDescriptor.dup(fd)) {
            nativeSetFileDescriptor(nativeHandle, duplicate.getFd(), offset, length);
        }
        source = "androidfd://media";
        headers = Collections.emptyMap();
        state = State.INITIALIZED;
    }

    public synchronized void prepareAsync() { prepareInternal(false); }

    /** Waits for FILE_LOADED, with a 30 second timeout. Prefer prepareAsync on the UI thread. */
    public synchronized void prepare() throws IOException {
        try { prepareInternal(true); }
        catch (IllegalStateException e) { throw new IOException("Unable to prepare media", e); }
    }

    private void prepareInternal(boolean wait) {
        require(State.INITIALIZED, State.STOPPED);
        String[] fields = new String[headers.size()];
        int index = 0;
        for (Map.Entry<String, String> e : headers.entrySet()) fields[index++] = e.getKey() + ": " + e.getValue();
        state = State.PREPARING;
        try {
            nativePrepare(nativeHandle, source, fields, wait, ++generation);
            if (wait) state = State.PREPARED;
        } catch (RuntimeException e) { state = State.ERROR; throw e; }
    }

    public synchronized void start() {
        require(State.PREPARED, State.STARTED, State.PAUSED, State.COMPLETED);
        if (state == State.COMPLETED) {
            state = State.STOPPED;
            restartOnPrepared = true;
            prepareInternal(false);
            return;
        }
        set("pause", "no");
        state = State.STARTED;
        updateScreen();
    }

    public synchronized void pause() {
        require(State.PREPARED, State.STARTED, State.PAUSED);
        set("pause", "yes");
        state = State.PAUSED;
        updateScreen();
    }

    public synchronized void stop() {
        require(State.PREPARED, State.STARTED, State.PAUSED, State.COMPLETED, State.STOPPED);
        nativeCommand(nativeHandle, new String[]{"stop"});
        state = State.STOPPED;
        updateScreen();
    }

    public synchronized void seekTo(int milliseconds) { seekTo((long) milliseconds); }
    public synchronized void seekTo(long milliseconds) {
        require(State.PREPARED, State.STARTED, State.PAUSED);
        if (milliseconds < 0) throw new IllegalArgumentException("Negative seek position");
        nativeSeek(nativeHandle, milliseconds / 1000.0);
    }

    public synchronized int getCurrentPosition() { return clampTime(getCurrentPositionLong()); }
    public synchronized long getCurrentPositionLong() { return Math.max(0, (long) (number("time-pos", 0) * 1000)); }
    public synchronized int getDuration() { return clampTime(getDurationLong()); }
    public synchronized long getDurationLong() { return Math.max(0, (long) (number("duration", 0) * 1000)); }
    private static int clampTime(long time) { return (int) Math.min(Integer.MAX_VALUE, time); }
    public synchronized int getVideoWidth() { return (int) number("dwidth", 0); }
    public synchronized int getVideoHeight() { return (int) number("dheight", 0); }
    public synchronized boolean isPlaying() { checkAlive(); return state == State.STARTED; }
    public synchronized boolean isLooping() { checkAlive(); return looping; }

    public synchronized void setLooping(boolean enabled) {
        set("loop-file", enabled ? "inf" : "no"); looping = enabled;
    }
    /** mpv volume applies to all channels. Use the one-argument method for explicit master volume. */
    public synchronized void setVolume(float left, float right) {
        validateVolume(left); validateVolume(right);
        setVolume((left + right) / 2f);
    }
    private static void validateVolume(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value < 0 || value > 1)
            throw new IllegalArgumentException("Volume must be 0..1");
    }
    public synchronized void setVolume(float value) {
        validateVolume(value);
        set("volume", Float.toString(value * 100)); volume = value;
    }
    public synchronized void setSpeed(float value) {
        if ((Float.isNaN(value) || Float.isInfinite(value)) || value <= 0) throw new IllegalArgumentException("Speed must be positive");
        set("speed", Float.toString(value)); speed = value;
    }
    public synchronized float getSpeed() { checkAlive(); return speed; }
    public synchronized void setHardwareDecoding(boolean enabled) {
        set("hwdec", enabled ? "mediacodec-copy" : "no"); hardwareDecoding = enabled;
    }

    public synchronized void setDisplay(SurfaceHolder value) {
        checkAlive();
        Surface next = value == null ? null : value.getSurface();
        attach(next);
        if (holder != null) holder.setKeepScreenOn(false);
        holder = value;
        updateScreen();
    }
    public synchronized void setSurface(Surface value) {
        checkAlive();
        attach(value);
        if (holder != null) holder.setKeepScreenOn(false);
        holder = null;
    }
    private void attach(Surface value) {
        if (value != null && !value.isValid()) throw new IllegalArgumentException("Invalid Surface");
        nativeSetSurface(nativeHandle, value);
        surface = value;
    }
    public synchronized void setSurfaceSize(int width, int height) {
        if (width < 0 || height < 0) throw new IllegalArgumentException("Invalid surface size");
        set("android-surface-size", width + "x" + height);
    }
    public synchronized void setScreenOnWhilePlaying(boolean enabled) { checkAlive(); screenOn = enabled; updateScreen(); }
    private void updateScreen() { if (holder != null) holder.setKeepScreenOn(screenOn && state == State.STARTED); }

    public synchronized void reset() {
        checkAlive();
        long old = nativeHandle;
        nativeHandle = 0;
        ++generation; // Invalidate callbacks already posted by the old instance.
        state = State.IDLE;
        updateScreen();
        nativeRelease(old);
        handler.removeCallbacksAndMessages(null);
        source = null;
        headers = Collections.emptyMap();
        restartOnPrepared = false;
        try {
            nativeHandle = nativeCreate(new WeakReference<>(this), generation);
            setLooping(looping);
            setVolume(volume);
            setSpeed(speed);
            setHardwareDecoding(hardwareDecoding);
            if (surface != null && surface.isValid()) nativeSetSurface(nativeHandle, surface);
        } catch (RuntimeException e) { release(); throw e; }
    }

    public synchronized void release() {
        if (state == State.RELEASED) return;
        ++generation;
        state = State.RELEASED;
        updateScreen();
        long old = nativeHandle;
        nativeHandle = 0;
        nativeRelease(old);
        handler.removeCallbacksAndMessages(null);
        surface = null;
        holder = null;
        onPrepared = null; onCompletion = null; onBuffering = null; onSeek = null;
        onSize = null; onError = null; onInfo = null; onText = null;
    }
    @Override public void close() { release(); }

    private void checkAlive() { if (nativeHandle == 0) throw new IllegalStateException("Player released"); }
    private void require(State... allowed) {
        checkAlive();
        for (State candidate : allowed) if (state == candidate) return;
        throw new IllegalStateException("Invalid player state: " + state);
    }
    private void set(String name, String value) { checkAlive(); nativeSetProperty(nativeHandle, name, value); }
    private double number(String name, double fallback) {
        checkAlive(); return nativeGetNumber(nativeHandle, name, fallback);
    }

    // Native event thread only enqueues work. Never acquire the player monitor here:
    // release/reset may be holding it while joining that thread.
    private static void postEventFromNative(Object reference, int generation, int event, int arg1, int arg2, byte[] bytes) {
        String text = bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
        MpvPlayer player = (MpvPlayer) ((WeakReference<?>) reference).get();
        if (player != null) player.handler.post(() -> player.dispatch(generation, event, arg1, arg2, text));
    }

    private synchronized void dispatch(int token, int event, int arg1, int arg2, String text) {
        if (token != generation || state == State.RELEASED || state == State.STOPPED) return;
        switch (event) {
            case PREPARED:
                if (state != State.PREPARING && state != State.PREPARED) return;
                state = State.PREPARED;
                if (restartOnPrepared) { restartOnPrepared = false; start(); }
                else if (onPrepared != null) onPrepared.onPrepared(this);
                break;
            case COMPLETE:
                state = State.COMPLETED;
                updateScreen();
                if (onCompletion != null) onCompletion.onCompletion(this);
                break;
            case ERROR:
                state = State.ERROR;
                updateScreen();
                if ((onError == null || !onError.onError(this, MEDIA_ERROR_UNKNOWN, arg1)) && token == generation && onCompletion != null)
                    onCompletion.onCompletion(this);
                break;
            case BUFFERING: if (onBuffering != null) onBuffering.onBufferingUpdate(this, arg1); break;
            case SEEK_COMPLETE: if (onSeek != null) onSeek.onSeekComplete(this); break;
            case VIDEO_SIZE: if (onSize != null) onSize.onVideoSizeChanged(this, arg1, arg2); break;
            case INFO: if (onInfo != null) onInfo.onInfo(this, arg1, arg2); break;
            case TIMED_TEXT: if (onText != null) onText.onTimedText(this, text == null || text.isEmpty() ? null : TimedText.fromText(text)); break;
            default: break;
        }
    }

    public synchronized void setOnPreparedListener(OnPreparedListener listener) { onPrepared = listener; }
    public synchronized void setOnCompletionListener(OnCompletionListener listener) { onCompletion = listener; }
    public synchronized void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) { onBuffering = listener; }
    public synchronized void setOnSeekCompleteListener(OnSeekCompleteListener listener) { onSeek = listener; }
    public synchronized void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) { onSize = listener; }
    public synchronized void setOnErrorListener(OnErrorListener listener) { onError = listener; }
    public synchronized void setOnInfoListener(OnInfoListener listener) { onInfo = listener; }
    public synchronized void setOnTimedTextListener(OnTimedTextListener listener) { onText = listener; }

    private static native long nativeCreate(Object reference, int generation);
    private static native void nativeRelease(long handle);
    private static native void nativePrepare(long handle, String source, String[] headers, boolean wait, int generation);
    private static native void nativeCommand(long handle, String[] arguments);
    private static native void nativeSeek(long handle, double seconds);
    private static native void nativeSetProperty(long handle, String name, String value);
    private static native double nativeGetNumber(long handle, String name, double fallback);
    private static native void nativeSetSurface(long handle, Surface surface);
    private static native void nativeSetFileDescriptor(long handle, int fd, long offset, long length) throws IOException;
}
