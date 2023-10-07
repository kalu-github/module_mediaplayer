package lib.kalu.mediaplayer.core.kernel.audio.android;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.audio.AudioBasePlayer;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class AudioAndroidPlayer extends AudioBasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;

    private MediaPlayer mMediaPlayer = null;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;

    public AudioAndroidPlayer(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent eventApi) {
        super(playerApi, eventApi);
    }

    @NonNull
    @Override
    public AudioAndroidPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser, boolean isMainThread) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayerCollects error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release(isMainThread);
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        try {
            releaseDecoder(false, true);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();
            mMediaPlayer.setLooping(false);
            setVolume(1F, 1F);
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url, @NonNull boolean prepareAsync) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (url == null || url.length() == 0)
                throw new IllegalArgumentException("url error: " + url);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_START);
            initListener();
            mMediaPlayer.setDataSource(context, Uri.parse(url), null);
            if (prepareAsync) {
                mMediaPlayer.prepareAsync();
            } else {
                mMediaPlayer.prepare();
            }
        } catch (IllegalArgumentException e) {
            MPLogUtil.log("AudioAndroidPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_URL);
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
        }
    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.setOnErrorListener(onErrorListener);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setOnInfoListener(onInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => initListener => " + e.getMessage());
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
//            MPLogUtil.log("AudioAndroidPlayer => " + e.getMessage());
//        }
//    }

    @Override
    public void release(boolean isMainThread) {
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
            mMediaPlayer.setSurface(null);
            if (isMainThread) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
                mPrepared = false;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mMediaPlayer.reset();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                        mPrepared = false;
                    }
                }).start();
            }
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => release => " + e.getMessage());
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
            MPLogUtil.log("AudioAndroidPlayer => start => " + e.getMessage());
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
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.pause();
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop(boolean isMainThread) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (isMainThread) {
                mMediaPlayer.stop();
                mPrepared = false;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mMediaPlayer.stop();
                        mPrepared = false;
                    }
                }).start();
            }
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => stop => " + e.getMessage());
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
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            return mMediaPlayer.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long seek) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (seek < 0)
                throw new Exception("seek error: " + seek);
            if (!mPrepared) {
                long position = getPosition();
                if (position > 0) {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_START);
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mMediaPlayer.seekTo(seek, MediaPlayer.SEEK_CLOSEST);
                mMediaPlayer.seekTo((int) seek);
            } else {
                mMediaPlayer.seekTo((int) seek);
            }
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => seekTo => " + e.getMessage());
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
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => getPosition => " + e.getMessage());
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
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            int duration = mMediaPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => getDuration => " + e.getMessage());
            return -1L;
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
    public float getSpeed() {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            return mMediaPlayer.getPlaybackParams().getSpeed();
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => getSpeed => " + e.getMessage());
            return 1f;
        }
    }

    @Override
    public void setSpeed(float speed) {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
        } catch (Exception e) {
            MPLogUtil.log("AudioAndroidPlayer => setSpeed => " + e.getMessage());
        }
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            try {
                MPLogUtil.log("AudioAndroidPlayer => onError => what = " + what);
                if (what == -38) {
                    throw new Exception("what warning: " + what);
                } else if (what == -10005) {
                    throw new Exception("what warning: " + what);
                } else {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
                }
            } catch (Exception e) {
                MPLogUtil.log("AudioAndroidPlayer => onError => " + e.getMessage());
            }
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            MPLogUtil.log("AudioAndroidPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };

    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            MPLogUtil.log("AudioAndroidPlayer => onInfo => what = " + what);
            // 缓冲开始
            if (what == PlayerType.EventType.EVENT_BUFFERING_START) {
                try {
                    boolean prepared = isPrepared();
                    if (!prepared)
                        throw new Exception("prepared warning: false");
                    long position = getPosition();
                    if (position < 0)
                        throw new Exception("position warning: " + position);
                    long duration = getDuration();
                    if (duration < 0)
                        throw new Exception("duration warning: " + duration);
                    long seek = getSeek();
                    if (position <= seek)
                        throw new Exception("position warning: " + position + ", seek warning: " + seek);
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_START);
                } catch (Exception e) {
                }
            }
            // 缓冲结束
            else if (what == PlayerType.EventType.EVENT_BUFFERING_STOP) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_STOP);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            }
            // 开始播放1
            else if (what == PlayerType.EventType.EVENT_VIDEO_START) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START);
//                if (!mPlayWhenReady) {
//                    mPlayWhenReady = true;
//                    pause();
//                }
            }
            // 开始播放2
            else if (what == PlayerType.EventType.EVENT_VIDEO_START_903) {
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START_903);
            }
            return true;
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_STOP);
            try {
                MPLogUtil.log("AudioAndroidPlayer => onSeekComplete =>");
                start();
            } catch (Exception e) {
                MPLogUtil.log("AudioAndroidPlayer => onSeekComplete => " + e.getMessage());
            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mPrepared = true;
            try {
                long seek = getSeek();
                if (seek <= 0)
                    throw new Exception("seek warning: " + seek);
                seekTo(seek);
            } catch (Exception e) {
                MPLogUtil.log("AudioAndroidPlayer => onPrepared => " + e.getMessage());
                start();
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
            MPLogUtil.log("AudioAndroidPlayer => setVolume => " + e.getMessage());
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
