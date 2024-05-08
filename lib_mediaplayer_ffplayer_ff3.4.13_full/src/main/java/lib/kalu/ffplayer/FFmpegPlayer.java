package lib.kalu.ffplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lib.kalu.ffplayer.inter.OnBufferingUpdateListener;
import lib.kalu.ffplayer.inter.OnCompletionListener;
import lib.kalu.ffplayer.inter.OnErrorListener;
import lib.kalu.ffplayer.inter.OnInfoListener;
import lib.kalu.ffplayer.inter.OnPreparedListener;
import lib.kalu.ffplayer.inter.OnSeekCompleteListener;
import lib.kalu.ffplayer.inter.OnTimedTextListener;
import lib.kalu.ffplayer.inter.OnVideoSizeChangedListener;

@Keep
public class FFmpegPlayer {

    /**
     * Constant to retrieve only the new metadata since the last
     * call.
     * // FIXME: unhide.
     * // FIXME: add link to getMetadata(boolean, boolean)
     * {@hide}
     */
    public static final boolean METADATA_UPDATE_ONLY = true;

    /**
     * Constant to retrieve all the metadata.
     * // FIXME: unhide.
     * // FIXME: add link to getMetadata(boolean, boolean)
     * {@hide}
     */
    public static final boolean METADATA_ALL = false;

    /**
     * Constant to enable the metadata filter during retrieval.
     * // FIXME: unhide.
     * // FIXME: add link to getMetadata(boolean, boolean)
     * {@hide}
     */
    public static final boolean APPLY_METADATA_FILTER = true;

    /**
     * Constant to disable the metadata filter during retrieval.
     * // FIXME: unhide.
     * // FIXME: add link to getMetadata(boolean, boolean)
     * {@hide}
     */
    public static final boolean BYPASS_METADATA_FILTER = false;

    /*static {
        System.loadLibrary("media_jni");
        native_init();
    }*/

    private final static String TAG = "FFmpegMediaPlayer";

    static {
        try {
            System.loadLibrary("ffplayer");
            native_init();
        } catch (Exception e) {
            throw e;
        }
    }

    // Name of the remote interface for the media player. Must be kept
    // in sync with the 2nd parameter of the IMPLEMENT_META_INTERFACE
    // macro invocation in IMediaPlayer.cpp
    private final static String IMEDIA_PLAYER = "wseemann.media.IMediaPlayer";

    private long mNativeContext; // accessed by native methods
    private int mNativeSurfaceTexture;  // accessed by native methods
    private int mListenerContext; // accessed by native methods
    private SurfaceHolder mSurfaceHolder;
    private EventHandler mEventHandler;
    private PowerManager.WakeLock mWakeLock = null;
    private boolean mScreenOnWhilePlaying;
    private boolean mStayAwake;

    /**
     * Default constructor. Consider using one of the create() methods for
     * synchronously instantiating a MediaPlayer from a Uri or resource.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances may
     * result in an exception.</p>
     */
    public FFmpegPlayer() {

        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }

        /* Native setup requires a weak reference to our object.
         * It's easier to create it here than in C++.
         */
        native_setup(new WeakReference<>(this));
    }

    /*
     * Update the MediaPlayer SurfaceTexture.
     * Call after setting a new display surface.
     */
    private native void _setVideoSurface(Surface surface);

    /**
     * Create a request parcel which can be routed to the native media
     * player using {@link #invoke(Parcel, Parcel)}. The Parcel
     * returned has the proper InterfaceToken set. The caller should
     * not overwrite that token, i.e it can only append data to the
     * Parcel.
     *
     * @return A parcel suitable to hold a request for the native
     * player.
     * {@hide}
     */
    public Parcel newRequest() {
        Parcel parcel = Parcel.obtain();
        parcel.writeInterfaceToken(IMEDIA_PLAYER);
        return parcel;
    }

    /**
     * Invoke a generic method on the native player using opaque
     * parcels for the request and reply. Both payloads' format is a
     * convention between the java caller and the native player.
     * Must be called after setDataSource to make sure a native player
     * exists.
     *
     * @param request Parcel with the data for the extension. The
     *                caller must use {@link #newRequest()} to get one.
     * @param reply   Output parcel with the data returned by the
     *                native player.
     * @return The status code see utils/Errors.h
     * {@hide}
     */
    public int invoke(Parcel request, Parcel reply) {
        int retcode = native_invoke(request, reply);
        reply.setDataPosition(0);
        return retcode;
    }

    /**
     * Sets the {@link SurfaceHolder} to use for displaying the video
     * portion of the media.
     * <p>
     * Either a surface holder or surface must be set if a display or video sink
     * is needed.  Not calling this method or {@link #setSurface(Surface)}
     * when playing back a video will result in only the audio track being played.
     * A null surface holder or surface will result in only the audio track being
     * played.
     *
     * @param sh the SurfaceHolder to use for video display
     */
    public void setDisplay(SurfaceHolder sh) {
        mSurfaceHolder = sh;
        Surface surface;
        if (sh != null) {
            surface = sh.getSurface();
        } else {
            surface = null;
        }
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }

    /**
     * Sets the {@link Surface} to be used as the sink for the video portion of
     * the media. This is similar to {@link #setDisplay(SurfaceHolder)}, but
     * does not support {@link #setScreenOnWhilePlaying(boolean)}.  Setting a
     * Surface will un-set any Surface or SurfaceHolder that was previously set.
     * A null surface will result in only the audio track being played.
     * <p>
     * If the Surface sends frames to a {@link SurfaceTexture}, the timestamps
     * returned from {@link SurfaceTexture#getTimestamp()} will have an
     * unspecified zero point.  These timestamps cannot be directly compared
     * between different media sources, different instances of the same media
     * source, or multiple runs of the same program.  The timestamp is normally
     * monotonically increasing and is unaffected by time-of-day adjustments,
     * but it is reset when the position is set.
     *
     * @param surface The {@link Surface} to be used for the video portion of
     *                the media.
     */
    public void setSurface(Surface surface) {
        if (mScreenOnWhilePlaying && surface != null) {
            Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective for Surface");
        }
        mSurfaceHolder = null;
        _setVideoSurface(surface);
        updateSurfaceScreenOn();
    }

    /**
     * Convenience method to create a MediaPlayer for a given Uri.
     * On success, {@link #prepare()} will already have been called and must not be called again.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances will
     * result in an exception.</p>
     *
     * @param context the Context to use
     * @param uri     the Uri from which to get the datasource
     * @return a MediaPlayer object, or null if creation failed
     */
    public static FFmpegPlayer create(Context context, Uri uri) {
        return create(context, uri, null);
    }

    /**
     * Convenience method to create a MediaPlayer for a given Uri.
     * On success, {@link #prepare()} will already have been called and must not be called again.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances will
     * result in an exception.</p>
     *
     * @param context the Context to use
     * @param uri     the Uri from which to get the datasource
     * @param holder  the SurfaceHolder to use for displaying the video
     * @return a MediaPlayer object, or null if creation failed
     */
    public static FFmpegPlayer create(Context context, Uri uri, SurfaceHolder holder) {

        try {
            FFmpegPlayer mp = new FFmpegPlayer();
            mp.setDataSource(context, uri);
            if (holder != null) {
                mp.setDisplay(holder);
            }
            mp.prepare();
            return mp;
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        }

        return null;
    }

    // Note no convenience method to create a MediaPlayer with SurfaceTexture sink.

    /**
     * Convenience method to create a MediaPlayer for a given resource id.
     * On success, {@link #prepare()} will already have been called and must not be called again.
     * <p>When done with the MediaPlayer, you should call  {@link #release()},
     * to free the resources. If not released, too many MediaPlayer instances will
     * result in an exception.</p>
     *
     * @param context the Context to use
     * @param resid   the raw resource id (<var>R.raw.&lt;something></var>) for
     *                the resource to use as the datasource
     * @return a MediaPlayer object, or null if creation failed
     */
    public static FFmpegPlayer create(Context context, int resid) {
        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
            if (afd == null) return null;

            FFmpegPlayer mp = new FFmpegPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
            return mp;
        } catch (IOException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        } catch (SecurityException ex) {
            Log.d(TAG, "create failed:", ex);
            // fall through
        }
        return null;
    }

    /**
     * Sets the data source as a content Uri.
     *
     * @param context the Context to use when resolving the Uri
     * @param uri     the Content URI of the data you want to play
     * @throws IllegalStateException if it is called in an invalid state
     */
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(context, uri, null);
    }

    /**
     * Sets the data source as a content Uri.
     *
     * @param context the Context to use when resolving the Uri
     * @param uri     the Content URI of the data you want to play
     * @param headers the headers to be sent together with the request for the data
     * @throws IllegalStateException if it is called in an invalid state
     */
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("file")) {
            setDataSource(uri.getPath());
            return;
        }

        AssetFileDescriptor fd = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            fd = resolver.openAssetFileDescriptor(uri, "r");
            if (fd == null) {
                return;
            }
            // Note: using getDeclaredLength so that our behavior is the same
            // as previous versions when the content provider is returning
            // a full file.
            if (fd.getDeclaredLength() < 0) {
                setDataSource(fd.getFileDescriptor());
            } else {
                setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
            }
            return;
        } catch (SecurityException ex) {
        } catch (IOException ex) {
        } finally {
            if (fd != null) {
                fd.close();
            }
        }

        Log.d(TAG, "Couldn't open file on client side, trying server side");
        setDataSource(uri.toString(), headers);
    }

    /**
     * Sets the data source (file-path or http/rtsp URL) to use.
     *
     * @param path the path of the file, or the http/rtsp URL of the stream you want to play
     * @throws IllegalStateException if it is called in an invalid state
     *
     *                               <p>When <code>path</code> refers to a local file, the file may actually be opened by a
     *                               process other than the calling application.  This implies that the pathname
     *                               should be an absolute path (as any other process runs with unspecified current working
     *                               directory), and that the pathname should reference a world-readable file.
     *                               As an alternative, the application could first open the file for reading,
     *                               and then use the file descriptor form {@link #setDataSource(FileDescriptor)}.
     */
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        setDataSource(path, null, null);
    }

    /**
     * Sets the data source (file-path or http/rtsp URL) to use.
     *
     * @param path    the path of the file, or the http/rtsp URL of the stream you want to play
     * @param headers the headers associated with the http request for the stream you want to play
     * @throws IllegalStateException if it is called in an invalid state
     * @hide pending API council
     */
    public void setDataSource(String path, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        String[] keys = null;
        String[] values = null;

        if (headers != null) {
            keys = new String[headers.size()];
            values = new String[headers.size()];

            int i = 0;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                keys[i] = entry.getKey();
                values[i] = entry.getValue();
                ++i;
            }
        }
        setDataSource(path, keys, values);
    }

    private void setDataSource(String path, String[] keys, String[] values) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

        final Uri uri = Uri.parse(path);
        if ("file".equals(uri.getScheme())) {
            path = uri.getPath();
        }

        final File file = new File(path);
        if (file.exists()) {
            //FileInputStream is = new FileInputStream(file);
            //FileDescriptor fd = is.getFD();
            //setDataSource(fd);
            //is.close();

            _setDataSource(path, keys, values);
        } else {
            _setDataSource(path, keys, values);
        }
    }

    private native void _setDataSource(String path, String[] keys, String[] values) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    /**
     * Sets the data source (FileDescriptor) to use. It is the caller's responsibility
     * to close the file descriptor. It is safe to do so as soon as this call returns.
     *
     * @param fd the FileDescriptor for the file you want to play
     * @throws IllegalStateException if it is called in an invalid state
     */
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        // intentionally less than LONG_MAX
        setDataSource(fd, 0, 0x7ffffffffffffffL);
    }

    /**
     * Sets the data source (FileDescriptor) to use.  The FileDescriptor must be
     * seekable (N.B. a LocalSocket is not seekable). It is the caller's responsibility
     * to close the file descriptor. It is safe to do so as soon as this call returns.
     *
     * @param fd     the FileDescriptor for the file you want to play
     * @param offset the offset into the file where the data to be played starts, in bytes
     * @param length the length in bytes of the data to be played
     * @throws IllegalStateException if it is called in an invalid state
     */
    public void setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException {
        //disableProxyListener();
        _setDataSource(fd, offset, length);
    }

    private native void _setDataSource(FileDescriptor fd, long offset, long length) throws IOException, IllegalArgumentException, IllegalStateException;


    /**
     * Prepares the player for playback, synchronously.
     * <p>
     * After setting the datasource and the display surface, you need to either
     * call prepare() or prepareAsync(). For files, it is OK to call prepare(),
     * which blocks until MediaPlayer is ready for playback.
     *
     * @throws IllegalStateException if it is called in an invalid state
     */
    public native void prepare() throws IOException, IllegalStateException;

    /**
     * Prepares the player for playback, asynchronously.
     * <p>
     * After setting the datasource and the display surface, you need to either
     * call prepare() or prepareAsync(). For streams, you should call prepareAsync(),
     * which returns immediately, rather than blocking until enough data has been
     * buffered.
     *
     * @throws IllegalStateException if it is called in an invalid state
     */
    public native void prepareAsync() throws IllegalStateException;

    /**
     * Starts or resumes playback. If playback had previously been paused,
     * playback will continue from where it was paused. If playback had
     * been stopped, or never started before, playback will start at the
     * beginning.
     *
     * @throws IllegalStateException if it is called in an invalid state
     */
    public void start() throws IllegalStateException {
        stayAwake(true);
        _start();
    }

    private native void _start() throws IllegalStateException;

    /**
     * Stops playback after playback has been stopped or paused.
     *
     * @throws IllegalStateException if the internal player engine has not been
     *                               initialized.
     */
    public void stop() throws IllegalStateException {
        stayAwake(false);
        _stop();
    }

    private native void _stop() throws IllegalStateException;

    /**
     * Pauses playback. Call start() to resume.
     *
     * @throws IllegalStateException if the internal player engine has not been
     *                               initialized.
     */
    public void pause() throws IllegalStateException {
        stayAwake(false);
        _pause();
    }

    private native void _pause() throws IllegalStateException;

    /**
     * Set the low-level power management behavior for this MediaPlayer.  This
     * can be used when the MediaPlayer is not playing through a SurfaceHolder
     * set with {@link #setDisplay(SurfaceHolder)} and thus can use the
     * high-level {@link #setScreenOnWhilePlaying(boolean)} feature.
     *
     * <p>This function has the MediaPlayer access the low-level power manager
     * service to control the device's power usage while playing is occurring.
     * The parameter is a combination of {@link PowerManager} wake flags.
     * Use of this method requires {@link android.Manifest.permission#WAKE_LOCK}
     * permission.
     * By default, no attempt is made to keep the device awake during playback.
     *
     * @param context the Context to use
     * @param mode    the power/wake mode to set
     * @see PowerManager
     */
    public void setWakeMode(Context context, int mode) {
        boolean washeld = false;
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                washeld = true;
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(mode | PowerManager.ON_AFTER_RELEASE, FFmpegPlayer.class.getName());
        mWakeLock.setReferenceCounted(false);
        if (washeld) {
            mWakeLock.acquire();
        }
    }

    /**
     * Control whether we should use the attached SurfaceHolder to keep the
     * screen on while video playback is occurring.  This is the preferred
     * method over {@link #setWakeMode} where possible, since it doesn't
     * require that the application have permission for low-level wake lock
     * access.
     *
     * @param screenOn Supply true to keep the screen on, false to allow it
     *                 to turn off.
     */
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            if (screenOn && mSurfaceHolder == null) {
                Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            mScreenOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }

    private void stayAwake(boolean awake) {
        if (mWakeLock != null) {
            if (awake && !mWakeLock.isHeld()) {
                mWakeLock.acquire();
            } else if (!awake && mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
        mStayAwake = awake;
        updateSurfaceScreenOn();
    }

    private void updateSurfaceScreenOn() {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
        }
    }

    /**
     * Returns the width of the video.
     *
     * @return the width of the video, or 0 if there is no video,
     * no display surface was set, or the width has not been determined
     * yet. The OnVideoSizeChangedListener can be registered via
     * {@link #setOnVideoSizeChangedListener(OnVideoSizeChangedListener)}
     * to provide a notification when the width is available.
     */
    public native int getVideoWidth();

    /**
     * Returns the height of the video.
     *
     * @return the height of the video, or 0 if there is no video,
     * no display surface was set, or the height has not been determined
     * yet. The OnVideoSizeChangedListener can be registered via
     * {@link #setOnVideoSizeChangedListener(OnVideoSizeChangedListener)}
     * to provide a notification when the height is available.
     */
    public native int getVideoHeight();

    /**
     * Checks whether the MediaPlayer is playing.
     *
     * @return true if currently playing, false otherwise
     */
    public native boolean isPlaying();

    /**
     * Seeks to specified time position.
     *
     * @param msec the offset in milliseconds from the start to seek to
     * @throws IllegalStateException if the internal player engine has not been
     *                               initialized
     */
    public native void seekTo(int msec) throws IllegalStateException;

    /**
     * Gets the current playback position.
     *
     * @return the current position in milliseconds
     */
    public native int getCurrentPosition();

    /**
     * Gets the duration of the file.
     *
     * @return the duration in milliseconds
     */
    public native int getDuration();

    /**
     * Gets the media metadata.
     *
     * @param update_only  controls whether the full set of available
     *                     metadata is returned or just the set that changed since the
     *                     last call. See {@see #METADATA_UPDATE_ONLY} and {@see
     *                     #METADATA_ALL}.
     * @param apply_filter if true only metadata that matches the
     *                     filter is returned. See {@see #APPLY_METADATA_FILTER} and {@see
     *                     #BYPASS_METADATA_FILTER}.
     * @return The metadata, possibly empty. null if an error occured.
     * // FIXME: unhide.
     * {@hide}
     */
    public Metadata getMetadata() {//final boolean update_only,
        //	final boolean apply_filter) {
        boolean update_only = false;
        boolean apply_filter = false;

        Metadata data = new Metadata();
        HashMap<String, String> metadata = null;
        if ((metadata = native_getMetadata(update_only, apply_filter, metadata)) == null) {
            return null;
        }

        // Metadata takes over the parcel, don't recycle it unless
        // there is an error.
        if (!data.parse(metadata)) {
            return null;
        }
        return data;
    }

    /**
     * Set a filter for the metadata update notification and update
     * retrieval. The caller provides 2 set of metadata keys, allowed
     * and blocked. The blocked set always takes precedence over the
     * allowed one.
     * Metadata.MATCH_ALL and Metadata.MATCH_NONE are 2 sets available as
     * shorthands to allow/block all or no metadata.
     * <p>
     * By default, there is no filter set.
     *
     * @param allow Is the set of metadata the client is interested
     *              in receiving new notifications for.
     * @param block Is the set of metadata the client is not interested
     *              in receiving new notifications for.
     * @return The call status code.
     * <p>
     * // FIXME: unhide.
     * {@hide}
     */
    public int setMetadataFilter(Set<String> allow, Set<String> block) {
        int i = 0;

        String[] allowed = new String[allow.size()];
        String[] blocked = new String[block.size()];

        for (String s : allow) {
            allowed[i] = s;
            i++;
        }

        i = 0;

        for (String s : block) {
            blocked[i] = s;
            i++;
        }

        return native_setMetadataFilter(allowed, blocked);
    }

    /**
     * Set the MediaPlayer to start when this MediaPlayer finishes playback
     * (i.e. reaches the end of the stream).
     * The media framework will attempt to transition from this player to
     * the next as seamlessly as possible. The next player can be set at
     * any time before completion. The next player must be prepared by the
     * app, and the application should not call start() on it.
     * The next MediaPlayer must be different from 'this'. An exception
     * will be thrown if next == this.
     * The application may call setNextMediaPlayer(null) to indicate no
     * next player should be started at the end of playback.
     * If the current player is looping, it will keep looping and the next
     * player will not be started.
     *
     * @param next the player to start after this one completes playback.
     */
    public native void setNextMediaPlayer(FFmpegPlayer next);

    /**
     * Releases resources associated with this MediaPlayer object.
     * It is considered good practice to call this method when you're
     * done using the MediaPlayer. For instance, whenever the Activity
     * of an application is paused, this method should be invoked to
     * release the MediaPlayer object. In addition to unnecessary resources
     * (such as memory and instances of codecs) being hold, failure to
     * call this method immediately if a MediaPlayer object is no longer
     * needed may also lead to continuous battery consumption for mobile
     * devices, and playback failure if no multiple instances of the
     * same codec is supported on a device.
     */
    public void release() {
        stayAwake(false);
        updateSurfaceScreenOn();
        mOnPreparedListener = null;
        mOnBufferingUpdateListener = null;
        mOnCompletionListener = null;
        mOnSeekCompleteListener = null;
        mOnErrorListener = null;
        mOnInfoListener = null;
        mOnVideoSizeChangedListener = null;
        mOnTimedTextListener = null;
        _release();
    }

    private native void _release();

    /**
     * Resets the MediaPlayer to its uninitialized state. After calling
     * this method, you will have to initialize it again by setting the
     * data source and calling prepare().
     */
    public void reset() {
        stayAwake(false);
        _reset();

        // make sure none of the listeners get called anymore
        mEventHandler.removeCallbacksAndMessages(null);
    }

    private native void _reset();

    /**
     * Sets the audio stream type for this MediaPlayer. See {@link AudioManager}
     * for a list of stream types. Must call this method before prepare() or
     * prepareAsync() in order for the target stream type to become effective
     * thereafter.
     *
     * @param streamtype the audio stream type
     * @see AudioManager
     */
    public native void setAudioStreamType(int streamtype);

    /**
     * Sets the player to be looping or non-looping.
     *
     * @param looping whether to loop or not
     */
    public native void setLooping(boolean looping);

    /**
     * Checks whether the MediaPlayer is looping or non-looping.
     *
     * @return true if the MediaPlayer is currently looping, false otherwise
     */
    public native boolean isLooping();

    /**
     * Sets the volume on this player.
     * This API is recommended for balancing the output of audio streams
     * within an application. Unless you are writing an application to
     * control user settings, this API should be used in preference to
     * {@link AudioManager#setStreamVolume(int, int, int)} which sets the volume of ALL streams of
     * a particular type. Note that the passed volume values are raw scalars.
     * UI controls should be scaled logarithmically.
     *
     * @param leftVolume  left volume scalar
     * @param rightVolume right volume scalar
     */
    public native void setVolume(float leftVolume, float rightVolume);

    /**
     * Currently not implemented, returns null.
     *
     * @hide
     * @deprecated
     */
    public native Bitmap getFrameAt(int msec) throws IllegalStateException;

    /**
     * Sets the audio session ID.
     *
     * @param sessionId the audio session ID.
     *                  The audio session ID is a system wide unique identifier for the audio stream played by
     *                  this MediaPlayer instance.
     *                  The primary use of the audio session ID  is to associate audio effects to a particular
     *                  instance of MediaPlayer: if an audio session ID is provided when creating an audio effect,
     *                  this effect will be applied only to the audio content of media players within the same
     *                  audio session and not to the output mix.
     *                  When created, a MediaPlayer instance automatically generates its own audio session ID.
     *                  However, it is possible to force this player to be part of an already existing audio session
     *                  by calling this method.
     *                  This method must be called before one of the overloaded <code> setDataSource </code> methods.
     * @throws IllegalStateException if it is called in an invalid state
     */
    public native void setAudioSessionId(int sessionId) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns the audio session ID.
     *
     * @return the audio session ID. {@see #setAudioSessionId(int)}
     * Note that the audio session ID is 0 only if a problem occured when the MediaPlayer was contructed.
     */
    public native int getAudioSessionId();

    /**
     * Attaches an auxiliary effect to the player. A typical auxiliary effect is a reverberation
     * effect which can be applied on any sound source that directs a certain amount of its
     * energy to this effect. This amount is defined by setAuxEffectSendLevel().
     * {@see #setAuxEffectSendLevel(float)}.
     * <p>After creating an auxiliary effect (e.g.
     * {@link android.media.audiofx.EnvironmentalReverb}), retrieve its ID with
     * {@link android.media.audiofx.AudioEffect#getId()} and use it when calling this method
     * to attach the player to the effect.
     * <p>To detach the effect from the player, call this method with a null effect id.
     * <p>This method must be called after one of the overloaded <code> setDataSource </code>
     * methods.
     *
     * @param effectId system wide unique id of the effect to attach
     */
    public native void attachAuxEffect(int effectId);

    /* Do not change these values (starting with KEY_PARAMETER) without updating
     * their counterparts in include/media/mediaplayer.h!
     */
    /*
     * Key used in setParameter method.
     * Indicates the index of the timed text track to be enabled/disabled.
     * The index includes both the in-band and out-of-band timed text.
     * The index should start from in-band text if any. Application can retrieve the number
     * of in-band text tracks by using MediaMetadataRetriever::extractMetadata().
     * Note it might take a few hundred ms to scan an out-of-band text file
     * before displaying it.
     */
    private static final int KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX = 1000;
    /*
     * Key used in setParameter method.
     * Used to add out-of-band timed text source path.
     * Application can add multiple text sources by calling setParameter() with
     * KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE multiple times.
     */
    private static final int KEY_PARAMETER_TIMED_TEXT_ADD_OUT_OF_BAND_SOURCE = 1001;

    // There are currently no defined keys usable from Java with get*Parameter.
    // But if any keys are defined, the order must be kept in sync with include/media/mediaplayer.h.
    // private static final int KEY_PARAMETER_... = ...;

    /**
     * Sets the parameter indicated by key.
     *
     * @param key   key indicates the parameter to be set.
     * @param value value of the parameter to be set.
     * @return true if the parameter is set successfully, false otherwise
     * {@hide}
     */
    public native boolean setParameter(int key, Parcel value);

    /**
     * Sets the parameter indicated by key.
     *
     * @param key   key indicates the parameter to be set.
     * @param value value of the parameter to be set.
     * @return true if the parameter is set successfully, false otherwise
     * {@hide}
     */
    public boolean setParameter(int key, String value) {
        Parcel p = Parcel.obtain();
        p.writeString(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    /**
     * Sets the parameter indicated by key.
     *
     * @param key   key indicates the parameter to be set.
     * @param value value of the parameter to be set.
     * @return true if the parameter is set successfully, false otherwise
     * {@hide}
     */
    public boolean setParameter(int key, int value) {
        Parcel p = Parcel.obtain();
        p.writeInt(value);
        boolean ret = setParameter(key, p);
        p.recycle();
        return ret;
    }

    /**
     * Gets the value of the parameter indicated by key.
     *
     * @param key   key indicates the parameter to get.
     * @param reply value of the parameter to get.
     */
    private native void getParameter(int key, Parcel reply);

    /**
     * Gets the value of the parameter indicated by key.
     * The caller is responsible for recycling the returned parcel.
     *
     * @param key key indicates the parameter to get.
     * @return value of the parameter.
     * {@hide}
     */
    public Parcel getParcelParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        return p;
    }

    /**
     * Gets the value of the parameter indicated by key.
     *
     * @param key key indicates the parameter to get.
     * @return value of the parameter.
     * {@hide}
     */
    public String getStringParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        String ret = p.readString();
        p.recycle();
        return ret;
    }

    /**
     * Gets the value of the parameter indicated by key.
     *
     * @param key key indicates the parameter to get.
     * @return value of the parameter.
     * {@hide}
     */
    public int getIntParameter(int key) {
        Parcel p = Parcel.obtain();
        getParameter(key, p);
        int ret = p.readInt();
        p.recycle();
        return ret;
    }

    /**
     * Sets the send level of the player to the attached auxiliary effect
     * {@see #attachAuxEffect(int)}. The level value range is 0 to 1.0.
     * <p>By default the send level is 0, so even if an effect is attached to the player
     * this method must be called for the effect to be applied.
     * <p>Note that the passed level value is a raw scalar. UI controls should be scaled
     * logarithmically: the gain applied by audio framework ranges from -72dB to 0dB,
     * so an appropriate conversion from linear UI input x to level is:
     * x == 0 -> level = 0
     * 0 < x <= R -> level = 10^(72*(x-R)/20/R)
     *
     * @param level send level scalar
     */
    public native void setAuxEffectSendLevel(float level);

    /**
     * @param request    Parcel destinated to the media player. The
     *                   Interface token must be set to the IMediaPlayer
     *                   one to be routed correctly through the system.
     * @param reply[out] Parcel that will contain the reply.
     * @return The status code.
     */
    private native final int native_invoke(Parcel request, Parcel reply);


    /**
     * @param update_only  If true fetch only the set of metadata that have
     *                     changed since the last invocation of getMetadata.
     *                     The set is built using the unfiltered
     *                     notifications the native player sent to the
     *                     MediaPlayerService during that period of
     *                     time. If false, all the metadatas are considered.
     * @param apply_filter If true, once the metadata set has been built based on
     *                     the value update_only, the current filter is applied.
     * @param reply[out]   On return contains the serialized
     *                     metadata. Valid only if the call was successful.
     * @return The status code.
     */
    private native final HashMap<String, String> native_getMetadata(boolean update_only, boolean apply_filter, HashMap<String, String> reply);

    /**
     * @param request Parcel with the 2 serialized lists of allowed
     *                metadata types followed by the one to be
     *                dropped. Each list starts with an integer
     *                indicating the number of metadata type elements.
     * @return The status code.
     */
    private native final int native_setMetadataFilter(String[] allowed, String[] blocked);

    private static native final void native_init();

    private native final void native_setup(Object mediaplayer_this);

    private native final void native_finalize();

    /**
     * @param index The index of the text track to be turned on.
     * @return true if the text track is enabled successfully.
     * {@hide}
     */
    public boolean enableTimedTextTrackIndex(int index) {
        if (index < 0) {
            return false;
        }
        return setParameter(KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX, index);
    }

    /**
     * Enables the first timed text track if any.
     *
     * @return true if the text track is enabled successfully
     * {@hide}
     */
    public boolean enableTimedText() {
        return enableTimedTextTrackIndex(0);
    }

    /**
     * Disables timed text display.
     *
     * @return true if the text track is disabled successfully.
     * {@hide}
     */
    public boolean disableTimedText() {
        return setParameter(KEY_PARAMETER_TIMED_TEXT_TRACK_INDEX, -1);
    }

    /**
     * @param reply Parcel with audio/video duration info for battery
     *              tracking usage
     * @return The status code.
     * {@hide}
     */
    public native static int native_pullBatteryData(Parcel reply);

    @Override
    protected void finalize() {
        native_finalize();
    }

    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    private static final int MEDIA_NOP = 0; // interface test message
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_TIMED_TEXT = 99;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;

    private class EventHandler extends Handler {
        private FFmpegPlayer mMediaPlayer;

        public EventHandler(FFmpegPlayer mp, Looper looper) {
            super(looper);
            mMediaPlayer = mp;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mMediaPlayer.mNativeContext == 0) {
                Log.w(TAG, "mediaplayer went away with unhandled events");
                return;
            }
            switch (msg.what) {
                case MEDIA_PREPARED:
                    if (mOnPreparedListener != null) mOnPreparedListener.onPrepared(mMediaPlayer);
                    return;

                case MEDIA_PLAYBACK_COMPLETE:
                    if (mOnCompletionListener != null)
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    stayAwake(false);
                    return;

                case MEDIA_BUFFERING_UPDATE:
                    if (mOnBufferingUpdateListener != null)
                        mOnBufferingUpdateListener.onBufferingUpdate(mMediaPlayer, msg.arg1);
                    return;

                case MEDIA_SEEK_COMPLETE:
                    if (mOnSeekCompleteListener != null)
                        mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
                    return;

                case MEDIA_SET_VIDEO_SIZE:
                    if (mOnVideoSizeChangedListener != null)
                        mOnVideoSizeChangedListener.onVideoSizeChanged(mMediaPlayer, msg.arg1, msg.arg2);
                    return;

                case MEDIA_ERROR:
                    // For PV specific error values (msg.arg2) look in
                    // opencore/pvmi/pvmf/include/pvmf_return_codes.h
                    Log.e(TAG, "Error (" + msg.arg1 + "," + msg.arg2 + ")");
                    boolean error_was_handled = false;
                    if (mOnErrorListener != null) {
                        error_was_handled = mOnErrorListener.onError(mMediaPlayer, msg.arg1, msg.arg2);
                    }
                    if (mOnCompletionListener != null && !error_was_handled) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                    stayAwake(false);
                    return;

                case MEDIA_INFO:
                    if (msg.arg1 != MEDIA_INFO_VIDEO_TRACK_LAGGING) {
                        Log.i(TAG, "Info (" + msg.arg1 + "," + msg.arg2 + ")");
                    }
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mMediaPlayer, msg.arg1, msg.arg2);
                    }
                    // No real default action so far.
                    return;
                case MEDIA_TIMED_TEXT:
                    if (mOnTimedTextListener != null) {
                        if (msg.obj == null) {
                            mOnTimedTextListener.onTimedText(mMediaPlayer, null);
                        } else {
                            if (msg.obj instanceof byte[]) {
                                TimedText text = new TimedText((byte[]) (msg.obj));
                                mOnTimedTextListener.onTimedText(mMediaPlayer, text);
                            }
                        }
                    }
                    return;

                case MEDIA_NOP: // interface test message - ignore
                    break;

                default:
                    Log.e(TAG, "Unknown message type " + msg.what);
                    return;
            }
        }
    }

    /**
     * Called from native code when an interesting event happens.  This method
     * just uses the EventHandler system to post the event back to the main app thread.
     * We use a weak reference to the original MediaPlayer object so that the native
     * code is safe from the object disappearing from underneath it.  (This is
     * the cookie passed to native_setup().)
     */
    private static void postEventFromNative(Object mediaplayer_ref, int what, int arg1, int arg2, Object obj) {
        FFmpegPlayer mp = (FFmpegPlayer) ((WeakReference) mediaplayer_ref).get();
        if (mp == null) {
            return;
        }

        if (mp.mEventHandler != null) {
            Message m = mp.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            mp.mEventHandler.sendMessage(m);
        }
    }

    /**
     * Register a callback to be invoked when the media source is ready
     * for playback.
     *
     * @param listener the callback that will be run
     */
    public void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
    }

    private OnPreparedListener mOnPreparedListener;

    /**
     * Register a callback to be invoked when the end of a media source
     * has been reached during playback.
     *
     * @param listener the callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    private OnCompletionListener mOnCompletionListener;

    /**
     * Register a callback to be invoked when the status of a network
     * stream's buffer has changed.
     *
     * @param listener the callback that will be run.
     */
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
    }

    private OnBufferingUpdateListener mOnBufferingUpdateListener;


    /**
     * Register a callback to be invoked when a seek operation has been
     * completed.
     *
     * @param listener the callback that will be run
     */
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        mOnSeekCompleteListener = listener;
    }

    private OnSeekCompleteListener mOnSeekCompleteListener;

    /**
     * Register a callback to be invoked when the video size is
     * known or updated.
     *
     * @param listener the callback that will be run
     */
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        mOnVideoSizeChangedListener = listener;
    }

    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    /**
     * Register a callback to be invoked when a timed text is available
     * for display.
     *
     * @param listener the callback that will be run
     *                 {@hide}
     */
    public void setOnTimedTextListener(OnTimedTextListener listener) {
        mOnTimedTextListener = listener;
    }

    private OnTimedTextListener mOnTimedTextListener;


    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /**
     * Unspecified media player error.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_UNKNOWN = 1;

    /**
     * Media server died. In this case, the application must release the
     * MediaPlayer object and instantiate a new one.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_SERVER_DIED = 100;

    /**
     * The video is streamed and its container is not valid for progressive
     * playback i.e the video's index (e.g moov atom) is not at the start of the
     * file.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;

    /**
     * Register a callback to be invoked when an error has happened
     * during an asynchronous operation.
     *
     * @param listener the callback that will be run
     */
    public void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    }

    private OnErrorListener mOnErrorListener;


    /* Do not change these values without updating their counterparts
     * in include/media/mediaplayer.h!
     */
    /**
     * Unspecified media player info.
     *
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_UNKNOWN = 1;

    /**
     * The video is too complex for the decoder: it can't decode frames fast
     * enough. Possibly only the audio plays fine at this stage.
     *
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;

    /**
     * MediaPlayer is temporarily pausing playback internally in order to
     * buffer more data.
     *
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_BUFFERING_START = 701;

    /**
     * MediaPlayer is resuming playback after filling buffers.
     *
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_BUFFERING_END = 702;

    /**
     * Bad interleaving means that a media has been improperly interleaved or
     * not interleaved at all, e.g has all the video samples first then all the
     * audio ones. Video is playing but a lot of disk seeks may be happening.
     *
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_BAD_INTERLEAVING = 800;

    /**
     * The media cannot be seeked (e.g live stream)
     *
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_NOT_SEEKABLE = 801;

    /**
     * A new set of metadata is available.
     *
     * @see android.media.MediaPlayer.OnInfoListener
     */
    public static final int MEDIA_INFO_METADATA_UPDATE = 802;

    /**
     * Interface definition of a callback to be invoked to communicate some
     * info and/or warning about the media or its playback.
     */

    /**
     * Register a callback to be invoked when an info/warning is available.
     *
     * @param listener the callback that will be run
     */
    public void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    }

    private OnInfoListener mOnInfoListener;

    private int attachAuxEffectCompat(int effectId) {
        int ret = -3;

        return ret;
    }
}
