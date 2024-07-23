package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentPrepareGradient extends RelativeLayout implements ComponentApiPrepare {

    public ComponentPrepareGradient(Context context) {
        super(context);
        inflate();
        setComponentShowNetSpeed(false);
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_prepare_gradient;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.PREPARE_START:
                LogUtil.log("ComponentLoadingGradient => callEvent => PREPARE_START");
                show();
                break;
            case PlayerType.EventType.PREPARE_COMPLETE:
                LogUtil.log("ComponentLoadingGradient => callEvent => PREPARE_COMPLETE");
                hide();
                break;
            case PlayerType.EventType.ERROR:
                LogUtil.log("ComponentLoadingGradient => callEvent => ERROR");
                hide();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        // 网速
        updateNetSpeed();
    }

    @Override
    public void show() {
        try {
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            // 1
            ComponentApiPrepare.super.show();
            // 2
            setComponentText(getTitle());
            // 3
            updateNetSpeed();
        } catch (Exception e) {
            LogUtil.log("ComponentLoadingGradient => show => Exception " + e.getMessage());
        }
    }

    @Override
    public void hide() {
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            // 1
            ComponentApiPrepare.super.hide();
            // 2
            setComponentText("");
        } catch (Exception e) {
            LogUtil.log("ComponentLoadingGradient => hide => Exception " + e.getMessage());
        }
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_prepare_gradient_root;
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_prepare_gradient_bg;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_prepare_gradient_name;
    }

    @Override
    public int initViewIdNetSpeed() {
        return R.id.module_mediaplayer_component_prepare_gradient_net;
    }
}