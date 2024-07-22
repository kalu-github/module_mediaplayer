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
            case PlayerType.EventType.LOADING_START:
                LogUtil.log("ComponentLoadingGradient => callEvent => LOADING_START");
                show();
                break;
            case PlayerType.EventType.ERROR:
                LogUtil.log("ComponentLoadingGradient => callEvent => ERROR");
                hide();
                break;
            case PlayerType.EventType.PLAY_WHEN_READY_DELAYED_TIME_START:
                LogUtil.log("ComponentLoadingGradient => callEvent => PLAY_WHEN_READY_DELAYED_TIME_START");
                break;
            case PlayerType.EventType.PLAY_WHEN_READY_DELAYED_TIME_COMPLETE:
                LogUtil.log("ComponentLoadingGradient => callEvent => PLAY_WHEN_READY_DELAYED_TIME_COMPLETE");
                hide();
                break;
            case PlayerType.EventType.LOADING_STOP:
                LogUtil.log("ComponentLoadingGradient => callEvent => LOADING_STOP");
                try {
                    long playWhenReadyDelayedTime = getPlayWhenReadyDelayedTime();
                    if (playWhenReadyDelayedTime > 0L)
                        throw new Exception();
                    hide();
                } catch (Exception e) {
                }
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
            ComponentApiLoading.super.show();
            // 2
            setComponentText(getTitle());
            // 3
            updateNetSpeed();
        } catch (Exception e) {
            LogUtil.log("ComponentLoadingGradient => show => Exception "+e.getMessage());
        }
    }

    @Override
    public void hide() {
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            // 1
            ComponentApiLoading.super.hide();
            // 2
            setComponentText("");
        } catch (Exception e) {
            LogUtil.log("ComponentLoadingGradient => hide => Exception "+e.getMessage());
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