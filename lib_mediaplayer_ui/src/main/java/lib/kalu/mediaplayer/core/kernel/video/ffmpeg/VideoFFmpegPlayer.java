package lib.kalu.mediaplayer.core.kernel.video.ffmpeg;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;

import lib.kalu.ffplayer.FFmpegPlayer;
import lib.kalu.ffplayer.inter.OnBufferingUpdateListener;
import lib.kalu.ffplayer.inter.OnCompletionListener;
import lib.kalu.ffplayer.inter.OnErrorListener;
import lib.kalu.ffplayer.inter.OnInfoListener;
import lib.kalu.ffplayer.inter.OnPreparedListener;
import lib.kalu.ffplayer.inter.OnSeekCompleteListener;
import lib.kalu.ffplayer.inter.OnVideoSizeChangedListener;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public final class VideoFFmpegPlayer extends VideoBasePlayer {

    private FFmpegPlayer mFFmpegPlayer = null;

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
            clear();
            unRegistListener();
            release();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mFFmpegPlayer)
                throw new Exception("warning: null != mFFmpegPlayer");
            mFFmpegPlayer = new FFmpegPlayer();
            registListener();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => createDecoder => " + e.getMessage());
        }
    }


    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (url == null)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.LOADING_START);
            mFFmpegPlayer.setDataSource(context, Uri.parse(url), null);
            mFFmpegPlayer.prepare();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => startDecoder => " + e.getMessage());
            stop();
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR);
        }
    }

    @Override
    public void initOptions(Context context, StartArgs args) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            boolean mute = isMute();
            setVolume(mute ? 0L : 1L, mute ? 0L : 1L);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => initOptions => Exception " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            mFFmpegPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mFFmpegPlayer.setOnErrorListener(onErrorListener);
            mFFmpegPlayer.setOnCompletionListener(onCompletionListener);
            mFFmpegPlayer.setOnInfoListener(onInfoListener);
            mFFmpegPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mFFmpegPlayer.setOnPreparedListener(onPreparedListener);
            mFFmpegPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mFFmpegPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
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
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => unRegistListener => Exception " + e.getMessage());
        }
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
            mFFmpegPlayer.setSurface(null);
            mFFmpegPlayer.release();
            mFFmpegPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => start => " + e.getMessage());
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
            LogUtil.log("VideoFFmpegPlayer => start => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.pause();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => pause => " + e.getMessage());
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        clear();
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            mFFmpegPlayer.stop();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => stop => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            return mFFmpegPlayer.isPlaying();
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => stop => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("error: mFFmpegPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0 && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            onEvent(PlayerType.KernelType.FFPLAYER, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);
            mFFmpegPlayer.seekTo((int) seek);
            LogUtil.log("VideoFFmpegPlayer => seekTo =>");
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => seekTo => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            long currentPosition = mFFmpegPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => getPosition => " + e.getMessage());
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
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
            int duration = mFFmpegPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoFFmpegPlayer => getDuration => " + e.getMessage());
            return 0L;
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
            LogUtil.log("VideoFFmpegPlayer => setSurface => " + e.getMessage());
        }
    }

    @Override
    public boolean setSpeed(float speed) {
        try {
            if (null == mFFmpegPlayer)
                throw new Exception("mFFmpegPlayer error: null");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//                throw new Exception("only support above Android M");
            return false;
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(FFmpegPlayer mp, int what, int extra) {
            LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what);
            // 缓冲开始
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                if (isPrepared()) {
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.BUFFERING_START);
                } else {
                    LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", mPrepared = false");
                }
            }
            // 缓冲结束
            else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                if (isPrepared()) {
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.BUFFERING_STOP);
                } else {
                    LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", mPrepared = false");
                }
            }
            // 开始播放
            else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                try {
                    if (isPrepared())
                        throw new Exception("warning: mPrepared true");
                    setPrepared(true);
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.LOADING_STOP);
                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.RENDER_FIRST_FRAME);
                    long seek = getSeek();
                    if (seek <= 0) {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                        // 立即播放
                        boolean playWhenReady = isPlayWhenReady();
                        onEvent(PlayerType.KernelType.FFPLAYER, playWhenReady ? PlayerType.EventType.PLAY_WHEN_READY_TRUE : PlayerType.EventType.PLAY_WHEN_READY_FALSE);
                    } else {
                        onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.SEEK_START_FORWARD);
                        // 起播快进
                        setDoSeeking(true);
                        seekTo(seek);
                    }
                } catch (Exception e) {
                    LogUtil.log("VideoFFmpegPlayer => onInfo => what = " + what + ", msg = " + e.getMessage());
                }
            }
            return true;
        }
    };

    private OnSeekCompleteListener mOnSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(FFmpegPlayer mediaPlayer) {
            LogUtil.log("VideoFFmpegPlayer => onSeekComplete =>");
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.SEEK_FINISH);

            try {
                long seek = getSeek();
                if (seek <= 0L)
                    throw new Exception("warning: seek<=0");
                boolean doSeeking = isDoSeeking();
                if (!doSeeking)
                    throw new Exception("warning: doSeeking false");
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                // 立即播放
                boolean playWhenReady = isPlayWhenReady();
                onEvent(PlayerType.KernelType.FFPLAYER, playWhenReady ? PlayerType.EventType.PLAY_WHEN_READY_TRUE : PlayerType.EventType.PLAY_WHEN_READY_FALSE);
            } catch (Exception e) {
                LogUtil.log("VideoFFmpegPlayer => onSeekComplete => Exception1 " + e.getMessage());
            }

            try {
                boolean playing = isPlaying();
                if (playing)
                    throw new Exception("warning: playing true");
                start();
            } catch (Exception e) {
                LogUtil.log("VideoFFmpegPlayer => onSeekComplete => Exception2 " + e.getMessage());
            }
        }
    };

    private OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(FFmpegPlayer mp) {
            LogUtil.log("VideoFFmpegPlayer => onPrepared =>");
            start();
        }
    };

    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(FFmpegPlayer mp, int percent) {
            LogUtil.log("VideoFFmpegPlayer => onBufferingUpdate => percent = " + percent);
        }
    };

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(FFmpegPlayer mp, int what, int extra) {
            LogUtil.log("VideoFFmpegPlayer => onError => what = " + what);
            // ignore -38
            if (what == -38) {

            }
            // error
            else {
                stop();
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.STOP);
                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR);
            }
            return true;
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(FFmpegPlayer mp) {
            LogUtil.log("VideoFFmpegPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.COMPLETE);
        }
    };

    private OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(FFmpegPlayer mp, int width, int height) {
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
                onUpdateSizeChanged(PlayerType.KernelType.FFPLAYER, videoWidth, videoHeight, rotation, scaleType);
            } catch (Exception e) {
                LogUtil.log("VideoFFmpegPlayer => onVideoSizeChanged => " + e.getMessage());
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
            LogUtil.log("VideoFFmpegPlayer => setVolume => " + e.getMessage());
        }
    }

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
                            onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.RESUME);
                            start();
                        } catch (Exception e) {
                            LogUtil.log("VideoFFmpegPlayer => startPlayWhenReadyDelayedTime => Exception2 " + e.getMessage());
                        } finally {
                            stopCheckPreparedPlaying();
                        }
                    }
                };
            }
            mHandlerPlayWhenReadyDelayedTime.removeCallbacksAndMessages(null);
            mHandlerPlayWhenReadyDelayedTime.sendEmptyMessageDelayed(10008, delayedTime);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => startPlayWhenReadyDelayedTime => Exception1 " + e.getMessage());
        }
    }

    @Override
    public void stopPlayWhenReadyDelayedTime() {
        try {
            if (null == mHandlerPlayWhenReadyDelayedTime)
                throw new Exception("warning: mHandlerPlayWhenReadyDelayedTime null");
            mHandlerPlayWhenReadyDelayedTime.removeCallbacksAndMessages(null);
            mHandlerPlayWhenReadyDelayedTime = null;
            LogUtil.log("VideoFFmpegPlayer => playWhenReadyDelayedTime => stop mHandlerPlayWhenReadyDelayedTime");
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => playWhenReadyDelayedTime => " + e.getMessage());
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
                                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.LOADING_STOP);
                                long seek = getSeek();
                                if (seek <= 0) {
                                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                                } else {
                                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.START);
                                    // 起播快进
                                    onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.SEEK_START_FORWARD);
                                    setDoSeeking(true);
                                    seekTo(seek);
                                }
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoFFmpegPlayer => startCheckPreparedPlaying => loop next");
                                mHandlerPreparedPlaying.sendEmptyMessageDelayed(10001, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoFFmpegPlayer => startCheckPreparedPlaying => Exception2 " + e.getMessage());
                            stopCheckPreparedPlaying();
                        }
                    }
                };
            }
            mHandlerPreparedPlaying.removeCallbacksAndMessages(null);
            mHandlerPreparedPlaying.sendEmptyMessageDelayed(10001, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => startCheckPreparedPlaying => Exception1 " + e.getMessage());
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
            LogUtil.log("VideoFFmpegPlayer => stopCheckPreparedPlaying => stop mHandlerPreparedPlaying");
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => stopCheckPreparedPlaying => " + e.getMessage());
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
                                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.LOADING_STOP);
                                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR);
                                getPlayerApi().stop(true, false);
                                throw new Exception("warning: connect timeout");
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoFFmpegPlayer => startCheckConnectTimeout => loop next, cast = " + cast);
                                Message message = Message.obtain();
                                message.what = 10002;
                                message.obj = start;
                                mHandlerConnectTimeout.sendMessageDelayed(message, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoFFmpegPlayer => startCheckConnectTimeout => Exception2 " + e.getMessage());
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
            LogUtil.log("VideoFFmpegPlayer => startCheckConnectTimeout => Exception1 " + e.getMessage());
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
            LogUtil.log("VideoFFmpegPlayer => stopCheckConnectTimeout => stop mHandlerConnectTimeout");
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => stopCheckConnectTimeout => " + e.getMessage());
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
                                onEvent(PlayerType.KernelType.FFPLAYER, PlayerType.EventType.ERROR_BUFFERING_TIMEOUT);
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
                                LogUtil.log("VideoFFmpegPlayer => startCheckBufferingTimeout => loop next, cast = " + cast);
                                Message message = Message.obtain();
                                message.what = 10003;
                                message.obj = start;
                                mHandlerBufferingTimeout.sendMessageDelayed(message, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoFFmpegPlayer => startCheckBufferingTimeout => " + e.getMessage());
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
            LogUtil.log("VideoFFmpegPlayer => startCheckBufferingTimeout => " + e.getMessage());
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
            LogUtil.log("VideoFFmpegPlayer => stopCheckBufferingTimeout => stop mHandlerBufferingTimeout");
        } catch (Exception e) {
            LogUtil.log("VideoFFmpegPlayer => stopCheckBufferingTimeout => " + e.getMessage());
        }
    }
}
