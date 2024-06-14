package lib.kalu.mediaplayer.core.kernel.video.vlc;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.vlc.widget.OnVlcInfoChangeListener;
import lib.kalu.vlc.widget.VlcPlayer;


public final class VideoVlcPlayer extends VideoBasePlayer {

    private lib.kalu.vlc.widget.VlcPlayer mVlcPlayer;
    private lib.kalu.vlc.widget.OnVlcInfoChangeListener mVlcPlayerListener;

    @Override
    public VideoVlcPlayer getPlayer() {
        return this;
    }


    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            if (isFromUser) {
                setEvent(null);
            }
            release();
        } catch (Exception e) {
        }
    }

    @Override
    public void createDecoder(Context context) {
        try {
            if (null != mVlcPlayer)
                throw new Exception("warning: null != mVlcPlayer");
            mVlcPlayer = new VlcPlayer(context);
            setLooping(false);
            setVolume(1F, 1F);
            initListener();
        } catch (Exception e) {
        }
    }

    @Override
    public void startDecoder(Context context, boolean prepareAsync, String url, Object... o) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            if (url == null || url.length() == 0)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_START);
            mVlcPlayer.setDataSource(Uri.parse(url), isPlayWhenReady());
            mVlcPlayer.play();
        } catch (Exception e) {
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.EVENT_ERROR_URL);
        }
    }

    /**
     * MediaPlayer视频播放器监听listener
     */
    private void initListener() {
        LogUtil.log("VideoVlcPlayer => initListener =>");
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
    public void release() {
        releaseParams();
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            if (null != mVlcPlayerListener) {
                mVlcPlayer.setOnVlcInfoChangeListener(null);
            }
            mVlcPlayerListener = null;
            mVlcPlayer.setSurface(null, 0, 0);
            mVlcPlayer.release();
            mVlcPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => release => " + e.getMessage());
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
            LogUtil.log("VideoVlcPlayer => start => " + e.getMessage());
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
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            mVlcPlayer.pause();
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        setPrepared(false);
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            mVlcPlayer.stop();
//            mVlcPlayer.reset();
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => stop => " + e.getMessage());
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
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            return mVlcPlayer.isPlaying();
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    /**
     * 调整进度
     */
    @Override
    public void seekTo(long position) {
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
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            long position = mVlcPlayer.getPosition();
            if (position < 0)
                throw new Exception("position warning: " + position);
            return position;
        } catch (Exception e) {
//            MPLogUtil.log("VideoVlcPlayer => getPosition => " + e.getMessage());
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
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
            long duration = mVlcPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoVlcPlayer => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public void setSurface(Surface sf, int w, int h) {
        LogUtil.log("VideoVlcPlayer => setSurface => sf = " + sf + ", mVlcPlayer = " + mVlcPlayer + ", w = " + w + ", h = " + h);
        if (null != sf && null != mVlcPlayer) {
            mVlcPlayer.setSurface(sf, w, h);
        }
    }

    @Override
    @FloatRange(from = 1F, to = 4F)
    public float getSpeed() {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mIjkPlayer error: null");
            float speed = mVlcPlayer.getSpeed();
            if (speed < 1f)
                throw new Exception("speed error: " + speed);
            return speed;
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => getSpeed => " + e.getMessage());
            return 1F;
        }
    }

    @Override
    public boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mIjkPlayer error: null");
            mVlcPlayer.setSpeed(speed);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => setSpeed => " + e.getMessage());
            return false;
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
}
