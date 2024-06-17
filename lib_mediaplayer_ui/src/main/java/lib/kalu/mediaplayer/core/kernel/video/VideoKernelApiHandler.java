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

    android.os.Handler[] mHandlerOpenUrl = new android.os.Handler[1];

    default void startCheckOpenUrlTimeout(boolean reset, long start, long timeout) {
        stopCheckOpenUrlTimeout();
        if (null == mHandlerOpenUrl[0]) {
            mHandlerOpenUrl[0] = new android.os.Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 1234) {
                        try {
                            boolean prepared = isPrepared();
                            if (prepared) {
                                stopCheckOpenUrlTimeout();
                            } else {
                                long current = System.currentTimeMillis();
                                long cast = current - start;
                                if (cast >= timeout)
                                    throw new Exception("warning: cast >= timeout");
                                mHandlerOpenUrl[0].sendEmptyMessageDelayed(1234, 1000);
                            }
                        } catch (Exception e) {
                            mHandlerOpenUrl[0].removeCallbacksAndMessages(null);
                            mHandlerOpenUrl[0] = null;
                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
                            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_NET);
                            getPlayerApi().stop(false);
                            if (reset) {
                                getPlayerApi().release();
                            }
                        }
                    }
                }
            };
        }
        mHandlerOpenUrl[0].sendEmptyMessageDelayed(1234, 1000);
    }

    default void stopCheckOpenUrlTimeout() {
        if (null != mHandlerOpenUrl[0]) {
            mHandlerOpenUrl[0].removeCallbacksAndMessages(null);
            mHandlerOpenUrl[0] = null;
        }
    }

    /***********/

    android.os.Handler[] mHandlerLoadBuffering = new android.os.Handler[1];

    default void startCheckLoadBufferingTimeout(boolean reset, boolean retry, long timeout) {
        stopCheckLoadBufferingTimeout();
        if (null == mHandlerLoadBuffering[0]) {
            mHandlerLoadBuffering[0] = new android.os.Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 4321) {
                        try {
                            if (!retry)
                                throw new Exception("warning: retry false");
                            long seek;
                            try {
                                if (isLive()) {   // 直播
                                    seek = 0;
                                } else {  // 点播
                                    seek = getPosition();
                                }
                            } catch (Exception e) {
                                seek = 0;
                            }
                            if (seek < 0) {
                                seek = 0;
                            }

                            getPlayerApi().stop(false);
                            if (reset) {
                                getPlayerApi().release();
                            }

                            getPlayerApi().restart(seek, true, true);
                            getPlayerApi().callEventListener(PlayerType.StateType.STATE_BUFFERING_START);
                            mHandlerLoadBuffering[0].sendEmptyMessageDelayed(4321, timeout);
                        } catch (Exception e) {
                            mHandlerLoadBuffering[0].removeCallbacksAndMessages(null);
                            mHandlerLoadBuffering[0] = null;
//            callEventListener(PlayerType.StateType.STATE_BUFFERING_STOP);
//            callEventListener(PlayerType.StateType.STATE_BUFFERING_TIMEOUT);
                            getPlayerApi().stop(false);
                            if (reset) {
                                getPlayerApi().release();
                            }
                        }
                    }
                }
            };
        }
        mHandlerLoadBuffering[0].sendEmptyMessageDelayed(4321, timeout);
    }

    default void stopCheckLoadBufferingTimeout() {
        if (null != mHandlerLoadBuffering[0]) {
            mHandlerLoadBuffering[0].removeCallbacksAndMessages(null);
            mHandlerLoadBuffering[0] = null;
        }
    }

    /***********/

    android.os.Handler[] mHandlerPreparedPlaying = new android.os.Handler[1];

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
                                    seekTo(seek);
                                }
                            } else {
                                mHandlerPreparedPlaying[0].sendEmptyMessageDelayed(10001, 100);
                            }
                        }
                    }
                };
            }
            mHandlerPreparedPlaying[0].sendEmptyMessageDelayed(10001, 100);
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => startCheckPreparedPlaying => " + e.getMessage());
        }
    }

    default void stopCheckPreparedPlaying() {
        try {
            if (null != mHandlerPreparedPlaying[0]) {
                mHandlerPreparedPlaying[0].removeCallbacksAndMessages(null);
                mHandlerPreparedPlaying[0] = null;
            }
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiHandler => stopCheckPreparedPlaying => " + e.getMessage());
        }
    }

    /***********/
}