package lib.kalu.mediaplayer.core.kernel.video;

import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

public interface VideoKernelApiHandler extends VideoKernelApiBase, VideoKernelApiEvent {

    /***********/

    android.os.Handler[] mHandlerPreparedPlaying = new android.os.Handler[]{null};

    default void startCheckPreparedPlaying(@PlayerType.KernelType.Value int kernelType) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPrepared true");
            if (null == mHandlerPreparedPlaying[0]) {
                mHandlerPreparedPlaying[0] = new android.os.Handler(Looper.getMainLooper()) {
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
                                onEvent(kernelType, PlayerType.EventType.EVENT_LOADING_STOP);
                                long seek = getSeek();
                                if (seek <= 0) {
                                    onEvent(kernelType, PlayerType.EventType.EVENT_VIDEO_START);
                                } else {
                                    onEvent(kernelType, PlayerType.EventType.EVENT_VIDEO_RENDERING_START);
                                    // 起播快进
                                    onEvent(kernelType, PlayerType.EventType.EVENT_SEEK_PLAY_RECORD);
                                    seekTo(seek);
                                }
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => loop next");
                                mHandlerPreparedPlaying[0].sendEmptyMessageDelayed(10001, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => Exception2 " + e.getMessage());
                            stopCheckPreparedPlaying();
                        }
                    }
                };
            }
            if (mHandlerPreparedPlaying[0].hasMessages(10001))
                throw new Exception("warning: hasMessages 10001");
            mHandlerPreparedPlaying[0].sendEmptyMessageDelayed(10001, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => Exception1 " + e.getMessage());
            stopCheckPreparedPlaying();
        }
    }

    default void stopCheckPreparedPlaying() {
        try {
            if (null != mHandlerPreparedPlaying[0]) {
                mHandlerPreparedPlaying[0].removeCallbacksAndMessages(null);
                mHandlerPreparedPlaying[0] = null;
                LogUtil.log("VideoKernelApiHandler => stopCheckPreparedPlaying => stop mHandlerPreparedPlaying");
            }
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => stopCheckPreparedPlaying => " + e.getMessage());
        }
    }

    /***********/

    android.os.Handler[] mHandlerConnectTimeout = new android.os.Handler[]{null};

    default void startCheckConnectTimeout(@PlayerType.KernelType.Value int kernelType, long timeout) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPrepared true");
            if (null == mHandlerConnectTimeout[0]) {
                mHandlerConnectTimeout[0] = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        try {
                            if (msg.what != 10002)
                                throw new Exception("warning: msg.what != 10002");
                            boolean prepared = isPrepared();
                            if (prepared)
                                throw new Exception("warning: prepared true");
                            long start = (long) msg.obj;
                            long current = System.currentTimeMillis();
                            // 超时
                            if (current - start >= timeout) {
                                onEvent(kernelType, PlayerType.EventType.EVENT_LOADING_STOP);
                                onEvent(kernelType, PlayerType.EventType.EVENT_ERROR_NET);
                                getPlayerApi().stop(true, false);
                                throw new Exception("warning: connect timeout");
                            }
                            // 轮询
                            else {
                                LogUtil.log("VideoKernelApiHandler => startCheckConnectTimeout => loop next");
                                Message message = Message.obtain();
                                message.what = 10002;
                                message.obj = System.currentTimeMillis();
                                mHandlerConnectTimeout[0].sendMessageDelayed(message, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiHandler => startCheckConnectTimeout => Exception2 " + e.getMessage());
                            stopCheckConnectTimeout();
                        }
                    }
                };
            }
            if (mHandlerConnectTimeout[0].hasMessages(10002))
                throw new Exception("warning: hasMessages 10002");
            mHandlerConnectTimeout[0].removeCallbacksAndMessages(null);
            Message message = Message.obtain();
            message.what = 10002;
            message.obj = System.currentTimeMillis();
            mHandlerConnectTimeout[0].sendMessageDelayed(message, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => startCheckConnectTimeout => Exception1 " + e.getMessage());
            stopCheckConnectTimeout();
        }
    }

    default void stopCheckConnectTimeout() {
        try {
            if (null != mHandlerConnectTimeout[0]) {
                mHandlerConnectTimeout[0].removeCallbacksAndMessages(null);
                mHandlerConnectTimeout[0] = null;
                LogUtil.log("VideoKernelApiHandler => stopCheckConnectTimeout => stop mHandlerConnectTimeout");
            }
            if (null != mHandlerPreparedPlaying[0]) {
                mHandlerPreparedPlaying[0].removeCallbacksAndMessages(null);
                mHandlerPreparedPlaying[0] = null;
                LogUtil.log("VideoKernelApiHandler => stopCheckConnectTimeout => stop mHandlerPreparedPlaying");
            }
            if (null != mHandlerBufferingTimeout[0]) {
                mHandlerBufferingTimeout[0].removeCallbacksAndMessages(null);
                mHandlerBufferingTimeout[0] = null;
                LogUtil.log("VideoKernelApiHandler => stopCheckConnectTimeout => stop mHandlerBufferingTimeout");
            }
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => stopCheckConnectTimeout => " + e.getMessage());
        }
    }

    /***********/

    android.os.Handler[] mHandlerBufferingTimeout = new android.os.Handler[1];

    default void startCheckBufferingTimeout(@PlayerType.KernelType.Value int kernelType, boolean bufferingTimeoutRetry, long timeout) {
        try {
            if (isPlaying())
                throw new Exception("warning: isPlaying true");
            if (null == mHandlerBufferingTimeout[0]) {
                mHandlerBufferingTimeout[0] = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {

                        try {
                            if (msg.what != 10003)
                                throw new Exception("warning: msg.what != 10003");
                            if (isPlaying())
                                throw new Exception("warning: isPlaying true");
                            long[] objs = (long[]) msg.obj;
                            long position = objs[0];
                            long start = objs[1];
                            long current = System.currentTimeMillis();
                            long newPosition = getPosition();

                            float value = (newPosition - position) / 1000f;

                            // 超时
                            if (value <= 0F && (current - start >= timeout)) {
                                onEvent(kernelType, PlayerType.EventType.EVENT_ERROR_BUFFERING_TIMEOUT);
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
                                    long seek = getSeek();
                                    long max = Math.max(newPosition, seek);
                                    getPlayerApi().restart(max, true);
                                }
                            }
                            // 轮询
                            else if (value <= 0F) {
                                LogUtil.log("VideoKernelApiHandler => startCheckBufferingTimeout => loop next");
                                Message message = Message.obtain();
                                message.what = 10003;
                                message.obj = new long[]{position, System.currentTimeMillis()};
                                mHandlerBufferingTimeout[0].sendMessageDelayed(message, 1000);
                            }
                            // 缓冲开播
                            else {
                                LogUtil.log("VideoKernelApiHandler => startCheckBufferingTimeout => loop complete, current playing");
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiHandler => startCheckBufferingTimeout => " + e.getMessage());
                            stopCheckBufferingTimeout();
                        }
                    }
                };
            }
            if (mHandlerBufferingTimeout[0].hasMessages(10003))
                throw new Exception("warning: hasMessages 10003");
            Message message = Message.obtain();
            message.what = 10003;
            long position = getPosition();
            if (position < 0) {
                position = 0L;
            }
            message.obj = new long[]{position, System.currentTimeMillis()};
            mHandlerBufferingTimeout[0].sendMessageDelayed(message, 1000);
        } catch (
                Exception e) {
            LogUtil.log("VideoKernelApiHandler => startCheckBufferingTimeout => " + e.getMessage());
            stopCheckBufferingTimeout();
        }
    }

    default void stopCheckBufferingTimeout() {
        try {
            if (null != mHandlerBufferingTimeout[0]) {
                mHandlerBufferingTimeout[0].removeCallbacksAndMessages(null);
                mHandlerBufferingTimeout[0] = null;
                LogUtil.log("VideoKernelApiHandler => stopCheckBufferingTimeout => stop mHandlerBufferingTimeout");
            }
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => stopCheckBufferingTimeout => " + e.getMessage());
        }
    }
}