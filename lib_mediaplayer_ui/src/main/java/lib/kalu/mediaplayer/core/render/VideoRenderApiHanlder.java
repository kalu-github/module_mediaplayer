package lib.kalu.mediaplayer.core.render;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoRenderApiHanlder extends VideoRenderApiBase {

    Handler[] mHandlerUpdateProgress = new Handler[]{null};

    default void startUpdateProgress() {

        try {
            if (null == mHandlerUpdateProgress[0]) {
                mHandlerUpdateProgress[0] = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        try {
                            if (msg.what != 10100)
                                throw new Exception("warning: msg.what != 10100");
                            VideoKernelApi videoKernel = getVideoKernel();
                            if (null == videoKernel)
                                throw new Exception("warning: videoKernel null");
                            videoKernel.onUpdateProgress();
                            if (null == mHandlerUpdateProgress)
                                throw new Exception("warning: mHandlerUpdateProgress null");
                            mHandlerUpdateProgress[0].sendEmptyMessageDelayed(10100, 1000);
                        } catch (Exception e) {
                            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress => handleMessage => Exception" + e.getMessage());
                        }
                    }
                };
            }
            mHandlerUpdateProgress[0].removeCallbacksAndMessages(null);
            mHandlerUpdateProgress[0].sendEmptyMessageDelayed(10100, 1000);
            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress =>");
        } catch (Exception e) {
            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress => Exception " + e.getMessage());
            stopUpdateProgress();
        }
    }

    default void stopUpdateProgress() {
        try {
            if (null == mHandlerUpdateProgress[0])
                throw new Exception("error: mHandlerUpdateProgress[0] null");
            mHandlerUpdateProgress[0].removeCallbacksAndMessages(null);
            mHandlerUpdateProgress[0] = null;
            LogUtil.log("VideoRenderApiHanlder => stopUpdateProgress =>");
        } catch (Exception e) {
            LogUtil.log("VideoRenderApiHanlder => stopUpdateProgress => Exception" + e.getMessage());
        }
    }
}