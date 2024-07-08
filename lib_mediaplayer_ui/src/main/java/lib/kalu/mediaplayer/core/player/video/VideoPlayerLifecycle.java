package lib.kalu.mediaplayer.core.player.video;

import android.view.View;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerLifecycle extends VideoPlayerApiBase, VideoPlayerApiKernel {

    default void detachedFromWindow() {
        try {
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            boolean doWindowing = videoKernel.isDoWindowing();
            if (doWindowing)
                throw new Exception("warning: doWindowing true");
            StartArgs tags = getTags();
            if (null == tags)
                throw new Exception("warning: tags null");
            boolean supportViewLifecycle = tags.isSupportViewLifecycle();
            if (!supportViewLifecycle)
                throw new Exception("warning: supportViewLifecycle false");
            String mediaUrl = tags.getUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            stop(true, false);
            release(true, true, false);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerLifecycle => detachedFromWindow => " + e.getMessage());
        }
    }

    default void attachedToWindow() {
        try {
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            boolean doWindowing = videoKernel.isDoWindowing();
            if (doWindowing)
                throw new Exception("warning: doWindowing true");
            StartArgs tags = getTags();
            if (null == tags)
                throw new Exception("warning: tags null");
            boolean supportViewLifecycle = tags.isSupportViewLifecycle();
            if (!supportViewLifecycle)
                throw new Exception("warning: supportViewLifecycle false");
            String mediaUrl = tags.getUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
//            resume(false);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerLifecycle => attachedToWindow => " + e.getMessage());
        }
    }

    default void windowVisibilityChanged(int visibility) {
        try {
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            boolean doWindowing = videoKernel.isDoWindowing();
            if (doWindowing)
                throw new Exception("warning: doWindowing true");
            StartArgs tags = getTags();
            if (null == tags)
                throw new Exception("warning: tags null");
            boolean supportViewLifecycle = tags.isSupportViewLifecycle();
            if (!supportViewLifecycle)
                throw new Exception("warning: supportViewLifecycle false");
            // show
            if (visibility == View.VISIBLE) {
                resume(false);
            }
            // hide
            else {
                pause(false);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerLifecycle => windowVisibilityChanged => " + e.getMessage());
        }
    }
}
