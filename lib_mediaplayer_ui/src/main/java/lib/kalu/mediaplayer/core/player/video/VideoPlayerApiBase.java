package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

public interface VideoPlayerApiBase {

    default StartArgs getStartArgs() {
        try {
            Object args = ((View) this).getTag(R.id.module_mediaplayer_id_startargs);
            if (null == args)
                throw new Exception("warning: args null");
            return (StartArgs) args;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => getStartArgs => " + e.getMessage());
            return null;
        }
    }

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
            View focus = decorView.findFocus();
            if (null == focus)
                throw new Exception("error: focus null");
            int focusId = focus.getId();
            if (focusId != R.id.module_mediaplayer_id_player)
                throw new Exception("error: focusId != R.id.module_mediaplayer_id_player");
//            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
//            if (null == viewRoot)
//                throw new Exception("viewRoot error: null");
//            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
//            if (null == tag)
//                throw new Exception("warning: tagId null");
//            int id = ((View) viewRoot.getParent()).getId();
//            if (id == (int) tag)
//                throw new Exception("warning: current not full");
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
            View rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (null == rootView)
                throw new Exception("error: rootView null");
            ViewParent parentView = rootView.getParent();
            if (null == parentView)
                throw new Exception("error: parentView null");
            if (parentView instanceof PlayerLayout)
                throw new Exception("warning: parentView is PlayerLayout");
            ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            return width != ViewGroup.LayoutParams.MATCH_PARENT && height != ViewGroup.LayoutParams.MATCH_PARENT;
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

    VideoKernelApi getVideoKernel();

    void setVideoKernel(VideoKernelApi kernel);

    void start(StartArgs builder);
}
