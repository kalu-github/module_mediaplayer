package lib.kalu.mediaplayer.core.kernel.video.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoAndroidPlayer extends VideoBasePlayer {

    private MediaPlayer mMediaPlayer = null;

    @Override
    public VideoAndroidPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        LogUtil.log("VideoAndroidPlayer => releaseDecoder => mMediaPlayer = " + mMediaPlayer + ", isFromUser = " + isFromUser);
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            clear();
            unRegistListener();
            release();
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mMediaPlayer)
                throw new Exception("warning: null == mMediaPlayer");
            mMediaPlayer = new MediaPlayer();
            registListener();
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => createDecoder => Exception " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("error: mMediaPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (url == null)
                throw new Exception("error: url null");
            // 拉流
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.LOADING_START);
            mMediaPlayer.setDataSource(context, Uri.parse(url), null);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mMediaPlayer.prepareAsync();
            } else {
                mMediaPlayer.prepare();
            }
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.ERROR_PARSE);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("error: mMediaPlayer null");
            boolean mute = isMute();
            setVolume(mute ? 0L : 1L, mute ? 0L : 1L);
            boolean looping = isLooping();
            setLooping(looping);
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => initOptions => Exception " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.setOnErrorListener(onErrorListener);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setOnInfoListener(onInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
            mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            LogUtil.log("VideoAndroidPlayer => registListener =>");
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => registListener => " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnInfoListener(null);
            mMediaPlayer.setOnBufferingUpdateListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            LogUtil.log("VideoAndroidPlayer => unRegistListener =>");
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => unRegistListener => " + e.getMessage());
        }
    }

    //    /**
//     * 用于播放raw和asset里面的视频文件
//     */
//    @Override
//    public void setDataSource(AssetFileDescriptor fd) {
//        try {
//            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//        } catch (Exception e) {
//            MPLogUtil.log("VideoAndroidPlayer => " + e.getMessage());
//        }
//    }

    @Override
    public void release() {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.setSurface(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => release => " + e.getMessage());
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.start();
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.pause();
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        clear();
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => stop => " + e.getMessage());
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            return mMediaPlayer.isPlaying();
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        try {
            if (seek < 0L)
                throw new Exception("error: seek<0");
            if (null == mMediaPlayer)
                throw new Exception("error: mMediaPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0L && seek > duration) {
                seek = duration;
            }

            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.SEEK_START);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo((int) seek);
            } else {
                int seekType = args.getSeekType();
                switch (seekType) {
                    case PlayerType.SeekType.ANDROID_SEEK_CLOSEST:
                        mMediaPlayer.seekTo(seek, MediaPlayer.SEEK_CLOSEST);
                        break;
                    case PlayerType.SeekType.ANDROID_SEEK_CLOSEST_SYNC:
                        mMediaPlayer.seekTo(seek, MediaPlayer.SEEK_CLOSEST_SYNC);
                        break;
                    case PlayerType.SeekType.ANDROID_SEEK_PREVIOUS_SYNC:
                        mMediaPlayer.seekTo(seek, MediaPlayer.SEEK_PREVIOUS_SYNC);
                        break;
                    case PlayerType.SeekType.ANDROID_SEEK_NEXT_SYNC:
                        mMediaPlayer.seekTo(seek, MediaPlayer.SEEK_NEXT_SYNC);
                        break;
                    default:
                        mMediaPlayer.seekTo((int) seek);
                        break;
                }
            }
            LogUtil.log("VideoAndroidPlayer => seekTo =>");
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => seekTo => Exception " + e.getMessage());
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
//            MPLogUtil.log("VideoAndroidPlayer => getPosition => " + e.getMessage());
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            int duration = mMediaPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoAndroidPlayer => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (null == surface)
                throw new Exception("surface error: null");
            mMediaPlayer.setSurface(surface);
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => setSurface => " + e.getMessage());
        }
    }

    @Override
    public boolean setSpeed(float speed) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            PlaybackParams playbackParams = mMediaPlayer.getPlaybackParams();
            if (null != playbackParams) {
                playbackParams = new PlaybackParams();
            }
            playbackParams.setSpeed(speed);
            mMediaPlayer.setPlaybackParams(playbackParams);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            try {
                LogUtil.log("VideoAndroidPlayer => onError => what = " + what);
                if (what == -38) {
                    throw new Exception("what warning: " + what);
                } else if (what == -10005) {
                    throw new Exception("what warning: " + what);
                } else {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.LOADING_STOP);
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.ERROR_PARSE);
                }
            } catch (Exception e) {
                LogUtil.log("VideoAndroidPlayer => onError => " + e.getMessage());
            }
            return true;
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {

        @SuppressLint("StaticFieldLeak")
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            LogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", extra = " + extra);
            switch (what) {
                // 缓冲开始
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    if (isPrepared()) {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.BUFFERING_START);
                    } else {
                        LogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
                // 缓冲结束
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (isPrepared()) {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.BUFFERING_STOP);
                    } else {
                        LogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
//                // 开始播放
                case 903:
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    try {
                        if (isPrepared())
                            throw new Exception("warning: mPrepared true");
                        setPrepared(true);
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.LOADING_STOP);
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.VIDEO_RENDERING_START);
                        long seek = getSeek();
                        if (seek <= 0) {
                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.VIDEO_START);
                        } else {
                            // 起播快进
                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.VIDEO_RENDERING_START_SEEK);
                            seekTo(seek);
                        }
                    } catch (Exception e) {
                        LogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", msg = " + e.getMessage());
                    }
                    break;
            }
            return true;
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            LogUtil.log("VideoAndroidPlayer => onSeekComplete =>");

            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.SEEK_FINISH);

            try {
                long seek = getSeek();
                if (seek <= 0L)
                    throw new Exception("warning: seek<=0");
                setSeek(0L);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.VIDEO_START);
            } catch (Exception e) {
                LogUtil.log("VideoAndroidPlayer => onSeekComplete => Exception1 " + e.getMessage());
            }

            try {
                boolean playing = isPlaying();
                if (playing)
                    throw new Exception("warning: playing true");
                start();
            } catch (Exception e) {
                LogUtil.log("VideoAndroidPlayer => onSeekComplete => Exception2 " + e.getMessage());
            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            LogUtil.log("VideoAndroidPlayer => onPrepared =>");

            // 解决部分盒子不回调 info code=3
            try {
                startCheckPreparedPlaying(PlayerType.KernelType.ANDROID);
            } catch (Exception e) {
            }

            // 播放
            start();
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtil.log("VideoAndroidPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.VIDEO_END);
        }
    };


    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            LogUtil.log("VideoAndroidPlayer => onBufferingUpdate => percent = " + percent);
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            try {
                if (null == mp)
                    throw new Exception("error: MediaPlayer null");
                int videoWidth = mp.getVideoWidth();
                if (videoWidth <= 0)
                    throw new Exception("error: videoWidth <=0");
                int videoHeight = mp.getVideoHeight();
                if (videoHeight <= 0)
                    throw new Exception("error: videoHeight <=0");
                boolean videoSizeChanged = isVideoSizeChanged();
                if (videoSizeChanged)
                    throw new Exception("warning: videoSizeChanged = true");
                StartArgs args = getStartArgs();
                if (null == args)
                    throw new Exception("error: args null");
                setVideoSizeChanged(true);
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
                onUpdateSizeChanged(PlayerType.KernelType.ANDROID, videoWidth, videoHeight, rotation, scaleType);
            } catch (Exception e) {
                LogUtil.log("VideoAndroidPlayer => onVideoSizeChanged => " + e.getMessage());
            }
        }
    };

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            float value;
            if (isMute()) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
            }
            if (value > 1f) {
                value = 1f;
            }
            mMediaPlayer.setVolume(value, value);
        } catch (Exception e) {
            LogUtil.log("VideoAndroidPlayer => setVolume => " + e.getMessage());
        }
    }
}
