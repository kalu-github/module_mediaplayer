package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;



import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

interface VideoPlayerApiBase {

    default PlayerLayout getPlayerLayout() {
        try {
            return (PlayerLayout) ((View) this).getParent();
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => getPlayerLayout => " + e.getMessage());
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

    default boolean isParentEqualsPhoneWindow() {
        try {
            ViewGroup playerGroup = getBaseViewGroup();
            if (null == playerGroup)
                throw new Exception("playerGroup error: null");
            ViewGroup rootGroup = (ViewGroup) playerGroup.getParent();
            if (null == rootGroup)
                throw new Exception("rootGroup error: null");
            if (rootGroup instanceof PlayerLayout)
                throw new Exception("rootGroup warning: not phoneWindow");
            Class<?> superclass = rootGroup.getClass().getSuperclass();
            if (superclass.equals(PlayerLayout.class))
                throw new Exception("superclass error: " + superclass);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => isParentEqualsPhoneWindow => " + e.getMessage());
            return false;
        }
    }

    default boolean isFull() {
        try {
            boolean isPhoneWindow = isParentEqualsPhoneWindow();
            if (!isPhoneWindow)
                throw new Exception("isPhoneWindow error: false");
            return true;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => isFull => " + e.getMessage());
            return false;
        }
    }

    default boolean isFloat() {
        try {
            boolean isPhoneWindow = isParentEqualsPhoneWindow();
            if (!isPhoneWindow)
                throw new Exception("isPhoneWindow error: false");
            ViewGroup playerGroup = getBaseViewGroup();
            if (null == playerGroup)
                throw new Exception("playerGroup is null");
            ViewGroup.LayoutParams layoutParams = playerGroup.getLayoutParams();
            if (null == layoutParams)
                throw new Exception("layoutParams is null");
            return layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT || layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => isFloat => " + e.getMessage());
            return false;
        }
    }

    default View removeFromPlayerLayout() {
        try {
            boolean isPhoneWindow = isParentEqualsPhoneWindow();
            if (isPhoneWindow)
                throw new Exception("isPhoneWindow error: true");
            ViewGroup playerGroup = getBaseViewGroup();
            ViewGroup parentGroup = (ViewGroup) playerGroup.getParent();
            parentGroup.removeAllViews();
            playerGroup.setTag(R.id.module_mediaplayer_root, parentGroup);
            return playerGroup;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => removeBasePlayerViewGroupFromParent => " + e.getMessage());
            return null;
        }
    }

    default View removePlayerViewFromDecorView() {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView is null");
            View playerView = null;
            int childCount = decorView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = decorView.getChildAt(i);
                if (null == childAt)
                    continue;
                if (childAt.getId() == R.id.module_mediaplayer_root) {
                    playerView = childAt;
                    break;
                }
            }
            if (null == playerView)
                throw new Exception("not find playerView from decorView");
            decorView.removeView(playerView);
            return playerView;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => removePlayerViewFromDecorView => " + e.getMessage());
            return null;
        }
    }

    default boolean switchToDecorView(boolean isFull) {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView is null");
            // 1
            View playerView = removeFromPlayerLayout();
            if (null == playerView)
                throw new Exception("not find playerView");
            // 2
            int index = decorView.getChildCount();
            if (isFull) {
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                playerView.setLayoutParams(layoutParams);
            } else {
                int width = getBaseContext().getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_float_width);
                int height = width * 9 / 16;
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                playerView.setLayoutParams(layoutParams);
            }
            decorView.addView(playerView, index);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => switchToDecorView => " + e.getMessage());
            return false;
        }
    }

    default boolean switchToPlayerLayout() {
        try {
            View playerView = removePlayerViewFromDecorView();
            if (null == playerView)
                throw new Exception("not find playerView");
            ViewGroup playerGroup = (ViewGroup) playerView.getTag(R.id.module_mediaplayer_root);
            if (null == playerGroup)
                throw new Exception("not find playerGroup");
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            playerView.setLayoutParams(layoutParams);
            playerGroup.setTag(R.id.module_mediaplayer_root, null);
            playerGroup.addView(playerView);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => switchToPlayerLayout => " + e.getMessage());
            return false;
        }
    }

    default ViewGroup getBaseVideoViewGroup() {
        try {
            ViewGroup playerGroup = getBaseViewGroup();
            return playerGroup.findViewById(R.id.module_mediaplayer_video);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => getBaseVideoGroup => " + e.getMessage());
            return null;
        }
    }

    default ViewGroup getBaseControlViewGroup() {

        try {
            ViewGroup playerGroup = getBaseViewGroup();
            return playerGroup.findViewById(R.id.module_mediaplayer_control);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => getBaseControlViewGroup => " + e.getMessage());
            return null;
        }
    }

    default boolean hasPlayerChangeListener() {
        return null != getOnPlayerChangeListener();
    }

    default OnPlayerChangeListener getOnPlayerChangeListener() {
        return null;
    }

    default void removeOnPlayerChangeListener() {
    }

    default void setOnPlayerChangeListener( OnPlayerChangeListener l) {
    }

    default void callPlayerEvent(@PlayerType.StateType.Value int state) {
        // listener
        try {
            boolean hasListener = hasPlayerChangeListener();
            if (!hasListener)
                throw new Exception("not find PlayerChangeListener");
            OnPlayerChangeListener listener = getOnPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onChange(state);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => callPlayerEvent => " + e.getMessage());
        }

        // component
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callPlayerEvent(state);
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => callPlayerEvent => " + e.getMessage());
        }
    }

    default void callUpdateSeekProgress( long position,  long duration,   long max) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateSeekProgress(true, position, duration, max);
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => callUpdateSeekProgress => " + e.getMessage());
        }
    }

    default void callWindowEvent(@PlayerType.WindowType.Value int state) {

        // listener
        try {
            boolean hasListener = hasPlayerChangeListener();
            if (!hasListener)
                throw new Exception("not find PlayerChangeListener");
            OnPlayerChangeListener listener = getOnPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onWindow(state);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => callWindowEvent => " + e.getMessage());
        }

        // component
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callWindowEvent(state);
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => callWindowEvent => " + e.getMessage());
        }
    }

    default void setPlayWhenReady( boolean playWhenReady) {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            kernel.setPlayWhenReady(playWhenReady);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => setPlayWhenReady => " + e.getMessage());
        }
    }

    default boolean isPlayWhenReady() {
        try {
            VideoKernelApi kernel = getVideoKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            return kernel.isPlayWhenReady();
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => isPlayWhenReady => " + e.getMessage());
            return false;
        }
    }

    VideoKernelApi getVideoKernel();

    void setVideoKernel( VideoKernelApi kernel);

    void start( String url);

    void start( StartBuilder builder,  String playUrl);

    void start( StartBuilder builder,  String playUrl,  boolean retryBuffering);
}
