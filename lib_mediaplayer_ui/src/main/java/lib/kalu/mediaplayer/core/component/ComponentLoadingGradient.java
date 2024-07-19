package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentLoadingGradient extends RelativeLayout implements ComponentApiLoading {

    public ComponentLoadingGradient(Context context) {
        super(context);
        inflate();
        setComponentShowNetSpeed(false);
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_loading_gradient;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.LOADING_START:
                LogUtil.log("ComponentLoadingGradient => callEventListener => show => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.INIT:
            case PlayerType.StateType.LOADING_STOP:
            case PlayerType.StateType.ERROR:
            case PlayerType.StateType.RELEASE:
            case PlayerType.StateType.RELEASE_EXCEPTION:
                LogUtil.log("ComponentLoadingGradient => callEventListener => gone => playState = " + playState);
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
        ComponentApiLoading.super.show();

        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            String mediaTitle = getTitle();
            setComponentText(mediaTitle);
        } catch (Exception e) {
        }

        // 网速
        updateNetSpeed();
    }

    @Override
    public void hide() {
        ComponentApiLoading.super.hide();

        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            setComponentText("");
        } catch (Exception e) {
            LogUtil.log("ComponentLoadingGradient => gone => " + e.getMessage());
        }
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_loading_gradient_root;
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_loading_gradient_bg;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_loading_gradient_name;
    }

    @Override
    public int initViewIdNetSpeed() {
        return R.id.module_mediaplayer_component_loading_gradient_net;
    }
}