package lib.kalu.mediaplayer.core.kernel.video.ff;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;


import lib.kalu.ffplayer.FFmpegPlayer;
import lib.kalu.ffplayer.inter.OnBufferingUpdateListener;
import lib.kalu.ffplayer.inter.OnCompletionListener;
import lib.kalu.ffplayer.inter.OnErrorListener;
import lib.kalu.ffplayer.inter.OnInfoListener;
import lib.kalu.ffplayer.inter.OnPreparedListener;
import lib.kalu.ffplayer.inter.OnSeekCompleteListener;
import lib.kalu.ffplayer.inter.OnVideoSizeChangedListener;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;


public final class VideoFFmpegPlayer extends VideoBasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;

    private FFmpegPlayer mFFmpegPlayer = null;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;

    @Override
    public VideoFFmpegPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayerCollects error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, boolean logger, int seekParameters) {
        try {
            if (null != mFFmpegPlayer)
                throw new Exception("warning: null != mFFmpegPlayer");
            mFFmpegPlayer = new FFmpegPlayer();
            mFFmpegPlayer.setLooping(false);
            setVolume(1F, 1F);
            initListener();
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, String url, boolean prepareAsync) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            if (url == null || url.length() == 0)
                throw new IllegalArgumentException("url error: " + url);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_LOADING_START);
            mFFmpegPlayer.setDataSource(context, Uri.parse(url), null);
            mFFmpegPlayer.prepare();
        } catch (IllegalArgumentException e) {
            MPLogUtil.log("VideoFFmpegPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_ERROR_URL);
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_ERROR_PARSE);
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
        mFFmpegPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mFFmpegPlayer.setOnPreparedListener(onPreparedListener);
        mFFmpegPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mFFmpegPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
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
    public void release() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.setOnErrorListener(null);
            mFFmpegPlayer.setOnCompletionListener(null);
            mFFmpegPlayer.setOnInfoListener(null);
            mFFmpegPlayer.setOnBufferingUpdateListener(null);
            mFFmpegPlayer.setOnPreparedListener(null);
            mFFmpegPlayer.setOnVideoSizeChangedListener(null);
            mFFmpegPlayer.setOnSeekCompleteListener(null);
            mFFmpegPlayer.setOnBufferingUpdateListener(null);
            mFFmpegPlayer.setSurface(null);
            mFFmpegPlayer.reset();
            mFFmpegPlayer.release();
            mFFmpegPlayer = null;
            mPrepared = false;
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
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
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
    public void stop() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.stop();
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
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
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
            long duration = getDuration();
            if (seek > duration) {
                MPLogUtil.log("VideoFFmpegPlayer => seekTo => seek = " + seek + ", duration = " + duration);
                pause();
                stop();
                release();
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_ERROR_SEEK_TIME);
            } else {
                MPLogUtil.log("VideoFFmpegPlayer => seekTo => succ");
                mFFmpegPlayer.seekTo((int) seek);
            }
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
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            long currentPosition = mFFmpegPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => getPosition => " + e.getMessage());
            return -1L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            int duration = mFFmpegPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => getDuration => " + e.getMessage());
            return -1L;
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
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
    @FloatRange(from = 1F, to = 4F)
    public float getSpeed() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
//            return mFFmpegPlayer.getPlaybackParams().getSpeed();
            return 1f;
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => getSpeed => " + e.getMessage());
            return 1f;
        }
    }

    @Override
    public boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
            return false;
        } catch (Exception e) {
            MPLogUtil.log("VideoFFmpegPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(FFmpegPlayer mp, int what, int extra) {
            MPLogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what);
            // 缓冲开始
            if (what == PlayerType.EventType.EVENT_BUFFERING_START) {
                if (mPrepared) {
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_BUFFERING_START);
                } else {
                    MPLogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", mPrepared = false");
                }
            }
            // 缓冲结束
            else if (what == PlayerType.EventType.EVENT_BUFFERING_STOP) {
                if (mPrepared) {
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_BUFFERING_STOP);
                } else {
                    MPLogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", mPrepared = false");
                }
            }
            // 开始播放
            else if (what == PlayerType.EventType.EVENT_VIDEO_START) {
                try {
                    if (mPrepared)
                        throw new Exception("warning: mPrepared true");
                    mPrepared = true;
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_LOADING_STOP);
                    long seek = getSeek();
                    if (seek <= 0) {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_VIDEO_START);
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_VIDEO_RENDERING_START);
                        // 起播快进
                        seekTo(seek);
                    }
                } catch (Exception e) {
                    MPLogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", msg = " + e.getMessage());
                }
            }
            return true;
        }
    };

    private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(FFmpegPlayer mediaPlayer) {
            MPLogUtil.log("VideoFFmpegPlayer => onSeekComplete =>");
            try {
                long seek = getSeek();
                if (seek <= 0)
                    throw new Exception();
                setSeek(0);
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_VIDEO_START);
            } catch (Exception e) {
            }
        }
    };

    private OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(FFmpegPlayer mp) {
            MPLogUtil.log("VideoFFmpegPlayer => onPrepared =>");
            start();
        }
    };

    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(FFmpegPlayer mp, int percent) {
            MPLogUtil.log("VideoFFmpegPlayer => onBufferingUpdate => percent = " + percent);
        }
    };

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(FFmpegPlayer mp, int what, int extra) {
            MPLogUtil.log("VideoFFmpegPlayer => onError => what = " + what);
            // ignore -38
            if (what == -38) {

            }
            // error
            else {
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_ERROR_PARSE);
            }
            return true;
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(FFmpegPlayer mp) {
            MPLogUtil.log("VideoFFmpegPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };

    private OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(FFmpegPlayer o, int width, int height) {
            try {
                int w = o.getVideoWidth();
                int h = o.getVideoHeight();
                if (w < 0 || h < 0)
                    throw new Exception("w error: " + w + ", h error: " + h);
                onUpdateSizeChanged(PlayerType.KernelType.FFPLAYER, w, h, PlayerType.RotationType.Rotation_0);
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
    public void setLive(boolean live) {
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
