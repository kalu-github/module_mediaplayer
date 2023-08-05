package lib.kalu.mediaplayer.core.player;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.core.render.RenderFactoryManager;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiRender extends PlayerApiBase {

    default String screenshot() {
        try {
            RenderApi render = getVideoRender();
            return render.screenshot();
        } catch (Exception e) {
            return null;
        }
    }

    default void requestFocusFull() {
        try {
            ViewGroup decorView = findDecorView((View) this);
            if (null == decorView)
                throw new Exception("decorView error: null");
            View focus = decorView.findFocus();
            if (null == focus)
                throw new Exception("focus error: null");
            ((View) this).setTag(R.id.module_mediaplayer_window_id, focus);
            ((View) ((View) this).getParent()).setFocusable(true);
            ((View) this).setFocusable(true);
            ((View) this).requestFocus();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => requestFocusFull => " + e.getMessage());
        }
    }

    default void cleanFocusFull() {
        try {
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_window_id);
            if (null == tag)
                throw new Exception("tag error: null");
            ((View) this).setTag(R.id.module_mediaplayer_window_id, null);
            ((View) ((View) this).getParent()).setFocusable(false);
            ((View) this).setFocusable(false);
            ((View) tag).requestFocus();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => cleanFocusFull => " + e.getMessage());
        }
    }

    default boolean resetVideoRender() {
        try {
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            if (null == config)
                throw new Exception("config warning: null");
            int videoRender = config.getVideoRender();
            if (videoRender != PlayerType.RenderType.SURFACE_VIEW)
                throw new Exception("videoRender warning: not SURFACE_VIEW");
            int videoKernel = config.getVideoKernel();
            if (videoKernel != PlayerType.KernelType.IJK_MEDIACODEC && videoKernel != PlayerType.KernelType.EXO_V1 && videoKernel != PlayerType.KernelType.EXO_V2)
                throw new Exception("videoKernel waring: not ijk_mediacodec or exo_v1 or exo_v2");
            if (!(this instanceof PlayerApiKernel))
                throw new Exception("videoRender warning: not instanceof PlayerApiKernel");
            createVideoRender();
            ((PlayerApiKernel) this).attachVideoRender();
            updateVideoRenderBuffer(videoKernel == PlayerType.KernelType.IJK_MEDIACODEC ? 4000 : 400);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => resetVideoRender => " + e.getMessage());
            return false;
        }
    }

    default void checkPlaying() {
        try {
            if (!(this instanceof PlayerApiKernel))
                throw new Exception("this error: not instanceof PlayerApiKernel");
            boolean playing = ((PlayerApiKernel) this).isPlaying();
            ((View) this).setTag(R.id.module_mediaplayer_id_player_switch_window_check_playing, playing);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => checkPlaying => " + e.getMessage());
        }
    }

    default void switchPlaying() {
        try {
            if (!(this instanceof PlayerApiKernel))
                throw new Exception("this error: not instanceof PlayerApiKernel");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_switch_window_check_playing);
            ((View) this).setTag(R.id.module_mediaplayer_id_player_switch_window_check_playing, null);
            if (null == tag || ((boolean) tag))
                throw new Exception("tag warning: null");
            ((PlayerApiKernel) this).pause(true);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => switchPlaying => " + e.getMessage());
        }
    }

    default boolean startFull(boolean rememberPlaying) {
        try {
            boolean isPhoneWindow = isParentEqualsPhoneWindow();
            if (isPhoneWindow)
                throw new Exception("always full");
            requestFocusFull();
            boolean b = switchToDecorView(true);
            if (b) {
                resetVideoRender();
                callPlayerEvent(PlayerType.StateType.STATE_FULL_START);
                callWindowEvent(PlayerType.WindowType.FULL);
            }
            if (rememberPlaying) {
                checkPlaying();
            }
            return b;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => startFull => " + e.getMessage());
            return false;
        }
    }

    default boolean stopFull() {
        try {
            boolean isFull = isFull();
            if (!isFull)
                throw new Exception("not full");
            boolean b = switchToPlayerLayout();
            switchPlaying();
            if (b) {
                resetVideoRender();
                cleanFocusFull();
                callPlayerEvent(PlayerType.StateType.STATE_FULL_STOP);
                callWindowEvent(PlayerType.WindowType.NORMAL);
            }
            return b;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => stopFull => " + e.getMessage());
            return false;
        }
    }

    default boolean startFloat(boolean rememberPlaying) {
        try {
            boolean isPhoneWindow = isParentEqualsPhoneWindow();
            if (isPhoneWindow)
                throw new Exception("always Float");
            boolean switchToDecorView = switchToDecorView(false);
            if (switchToDecorView) {
                resetVideoRender();
                callPlayerEvent(PlayerType.StateType.STATE_FLOAT_START);
                callWindowEvent(PlayerType.WindowType.FLOAT);
            }
            if (rememberPlaying) {
                checkPlaying();
            }
            return switchToDecorView;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => startFloat => " + e.getMessage());
            return false;
        }
    }

    default boolean stopFloat() {
        try {
            boolean isFloat = isFloat();
            if (!isFloat)
                throw new Exception("not Float");
            boolean switchToPlayerLayout = switchToPlayerLayout();
            switchPlaying();
            if (switchToPlayerLayout) {
                resetVideoRender();
                callPlayerEvent(PlayerType.StateType.STATE_FLOAT_STOP);
                callWindowEvent(PlayerType.WindowType.NORMAL);
            }
            return switchToPlayerLayout;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => stopFloat => " + e.getMessage());
            return false;
        }
    }

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            RenderApi render = getVideoRender();
            if (null == render)
                throw new Exception("render error: null");
            render.setVideoScaleType(scaleType);
            PlayerManager.getInstance().setVideoScaleType(scaleType);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => setVideoScaleType => " + e.getMessage());
        }
    }

    default void setVideoSize(int width, int height) {
        try {
            RenderApi render = getVideoRender();
            render.setVideoSize(width, height);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => setVideoSize => " + e.getMessage());
        }
    }

    default void setVideoRotation(@PlayerType.RotationType.Value int rotation) {
        try {
            if (rotation == -1)
                return;
            RenderApi render = getVideoRender();
            render.setVideoRotation(rotation);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => setVideoRotation => " + e.getMessage());
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     *
     * @param enable
     */
    default void setMirrorRotation(boolean enable) {
        try {
            RenderApi render = getVideoRender();
            render.setScaleX(enable ? -1 : 1);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => setMirrorRotation => " + e.getMessage());
        }
    }

    default void showVideoReal() {
        try {
            ViewGroup viewGroup = getBaseVideoViewGroup();
            viewGroup.setVisibility(View.VISIBLE);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => showVideoReal => " + e.getMessage());
        }
    }

    default void hideReal() {
        try {
            ViewGroup viewGroup = getBaseVideoViewGroup();
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.INVISIBLE);
            }
            viewGroup.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => hideReal => " + e.getMessage());
        }
    }

    default RenderApi searchVideoRender() {
        try {
            ViewGroup group = getBaseVideoViewGroup();
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = group.getChildAt(i);
                if (null == view)
                    continue;
                if (!(view instanceof RenderApi))
                    continue;
                return (RenderApi) view;
            }
            throw new Exception("not find");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => searchVideoRender => " + e.getMessage());
            return null;
        }
    }

    default void setVideoRender(@PlayerType.RenderType int v) {
        try {
            PlayerManager.getInstance().setVideoRender(v);
        } catch (Exception e) {
        }
    }

    default void createVideoRender() {
        try {
            releaseVideoRender();
            int videoRender = PlayerManager.getInstance().getConfig().getVideoRender();
            setVideoRender(RenderFactoryManager.createRender(getBaseContext(), videoRender));
            addVideoRender();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => createRender => " + e.getMessage());
        }
    }

    default void updateVideoRenderBuffer(int delayMillis) {
        try {
            RenderApi render = searchVideoRender();
            if (null == render)
                throw new Exception("render warning: null");
            render.updateBuffer(delayMillis);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => updateVideoRenderBuffer => " + e.getMessage());
        }
    }

    default void addVideoRenderListener() {
        try {
            RenderApi render = searchVideoRender();
            if (null == render)
                throw new Exception("render warning: null");
            render.addListener();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => addVideoRenderListener => " + e.getMessage());
        }
    }

    default void addVideoRender() {
        try {
            RenderApi render = getVideoRender();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            render.setLayoutParams(params);
            ViewGroup viewGroup = getBaseVideoViewGroup();
            viewGroup.addView((View) render, 0);
            MPLogUtil.log("PlayerApiRender => addRender => succ");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => addVideoRender => " + e.getMessage());
        }
    }

    default void releaseVideoRender() {
        try {
            ViewGroup group = getBaseVideoViewGroup();
            if (null == group)
                throw new Exception("group error: null");
            int childCount = group.getChildCount();
            if (childCount <= 0)
                throw new Exception("childCount warning: " + childCount);
            for (int i = 0; i < childCount; i++) {
                View childAt = group.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof RenderApi))
                    continue;
                ((RenderApi) childAt).release();
            }
            group.removeAllViews();
            MPLogUtil.log("PlayerApiRender => releaseVideoRender => succ");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => releaseVideoRender => " + e.getMessage());
        }
    }

    RenderApi getVideoRender();

    void setVideoRender(@NonNull RenderApi render);

    void checkVideoReal();
}
