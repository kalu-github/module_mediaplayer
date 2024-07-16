package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

interface VideoPlayerApiBase {

    default PlayerLayout getPlayerLayout() {
        try {
            return (PlayerLayout) ((View) this).getParent();
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => getPlayerLayout => " + e.getMessage());
            return null;
        }
    }

    default ViewGroup findDecorView(View view) {
        try {
            View parent = (View) view.getParent();
            if (null == parent) {
                return (ViewGroup) view;
            } else {
                return findDecorView(parent);
            }
        } catch (Exception e) {
            return (ViewGroup) view;
        }
    }

    default Context getBaseContext() {
        return ((View) this).getContext().getApplicationContext();
    }

    default ViewGroup getBaseViewGroup() {
        return (ViewGroup) this;
    }

    default boolean isFull() {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
            if (null == viewRoot)
                throw new Exception("viewRoot error: null");
            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
            if (null == tag)
                throw new Exception("warning: tagId null");
            int id = ((View) viewRoot.getParent()).getId();
            if (id == (int) tag)
                throw new Exception("warning: current not full");
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => isFull => " + e.getMessage());
            return false;
        }
    }

    default boolean isFloat() {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
            if (null == viewRoot)
                throw new Exception("viewRoot error: null");
            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
            if (null == tag)
                throw new Exception("warning: tagId null");
            int id = ((View) viewRoot.getParent()).getId();
            if (id == (int) tag)
                throw new Exception("warning: current not float");
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => isFloat => " + e.getMessage());
            return false;
        }
    }

    default ViewGroup getBaseVideoViewGroup() {
        try {
            ViewGroup playerGroup = getBaseViewGroup();
            return playerGroup.findViewById(R.id.module_mediaplayer_video);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => getBaseVideoGroup => " + e.getMessage());
            return null;
        }
    }

    default ViewGroup getBaseComponentViewGroup() {

        try {
            ViewGroup playerGroup = getBaseViewGroup();
            return playerGroup.findViewById(R.id.module_mediaplayer_component);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => getBaseComponentViewGroup => " + e.getMessage());
            return null;
        }
    }

    default boolean isIjkUseMediaCodec() {
        try {
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: videoKernel null");
            return videoKernel.isIjkUseMediaCodec();
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => isIjkUseMediaCodec => " + e.getMessage());
            return false;
        }
    }

    VideoKernelApi getVideoKernel();

    void setVideoKernel(VideoKernelApi kernel);

    void start(String url);

    void start(StartArgs builder);
}
