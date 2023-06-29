package lib.kalu.mediaplayer.core.player;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.render.RenderApi;
import lib.kalu.mediaplayer.core.render.RenderFactoryManager;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiRender extends PlayerApiBase {

    default void clearSuface() {
        try {
            RenderApi render = getRender();
            render.clearCanvas();
        } catch (Exception e) {
        }
    }

    default void updateSuface() {
        try {
            RenderApi render = getRender();
            render.updateCanvas();
        } catch (Exception e) {
        }
    }

    default String screenshot() {
        try {
            RenderApi render = getRender();
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

    default boolean startFull() {
        try {
            boolean isPhoneWindow = isParentEqualsPhoneWindow();
            if (isPhoneWindow)
                throw new Exception("always full");
            requestFocusFull();
            boolean b = switchToDecorView(true);
            if (b) {
                callWindowEvent(PlayerType.WindowType.FULL);
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
            if (b) {
                callWindowEvent(PlayerType.WindowType.NORMAL);
                cleanFocusFull();
            }
            return b;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => stopFull => " + e.getMessage());
            return false;
        }
    }

    default boolean startFloat() {
        try {
            boolean isPhoneWindow = isParentEqualsPhoneWindow();
            if (isPhoneWindow)
                throw new Exception("always Float");
            boolean switchToDecorView = switchToDecorView(false);
            if (switchToDecorView) {
                callWindowEvent(PlayerType.WindowType.FLOAT);
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
            if (switchToPlayerLayout) {
                callWindowEvent(PlayerType.WindowType.NORMAL);
            }
            return switchToPlayerLayout;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => stopFloat => " + e.getMessage());
            return false;
        }
    }

    default void setScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            // 1
            RenderApi render = getRender();
            render.setScaleType(scaleType);
            // 2
            PlayerManager.getInstance().getConfig().newBuilder().setScaleType(scaleType).build();
        } catch (Exception e) {
        }
    }

    default void setVideoSize(int width, int height) {
        try {
            RenderApi render = getRender();
            render.setVideoSize(width, height);
        } catch (Exception e) {
        }
    }

    default void setVideoRotation(int rotation) {
        try {
            if (rotation == -1)
                return;
            RenderApi render = getRender();
            render.setVideoRotation(rotation);
        } catch (Exception e) {
        }
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     *
     * @param enable
     */
    default void setMirrorRotation(boolean enable) {
        try {
            RenderApi render = getRender();
            render.setScaleX(enable ? -1 : 1);
        } catch (Exception e) {
        }
    }

    default void showReal() {
        try {
            ViewGroup viewGroup = getBaseVideoViewGroup();
            viewGroup.setVisibility(View.VISIBLE);
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = viewGroup.getChildAt(i);
                child.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
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
        }
    }

    default void releaseRender() {
        try {
            // 1
            RenderApi render = searchRender();
            MPLogUtil.log("PlayerApiRender => releaseRender => render = " + render);
            if (null != render) {
                render.releaseReal();
                MPLogUtil.log("PlayerApiRender => releaseRender => succ");
            } else {
                MPLogUtil.log("PlayerApiRender => releaseRender => fail");
            }
            // 2
            clearRender();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => releaseRender => " + e.getMessage());
        }
    }

    default void clearRender() {
        try {
            ViewGroup group = getBaseVideoViewGroup();
            MPLogUtil.log("PlayerApiRender => clearRender => group = " + group);
            group.removeAllViews();
            MPLogUtil.log("PlayerApiRender => clearRender => succ");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => clearRender => " + e.getMessage());
        }
    }

    default RenderApi searchRender() {
        RenderApi render = null;
        try {
            ViewGroup group = getBaseVideoViewGroup();
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = group.getChildAt(i);
                if (null == view)
                    continue;
                if (!(view instanceof RenderApi))
                    continue;
                render = (RenderApi) view;
                break;
            }
        } catch (Exception e) {
        }
        MPLogUtil.log("PlayerApiRender => searchRender => render = " + render);
        return render;
    }

    default void setRender(@PlayerType.RenderType int v) {
        try {
            PlayerBuilder config = PlayerManager.getInstance().getConfig();
            PlayerBuilder.Builder builder = config.newBuilder();
            builder.setRender(v);
            PlayerManager.getInstance().setConfig(config);
        } catch (Exception e) {
        }
    }

    default void createRender() {
        try {
            // 1
            int type = PlayerManager.getInstance().getConfig().getRender();
            RenderApi render = RenderFactoryManager.getRender(getBaseContext(), type);
            MPLogUtil.log("PlayerApiRender => createRender => render = " + render);
            // 2
            releaseRender();
            // 2
            setRender(render);
            // 3
            updateRender();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => createRender => " + e.getMessage());
        }
    }

    default void updateRender() {
        try {
            // 1
            RenderApi render = getRender();
            MPLogUtil.log("PlayerApiRender => updateRender => render = " + render);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            render.setLayoutParams(params);
            // 2
            ViewGroup viewGroup = getBaseVideoViewGroup();
            viewGroup.addView((View) render, 0);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiRender => updateRender => " + e.getMessage());
        }
    }

    RenderApi getRender();

    void setRender(@NonNull RenderApi render);

    void checkReal();
}
