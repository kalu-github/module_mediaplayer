package lib.kalu.mediaplayer.core.render;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoRenderApiHanlder extends VideoRenderApiBase {

    Handler[] mHandlerUpdateProgress = new Handler[1];

    default void startUpdateProgress() {
        try {
            if (null == mHandlerUpdateProgress[0]) {
                mHandlerUpdateProgress[0] = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 10100) {
                            VideoKernelApi kernelApi = getKernel();
                            if (null != kernelApi) {
                                kernelApi.onUpdateProgress();
                            }
//                            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress => loop next kernelApi = " + kernelApi);
                            mHandlerUpdateProgress[0].sendEmptyMessageDelayed(10100, 1000);
                        }
                    }
                };
            }
            if (mHandlerUpdateProgress[0].hasMessages(10100))
                throw new Exception("warning: hasMessages 10100");
            mHandlerUpdateProgress[0].sendEmptyMessageDelayed(10100, 1000);
        } catch (Exception e) {
            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress => Exception " + e.getMessage());
            stopUpdateProgress();
        }
    }

    default void stopUpdateProgress() {
        try {
            if (null != mHandlerUpdateProgress[0]) {
                mHandlerUpdateProgress[0].removeMessages(10100);
                mHandlerUpdateProgress[0].removeCallbacksAndMessages(null);
                mHandlerUpdateProgress[0] = null;
                LogUtil.log("VideoRenderApiHanlder => stopUpdateProgress => stop mHandlerUpdateProgress");
            }
        } catch (Exception e) {
            LogUtil.log("VideoRenderApiHanlder => stopUpdateProgress => Exception" + e.getMessage());
        }
    }
}