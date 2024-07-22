package lib.kalu.mediaplayer.core.kernel.video.vlc;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.vlc.util.VlcLogUtil;
import lib.kalu.vlc.widget.OnVlcInfoChangeListener;
import lib.kalu.vlc.widget.VlcPlayer;


public final class VideoVlcPlayer extends VideoBasePlayer {

    private lib.kalu.vlc.widget.VlcPlayer mVlcPlayer;

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
            clear();
            unRegistListener();
            release();
        } catch (Exception e) {
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mVlcPlayer)
                throw new Exception("warning: null != mVlcPlayer");
            mVlcPlayer = new VlcPlayer(context);
            registListener();
        } catch (Exception e) {
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mVlcPlayer)
                throw new Exception("error: mVlcPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (url == null)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.LOADING_START);
            mVlcPlayer.setDataSource(Uri.parse(url), isPlayWhenReady());
            mVlcPlayer.play();
        } catch (Exception e) {
            stop();
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.ERROR);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {

        try {
            if (null == mVlcPlayer)
                throw new Exception("error: mVlcPlayer null");
            boolean mute = isMute();
            setVolume(mute ? 0L : 1L, mute ? 0L : 1L);
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => initOptions => Exception " + e.getMessage());
        }

        try {
            if (null == mVlcPlayer)
                throw new Exception("error: mVlcPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            Class<?> clazz = Class.forName("lib.kalu.vlc.util.VlcLogUtil");
            if (null == clazz)
                throw new Exception("warning: lib.kalu.vlc.util.VlcLogUtil not find");
            boolean log = args.isLog();
            VlcLogUtil.setLogger(log);
        } catch (Exception e) {
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mVlcPlayer)
                throw new Exception("error: mVlcPlayer null");
            mVlcPlayer.setOnVlcInfoChangeListener(mVlcPlayerListener);
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mVlcPlayer)
                throw new Exception("error: mVlcPlayer null");
            mVlcPlayer.setOnVlcInfoChangeListener(null);
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => unRegistListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void release() {
        try {
            if (null == mVlcPlayer)
                throw new Exception("mVlcPlayer error: null");
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
        clear();
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
    public boolean setSpeed(float speed) {
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

    private final OnVlcInfoChangeListener mVlcPlayerListener = new OnVlcInfoChangeListener() {
        @Override
        public void onStart() {
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.LOADING_START);
        }

        @Override
        public void onPlay() {
//            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.LOADING_STOP);
//            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.VIDEO_START);
//
//            long seek = getSeek();
//            if (seek > 0) {
//                seekTo(seek);
//            }
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onEnd() {
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.COMPLETE);
        }

        @Override
        public void onError() {
            stop();
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.ERROR);
        }
    };

    /****************/

    // 起播参数
    private StartArgs mStartArgs = null;

    @Override
    public void setStartArgs(StartArgs args) {
        this.mStartArgs = args;
    }

    @Override
    public StartArgs getStartArgs() {
        return this.mStartArgs;
    }

    // 正在切换window
    private boolean mDoWindowing = false;

    @Override
    public void setDoWindowing(boolean v) {
        this.mDoWindowing = v;
    }

    @Override
    public boolean isDoWindowing() {
        return this.mDoWindowing;
    }

    // 起播快进
    private boolean mDoSeeking = false;

    @Override
    public boolean isDoSeeking() {
        return this.mDoSeeking;
    }

    @Override
    public void setDoSeeking(boolean flag) {
        this.mDoSeeking = flag;
    }

    // 单片循环
    private boolean mLooping = false;

    @Override
    public void setLooping(boolean v) {
        this.mLooping = v;
    }

    @Override
    public boolean isLooping() {
        return this.mLooping;
    }

    // 是否静音
    private boolean mMute = false;

    @Override
    public boolean isMute() {
        return this.mMute;
    }

    @Override
    public void setMute(boolean v) {
        this.mMute = v;
    }

    // 准备
    private boolean mPrepared = false;

    @Override
    public boolean isPrepared() {
        return this.mPrepared;
    }

    @Override
    public void setPrepared(boolean v) {
        this.mPrepared = v;
    }

    // 大小变化
    private boolean mVideoSizeChanged = false;

    @Override
    public boolean isVideoSizeChanged() {
        return this.mVideoSizeChanged;
    }

    @Override
    public void setVideoSizeChanged(boolean v) {
        this.mVideoSizeChanged = v;
    }

    /****************/

    private android.os.Handler mHandlerPlayWhenReadyDelayedTime = null;

    @Override
    public void startPlayWhenReadyDelayedTime(long delayedTime) {
        try {
            if (!isPrepared())
                throw new Exception("warning: isPrepared false");
            if (null == mHandlerPlayWhenReadyDelayedTime) {
                mHandlerPlayWhenReadyDelayedTime = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        try {
                            // 起播快进
                            onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.RESUME);
                            start();
                        } catch (Exception e) {
                            LogUtil.log("VideoVlcPlayer => startPlayWhenReadyDelayedTime => Exception2 " + e.getMessage());
                        } finally {
                            stopCheckPreparedPlaying();
                        }
                    }
                };
            }
            mHandlerPlayWhenReadyDelayedTime.removeCallbacksAndMessages(null);
            mHandlerPlayWhenReadyDelayedTime.sendEmptyMessageDelayed(10008, delayedTime);
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => startPlayWhenReadyDelayedTime => Exception1 " + e.getMessage());
        }
    }

    @Override
    public void stopPlayWhenReadyDelayedTime() {
        try {
            if (null == mHandlerPlayWhenReadyDelayedTime)
                throw new Exception("warning: mHandlerPlayWhenReadyDelayedTime null");
            mHandlerPlayWhenReadyDelayedTime.removeCallbacksAndMessages(null);
            mHandlerPlayWhenReadyDelayedTime = null;
            LogUtil.log("VideoVlcPlayer => playWhenReadyDelayedTime => stop mHandlerPlayWhenReadyDelayedTime");
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => playWhenReadyDelayedTime => " + e.getMessage());
        }
    }

    /***********/

    private android.os.Handler mHandlerPreparedPlaying = null;

    @Override
    public void startCheckPreparedPlaying() {
        try {
            if (isPrepared())
                throw new Exception("warning: isPrepared true");
            if (null == mHandlerPreparedPlaying) {
                mHandlerPreparedPlaying = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        try {
                            if (msg.what != 10001)
                                throw new Exception("warning: msg.what != 10001");
                            if (isPrepared())
                                throw new Exception("warning: mPrepared true");
                            // 开播
                            if (isPlaying()) {
                                setPrepared(true);
                                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.LOADING_STOP);
                                long seek = getSeek();
                                if (seek <= 0) {
                                    onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.START);
                                } else {
                                    onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.START);
                                    // 起播快进
                                    onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.SEEK_START_FORWARD);
                                    setDoSeeking(true);
                                    seekTo(seek);
                                }
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoVlcPlayer => startCheckPreparedPlaying => loop next");
                                mHandlerPreparedPlaying.sendEmptyMessageDelayed(10001, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoVlcPlayer => startCheckPreparedPlaying => Exception2 " + e.getMessage());
                            stopCheckPreparedPlaying();
                        }
                    }
                };
            }
            mHandlerPreparedPlaying.removeCallbacksAndMessages(null);
            mHandlerPreparedPlaying.sendEmptyMessageDelayed(10001, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => startCheckPreparedPlaying => Exception1 " + e.getMessage());
            stopCheckPreparedPlaying();
        }
    }

    @Override
    public void stopCheckPreparedPlaying() {
        try {
            if (null == mHandlerPreparedPlaying)
                throw new Exception("warning: mHandlerPreparedPlaying null");
            mHandlerPreparedPlaying.removeCallbacksAndMessages(null);
            mHandlerPreparedPlaying = null;
            LogUtil.log("VideoVlcPlayer => stopCheckPreparedPlaying => stop mHandlerPreparedPlaying");
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => stopCheckPreparedPlaying => " + e.getMessage());
        }
    }

    /***********/

    private android.os.Handler mHandlerConnectTimeout = null;

    @Override
    public void startCheckConnectTimeout(long timeout) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPrepared true");
            if (null == mHandlerConnectTimeout) {
                mHandlerConnectTimeout = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        try {
                            if (isPrepared())
                                throw new Exception("warning: isPrepared true");
                            if (msg.what != 10002)
                                throw new Exception("warning: msg.what != 10002");
                            long start = (long) msg.obj;
                            long current = System.currentTimeMillis();
                            long cast = current - start;
                            // 超时
                            if (cast >= timeout) {
                                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.LOADING_STOP);
                                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.ERROR);
                                getPlayerApi().stop(true, false);
                                throw new Exception("warning: connect timeout");
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoVlcPlayer => startCheckConnectTimeout => loop next, cast = " + cast);
                                Message message = Message.obtain();
                                message.what = 10002;
                                message.obj = start;
                                mHandlerConnectTimeout.sendMessageDelayed(message, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoVlcPlayer => startCheckConnectTimeout => Exception2 " + e.getMessage());
                            stopCheckConnectTimeout();
                        }
                    }
                };
            }
            mHandlerConnectTimeout.removeCallbacksAndMessages(null);
            Message message = Message.obtain();
            message.what = 10002;
            message.obj = System.currentTimeMillis();
            mHandlerConnectTimeout.sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => startCheckConnectTimeout => Exception1 " + e.getMessage());
            stopCheckConnectTimeout();
        }
    }

    @Override
    public void stopCheckConnectTimeout() {
        try {
            if (null == mHandlerConnectTimeout)
                throw new Exception("warning: mHandlerConnectTimeout null");
            mHandlerConnectTimeout.removeCallbacksAndMessages(null);
            mHandlerConnectTimeout = null;
            LogUtil.log("VideoVlcPlayer => stopCheckConnectTimeout => stop mHandlerConnectTimeout");
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => stopCheckConnectTimeout => " + e.getMessage());
        }
    }

    /***********/

    private android.os.Handler mHandlerBufferingTimeout = null;

    @Override
    public void startCheckBufferingTimeout(boolean bufferingTimeoutRetry, long timeout) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPlaying true");
            if (null == mHandlerBufferingTimeout) {
                mHandlerBufferingTimeout = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {

                        try {
                            if (isPrepared())
                                throw new Exception("warning: isPrepared true");
                            if (msg.what != 10003)
                                throw new Exception("warning: msg.what != 10003");
                            long start = (long) msg.obj;
                            long current = System.currentTimeMillis();
                            float cast = current - start;

                            // 超时
                            if (cast >= timeout) {
                                onEvent(PlayerType.KernelType.VLC, PlayerType.EventType.ERROR_BUFFERING_TIMEOUT);
                                // 1
                                stopCheckBufferingTimeout();
//                              // 2
                                getPlayerApi().stop(true, true);
                                if (!bufferingTimeoutRetry)
                                    throw new Exception("warning: bufferingTimeoutRetry false");
                                boolean live = isLive();
                                if (live) {
                                    getPlayerApi().restart();
                                } else {
                                    long position = getPosition();
                                    getPlayerApi().restart(position);
                                }
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoVlcPlayer => startCheckBufferingTimeout => loop next, cast = " + cast);
                                Message message = Message.obtain();
                                message.what = 10003;
                                message.obj = start;
                                mHandlerBufferingTimeout.sendMessageDelayed(message, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoVlcPlayer => startCheckBufferingTimeout => " + e.getMessage());
                            stopCheckBufferingTimeout();
                        }
                    }
                };
            }
            mHandlerBufferingTimeout.removeCallbacksAndMessages(null);
            Message message = Message.obtain();
            message.what = 10003;
            message.obj = System.currentTimeMillis();
            mHandlerBufferingTimeout.sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => startCheckBufferingTimeout => " + e.getMessage());
            stopCheckBufferingTimeout();
        }
    }

    @Override
    public void stopCheckBufferingTimeout() {
        try {
            if (null == mHandlerBufferingTimeout)
                throw new Exception("warning: mHandlerBufferingTimeout null");
            mHandlerBufferingTimeout.removeCallbacksAndMessages(null);
            mHandlerBufferingTimeout = null;
            LogUtil.log("VideoVlcPlayer => stopCheckBufferingTimeout => stop mHandlerBufferingTimeout");
        } catch (Exception e) {
            LogUtil.log("VideoVlcPlayer => stopCheckBufferingTimeout => " + e.getMessage());
        }
    }
}
