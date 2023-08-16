package lib.kalu.mediaplayer.core.kernel.video.ff;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.ffplayer.FFmpegPlayer;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class VideoFFmpegPlayer extends BasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;

    private FFmpegPlayer mFFmpegPlayer = null;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;

    public VideoFFmpegPlayer(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent eventApi, @NonNull boolean retryBuffering) {
        super(playerApi, eventApi, retryBuffering);
    }

    @NonNull
    @Override
    public VideoFFmpegPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser, boolean isMainThread) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayerCollects error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release(isMainThread);
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        try {
            releaseDecoder(false, true);
            mFFmpegPlayer = new FFmpegPlayer();
            mFFmpegPlayer.setLooping(false);
            setVolume(1F, 1F);
            initListener();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            if (url == null || url.length() == 0)
                throw new IllegalArgumentException("url error: " + url);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_START);
            mFFmpegPlayer.reset();
            mFFmpegPlayer.setDataSource(context, Uri.parse(url), null);
            mFFmpegPlayer.prepare();
        } catch (IllegalArgumentException e) {
            MPLogUtil.log("VideoFFmpegPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_URL);
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {

    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
        mFFmpegPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mFFmpegPlayer.setOnErrorListener(onErrorListener);
        mFFmpegPlayer.setOnCompletionListener(onCompletionListener);
        mFFmpegPlayer.setOnInfoListener(onInfoListener);
//        mFFmpegPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mFFmpegPlayer.setOnPreparedListener(onPreparedListener);
        mFFmpegPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

//    /**
//     * 用于播放raw和asset里面的视频文件
//     */
//    @Override
//    public void setDataSource(AssetFileDescriptor fd) {
//        try {
//            mFFmpegPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => " + e.getMessage());
//        }
//    }

    @Override
    public void release(boolean isMainThread) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.setOnErrorListener(null);
            mFFmpegPlayer.setOnCompletionListener(null);
            mFFmpegPlayer.setOnInfoListener(null);
            mFFmpegPlayer.setOnBufferingUpdateListener(null);
            mFFmpegPlayer.setOnPreparedListener(null);
            mFFmpegPlayer.setOnVideoSizeChangedListener(null);
            mFFmpegPlayer.setSurface(null);
            if (isMainThread) {
                mFFmpegPlayer.reset();
                mFFmpegPlayer.release();
                mFFmpegPlayer = null;
                mPrepared = false;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mFFmpegPlayer.reset();
                        mFFmpegPlayer.release();
                        mFFmpegPlayer = null;
                        mPrepared = false;
                    }
                }).start();
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.start();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.pause();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop(boolean isMainThread) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            if (isMainThread) {
                mFFmpegPlayer.stop();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mFFmpegPlayer.stop();
                    }
                }).start();
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => stop => " + e.getMessage());
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            return mFFmpegPlayer.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => stop => " + e.getMessage());
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            if (seek < 0)
                throw new Exception("seek error: " + seek);
            mFFmpegPlayer.seekTo((int) seek);
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => seekTo => " + e.getMessage());
        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            return mFFmpegPlayer.getCurrentPosition();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => getPosition => " + e.getMessage());
            return 0;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            return mFFmpegPlayer.getDuration();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => getDuration => " + e.getMessage());
            return 0;
        }
    }

    @Override
    public void setSurface(@NonNull Surface surface, int w, int h) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            if (null == surface)
                throw new Exception("surface error: null");
            mFFmpegPlayer.setSurface(surface);
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => setSurface => " + e.getMessage());
        }
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        this.mPlayWhenReady = playWhenReady;
    }

    @Override
    public boolean isPlayWhenReady() {
        return mPlayWhenReady;
    }

    @Override
    public void setRetryBuffering(@NonNull boolean retryBuffering) {

    }

    @Override
    public float getSpeed() {
//        try {
//            if (null == mFFmpegPlayer)
//                throw new Exception("mFFmpegPlayer error: null");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
//            return mFFmpegPlayer.getPlaybackParams().getSpeed();
//        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => getSpeed => " + e.getMessage());
//            return 1f;
//        }
        return 1F;
    }

    @Override
    public void setSpeed(float speed) {
//        try {
//            if (null == mFFmpegPlayer)
//                throw new Exception("mFFmpegPlayer error: null");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
//            mFFmpegPlayer.setPlaybackParams(mFFmpegPlayer.getPlaybackParams().setSpeed(speed));
//        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => setSpeed => " + e.getMessage());
//        }
    }

    private FFmpegPlayer.OnErrorListener onErrorListener = new FFmpegPlayer.OnErrorListener() {
        @Override
        public boolean onError(FFmpegPlayer mp, int what, int extra) {
            MPLogUtil.log("VideoFFmpegPlayer => onError => what = " + what);
            // ignore -38
            if (what == -38) {

            }
            // error
            else {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
            }
            return true;
        }
    };

    private FFmpegPlayer.OnCompletionListener onCompletionListener = new FFmpegPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(FFmpegPlayer mp) {
            MPLogUtil.log("VideoFFmpegPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };

    private FFmpegPlayer.OnInfoListener onInfoListener = new FFmpegPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(FFmpegPlayer mp, int what, int extra) {
            MPLogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what);
            // 缓冲开始
            if (what == PlayerType.EventType.EVENT_BUFFERING_START) {
                long position = getPosition();
                long seek = getSeek();
                long duration = getDuration();
                if (duration > 0 && position > seek) {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_START);
                }
            }
            // 缓冲结束
            else if (what == PlayerType.EventType.EVENT_BUFFERING_STOP) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_STOP);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            }
            // 开始播放
            else if (what == PlayerType.EventType.EVENT_VIDEO_START) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START);
                if (!mPlayWhenReady) {
                    pause();
                }
            }
            return true;
        }
    };

    private FFmpegPlayer.OnPreparedListener onPreparedListener = new FFmpegPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(FFmpegPlayer mp) {
            mPrepared = true;
            try {
                long seek = getSeek();
                if (seek <= 0)
                    throw new Exception("seek warning: " + seek);
                seekTo(seek);
            } catch (Exception e) {
                MPLogUtil.log("VideoFFmpegPlayer => onPrepared => " + e.getMessage());
            }
            start();
        }
    };

    private FFmpegPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new FFmpegPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(FFmpegPlayer o, int width, int height) {
            try {
                int w = o.getVideoWidth();
                int h = o.getVideoHeight();
                if (w < 0 || h < 0)
                    throw new Exception("w error: " + w + ", h error: " + h);
                onMeasure(PlayerType.KernelType.ANDROID, w, h, PlayerType.RotationType.Rotation_0);
            } catch (Exception e) {
                MPLogUtil.log("VideoFFmpegPlayer => onVideoSizeChanged => " + e.getMessage());
            }
        }
    };

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            float value;
            if (isMute()) {
                value = 0F;
            } else {
                value = Math.max(v1, v2);
            }
            if (value > 1f) {
                value = 1f;
            }
            mFFmpegPlayer.setVolume(value, value);
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => setVolume => " + e.getMessage());
        }
    }

    @Override
    public boolean isMute() {
        return mMute;
    }

    @Override
    public void setMute(boolean v) {
        mMute = v;
        setVolume(v ? 0f : 1f, v ? 0f : 1f);
    }

    @Override
    public long getSeek() {
        return mSeek;
    }

    @Override
    public void setSeek(long seek) {
        if (seek < 0)
            return;
        mSeek = seek;
    }

    @Override
    public long getMax() {
        return mMax;
    }

    @Override
    public void setMax(long max) {
        if (max < 0)
            return;
        mMax = max;
    }

    @Override
    public boolean isLive() {
        return mLive;
    }

    @Override
    public void setLive(@NonNull boolean live) {
        this.mLive = live;
    }

    @Override
    public void setLooping(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public boolean isLooping() {
        return mLoop;
    }

    @Override
    public boolean isPrepared() {
        return mPrepared;
    }
}
