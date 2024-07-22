package lib.kalu.mediaplayer.core.kernel.video;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.HashMap;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
public interface VideoKernelApiHandler extends VideoKernelApiBase, VideoKernelApiEvent, VideoKernelApiStartArgs {

    default void clearHandler() {
        stopPlayWhenReadyDelayedTime();
        stopCheckBufferingTimeout();
        stopCheckPreparedPlaying();
        stopCheckConnectTimeout();
    }

    /***********/

    HashMap<VideoKernelApiBase, android.os.Handler> mHandlerPlayWhenReadyDelayedTime = new HashMap<>();

    default void startPlayWhenReadyDelayedTime(@PlayerType.KernelType.Value int kernelType, long delayedTime) {
        try {
            if (!isPrepared())
                throw new Exception("warning: isPrepared false");
            Handler handler = mHandlerPlayWhenReadyDelayedTime.get(this);
            if (null == handler) {
                handler = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        try {
                            onEvent(kernelType, PlayerType.EventType.PLAY_WHEN_READY_DELAYED_TIME_COMPLETE);
                            onEvent(kernelType, PlayerType.EventType.RESUME);
                            start();
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiHandler => startPlayWhenReadyDelayedTime => Exception2 " + e.getMessage());
                        } finally {
                            stopCheckPreparedPlaying();
                        }
                    }
                };
                mHandlerPlayWhenReadyDelayedTime.put(this, handler);
            }
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessageDelayed(10008, delayedTime);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => startPlayWhenReadyDelayedTime => Exception1 " + e.getMessage());
        }
    }

    default void stopPlayWhenReadyDelayedTime() {
        try {
            Handler handler = mHandlerPlayWhenReadyDelayedTime.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeCallbacksAndMessages(null);
            mHandlerPlayWhenReadyDelayedTime.remove(this);
            LogUtil.log("VideoKernelApiHandler => playWhenReadyDelayedTime => stop mHandlerPlayWhenReadyDelayedTime");
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => playWhenReadyDelayedTime => " + e.getMessage());
        }
    }

    /***********/

    HashMap<VideoKernelApiBase, android.os.Handler> mHandlerPreparedPlaying = new HashMap<>();

    default void startCheckPreparedPlaying(@PlayerType.KernelType.Value int kernelType) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPrepared true");
            if (!isPrepared())
                throw new Exception("warning: isPrepared false");
            Handler handler = mHandlerPreparedPlaying.get(this);
            if (null == handler) {
                handler = new android.os.Handler(Looper.getMainLooper()) {
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
                                onEvent(kernelType, PlayerType.EventType.LOADING_STOP);
                                long seek = getPlayWhenReadySeekToPosition();
                                if (seek <= 0) {
                                    onEvent(kernelType, PlayerType.EventType.START);
                                } else {
                                    onEvent(kernelType, PlayerType.EventType.START);
                                    // 起播快进
                                    onEvent(kernelType, PlayerType.EventType.SEEK_START_FORWARD);
                                    setPlayWhenReadySeekFinish(true);
                                    seekTo(seek);
                                }
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => loop next");
                                sendEmptyMessageDelayed(10001, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => Exception2 " + e.getMessage());
                            stopCheckPreparedPlaying();
                        }
                    }
                };
                mHandlerPreparedPlaying.put(this, handler);
            }
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessageDelayed(10001, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => Exception1 " + e.getMessage());
            stopCheckPreparedPlaying();
        }
    }

    default void stopCheckPreparedPlaying() {
        try {
            Handler handler = mHandlerPreparedPlaying.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeCallbacksAndMessages(null);
            mHandlerPreparedPlaying.remove(this);
            LogUtil.log("VideoKernelApiHandler => stopCheckPreparedPlaying => stop mHandlerPreparedPlaying");
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => stopCheckPreparedPlaying => " + e.getMessage());
        }
    }

    /***********/

    HashMap<VideoKernelApiBase, android.os.Handler> mHandlerConnectTimeout = new HashMap<>();

    default void startCheckConnectTimeout(@PlayerType.KernelType.Value int kernelType, long timeout) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPrepared true");
            Handler handler = mHandlerConnectTimeout.get(this);
            if (null == handler) {
                handler = new android.os.Handler(Looper.getMainLooper()) {
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
                                onEvent(kernelType, PlayerType.EventType.LOADING_STOP);
                                onEvent(kernelType, PlayerType.EventType.ERROR);
                                getPlayerApi().stop(true, false);
                                throw new Exception("warning: connect timeout");
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoKernelApiHandler => startCheckConnectTimeout => loop next, cast = " + cast);
                                Message message = Message.obtain();
                                message.what = 10002;
                                message.obj = start;
                                sendMessageDelayed(message, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiHandler => startCheckConnectTimeout => Exception2 " + e.getMessage());
                            stopCheckConnectTimeout();
                        }
                    }
                };
                mHandlerConnectTimeout.put(this, handler);
            }
            handler.removeCallbacksAndMessages(null);
            Message message = Message.obtain();
            message.what = 10002;
            message.obj = System.currentTimeMillis();
            handler.sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => startCheckConnectTimeout => Exception1 " + e.getMessage());
            stopCheckConnectTimeout();
        }
    }

    default void stopCheckConnectTimeout() {
        try {
            Handler handler = mHandlerConnectTimeout.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeCallbacksAndMessages(null);
            mHandlerConnectTimeout.remove(this);
            LogUtil.log("VideoKernelApiHandler => stopCheckConnectTimeout => stop mHandlerConnectTimeout");
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => stopCheckConnectTimeout => " + e.getMessage());
        }
    }

    /***********/

    HashMap<VideoKernelApiBase, android.os.Handler> mHandlerBufferingTimeout = new HashMap<>();

    default void startCheckBufferingTimeout(@PlayerType.KernelType.Value int kernelType, boolean bufferingTimeoutRetry, long timeout) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPlaying true");
            Handler handler = mHandlerBufferingTimeout.get(this);
            if (null == handler) {
                handler = new android.os.Handler(Looper.getMainLooper()) {
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
                                onEvent(kernelType, PlayerType.EventType.ERROR_BUFFERING_TIMEOUT);
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
                                LogUtil.log("VideoKernelApiHandler => startCheckBufferingTimeout => loop next, cast = " + cast);
                                Message message = Message.obtain();
                                message.what = 10003;
                                message.obj = start;
                                sendMessageDelayed(message, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiHandler => startCheckBufferingTimeout => " + e.getMessage());
                            stopCheckBufferingTimeout();
                        }
                    }
                };
                mHandlerBufferingTimeout.put(this, handler);
            }
            handler.removeCallbacksAndMessages(null);
            Message message = Message.obtain();
            message.what = 10003;
            message.obj = System.currentTimeMillis();
            handler.sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => startCheckBufferingTimeout => " + e.getMessage());
            stopCheckBufferingTimeout();
        }
    }

    default void stopCheckBufferingTimeout() {
        try {
            Handler handler = mHandlerBufferingTimeout.get(this);
            if (null == handler)
                throw new Exception("warning: handler null");
            handler.removeCallbacksAndMessages(null);
            mHandlerBufferingTimeout.remove(this);
            LogUtil.log("VideoKernelApiHandler => stopCheckBufferingTimeout => stop mHandlerBufferingTimeout");
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => stopCheckBufferingTimeout => " + e.getMessage());
        }
    }
}