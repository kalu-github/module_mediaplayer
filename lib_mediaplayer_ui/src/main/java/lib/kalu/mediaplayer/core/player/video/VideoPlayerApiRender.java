package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.core.render.VideoRenderFactoryManager;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

interface VideoPlayerApiRender extends VideoPlayerApiBase, VideoPlayerApiListener {

    default String screenshot() {
        try {
            VideoRenderApi render = getVideoRender();
            return render.screenshot(getPlayerLayout().getUrl(), getPlayerLayout().getPosition());
        } catch (Exception e) {
            return null;
        }
    }

    default boolean startFull(@PlayerType.KernelType.Value int kernelType, @PlayerType.RenderType.Value int renderType) {
        try {
            callEvent(PlayerType.StateType.FULL_START);
            boolean full = isFull();
            if (full)
                throw new Exception("warning: full true");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            // 1
            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
            if (null == viewRoot)
                throw new Exception("viewRoot error: null");
            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
            if (null == tag)
                throw new Exception("warning: tagId null");
            // 2
            ((PlayerView) viewRoot).setDoWindowing(true);
            // 2
            ViewGroup viewParent = decorView.findViewById((int) tag);
            viewParent.removeView(viewRoot);
            // 2
            int childCount = decorView.getChildCount();
            decorView.addView(viewRoot, childCount);
            // 3
            viewRoot.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            viewRoot.requestFocus();
            // 4
            initRenderView(kernelType, renderType);
            // 5
            ((PlayerView) viewRoot).setDoWindowing(false);
            callEvent(PlayerType.StateType.FULL_SUCC);
            callWindow(PlayerType.WindowType.FULL);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => startFull => " + e.getMessage());
            callEvent(PlayerType.StateType.FULL_FAIL);
            return false;
        }
    }

    default boolean stopFull(@PlayerType.KernelType.Value int kernelType, @PlayerType.RenderType.Value int renderType) {
        try {
            callEvent(PlayerType.StateType.FULL_START);
            boolean isFull = isFull();
            if (!isFull)
                throw new Exception("not full");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            // 1
            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
            if (null == viewRoot)
                throw new Exception("viewRoot error: null");
            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
            if (null == tag)
                throw new Exception("warning: tagId null");
            // 2
            ((PlayerView) viewRoot).setDoWindowing(true);
            // 2
            decorView.removeView(viewRoot);
            // 2
            ViewGroup viewParent = decorView.findViewById((int) tag);
            viewParent.addView(viewRoot, 0);
            // 3
            viewRoot.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            viewRoot.requestFocus();
            // 4
            initRenderView(kernelType, renderType);
            // 5
            ((PlayerView) viewRoot).setDoWindowing(false);
            callEvent(PlayerType.StateType.FULL_SUCC);
            callWindow(PlayerType.WindowType.NORMAL);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => stopFull => " + e.getMessage());
            callEvent(PlayerType.StateType.FULL_FAIL);
            return false;
        }
    }

    default boolean startFloat(@PlayerType.KernelType.Value int kernelType, @PlayerType.RenderType.Value int renderType) {
        try {
            callEvent(PlayerType.StateType.FLOAT_START);
            boolean full = isFull();
            if (full)
                throw new Exception("warning: full true");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            // 1
            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
            if (null == viewRoot)
                throw new Exception("viewRoot error: null");
            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
            if (null == tag)
                throw new Exception("warning: tagId null");
            // 2
            ((PlayerView) viewRoot).setDoWindowing(true);
            // 2
            ViewGroup viewParent = decorView.findViewById((int) tag);
            viewParent.removeView(viewRoot);
            // 2
            int width = getBaseContext().getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_float_width);
            int height = width * 9 / 16;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewRoot.setLayoutParams(layoutParams);
            // 2
            int childCount = decorView.getChildCount();
            decorView.addView(viewRoot, childCount);
            // 3
            viewRoot.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            viewRoot.requestFocus();
            // 4
            initRenderView(kernelType, renderType);
            // 5
            ((PlayerView) viewRoot).setDoWindowing(false);
            callEvent(PlayerType.StateType.FLOAT_SUCC);
            callWindow(PlayerType.WindowType.FLOAT);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => startFull => " + e.getMessage());
            callEvent(PlayerType.StateType.FLOAT_FAIL);
            return false;
        }
    }

    default boolean stopFloat(@PlayerType.KernelType.Value int kernelType, @PlayerType.RenderType.Value int renderType) {
        try {
            callEvent(PlayerType.StateType.FLOAT_START);
            boolean isFloat = isFloat();
            if (!isFloat)
                throw new Exception("not Float");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            // 1
            ViewGroup viewRoot = decorView.findViewById(R.id.module_mediaplayer_root);
            if (null == viewRoot)
                throw new Exception("viewRoot error: null");
            Object tag = viewRoot.getTag(R.id.module_mediaplayer_root_parent_id);
            if (null == tag)
                throw new Exception("warning: tagId null");
            // 2
            ((PlayerView) viewRoot).setDoWindowing(true);
            // 2
            decorView.removeView(viewRoot);
            // 2
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            viewRoot.setLayoutParams(layoutParams);
            // 2
            ViewGroup viewParent = decorView.findViewById((int) tag);
            viewParent.addView(viewRoot, 0);
            // 3
            viewRoot.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            viewRoot.requestFocus();
            // 4
            initRenderView(kernelType, renderType);
            // 5
            ((PlayerView) viewRoot).setDoWindowing(false);
            callEvent(PlayerType.StateType.FULL_SUCC);
            callWindow(PlayerType.WindowType.NORMAL);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => stopFloat => " + e.getMessage());
            callEvent(PlayerType.StateType.FLOAT_FAIL);
            return false;
        }
    }

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            VideoRenderApi render = getVideoRender();
            if (null == render)
                throw new Exception("render error: null");
            render.setVideoScaleType(scaleType);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => setVideoScaleType => " + e.getMessage());
        }
    }

    @PlayerType.ScaleType.Value
    default int getVideoScaleType() {
        try {
            VideoRenderApi render = getVideoRender();
            if (null == render)
                throw new Exception("render error: null");
            return render.getVideoScaleType();
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => getVideoScaleType => " + e.getMessage());
            return PlayerType.ScaleType.DEFAULT;
        }
    }

    default void setVideoFormat(int width, int height, @PlayerType.RotationType.Value int rotation, @PlayerType.ScaleType.Value int scaleType) {
        try {
            VideoRenderApi render = getVideoRender();
            render.setVideoFormat(width, height, rotation, scaleType);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => setVideoFormat => " + e.getMessage());
        }
    }

    default void setVideoSize(int width, int height) {
        try {
            VideoRenderApi render = getVideoRender();
            render.setVideoSize(width, height);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => setVideoSize => " + e.getMessage());
        }
    }

    default void setVideoRotation(@PlayerType.RotationType.Value int rotation) {
        try {
            if (rotation == -1)
                return;
            VideoRenderApi render = getVideoRender();
            render.setVideoRotation(rotation);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => setVideoRotation => " + e.getMessage());
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     *
     * @param enable
     */
    default void setMirrorRotation(boolean enable) {
        try {
            VideoRenderApi render = getVideoRender();
            render.setScaleX(enable ? -1 : 1);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => setMirrorRotation => " + e.getMessage());
        }
    }

//    default void showVideoView() {
//        try {
//            ViewGroup viewGroup = getBaseVideoViewGroup();
//            viewGroup.setVisibility(View.VISIBLE);
//            int count = viewGroup.getChildCount();
//            for (int i = 0; i < count; i++) {
//                View child = viewGroup.getChildAt(i);
//                child.setVisibility(View.VISIBLE);
//            }
//        } catch (Exception e) {
//            MPLogUtil.log("VideoPlayerApiRender => showVideoView => " + e.getMessage());
//        }
//    }
//
//    default void hideVideoView() {
//        try {
//            ViewGroup viewGroup = getBaseVideoViewGroup();
//            int count = viewGroup.getChildCount();
//            for (int i = 0; i < count; i++) {
//                View child = viewGroup.getChildAt(i);
//                child.setVisibility(View.INVISIBLE);
//            }
//            viewGroup.setVisibility(View.INVISIBLE);
//        } catch (Exception e) {
//            MPLogUtil.log("VideoPlayerApiRender => hideVideoView => " + e.getMessage());
//        }
//    }

    default VideoRenderApi searchVideoRender() {
        try {
            ViewGroup group = getBaseVideoViewGroup();
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = group.getChildAt(i);
                if (null == view)
                    continue;
                if (!(view instanceof VideoRenderApi))
                    continue;
                return (VideoRenderApi) view;
            }
            throw new Exception("not find");
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => searchVideoRender => " + e.getMessage());
            return null;
        }
    }

    default void setVideoRender(@PlayerType.RenderType int v) {
//        try {
//            PlayerSDK.init().setRender(v);
//        } catch (Exception e) {
//        }
    }

    default void addVideoRenderListener() {
        try {
            VideoRenderApi render = searchVideoRender();
            if (null == render)
                throw new Exception("render warning: null");
            render.addListener();
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => addVideoRenderListener => " + e.getMessage());
        }
    }

    VideoRenderApi getVideoRender();

    void setVideoRender(VideoRenderApi render);

//    default void releaseVideoRender() {
//        try {
//            // 1
//            int videoRender = PlayerManager.init().getConfig().getRender();
//            setVideoRender(VideoRenderFactoryManager.createRender(getBaseContext(), videoRender));
//            // 2
//            VideoRenderApi render = getVideoRender();
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
//            render.setLayoutParams(params);
//            ViewGroup viewGroup = getBaseVideoViewGroup();
//            viewGroup.addView((View) render, 0);
//        } catch (Exception e) {
//            MPLogUtil.log("VideoPlayerApiRender => initVideoRender => " + e.getMessage());
//        }
//    }

    default void stopRender() {
        try {
            VideoRenderApi videoRender = getVideoRender();
            videoRender.stopUpdateProgress();
        } catch (Exception e) {
        }
    }

    default void releaseRender() {
        try {
            ViewGroup renderGroup = getBaseVideoViewGroup();
            if (null == renderGroup)
                throw new Exception("warning: null renderGroup");
            int childCount = renderGroup.getChildCount();
            if (childCount == 0)
                throw new Exception("warning: childCount == 0");
            for (int i = 0; i < childCount; i++) {
                View childAt = renderGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof VideoRenderApi))
                    continue;
                ((VideoRenderApi) childAt).release();
            }
            renderGroup.removeAllViews();
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => releaseRender => " + e.getMessage());
        }
    }

    default void checkRenderNull(int renderType, boolean release) {
        try {
            if (release) {
                releaseRender();
            }
            ViewGroup renderGroup = getBaseVideoViewGroup();
            if (null == renderGroup)
                throw new Exception("renderGroup error: null");
            int childCount = renderGroup.getChildCount();
            if (childCount > 0)
                throw new Exception("error: renderGroup childCount > 0");
            VideoRenderApi videoRender = VideoRenderFactoryManager.createRender(getBaseContext(), renderType);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            videoRender.setLayoutParams(layoutParams);
            renderGroup.addView((View) videoRender, 0);
            setVideoRender(videoRender);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => checkRenderNull => " + e.getMessage());
        }
    }

    default void attachRenderKernel() {
        try {
            VideoRenderApi videoRender = getVideoRender();
            if (null == videoRender)
                throw new Exception("error: null == videoRender");
            VideoKernelApi videoKernel = getVideoKernel();
            if (null == videoKernel)
                throw new Exception("error: null == videoKernel");
            videoRender.setVideoKernel(videoKernel);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => attachRenderKernel => " + e.getMessage());
        }
    }

    default void initRenderView(@PlayerType.KernelType.Value int kernelType, @PlayerType.RenderType.Value int renderType) {
        try {
            boolean ijkUseMediaCodec = isIjkUseMediaCodec();
            if (ijkUseMediaCodec && kernelType == PlayerType.KernelType.IJK && renderType == PlayerType.RenderType.SURFACE_VIEW) {
                releaseRender();
                checkRenderNull(PlayerType.RenderType.SURFACE_VIEW, true);
                attachRenderKernel();
            } else if (ijkUseMediaCodec && kernelType == PlayerType.KernelType.IJK) {
                VideoRenderApi videoRender = getVideoRender();
                videoRender.reset();
            } else if (kernelType == PlayerType.KernelType.IJK) {
                VideoRenderApi videoRender = getVideoRender();
                videoRender.reset();
            } else {
                throw new Exception("warning: kernel not ijk");
            }
        } catch (
                Exception e) {
            LogUtil.log("VideoPlayerApiRender => resetRenderView => " + e.getMessage());
        }
    }

    void checkVideoView();
}
