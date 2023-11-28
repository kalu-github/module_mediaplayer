/*
 * Copyright (C) 2006 Bilibili
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.player;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import lib.kalu.ijkplayer.util.IjkLogUtil;
import tv.danmaku.ijk.media.player.misc.IAndroidIO;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

/**
 * @author bbcallen
 * <p>
 * Java wrapper of ffplay.
 */
public final class IjkMediaPlayer extends AbstractMediaPlayer {

    private static final int MEDIA_NOP = 0; // interface test message
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_TIMED_TEXT = 99;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;

    protected static final int MEDIA_SET_VIDEO_SAR = 10001;

    //----------------------------------------
    // options
    public static final int IJK_LOG_UNKNOWN = 0;
    public static final int IJK_LOG_DEFAULT = 1;

    public static final int IJK_LOG_VERBOSE = 2;
    public static final int IJK_LOG_DEBUG = 3;
    public static final int IJK_LOG_INFO = 4;
    public static final int IJK_LOG_WARN = 5;
    public static final int IJK_LOG_ERROR = 6;
    public static final int IJK_LOG_FATAL = 7;
    public static final int IJK_LOG_SILENT = 8;

    public static final int OPT_CATEGORY_FORMAT = 1;
    public static final int OPT_CATEGORY_CODEC = 2;
    public static final int OPT_CATEGORY_SWS = 3;
    public static final int OPT_CATEGORY_PLAYER = 4;

    public static final int SDL_FCC_YV12 = 0x32315659; // YV12
    public static final int SDL_FCC_RV16 = 0x36315652; // RGB565
    public static final int SDL_FCC_RV32 = 0x32335652; // RGBX8888
    //----------------------------------------

    //----------------------------------------
    // properties
    public static final int PROP_FLOAT_VIDEO_DECODE_FRAMES_PER_SECOND = 10001;
    public static final int PROP_FLOAT_VIDEO_OUTPUT_FRAMES_PER_SECOND = 10002;
    public static final int FFP_PROP_FLOAT_PLAYBACK_RATE = 10003;
    public static final int FFP_PROP_FLOAT_PITCH_RATE = 11000;
    public static final int FFP_PROP_FLOAT_DROP_FRAME_RATE = 10007;

    public static final int FFP_PROP_INT64_SELECTED_VIDEO_STREAM = 20001;
    public static final int FFP_PROP_INT64_SELECTED_AUDIO_STREAM = 20002;
    public static final int FFP_PROP_INT64_SELECTED_TIMEDTEXT_STREAM = 20011;

    public static final int FFP_PROP_INT64_VIDEO_DECODER = 20003;
    public static final int FFP_PROP_INT64_AUDIO_DECODER = 20004;
    public static final int FFP_PROPV_DECODER_UNKNOWN = 0;
    public static final int FFP_PROPV_DECODER_AVCODEC = 1;
    public static final int FFP_PROPV_DECODER_MEDIACODEC = 2;
    public static final int FFP_PROPV_DECODER_VIDEOTOOLBOX = 3;
    public static final int FFP_PROP_INT64_VIDEO_CACHED_DURATION = 20005;
    public static final int FFP_PROP_INT64_AUDIO_CACHED_DURATION = 20006;
    public static final int FFP_PROP_INT64_VIDEO_CACHED_BYTES = 20007;
    public static final int FFP_PROP_INT64_AUDIO_CACHED_BYTES = 20008;
    public static final int FFP_PROP_INT64_VIDEO_CACHED_PACKETS = 20009;
    public static final int FFP_PROP_INT64_AUDIO_CACHED_PACKETS = 20010;
    public static final int FFP_PROP_INT64_ASYNC_STATISTIC_BUF_BACKWARDS = 20201;
    public static final int FFP_PROP_INT64_ASYNC_STATISTIC_BUF_FORWARDS = 20202;
    public static final int FFP_PROP_INT64_ASYNC_STATISTIC_BUF_CAPACITY = 20203;
    public static final int FFP_PROP_INT64_TRAFFIC_STATISTIC_BYTE_COUNT = 20204;
    public static final int FFP_PROP_INT64_CACHE_STATISTIC_PHYSICAL_POS = 20205;
    public static final int FFP_PROP_INT64_CACHE_STATISTIC_FILE_FORWARDS = 20206;
    public static final int FFP_PROP_INT64_CACHE_STATISTIC_FILE_POS = 20207;
    public static final int FFP_PROP_INT64_CACHE_STATISTIC_COUNT_BYTES = 20208;
    public static final int FFP_PROP_INT64_LOGICAL_FILE_SIZE = 20209;
    public static final int FFP_PROP_INT64_SHARE_CACHE_DATA = 20210;
    public static final int FFP_PROP_INT64_BIT_RATE = 20100;
    public static final int FFP_PROP_INT64_TCP_SPEED = 20200;
    public static final int FFP_PROP_INT64_LATEST_SEEK_LOAD_DURATION = 20300;
    public static final int FFP_PROP_INT64_IMMEDIATE_RECONNECT = 20211;
    public static final int FFP_PROP_INT64_CHANNEL_CONFIG = 21000;

    //----------------------------------------


    private long mNativeMediaPlayer;

    private long mNativeMediaDataSource;


    private long mNativeAndroidIO;


    private int mNativeSurfaceTexture;


    private int mListenerContext;

    private SurfaceHolder mSurfaceHolder[] = new SurfaceHolder[2];
    private EventHandler mEventHandler;
    private PowerManager.WakeLock mWakeLock = null;
    private boolean mScreenOnWhilePlaying;
    private boolean mStayAwake;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;
    private int mSurfaceIndex = 0;
    private String mDataSource;

    public IjkMediaPlayer() {
        System.loadLibrary("ijkplayer");
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }

        /*
         * Native setup requires a weak reference to our object. It's easier to
         * create it here than in C++.
         */
        native_setup(new WeakReference<IjkMediaPlayer>(this));
    }

    private native void _setFrameAtTime(String imgCachePath, long startTime, long endTime, int num, int imgDefinition)
            throws IllegalArgumentException, IllegalStateException;

    /*
     * Update the IjkMediaPlayer SurfaceTexture. Call after setting a new
     * display surface.
     */
    private native void _setVideoSurface(Surface surface, int index);


    public void setSurfaceIndex(int idx) {
        mSurfaceIndex = idx;
    }

    /**
     * Sets the {@link SurfaceHolder} to use for displaying the video portion of
     * the media.
     * <p>
     * Either a surface holder or surface must be set if a display or video sink
     * is needed. Not calling this method or {@link #setSurface(Surface)} when
     * playing back a video will result in only the audio track being played. A
     * null surface holder or surface will result in only the audio track being
     * played.
     *
     * @param sh the SurfaceHolder to use for video display
     */
    @Override
    public void setDisplay(SurfaceHolder sh) {
        mSurfaceHolder[mSurfaceIndex] = sh;
        Surface surface;
        if (sh != null) {
            surface = sh.getSurface();
        } else {
            surface = null;
        }
        _setVideoSurface(surface, mSurfaceIndex);
        updateSurfaceScreenOn();
    }


    /**
     * Sets the {@link Surface} to be used as the sink for the video portion of
     * the media. This is similar to {@link #setDisplay(SurfaceHolder)}, but
     * does not support {@link #setScreenOnWhilePlaying(boolean)}. Setting a
     * Surface will un-set any Surface or SurfaceHolder that was previously set.
     * A null surface will result in only the audio track being played.
     * <p>
     * If the Surface sends frames to a {@link SurfaceTexture}, the timestamps
     * returned from {@link SurfaceTexture#getTimestamp()} will have an
     * unspecified zero point. These timestamps cannot be directly compared
     * between different media sources, different instances of the same media
     * source, or multiple runs of the same program. The timestamp is normally
     * monotonically increasing and is unaffected by time-of-day adjustments,
     * but it is reset when the position is set.
     *
     * @param surface The {@link Surface} to be used for the video portion of the
     *                media.
     */
    @Override
    public void setSurface(Surface surface) {
        if (mScreenOnWhilePlaying && surface != null) {
            IjkLogUtil.log("setSurface => setScreenOnWhilePlaying(true) is ineffective for Surface");
        }
        mSurfaceHolder[mSurfaceIndex] = null;
        _setVideoSurface(surface, mSurfaceIndex);
        updateSurfaceScreenOn();
    }

    public void setAndroidIOCallback(IAndroidIO androidIO)
            throws IllegalArgumentException, SecurityException, IllegalStateException {
        _setAndroidIOCallback(androidIO);
    }

    private native void _setDataSource(String path, String[] keys, String[] values)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    private native void _setDataSourceFd(int fd)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    private native void _setDataSource(IMediaDataSource mediaDataSource)
            throws IllegalArgumentException, SecurityException, IllegalStateException;

    private native void _setAndroidIOCallback(IAndroidIO androidIO)
            throws IllegalArgumentException, SecurityException, IllegalStateException;

    @Override
    public String getDataSource() {
        return mDataSource;
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        _prepareAsync();
    }

    public native void _prepareAsync() throws IllegalStateException;

    @Override
    public void start() throws IllegalStateException {
        stayAwake(true);
        _start();
    }

    private native void _start() throws IllegalStateException;

    @Override
    public void stop() throws IllegalStateException {
        stayAwake(false);
        _stop();
    }

    private native void _stop() throws IllegalStateException;

    @Override
    public void pause() throws IllegalStateException {
        stayAwake(false);
        _pause();
    }

    private native void _pause() throws IllegalStateException;

    @SuppressLint("Wakelock")
    @Override
    public void setWakeMode(Context context, int mode) {
        boolean washeld = false;
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                washeld = true;
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(mode | PowerManager.ON_AFTER_RELEASE,
                IjkMediaPlayer.class.getName());
        mWakeLock.setReferenceCounted(false);
        if (washeld) {
            mWakeLock.acquire();
        }
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (mScreenOnWhilePlaying != screenOn) {
            if (screenOn && mSurfaceHolder[0] == null && mSurfaceHolder[1] == null) {
                IjkLogUtil.log("setScreenOnWhilePlaying => setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            mScreenOnWhilePlaying = screenOn;
            updateSurfaceScreenOn();
        }
    }

    @SuppressLint("Wakelock")
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
        if (mSurfaceHolder[0] != null) {
            mSurfaceHolder[0].setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
        }
        if (mSurfaceHolder[1] != null) {
            mSurfaceHolder[1].setKeepScreenOn(mScreenOnWhilePlaying && mStayAwake);
        }
    }

    @Override
    public IjkTrackInfo[] getTrackInfo() {
        Bundle bundle = getMediaMeta();
        if (bundle == null)
            return null;

        IjkMediaMeta mediaMeta = IjkMediaMeta.parse(bundle);
        if (mediaMeta == null || mediaMeta.mStreams == null)
            return null;

        ArrayList<IjkTrackInfo> trackInfos = new ArrayList<IjkTrackInfo>();
        for (IjkMediaMeta.IjkStreamMeta streamMeta : mediaMeta.mStreams) {
            IjkTrackInfo trackInfo = new IjkTrackInfo(streamMeta);
            if (streamMeta.mType.equalsIgnoreCase(IjkMediaMeta.IJKM_VAL_TYPE__VIDEO)) {
                trackInfo.setTrackType(ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
            } else if (streamMeta.mType.equalsIgnoreCase(IjkMediaMeta.IJKM_VAL_TYPE__AUDIO)) {
                trackInfo.setTrackType(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
            } else if (streamMeta.mType.equalsIgnoreCase(IjkMediaMeta.IJKM_VAL_TYPE__TIMEDTEXT)) {
                trackInfo.setTrackType(ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
            }
            trackInfos.add(trackInfo);
        }

        return trackInfos.toArray(new IjkTrackInfo[trackInfos.size()]);
    }

    // TODO: @Override
    public int getSelectedTrack(int trackType) {
        switch (trackType) {
            case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                return (int) _getPropertyLong(FFP_PROP_INT64_SELECTED_VIDEO_STREAM, -1);
            case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                return (int) _getPropertyLong(FFP_PROP_INT64_SELECTED_AUDIO_STREAM, -1);
            case ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                return (int) _getPropertyLong(FFP_PROP_INT64_SELECTED_TIMEDTEXT_STREAM, -1);
            default:
                return -1;
        }
    }

    // experimental, should set DEFAULT_MIN_FRAMES and MAX_MIN_FRAMES to 25
    // TODO: @Override
    public void selectTrack(int track) {
        _setStreamSelected(track, true);
    }

    // experimental, should set DEFAULT_MIN_FRAMES and MAX_MIN_FRAMES to 25
    // TODO: @Override
    public void deselectTrack(int track) {
        _setStreamSelected(track, false);
    }

    private native void _setStreamSelected(int stream, boolean select);

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    @Override
    public int getVideoSarNum() {
        return mVideoSarNum;
    }

    @Override
    public int getVideoSarDen() {
        return mVideoSarDen;
    }

    @Override
    public native boolean isPlaying();

    @Override
    public native void seekTo(long msec) throws IllegalStateException;

    @Override
    public native long getCurrentPosition();

    @Override
    public native long getDuration();

    @Override
    public void release() {
        stayAwake(false);
        updateSurfaceScreenOn();
        resetListeners();
        _release();
    }

    private native void _release();

    @Override
    public void reset() {
        stayAwake(false);
        _reset();
        // make sure none of the listeners get called anymore
        mEventHandler.removeCallbacksAndMessages(null);

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    private native void _reset();

    /**
     * Sets the player to be looping or non-looping.
     *
     * @param looping whether to loop or not
     */
    @Override
    public void setLooping(boolean looping) {
        int loopCount = looping ? 0 : 1;
        setOption(OPT_CATEGORY_PLAYER, "loop", loopCount);
        _setLoopCount(loopCount);
    }

    private native void _setLoopCount(int loopCount);

    @Override
    public boolean isLooping() {
        return _getLoopCount() != 1;
    }

    private native int _getLoopCount();

    public void setSpeed(float speed) {
        _setPropertyFloat(FFP_PROP_FLOAT_PLAYBACK_RATE, speed);
    }

    public float getSpeed() {
        return _getPropertyFloat(FFP_PROP_FLOAT_PLAYBACK_RATE, -1F);
    }

    public void setPitch(float pitch) {
        _setPropertyFloat(FFP_PROP_FLOAT_PITCH_RATE, pitch);
    }

    public float getPitch(float pitch) {
        return _getPropertyFloat(FFP_PROP_FLOAT_PITCH_RATE, .0f);
    }


    public int getVideoDecoder() {
        return (int) _getPropertyLong(FFP_PROP_INT64_VIDEO_DECODER, FFP_PROPV_DECODER_UNKNOWN);
    }

    public float getVideoOutputFramesPerSecond() {
        return _getPropertyFloat(PROP_FLOAT_VIDEO_OUTPUT_FRAMES_PER_SECOND, 0.0f);
    }

    public float getVideoDecodeFramesPerSecond() {
        return _getPropertyFloat(PROP_FLOAT_VIDEO_DECODE_FRAMES_PER_SECOND, 0.0f);
    }

    public void setChannelConfig(int config) {
        _setPropertyLong(FFP_PROP_INT64_CHANNEL_CONFIG, config);
    }

    public float getChannelConfig() {
        return _getPropertyLong(FFP_PROP_INT64_CHANNEL_CONFIG, 0);
    }

    public long getVideoCachedDuration() {
        return _getPropertyLong(FFP_PROP_INT64_VIDEO_CACHED_DURATION, 0);
    }

    public long getAudioCachedDuration() {
        return _getPropertyLong(FFP_PROP_INT64_AUDIO_CACHED_DURATION, 0);
    }

    public long getVideoCachedBytes() {
        return _getPropertyLong(FFP_PROP_INT64_VIDEO_CACHED_BYTES, 0);
    }

    public long getAudioCachedBytes() {
        return _getPropertyLong(FFP_PROP_INT64_AUDIO_CACHED_BYTES, 0);
    }

    public long getVideoCachedPackets() {
        return _getPropertyLong(FFP_PROP_INT64_VIDEO_CACHED_PACKETS, 0);
    }

    public long getAudioCachedPackets() {
        return _getPropertyLong(FFP_PROP_INT64_AUDIO_CACHED_PACKETS, 0);
    }

    public long getAsyncStatisticBufBackwards() {
        return _getPropertyLong(FFP_PROP_INT64_ASYNC_STATISTIC_BUF_BACKWARDS, 0);
    }

    public long getAsyncStatisticBufForwards() {
        return _getPropertyLong(FFP_PROP_INT64_ASYNC_STATISTIC_BUF_FORWARDS, 0);
    }

    public long getAsyncStatisticBufCapacity() {
        return _getPropertyLong(FFP_PROP_INT64_ASYNC_STATISTIC_BUF_CAPACITY, 0);
    }

    public long getTrafficStatisticByteCount() {
        return _getPropertyLong(FFP_PROP_INT64_TRAFFIC_STATISTIC_BYTE_COUNT, 0);
    }

    public long getCacheStatisticPhysicalPos() {
        return _getPropertyLong(FFP_PROP_INT64_CACHE_STATISTIC_PHYSICAL_POS, 0);
    }

    public long getCacheStatisticFileForwards() {
        return _getPropertyLong(FFP_PROP_INT64_CACHE_STATISTIC_FILE_FORWARDS, 0);
    }

    public long getCacheStatisticFilePos() {
        return _getPropertyLong(FFP_PROP_INT64_CACHE_STATISTIC_FILE_POS, 0);
    }

    public long getCacheStatisticCountBytes() {
        return _getPropertyLong(FFP_PROP_INT64_CACHE_STATISTIC_COUNT_BYTES, 0);
    }

    public long getFileSize() {
        return _getPropertyLong(FFP_PROP_INT64_LOGICAL_FILE_SIZE, 0);
    }

    public long getBitRate() {
        return _getPropertyLong(FFP_PROP_INT64_BIT_RATE, 0);
    }

    public long getTcpSpeed() {
        return _getPropertyLong(FFP_PROP_INT64_TCP_SPEED, 0);
    }

    public long getSeekLoadDuration() {
        return _getPropertyLong(FFP_PROP_INT64_LATEST_SEEK_LOAD_DURATION, 0);
    }

    private native float _getPropertyFloat(int property, float defaultValue);

    private native void _setPropertyFloat(int property, float value);

    private native long _getPropertyLong(int property, long defaultValue);

    private native void _setPropertyLong(int property, long value);

    public float getDropFrameRate() {
        return _getPropertyFloat(FFP_PROP_FLOAT_DROP_FRAME_RATE, .0f);
    }

    @Override
    public native void setVolume(float leftVolume, float rightVolume);

    @Override
    public native int getAudioSessionId();

    @Override
    public MediaInfo getMediaInfo() {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.mMediaPlayerName = "ijkplayer";

        String videoCodecInfo = _getVideoCodecInfo();
        if (!TextUtils.isEmpty(videoCodecInfo)) {
            String nodes[] = videoCodecInfo.split(",");
            if (nodes.length >= 2) {
                mediaInfo.mVideoDecoder = nodes[0];
                mediaInfo.mVideoDecoderImpl = nodes[1];
            } else if (nodes.length >= 1) {
                mediaInfo.mVideoDecoder = nodes[0];
                mediaInfo.mVideoDecoderImpl = "";
            }
        }

        String audioCodecInfo = _getAudioCodecInfo();
        if (!TextUtils.isEmpty(audioCodecInfo)) {
            String nodes[] = audioCodecInfo.split(",");
            if (nodes.length >= 2) {
                mediaInfo.mAudioDecoder = nodes[0];
                mediaInfo.mAudioDecoderImpl = nodes[1];
            } else if (nodes.length >= 1) {
                mediaInfo.mAudioDecoder = nodes[0];
                mediaInfo.mAudioDecoderImpl = "";
            }
        }

        try {
            mediaInfo.mMeta = IjkMediaMeta.parse(_getMediaMeta());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return mediaInfo;
    }

    private native String _getVideoCodecInfo();

    private native String _getAudioCodecInfo();

    public void setOption(int category, String name, String value) {
        _setOption(category, name, value);
    }

    public void setOption(int category, String name, long value) {
        _setOption(category, name, value);
    }

    private native void _setOption(int category, String name, String value);

    private native void _setOption(int category, String name, long value);

    public Bundle getMediaMeta() {
        return _getMediaMeta();
    }

    private native Bundle _getMediaMeta();

    public static String getColorFormatName(int mediaCodecColorFormat) {
        return _getColorFormatName(mediaCodecColorFormat);
    }

    private static native String _getColorFormatName(int mediaCodecColorFormat);

    @Override
    public void setAudioStreamType(int streamtype) {
        // do nothing
    }

    @Override
    public void setKeepInBackground(boolean keepInBackground) {
        // do nothing
    }

    private static native void native_init();

    private native void native_setup(Object IjkMediaPlayer_this);

    private native void native_finalize();

    private native void native_message_loop(Object IjkMediaPlayer_this);

    protected void finalize() throws Throwable {
        super.finalize();
        native_finalize();
    }

    public void httphookReconnect() {
        _setPropertyLong(FFP_PROP_INT64_IMMEDIATE_RECONNECT, 1);
    }

    public void setCacheShare(int share) {
        _setPropertyLong(FFP_PROP_INT64_SHARE_CACHE_DATA, (long) share);
    }

    private static class EventHandler extends Handler {
        private final WeakReference<IjkMediaPlayer> mWeakPlayer;

        public EventHandler(IjkMediaPlayer mp, Looper looper) {
            super(looper);
            mWeakPlayer = new WeakReference<IjkMediaPlayer>(mp);
        }

        @Override
        public void handleMessage(Message msg) {

            try {
                if (null == mWeakPlayer)
                    throw new Exception("mWeakPlayer error: null");
                IjkMediaPlayer player = mWeakPlayer.get();
                if (null == player)
                    throw new Exception("mWeakPlayer.get() -> player error: null");
                if (player.mNativeMediaPlayer == 0)
                    throw new Exception("mWeakPlayer.get() -> player.mNativeMediaPlayer error: null");
                IjkLogUtil.log("IjkMediaPlayer => handleMessage => " + String.format(Locale.US, "msg.what = %d, msg.arg1 = %d, msg.arg2 = %d", msg.what, msg.arg1, msg.arg2));
                switch (msg.what) {
                    case MEDIA_PREPARED:
                        player.notifyOnPrepared();
                        return;
                    case MEDIA_PLAYBACK_COMPLETE:
                        player.stayAwake(false);
                        player.notifyOnCompletion();
                        return;
                    case MEDIA_BUFFERING_UPDATE:
                        long bufferPosition = msg.arg1;
                        if (bufferPosition < 0) {
                            bufferPosition = 0;
                        }
                        long percent = 0;
                        long duration = player.getDuration();
                        if (duration > 0) {
                            percent = bufferPosition * 100 / duration;
                        }
                        if (percent >= 100) {
                            percent = 100;
                        }
                        // DebugLog.efmt(TAG, "Buffer (%d%%) %d/%d",  percent, bufferPosition, duration);
                        player.notifyOnBufferingUpdate((int) percent);
                        return;
                    case MEDIA_SEEK_COMPLETE:
                        player.notifyOnSeekComplete();
                        return;
                    case MEDIA_SET_VIDEO_SIZE:
                        player.mVideoWidth = msg.arg1;
                        player.mVideoHeight = msg.arg2;
                        player.notifyOnVideoSizeChanged(player.mVideoWidth, player.mVideoHeight, player.mVideoSarNum, player.mVideoSarDen);
                        return;
                    case MEDIA_ERROR:
                        if (!player.notifyOnError(msg.arg1, msg.arg2)) {
                            player.notifyOnCompletion();
                        }
                        player.stayAwake(false);
                        return;
                    case MEDIA_INFO:
                        player.notifyOnInfo(msg.arg1, msg.arg2);
                        // No real default action so far.
                        return;
                    case MEDIA_TIMED_TEXT:
                        if (msg.obj == null) {
                            player.notifyOnTimedText(null);
                        } else {
                            IjkTimedText text = new IjkTimedText(new Rect(0, 0, 1, 1), (String) msg.obj);
                            player.notifyOnTimedText(text);
                        }
                        return;
                    case MEDIA_NOP: // interface test message - ignore
                        break;
                    case MEDIA_SET_VIDEO_SAR:
                        player.mVideoSarNum = msg.arg1;
                        player.mVideoSarDen = msg.arg2;
                        player.notifyOnVideoSizeChanged(player.mVideoWidth, player.mVideoHeight, player.mVideoSarNum, player.mVideoSarDen);
                        break;
                    default:
                        throw new Exception("what error: Unknown message type " + msg.what);
                }
            } catch (Exception e) {
                IjkLogUtil.log("IjkMediaPlayer => handleMessage => " + e.getMessage());
            }
        }
    }

    /*
     * Called from native code when an interesting event happens. This method
     * just uses the EventHandler system to post the event back to the main app
     * thread. We use a weak reference to the original IjkMediaPlayer object so
     * that the native code is safe from the object disappearing from underneath
     * it. (This is the cookie passed to native_setup().)
     */
    private static void postEventFromNative(Object weakThiz, int what, int arg1, int arg2, Object obj) {
        if (weakThiz == null)
            return;

        @SuppressWarnings("rawtypes")
        IjkMediaPlayer mp = (IjkMediaPlayer) ((WeakReference) weakThiz).get();
        if (mp == null) {
            return;
        }

        if (what == MEDIA_INFO && arg1 == MEDIA_INFO_STARTED_AS_NEXT) {
            // this acquires the wakelock if needed, and sets the client side
            // state
            mp.start();
        }
        if (mp.mEventHandler != null) {
            Message m = mp.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            mp.mEventHandler.sendMessage(m);
        }
    }

    /*
     * ControlMessage
     */

    private OnControlMessageListener mOnControlMessageListener;

    public void setOnControlMessageListener(OnControlMessageListener listener) {
        mOnControlMessageListener = listener;
    }

    public interface OnControlMessageListener {
        String onControlResolveSegmentUrl(int segment);
    }

    /*
     * NativeInvoke
     */

    private OnNativeInvokeListener mOnNativeInvokeListener;

    public void setOnNativeInvokeListener(OnNativeInvokeListener listener) {
        mOnNativeInvokeListener = listener;
    }

    public interface OnNativeInvokeListener {

        int CTRL_WILL_TCP_OPEN = 0x20001;               // NO ARGS
        int CTRL_DID_TCP_OPEN = 0x20002;                // ARG_ERROR, ARG_FAMILIY, ARG_IP, ARG_PORT, ARG_FD

        int CTRL_WILL_HTTP_OPEN = 0x20003;              // ARG_URL, ARG_SEGMENT_INDEX, ARG_RETRY_COUNTER
        int CTRL_WILL_LIVE_OPEN = 0x20005;              // ARG_URL, ARG_RETRY_COUNTER
        int CTRL_WILL_CONCAT_RESOLVE_SEGMENT = 0x20007; // ARG_URL, ARG_SEGMENT_INDEX, ARG_RETRY_COUNTER

        int EVENT_WILL_HTTP_OPEN = 0x1;                 // ARG_URL
        int EVENT_DID_HTTP_OPEN = 0x2;                  // ARG_URL, ARG_ERROR, ARG_HTTP_CODE
        int EVENT_WILL_HTTP_SEEK = 0x3;                 // ARG_URL, ARG_OFFSET
        int EVENT_DID_HTTP_SEEK = 0x4;                  // ARG_URL, ARG_OFFSET, ARG_ERROR, ARG_HTTP_CODE, ARG_FILE_SIZE

        String ARG_URL = "url";
        String ARG_SEGMENT_INDEX = "segment_index";
        String ARG_RETRY_COUNTER = "retry_counter";

        String ARG_ERROR = "error";
        String ARG_FAMILIY = "family";
        String ARG_IP = "ip";
        String ARG_PORT = "port";
        String ARG_FD = "fd";

        String ARG_OFFSET = "offset";
        String ARG_HTTP_CODE = "http_code";
        String ARG_FILE_SIZE = "file_size";

        /*
         * @return true if invoke is handled
         * @throws Exception on any error
         */
        boolean onNativeInvoke(int what, Bundle args);
    }

    private static boolean onNativeInvoke(Object weakThiz, int what, Bundle args) {
        IjkLogUtil.log("onNativeInvoke => what " + what + ", args = " + args);
        if (weakThiz == null || !(weakThiz instanceof WeakReference<?>))
            throw new IllegalStateException("<null weakThiz>.onNativeInvoke()");

        @SuppressWarnings("unchecked")
        WeakReference<IjkMediaPlayer> weakPlayer = (WeakReference<IjkMediaPlayer>) weakThiz;
        IjkMediaPlayer player = weakPlayer.get();
        if (player == null)
            throw new IllegalStateException("<null weakPlayer>.onNativeInvoke()");

        OnNativeInvokeListener listener = player.mOnNativeInvokeListener;
        if (listener != null && listener.onNativeInvoke(what, args))
            return true;

        switch (what) {
            case OnNativeInvokeListener.CTRL_WILL_CONCAT_RESOLVE_SEGMENT: {
                OnControlMessageListener onControlMessageListener = player.mOnControlMessageListener;
                if (onControlMessageListener == null)
                    return false;

                int segmentIndex = args.getInt(OnNativeInvokeListener.ARG_SEGMENT_INDEX, -1);
                if (segmentIndex < 0)
                    throw new InvalidParameterException("onNativeInvoke(invalid segment index)");

                String newUrl = onControlMessageListener.onControlResolveSegmentUrl(segmentIndex);
                if (newUrl == null)
                    throw new RuntimeException(new IOException("onNativeInvoke() = <NULL newUrl>"));

                args.putString(OnNativeInvokeListener.ARG_URL, newUrl);
                return true;
            }
            default:
                return false;
        }
    }

    /*
     * MediaCodec select
     */

    public interface OnMediaCodecSelectListener {
        String onMediaCodecSelect(IMediaPlayer mp, String mimeType, int profile, int level);
    }

    private OnMediaCodecSelectListener mOnMediaCodecSelectListener;

    public void setOnMediaCodecSelectListener(OnMediaCodecSelectListener listener) {
        mOnMediaCodecSelectListener = listener;
    }

    public void resetListeners() {
        super.resetListeners();
        mOnMediaCodecSelectListener = null;
    }

    private static String onSelectCodec(Object weakThiz, String mimeType, int profile, int level) {
        if (weakThiz == null || !(weakThiz instanceof WeakReference<?>))
            return null;

        @SuppressWarnings("unchecked")
        WeakReference<IjkMediaPlayer> weakPlayer = (WeakReference<IjkMediaPlayer>) weakThiz;
        IjkMediaPlayer player = weakPlayer.get();
        if (player == null)
            return null;

        OnMediaCodecSelectListener listener = player.mOnMediaCodecSelectListener;
        if (listener == null)
            listener = DefaultMediaCodecSelector.sInstance;

        return listener.onMediaCodecSelect(player, mimeType, profile, level);
    }

    public static class DefaultMediaCodecSelector implements OnMediaCodecSelectListener {
        public static final DefaultMediaCodecSelector sInstance = new DefaultMediaCodecSelector();

        @SuppressWarnings("deprecation")
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public String onMediaCodecSelect(IMediaPlayer mp, String mimeType, int profile, int level) {
            IjkLogUtil.log("IjkMediaPlayer => onMediaCodecSelect => " + String.format(Locale.US, "mimeType=%s, profile=%d, level=%d", mimeType, profile, level));
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) // 4.1
                    throw new Exception("SDK_INT error: Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN");
                if (null == mimeType || mimeType.length() == 0)
                    throw new Exception("mimeType error: " + mimeType);
                ArrayList<IjkMediaCodecInfo> codecInfos = new ArrayList<IjkMediaCodecInfo>();
                int codecCount = MediaCodecList.getCodecCount();
                for (int i = 0; i < codecCount; i++) {
                    MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
                    if (null == mediaCodecInfo)
                        continue;
                    String mediaCodecName = mediaCodecInfo.getName();
                    boolean mediaCodecEncoder = mediaCodecInfo.isEncoder();
                    IjkLogUtil.log("IjkMediaPlayer => onMediaCodecSelect => "+String.format(Locale.US, "mediaCodecName = %s, mediaCodecEncoder = %b", mediaCodecName, mediaCodecEncoder));
                    if (mediaCodecEncoder)
                        continue;
                    String[] supportedTypes = mediaCodecInfo.getSupportedTypes();
                    if (null == supportedTypes)
                        continue;
                    for (String supportedMimeType : supportedTypes) {
                        if (null == supportedMimeType || supportedMimeType.length() == 0)
                            continue;
                        IjkLogUtil.log("IjkMediaPlayer => onMediaCodecSelect => " + String.format(Locale.US, "mediaCodecName = %s, mediaCodecEncoder = false, supportedMimeType = %s", mediaCodecName, supportedMimeType));
                        if (!supportedMimeType.equalsIgnoreCase(mimeType))
                            continue;
                        IjkMediaCodecInfo ijkMediaCodecInfo = IjkMediaCodecInfo.setupCandidate(mediaCodecInfo, mimeType);
                        if (null == ijkMediaCodecInfo)
                            continue;
                        codecInfos.add(ijkMediaCodecInfo);
                        ijkMediaCodecInfo.dumpProfileLevels(mimeType);
                        IjkLogUtil.log("IjkMediaPlayer => onMediaCodecSelect => " + String.format(Locale.US, "mediaCodecName = %s, mediaCodecEncoder = false, mediaCodecRank = %d", supportedMimeType, ijkMediaCodecInfo.mRank));
                    }
                }
                if (null == codecInfos || codecInfos.size() == 0)
                    throw new Exception("codecInfos error: " + codecInfos);
                IjkMediaCodecInfo bestCodec = codecInfos.get(0);
                for (IjkMediaCodecInfo o : codecInfos) {
                    if (o.mRank > bestCodec.mRank) {
                        bestCodec = o;
                    }
                }
                if (bestCodec.mRank < IjkMediaCodecInfo.RANK_LAST_CHANCE)
                    throw new Exception("mRank error: " + String.format(Locale.US, "unaccetable codec = %s", bestCodec.mCodecInfo.getName()));
                IjkLogUtil.log("IjkMediaPlayer => onMediaCodecSelect => " + String.format(Locale.US, "onChecked => mediaCodecName = %s, mediaCodecRank = %d", bestCodec.mCodecInfo.getName(), bestCodec.mRank));
                return bestCodec.mCodecInfo.getName();
            } catch (Exception e) {
                IjkLogUtil.log("IjkMediaPlayer => onMediaCodecSelect => " + e.getMessage());
                return null;
            }
        }
    }

    public static native void native_profileBegin(String libName);

    public static native void native_profileEnd();

    public static native void native_setLogLevel(int level);

    public static native void native_setLogger(boolean enable);

    /************/

    @Override
    public void setDataSource(Context context, Uri uri) {
        setDataSource(context, uri, null);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) {
        try {
            if (null == uri)
                throw new Exception("uri error: null");
            // file
            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                String path = uri.getPath();
                if (null == path || path.length() <= 0)
                    throw new Exception("path error: " + path);
                setDataSource(path);
            }
            // content
            else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {

                AssetFileDescriptor fd = null;
                try {
                    ContentResolver resolver = context.getContentResolver();
                    fd = resolver.openAssetFileDescriptor(uri, "r");
                    // assest raw
                    if (null != fd) {
                        if (fd.getDeclaredLength() < 0) {
                            setDataSource(fd.getFileDescriptor());
                        } else {
                            setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
                        }
                    }
                    // content
                    else {
                        if (!Settings.AUTHORITY.equals(uri.getAuthority()))
                            throw new Exception("authority error: not " + Settings.AUTHORITY);
                        Uri contentUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.getDefaultType(uri));
                        if (null == contentUri)
                            throw new FileNotFoundException("contentUri error: null");
                        setDataSource(contentUri.toString(), headers);
                    }
                } catch (Exception e) {
                } finally {
                    if (null != fd) {
                        fd.close();
                    }
                }
            }
            // intent
            else {
                String url = uri.toString();
                if (null == url || url.length() == 0)
                    throw new Exception("url error: " + url);
                setDataSource(url, headers);
            }
        } catch (Exception e) {
            IjkLogUtil.log("IjkMediaPlayer => setDataSource => " + e.getMessage());
        }
    }

    @Override
    public void setDataSource(String path) {
        try {
            mDataSource = path;
            _setDataSource(path, null, null);
        } catch (Exception e) {
            IjkLogUtil.log("IjkMediaPlayer => setDataSource => " + e.getMessage());
        }
    }

    public void setDataSource(String path, Map<String, String> headers) {
        try {
            if (headers != null && !headers.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append(":");
                    String value = entry.getValue();
                    if (!TextUtils.isEmpty(value))
                        sb.append(entry.getValue());
                    sb.append("\r\n");
                    setOption(OPT_CATEGORY_FORMAT, "headers", sb.toString());
                    setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "async,cache,crypto,file,http,https,ijkhttphook,ijkinject,ijklivehook,ijklongurl,ijksegment,ijktcphook,pipe,rtp,tcp,tls,udp,ijkurlhook,data");
                }
            }
            setDataSource(path);
        } catch (Exception e) {
            IjkLogUtil.log("IjkMediaPlayer => setDataSource => " + e.getMessage());
        }
    }

    @Override
    public void setDataSource(FileDescriptor fd) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                try {
                    Field f = fd.getClass().getDeclaredField("descriptor"); //NoSuchFieldException
                    f.setAccessible(true);
                    int native_fd = f.getInt(fd); //IllegalAccessException
                    _setDataSourceFd(native_fd);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ParcelFileDescriptor pfd = ParcelFileDescriptor.dup(fd);
                try {
                    _setDataSourceFd(pfd.getFd());
                } finally {
                    pfd.close();
                }
            }
        } catch (Exception e) {
            IjkLogUtil.log("IjkMediaPlayer => setDataSourceFileDescriptor => " + e.getMessage());
        }
    }

    private void setDataSource(FileDescriptor fd, long offset, long length) {
        try {
            setDataSource(fd);
        } catch (Exception e) {
            IjkLogUtil.log("IjkMediaPlayer => setDataSource => " + e.getMessage());
        }
    }

    public void setDataSource(IMediaDataSource mediaDataSource) {
        try {
            _setDataSource(mediaDataSource);
        } catch (Exception e) {
            IjkLogUtil.log("IjkMediaPlayer => setDataSource => " + e.getMessage());
        }
    }
}
