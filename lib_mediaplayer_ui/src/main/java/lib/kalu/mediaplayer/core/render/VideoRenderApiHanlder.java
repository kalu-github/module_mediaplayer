package lib.kalu.mediaplayer.core.render;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashMap;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoRenderApiHanlder extends VideoRenderApiBase {

    HashMap<VideoRenderApiBase, Handler> mHandlerUpdateProgress = new HashMap<>();

    default void startUpdateProgress() {
        try {
            Handler handler = mHandlerUpdateProgress.get(this);
            if (null == handler) {
                handler = new Handler(Looper.getMainLooper()) {
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
                            sendEmptyMessageDelayed(10100, 1000);
                        } catch (Exception e) {
                            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress => handleMessage => Exception" + e.getMessage());
                        }
                    }
                };
                mHandlerUpdateProgress.put(this, handler);
            }
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessageDelayed(10100, 1000);
            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress =>");
        } catch (Exception e) {
            LogUtil.log("VideoRenderApiHanlder => startUpdateProgress => Exception " + e.getMessage());
            stopUpdateProgress();
        }
    }

    default void stopUpdateProgress() {
        try {
            Handler handler = mHandlerUpdateProgress.get(this);
            if (null == handler)
                throw new Exception("error: handler null");
            handler.removeCallbacksAndMessages(null);
            mHandlerUpdateProgress.remove(this);
            LogUtil.log("VideoRenderApiHanlder => stopUpdateProgress =>");
        } catch (Exception e) {
            LogUtil.log("VideoRenderApiHanlder => stopUpdateProgress => Exception" + e.getMessage());
        }
    }
}