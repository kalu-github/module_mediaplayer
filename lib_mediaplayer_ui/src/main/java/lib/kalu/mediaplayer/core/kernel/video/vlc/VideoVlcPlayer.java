package lib.kalu.mediaplayer.core.kernel.video.vlc;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.vlc.widget.OnVlcInfoChangeListener;
import lib.kalu.vlc.widget.VlcPlayer;

@Keep
public final class VideoVlcPlayer extends BasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;

    private lib.kalu.vlc.widget.VlcPlayer mVlcPlayer;
    private lib.kalu.vlc.widget.OnVlcInfoChangeListener mVlcPlayerListener;

    public VideoVlcPlayer(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent eventApi, @NonNull boolean retryBuffering) {
        super(playerApi, eventApi, retryBuffering);
    }

    @NonNull
    @Override
    public VideoVlcPlayer getPlayer() {
        return this;
    }


    @Override
    public void releaseDecoder(boolean isFromUser, boolean isMainThread) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release(isMainThread);
        } catch (Exception e) {
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        try {
            releaseDecoder(false, true);
            mVlcPlayer = new VlcPlayer(context);
            setLooping(false);
            setVolume(1F, 1F);
            initListener();
        } catch (Exception e) {
        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            if (url == null || url.length() == 0)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_START);
            mVlcPlayer.setDataSource(Uri.parse(url), mPlayWhenReady);
            mVlcPlayer.play();
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_URL);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {

    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
        MPLogUtil.log("VideoVlcPlayer => initListener =>");
        mVlcPlayerListener = new OnVlcInfoChangeListener() {
            @Override
            public void onStart() {
                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_START);
            }

            @Override
            public void onPlay() {
                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_VIDEO_START);

                long seek = getSeek();
                if (seek > 0) {
                    seekTo(seek);
                }
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onEnd() {
                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_VIDEO_END);
            }

            @Override
            public void onError() {
                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_PARSE);
            }
        };
        mVlcPlayer.setOnVlcInfoChangeListener(mVlcPlayerListener);
    }

    @Override
    public void release(boolean isMainThread) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            if (null != mVlcPlayerListener) {
                mVlcPlayer.setOnVlcInfoChangeListener(null);
            }
            mVlcPlayerListener = null;
            mVlcPlayer.setSurface(null, 0, 0);
            if (isMainThread) {
                mVlcPlayer.release();
                mVlcPlayer = null;
                mPrepared = false;
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mVlcPlayer.release();
                        mVlcPlayer = null;
                        mPrepared = false;
                    }
                }).start();
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoVlcPlayer => release => " + e.getMessage());
        }
    }

    /**
     * 播放
     */
    @Override
    public void start() {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            mVlcPlayer.play();
        } catch (Exception e) {
            MPLogUtil.log("VideoVlcPlayer => start => " + e.getMessage());
        }
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            mVlcPlayer.pause();
        } catch (Exception e) {
            MPLogUtil.log("VideoVlcPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop(boolean isMainThread) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            if (isMainThread) {
                mVlcPlayer.stop();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mVlcPlayer.stop();
                    }
                }).start();
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoVlcPlayer => stop => " + e.getMessage());
        }
    }

    /**
     * 是否正在播放
     */
    @Override
    public boolean isPlaying() {
        if (null != mVlcPlayer) {
            return mVlcPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(@NonNull long position) {
//        try {
//            mVlcPlayer.seekTo((int) time);
//        } catch (IllegalStateException e) {
//        }
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getPosition() {
        if (null != mVlcPlayer) {
            return mVlcPlayer.getPosition();
        } else {
            return 0L;
        }
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (null != mVlcPlayer) {
            return mVlcPlayer.getDuration();
        } else {
            return 0L;
        }
    }

    @Override
    public void setSurface(@NonNull Surface sf, int w, int h) {
        MPLogUtil.log("VideoVlcPlayer => setSurface => sf = " + sf + ", mVlcPlayer = " + mVlcPlayer + ", w = " + w + ", h = " + h);
        if (null != sf && null != mVlcPlayer) {
            mVlcPlayer.setSurface(sf, w, h);
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

    /**
     * 获取播放速度
     *
     * @return 播放速度
     */
    @Override
    public float getSpeed() {
        if (null != mVlcPlayer) {
            return mVlcPlayer.getSpeed();
        } else {
            return 1F;
        }
    }

    /**
     * 设置播放速度
     *
     * @param speed 速度
     */
    @Override
    public void setSpeed(float speed) {
        if (null != mVlcPlayer) {
            mVlcPlayer.setSpeed(speed);
        }
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        if (null != mVlcPlayer) {
            boolean videoMute = isMute();
            if (videoMute) {
                mVlcPlayer.setVolume(0F, 0F);
            } else {
                float value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
                mVlcPlayer.setVolume(value, value);
            }
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
