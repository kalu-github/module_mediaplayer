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

public interface VideoKernelApiCheck extends VideoKernelApiBase, VideoKernelApiEvent {

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
                        if (msg.what == 10001) {
                            long position = getPosition();
                            LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => position = " + position);
                            if (position > 0) {
                                // 1
                                stopCheckPreparedPlaying();
                                // 2
                                setPrepared(true);
                                onEvent(kernelType, PlayerType.EventType.EVENT_LOADING_STOP);
                                long seek = getSeek();
                                if (seek <= 0) {
                                    onEvent(kernelType, PlayerType.EventType.EVENT_VIDEO_START);
                                } else {
                                    onEvent(kernelType, PlayerType.EventType.EVENT_VIDEO_RENDERING_START);
                                    // 起播快进
                                    onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_SEEK_PLAY_RECORD);
                                    seekTo(seek);
                                }
                            } else {
                                mHandlerPreparedPlaying[0].sendEmptyMessageDelayed(10001, 100);
                            }
                        }
                    }
                };
            }
            mHandlerPreparedPlaying[0].removeCallbacksAndMessages(null);
            mHandlerPreparedPlaying[0].sendEmptyMessageDelayed(10001, 100);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiCheck => startCheckPreparedPlaying => " + e.getMessage());
        }
    }

    default void stopCheckPreparedPlaying() {
        try {
            if (null != mHandlerPreparedPlaying[0]) {
                mHandlerPreparedPlaying[0].removeCallbacksAndMessages(null);
                mHandlerPreparedPlaying[0] = null;
            }
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiCheck => stopCheckPreparedPlaying => " + e.getMessage());
        }
    }

    /***********/

    android.os.Handler[] mHandlerConnectTimeout = new android.os.Handler[]{null};

    default void startCheckConnectTimeout(@PlayerType.KernelType.Value int kernelType, long timeout) {
        try {
            if (isPrepared())
                throw new Exception("warning: isPrepared true");
            long startMillis = System.currentTimeMillis();
            if (null == mHandlerConnectTimeout[0]) {
                mHandlerConnectTimeout[0] = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        try {
                            if (msg.what != 10002)
                                throw new Exception("warning: msg.what != 10002");
                            boolean prepared = isPrepared();
                            if (prepared) {
                                stopCheckConnectTimeout();
                            } else {
                                long currentMillis = System.currentTimeMillis();
                                if (currentMillis - startMillis >= timeout) {
                                    // 1
                                    stopCheckConnectTimeout();
                                    // 2
                                    onEvent(kernelType, PlayerType.EventType.EVENT_LOADING_STOP);
                                    onEvent(kernelType, PlayerType.EventType.EVENT_ERROR_NET);
                                    getPlayerApi().stop(true, false);
                                } else {
                                    mHandlerConnectTimeout[0].sendEmptyMessageDelayed(10002, 1000);
                                }
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiCheck => startCheckConnectTimeout => " + e.getMessage());
                        }
                    }
                };
            }
            mHandlerConnectTimeout[0].removeCallbacksAndMessages(null);
            mHandlerConnectTimeout[0].sendEmptyMessageDelayed(10002, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiCheck => startCheckConnectTimeout => " + e.getMessage());
        }
    }

    default void stopCheckConnectTimeout() {
        try {
            if (null != mHandlerConnectTimeout[0]) {
                mHandlerConnectTimeout[0].removeCallbacksAndMessages(null);
                mHandlerConnectTimeout[0] = null;
            }
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiCheck => stopCheckConnectTimeout => " + e.getMessage());
        }
    }

    /***********/

    android.os.Handler[] mHandlerBufferingTimeout = new android.os.Handler[1];

    default void startCheckBufferingTimeout(@PlayerType.KernelType.Value int kernelType, boolean bufferingTimeoutRetry, long timeout) {
        try {
            if (isPlaying())
                throw new Exception("warning: isPlaying true");
            long startMillis = System.currentTimeMillis();
            if (null == mHandlerBufferingTimeout[0]) {
                mHandlerBufferingTimeout[0] = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {

                        try {
                            if (msg.what != 10003)
                                throw new Exception("warning: msg.what != 10003");
                            if (isPlaying())
                                throw new Exception("warning: isPlaying true");

                            long currentMillis = System.currentTimeMillis();
                            if (currentMillis - startMillis >= timeout) {
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
                                    long position = getPosition();
                                    long max = Math.max(position, seek);
                                    getPlayerApi().restart(max, true);
                                }
                            } else {
                                mHandlerBufferingTimeout[0].sendEmptyMessageDelayed(10003, 1000);
                            }
                        } catch (Exception e) {
                            LogUtil.log("VideoKernelApiCheck => startCheckBufferingTimeout => " + e.getMessage());
                        }
                    }
                };
            }
            mHandlerBufferingTimeout[0].removeCallbacksAndMessages(null);
            mHandlerBufferingTimeout[0].sendEmptyMessageDelayed(10003, timeout);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiCheck => startCheckBufferingTimeout => " + e.getMessage());
        }
    }

    default void stopCheckBufferingTimeout() {
        try {
            if (null != mHandlerBufferingTimeout[0]) {
                mHandlerBufferingTimeout[0].removeCallbacksAndMessages(null);
                mHandlerBufferingTimeout[0] = null;
            }
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiCheck => stopCheckBufferingTimeout => " + e.getMessage());
        }
    }

/***********/
}