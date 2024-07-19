package lib.kalu.mediaplayer.core.player.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
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

    default boolean startFull() {
        try {
            callEvent(PlayerType.StateType.FULL_START);
            boolean full = isFull();
            LogUtil.log("VideoPlayerApiRender => startFull => full = " + full);
            if (full)
                throw new Exception("warning: full true");
            ViewGroup decorView = findDecorView((View) this);
            LogUtil.log("VideoPlayerApiRender => startFull => decorView = " + decorView);
            if (null == decorView)
                throw new Exception("error: decorView null");
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            LogUtil.log("VideoPlayerApiRender => startFull => rootView = " + rootView);
            if (null == rootView)
                throw new Exception("error: rootView null");
            ViewParent rootViewParent = rootView.getParent();
            LogUtil.log("VideoPlayerApiRender => startFull => rootViewParent = " + rootViewParent);
            if (null == rootViewParent)
                throw new Exception("error: rootViewParent null");
            // 保存全屏之前的focusId, 退出全屏时需要恢复focusId
            View focusView = decorView.findFocus();
            LogUtil.log("VideoPlayerApiRender => startFull => focusView = " + focusView);
            if (null == focusView)
                throw new Exception("error: focusView null");
            int parentId = ((View) rootViewParent).getId();
            int focusId = focusView.getId();
            rootView.setTag(R.id.module_mediaplayer_id_player_focus, focusId);
            rootView.setTag(R.id.module_mediaplayer_id_player_parent, parentId);
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            ((ViewGroup) rootViewParent).removeView(rootView);
            // 添加View
            int childCount = decorView.getChildCount();
            decorView.addView(rootView, childCount);
            // 切换焦点
            rootView.setFocusable(true);
            rootView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            rootView.requestFocus();
            LogUtil.log("VideoPlayerApiRender => startFull => requestFocus");
            // ...
            initRenderView();
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.StateType.FULL_SUCC);
            callWindow(PlayerType.WindowType.FULL);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => startFull => " + e.getMessage());
            callEvent(PlayerType.StateType.FULL_FAIL);
            return false;
        }
    }

    default boolean stopFull() {
        try {
            callEvent(PlayerType.StateType.FULL_START);
            boolean full = isFull();
            if (!full)
                throw new Exception("warning: full false");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("error: decorView null");
            // 查找View
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (null == rootView)
                throw new Exception("error: rootView null");
            Object tagFocus = rootView.getTag(R.id.module_mediaplayer_id_player_focus);
            if (null == tagFocus)
                throw new Exception("error: tagFocus null");
            Object tagParent = rootView.getTag(R.id.module_mediaplayer_id_player_parent);
            if (null == tagParent)
                throw new Exception("error: tagParent null");
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            decorView.removeView(rootView);
            // 添加View
            ViewGroup parentView = decorView.findViewById((int) tagParent);
            parentView.addView(rootView, 0);
            // 切换焦点
            rootView.setFocusable(false);
            rootView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            View focusView = decorView.findViewById((int) tagFocus);
            focusView.requestFocus();
            // 4
            initRenderView();
            // 5
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.StateType.FULL_SUCC);
            callWindow(PlayerType.WindowType.NORMAL);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => stopFull => " + e.getMessage());
            callEvent(PlayerType.StateType.FULL_FAIL);
            return false;
        }
    }

    default boolean startFloat() {
        try {
            callEvent(PlayerType.StateType.FLOAT_START);
            boolean aFloat = isFloat();
            LogUtil.log("VideoPlayerApiRender => startFloat => aFloat = " + aFloat);
            if (aFloat)
                throw new Exception("warning: aFloat true");
            ViewGroup decorView = findDecorView((View) this);
            LogUtil.log("VideoPlayerApiRender => startFloat => decorView = " + decorView);
            if (null == decorView)
                throw new Exception("error: decorView null");
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            LogUtil.log("VideoPlayerApiRender => startFloat => rootView = " + rootView);
            if (null == rootView)
                throw new Exception("error: rootView null");
            ViewParent rootViewParent = rootView.getParent();
            LogUtil.log("VideoPlayerApiRender => startFloat => rootViewParent = " + rootViewParent);
            if (null == rootViewParent)
                throw new Exception("error: rootViewParent null");
            int parentId = ((View) rootViewParent).getId();
            rootView.setTag(R.id.module_mediaplayer_id_player_parent, parentId);
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            ((ViewGroup) rootViewParent).removeView(rootView);
            // 切换大小
            int width = getBaseContext().getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_float_width);
            int height = width * 9 / 16;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rootView.setLayoutParams(layoutParams);
            // 添加View
            int childCount = decorView.getChildCount();
            decorView.addView(rootView, childCount);
            // ...
            initRenderView();
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.StateType.FLOAT_SUCC);
            callWindow(PlayerType.WindowType.FLOAT);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => startFloat => " + e.getMessage());
            callEvent(PlayerType.StateType.FLOAT_FAIL);
            return false;
        }
    }

    default boolean stopFloat() {
        try {
            callEvent(PlayerType.StateType.FLOAT_START);
            boolean aFloat = isFloat();
            if (!aFloat)
                throw new Exception("warning: aFloat false");
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("error: decorView null");
            // 查找View
            ViewGroup rootView = decorView.findViewById(R.id.module_mediaplayer_id_player);
            if (null == rootView)
                throw new Exception("error: rootView null");
            Object tagParent = rootView.getTag(R.id.module_mediaplayer_id_player_parent);
            if (null == tagParent)
                throw new Exception("error: tagParent null");
            // 移除View
            ((PlayerView) rootView).setDoWindowing(true);
            decorView.removeView(rootView);
            // 切换大小
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.setLayoutParams(layoutParams);
            // 添加View
            ViewGroup parentView = decorView.findViewById((int) tagParent);
            parentView.addView(rootView, 0);
            // 4
            initRenderView();
            // 5
            ((PlayerView) rootView).setDoWindowing(false);
            callEvent(PlayerType.StateType.FLOAT_SUCC);
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

    default void stopRenderUpdateProgress() {
        try {
            VideoRenderApi videoRender = getVideoRender();
            videoRender.stopUpdateProgress();
        } catch (Exception e) {
        }
    }

    default void startRenderUpdateProgress() {
        try {
            VideoRenderApi videoRender = getVideoRender();
            videoRender.startUpdateProgress();
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

    default void checkRenderNull(StartArgs args, boolean release) {
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
            Context context = getBaseContext();
            int renderType = args.getRenderType();
            VideoRenderApi videoRender = VideoRenderFactoryManager.createRender(context, renderType);
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

    default void initRenderView() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            @PlayerType.DecoderType.Value
            int decoderType = args.getDecoderType();
            @PlayerType.KernelType.Value
            int kernelType = args.getKernelType();
            @PlayerType.RenderType.Value
            int renderType = args.getRenderType();

            if (decoderType == PlayerType.DecoderType.IJK_ALL_CODEC && kernelType == PlayerType.KernelType.IJK && renderType == PlayerType.RenderType.SURFACE_VIEW) {
                releaseRender();
                StartArgs startArgs = new StartArgs.Builder().setRenderType(PlayerType.RenderType.SURFACE_VIEW).build();
                checkRenderNull(startArgs, true);
                attachRenderKernel();
            } else if (decoderType == PlayerType.DecoderType.IJK_ALL_CODEC && kernelType == PlayerType.KernelType.IJK) {
                VideoRenderApi videoRender = getVideoRender();
                videoRender.reset();
            } else if (kernelType == PlayerType.KernelType.IJK) {
                VideoRenderApi videoRender = getVideoRender();
                videoRender.reset();
            } else {
                throw new Exception("warning: kernel not ijk");
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiRender => resetRenderView => " + e.getMessage());
        }
    }

    void checkVideoVisibility();
}
