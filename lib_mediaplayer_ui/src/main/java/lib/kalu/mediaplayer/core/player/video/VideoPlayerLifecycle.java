package lib.kalu.mediaplayer.core.player.video;

import android.view.View;

import lib.kalu.mediaplayer.bean.args.StartArgs;
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
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("warning: tags null");
            boolean supportAutoRelease = tags.isSupportAutoRelease();
            if (!supportAutoRelease)
                throw new Exception("warning: supportAutoRelease false");
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
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("warning: tags null");
            boolean supportAutoRelease = tags.isSupportAutoRelease();
            if (!supportAutoRelease)
                throw new Exception("warning: supportAutoRelease false");
            String mediaUrl = tags.getUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            resume(false);
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
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("warning: tags null");
            boolean supportAutoRelease = tags.isSupportAutoRelease();
            if (!supportAutoRelease)
                throw new Exception("warning: supportAutoRelease false");
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
