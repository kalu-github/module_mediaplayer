package lib.kalu.mediaplayer.core.kernel.video.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AsyncPlayer;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;


import com.google.android.exoplayer2.PlaybackParameters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;


public final class VideoAndroidPlayer extends VideoBasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;

    private MediaPlayer mMediaPlayer = null;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;

    @Override
    public VideoAndroidPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        MPLogUtil.log("VideoAndroidPlayer => releaseDecoder => mMediaPlayer = " + mMediaPlayer + ", isFromUser = " + isFromUser);
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release();
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, boolean logger, int seekParameters) {
        try {
            if (null != mMediaPlayer)
                throw new Exception("warning: null == mMediaPlayer");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setLooping(false);
            setVolume(1F, 1F);
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, String url, boolean prepareAsync) {
        MPLogUtil.log("VideoAndroidPlayer => startDecoder => mMediaPlayer = " + mMediaPlayer + ", url = " + url + ", prepareAsync = " + prepareAsync);
        try {
            if (url == null || url.length() == 0)
                throw new IllegalArgumentException("url error: " + url);
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            // 监测拉流超时
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int timeoutSeconds = PlayerManager.getInstance().getConfig().getConnectTimeoutSeconds();
                    long maxTime = timeoutSeconds * 1000;
                    long startTime = System.currentTimeMillis();
                    while (true) {
                        MPLogUtil.log("VideoAndroidPlayer => startDecoder => mPrepared = " + mPrepared + ", mMediaPlayer = " + mMediaPlayer);
                        if (null == mMediaPlayer || mPrepared)
                            break;
                        long currtTime = System.currentTimeMillis();
                        long castTime = currtTime - startTime;
                        MPLogUtil.log("VideoAndroidPlayer => startDecoder => currtTime = " + currtTime + ", startTime = " + startTime + ", castTime = " + castTime + ", maxTime = " + maxTime);
                        if (castTime > maxTime) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_NET);
                                    pause();
                                    stop();
                                    release();
                                }
                            });
                            break;
                        }
                        SystemClock.sleep(100);
                    }
                }
            }).start();
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_START);
            initListener();
            mMediaPlayer.setDataSource(context, Uri.parse(url), null);
            if (prepareAsync) {
                mMediaPlayer.prepareAsync();
            } else {
                mMediaPlayer.prepare();
            }
            // 监测网络连接超时
        } catch (IllegalArgumentException e) {
            MPLogUtil.log("VideoAndroidPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_URL);
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => startDecoder => " + e.getMessage());
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
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => initListener => " + e.getMessage());
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
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnInfoListener(null);
            mMediaPlayer.setOnBufferingUpdateListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnSeekCompleteListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer.setSurface(null);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mPrepared = false;
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => release => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => start => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        try {
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mPrepared = false;
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => stop => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => isPlaying => " + e.getMessage());
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

            long duration = getDuration();
            if (seek > duration) {
                MPLogUtil.log("VideoAndroidPlayer => seekTo => seek = " + seek + ", duration = " + duration);
                pause();
                stop();
                release();
                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_SEEK_TIME);
            } else {
                MPLogUtil.log("VideoAndroidPlayer => seekTo => succ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mMediaPlayer.seekTo(seek, MediaPlayer.SEEK_CLOSEST);
                    mMediaPlayer.seekTo((int) seek);
                } else {
                    mMediaPlayer.seekTo((int) seek);
                }
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => seekTo => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => getPosition => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => getDuration => " + e.getMessage());
            return 0;
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
            MPLogUtil.log("VideoAndroidPlayer => setSurface => " + e.getMessage());
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
            if (null == mMediaPlayer)
                throw new Exception("mMediaPlayer error: null");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                throw new Exception("only support above Android M");
            PlaybackParams playbackParams = mMediaPlayer.getPlaybackParams();
            if (null == playbackParams)
                throw new Exception("playbackParams error: null");
            float speed = playbackParams.getSpeed();
            if (speed < 1f)
                throw new Exception("speed error: " + speed);
            return speed;
        } catch (Exception e) {
            MPLogUtil.log("VideoAndroidPlayer => getSpeed => " + e.getMessage());
            return 1F;
        }
    }

    @Override
    public boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed) {
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
            MPLogUtil.log("VideoAndroidPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            try {
                MPLogUtil.log("VideoAndroidPlayer => onError => what = " + what);
                if (what == -38) {
                    throw new Exception("what warning: " + what);
                } else if (what == -10005) {
                    throw new Exception("what warning: " + what);
                } else {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_PARSE);
                }
            } catch (Exception e) {
                MPLogUtil.log("VideoAndroidPlayer => onError => " + e.getMessage());
            }
            return true;
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {

        @SuppressLint("StaticFieldLeak")
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            MPLogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", extra = " + extra);
            switch (what) {
                // 缓冲开始
                case PlayerType.EventType.EVENT_BUFFERING_START:
                    if (mPrepared) {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_START);
                    } else {
                        MPLogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
                // 缓冲结束
                case PlayerType.EventType.EVENT_BUFFERING_STOP:
                    if (mPrepared) {
                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_BUFFERING_STOP);
                    } else {
                        MPLogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
//                // 开始播放
//                case 903:
//                case PlayerType.EventType.EVENT_VIDEO_START:
//                    try {
//                        if (mPrepared)
//                            throw new Exception("warning: mPrepared true");
//                        mPrepared = true;
//                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
//                        long seek = getSeek();
//                        if (seek <= 0) {
//                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START);
//                        } else {
//                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_RENDERING_START);
//                            // 起播快进
//                            seekTo(seek);
//                        }
//                    } catch (Exception e) {
//                        MPLogUtil.log("VideoAndroidPlayer => onInfo => what = " + what + ", msg = " + e.getMessage());
//                    }
//                    break;
            }
            return true;
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            MPLogUtil.log("VideoAndroidPlayer => onSeekComplete =>");
//            try {
//                long seek = getSeek();
//                if (seek <= 0)
//                    throw new Exception();
//                setSeek(0);
//                onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START);
//            } catch (Exception e) {
//            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            MPLogUtil.log("VideoAndroidPlayer => onPrepared =>");
//            start();
            mPrepared = true;
            start();
            try {

                long seek = getSeek();
                // 起播快进
                if (seek > 0) {
                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_RENDERING_START);
                    setSeek(0);
                    seekTo(seek);
                }

                // 监听播放状态
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long position = getPosition();
                        long olds = Math.max(position, seek);
                        while (true) {
                            if (null == mMediaPlayer || !mPrepared)
                                break;
                            long news = getPosition();
                            MPLogUtil.log("VideoAndroidPlayer => onPrepared => olds = " + olds + ", news = " + news);
                            if (news > olds) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                                        onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_START);
                                    }
                                });
                                break;
                            }
                            SystemClock.sleep(100);
                        }
                    }
                }).start();

            } catch (Exception e) {
                MPLogUtil.log("VideoAndroidPlayer => onPrepared => " + e.getMessage());
            }
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            MPLogUtil.log("VideoAndroidPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };


    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            MPLogUtil.log("VideoAndroidPlayer => onBufferingUpdate => percent = " + percent);
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            try {
                if (null == mp)
                    throw new Exception("mp error: null");
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                MPLogUtil.log("VideoAndroidPlayer => onVideoSizeChanged => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);
                if (videoWidth < 0 || videoHeight < 0)
                    throw new Exception("videoWidth error: " + videoWidth + ", videoHeight error: " + videoHeight);
                onUpdateSizeChanged(PlayerType.KernelType.ANDROID, videoWidth, videoHeight, PlayerType.RotationType.Rotation_0);
            } catch (Exception e) {
                MPLogUtil.log("VideoAndroidPlayer => onVideoSizeChanged => " + e.getMessage());
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
            MPLogUtil.log("VideoAndroidPlayer => setVolume => " + e.getMessage());
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
