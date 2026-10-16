package lib.kalu.mediaplayer.core.kernel.video.mpv;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mpvplayer.MpvPlayer;
import lib.kalu.mpvplayer.inter.OnBufferingUpdateListener;
import lib.kalu.mpvplayer.inter.OnCompletionListener;
import lib.kalu.mpvplayer.inter.OnErrorListener;
import lib.kalu.mpvplayer.inter.OnInfoListener;
import lib.kalu.mpvplayer.inter.OnPreparedListener;
import lib.kalu.mpvplayer.inter.OnSeekCompleteListener;
import lib.kalu.mpvplayer.inter.OnVideoSizeChangedListener;


public final class VideoMpvPlayer extends VideoBasePlayer {

    private final String TAG = "VideoMpvPlayer22";
    private boolean isVideoSizeChanged = false;
    private boolean isPrepared = false;
    private boolean isBuffering = false;
    private boolean mPlayWhenReadySeeking = false;
    private MpvPlayer mMpvPlayer = null;

    @Override
    public VideoMpvPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder() {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "releaseDecoder -> mMpvPlayer error: null");
                }
                return;
            }
            setEvent(null);
            unRegistListener();
            release();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "releaseDecoder -> " + e.getMessage());
            }
        }
    }

    @Override
    public void checkDecoder(Context context, StartArgs args) {
        try {
            if (null != mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "checkDecoder -> warning: null != mMpvPlayer");
                }
                return;
            }
            mMpvPlayer = new MpvPlayer();
            registListener();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "checkDecoder -> " + e.getMessage());
            }
        }
    }


    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> error: mMpvPlayer null");
                }
                return;
            }
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> error: args null");
                }
                return;
            }
            boolean containsVideoUrl = args.containsVideoUrl();
            if (!containsVideoUrl) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "startDecoder -> error: containsVideoUrl false");
                }
                return;
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.READY);
            mMpvPlayer.setDataSource(context, Uri.parse(args.getUrl()), null);
            mMpvPlayer.prepare();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "startDecoder -> " + e.getMessage());
            }
            stop();
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR_DECODE);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "initOptions -> error: mMpvPlayer null");
                }
                return;
            }
            boolean mute = args.isMute();
            if (mute) {
                mMpvPlayer.setVolume(0f, 0f);
            } else {
                mMpvPlayer.setVolume(1f, 1f);
            }
            boolean looping = args.isLooping();
            mMpvPlayer.setLooping(looping);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "initOptions -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "registListener -> error: mMpvPlayer null");
                }
                return;
            }
//            mMpvPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMpvPlayer.setOnErrorListener(onErrorListener);
            mMpvPlayer.setOnCompletionListener(onCompletionListener);
            mMpvPlayer.setOnInfoListener(onInfoListener);
            mMpvPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mMpvPlayer.setOnPreparedListener(onPreparedListener);
            mMpvPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mMpvPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "registListener -> Exception " + e.getMessage());
            }
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "unRegistListener -> mMpvPlayer error: null");
                }
                return;
            }
            mMpvPlayer.setOnErrorListener(null);
            mMpvPlayer.setOnCompletionListener(null);
            mMpvPlayer.setOnInfoListener(null);
            mMpvPlayer.setOnBufferingUpdateListener(null);
            mMpvPlayer.setOnPreparedListener(null);
            mMpvPlayer.setOnVideoSizeChangedListener(null);
            mMpvPlayer.setOnSeekCompleteListener(null);
            mMpvPlayer.setOnBufferingUpdateListener(null);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "unRegistListener -> Exception " + e.getMessage());
            }
        }
    }

    //    /**
//     * 用于播放raw和asset里面的视频文件
//     */
//    @Override
//    public void setDataSource(AssetFileDescriptor fd) {
//        try {
//            mMpvPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//        } catch (Exception e) {
//            MPLogUtil.log(TAG, "" + e.getMessage());
//        }
//    }

    @Override
    public void release() {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "release -> mMpvPlayer error: null");
                }
                return;
            }
            mMpvPlayer.setSurface(null);
            mMpvPlayer.release();
            mMpvPlayer = null;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isBuffering() {
        return isBuffering;
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "start -> mMpvPlayer error: null");
                }
                return;
            }
            mMpvPlayer.start();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "start -> " + e.getMessage());
            }
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "pause -> mPrepared warning: false");
                }
                return;
            }
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "pause -> mMpvPlayer error: null");
                }
                return;
            }
            mMpvPlayer.pause();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "pause -> " + e.getMessage());
            }
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "stop -> mMpvPlayer error: null");
                }
                return;
            }
            mMpvPlayer.stop();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "stop -> " + e.getMessage());
            }
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isPlaying -> mPrepared warning: false");
                }
                return false;
            }
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "isPlaying -> mMpvPlayer error: null");
                }
                return false;
            }
            return mMpvPlayer.isPlaying();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "isPlaying -> " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public void seekToDefaultPosition() {
        seekTo(0);
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        try {
            if (seek < 0L) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> error: seek<0");
                }
                return;
            }
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> error: mMpvPlayer null");
                }
                return;
            }
            StartArgs args = getStartArgs();
            if (null == args) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "seekTo -> error: args null");
                }
                return;
            }

            long duration = getDuration();
            if (duration > 0 && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            if (seek < position) {
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_REWIND);
            } else {
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_START_FORWARD);
            }
            mMpvPlayer.seekTo((int) seek);
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekTo ->");
            }
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "seekTo -> " + e.getMessage());
            }
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getPosition -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getPosition -> mMpvPlayer error: null");
                }
                return 0L;
            }
            long currentPosition = mMpvPlayer.getCurrentPosition();
            if (currentPosition < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getPosition -> currentPosition warning: " + currentPosition);
                }
                return 0L;
            }
            return currentPosition;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getPosition -> " + e.getMessage());
            }
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (!isPrepared) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getDuration -> mPrepared warning: false");
                }
                return 0L;
            }
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getDuration -> mMpvPlayer error: null");
                }
                return 0L;
            }
            int duration = mMpvPlayer.getDuration();
            if (duration <= 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getDuration -> duration warning: " + duration);
                }
                return 0L;
            }
            return duration;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getDuration -> " + e.getMessage());
            }
            return 0L;
        }
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setSurface -> mMpvPlayer error: null");
                }
                return;
            }
            if (null == surface) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setSurface -> surface error: null");
                }
                return;
            }
            mMpvPlayer.setSurface(surface);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setSurface -> " + e.getMessage());
            }
        }
    }

    @Override
    public void setSpeed(float speed) {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setSpeed -> mMpvPlayer error: null");
                }
                return;
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setSpeed -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getSpeed() {
        return 1.0f;
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(MpvPlayer mp, int what, int extra) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onInfo -> what = " + what);
            }
            // 缓冲开始
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                if (isPrepared) {
                    isBuffering = true;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_BUFFERING_START);
                } else {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onInfo -> what = " + what + ", mPrepared = false");
                    }
                }
            }
            // 缓冲结束
            else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                if (isPrepared) {
                    isBuffering = false;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_BUFFERING_STOP);
                } else {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onInfo -> what = " + what + ", mPrepared = false");
                    }
                }
            }
            // 开始播放
            else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                try {
                    if (isPrepared) {
                        if (LogUtil.DEBUG) {
                            LogUtil.log(TAG, "onInfo -> MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> warning: isPrepared true");
                        }
                        return true;
                    }
                    isPrepared = true;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_PLAYBACLK_SPEED);
                    long seek = getPlayWhenReadySeekToPosition();
                    if (seek <= 0) {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                        // 立即播放
                        boolean playWhenReady = isPlayWhenReady();
                        if (!playWhenReady) {
                            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                            pause();
                        }
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_SEEK);
                        // 起播快进
                        mPlayWhenReadySeeking = true;
                        seekTo(seek);
                    }
                } catch (Exception e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onInfo -> what = " + what + ", msg = " + e.getMessage());
                    }
                }
            }
            return true;
        }
    };

    private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MpvPlayer mediaPlayer) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onSeekComplete ->");
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_UPDATE_SEEK_FINISH);

            try {
                // 起播快进
                if (mPlayWhenReadySeeking) {
                    mPlayWhenReadySeeking = false;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                    boolean playWhenReady = isPlayWhenReady();
                    if (playWhenReady) {
                        boolean playing = isPlaying();
                        if (playing) {
                            if (LogUtil.DEBUG) {
                                LogUtil.log(TAG, "onSeekComplete -> warning: isPlaying true");
                            }
                            return;
                        }
                        start();
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PLAY_WHEN_READY_PAUSE);
                        pause();
                    }
                }
                // 正常快进&快退
                else {

                }
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onSeekComplete -> Exception " + e.getMessage());
                }
            }
        }
    };

    private OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MpvPlayer mp) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onPrepared ->");
            }
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.MEDIA_INFO_PREPARE);
            start();
        }
    };

    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MpvPlayer mp, int percent) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onBufferingUpdate -> percent = " + percent);
            }
        }
    };

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MpvPlayer mp, int what, int extra) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onError -> what = " + what);
            }
            // ignore -38
            if (what == -38) {

            }
            // error
            else {
                stop();
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR_PLAY);
            }
            return true;
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MpvPlayer mp) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "onCompletion ->");
            }
            stop();
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.END);
        }
    };

    private OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MpvPlayer mp, int width, int height) {
            try {
                if (null == mp) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onVideoSizeChanged -> error: MediaPlayer null");
                    }
                    return;
                }
                int videoWidth = mp.getVideoWidth();
                if (videoWidth <= 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onVideoSizeChanged -> error: videoWidth <= 0");
                    }
                    return;
                }
                int videoHeight = mp.getVideoHeight();
                if (videoHeight <= 0) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onVideoSizeChanged -> error: videoHeight <= 0");
                    }
                    return;
                }
                if (isVideoSizeChanged) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onVideoSizeChanged -> warning: isVideoSizeChanged true");
                    }
                    return;
                }
                StartArgs args = getStartArgs();
                if (null == args) {
                    if (LogUtil.DEBUG) {
                        LogUtil.log(TAG, "onVideoSizeChanged -> error: args null");
                    }
                    return;
                }
                isVideoSizeChanged = true;
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
                onVideoFormatChanged(PlayerType.KernelType.FFPLAYER, rotation, scaleType, videoWidth, videoHeight, -1);
            } catch (Exception e) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "onVideoSizeChanged -> " + e.getMessage());
                }
            }
        }
    };

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setVolume -> mMpvPlayer error: null");
                }
                return;
            }
            float volume = Math.max(v1, v2);
            if (volume < 0) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "setVolume -> error: volume < 0");
                }
                return;
            }
            mMpvPlayer.setVolume(volume, volume);
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "setVolume -> " + e.getMessage());
            }
        }
    }

    @Override
    public float getVolume() {
        try {
            if (null == mMpvPlayer) {
                if (LogUtil.DEBUG) {
                    LogUtil.log(TAG, "getVolume -> mMpvPlayer error: null");
                }
                return 0f;
            }
            return 0f;
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.log(TAG, "getVolume -> " + e.getMessage());
            }
            return 0f;
        }
    }
}
