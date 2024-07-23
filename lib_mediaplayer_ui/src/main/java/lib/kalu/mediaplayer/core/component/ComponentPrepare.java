package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentPrepare extends RelativeLayout implements ComponentApiPrepare {

    public ComponentPrepare(Context context) {
        super(context);
        inflate();
        setComponentShowNetSpeed(false);
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_prepare;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.PREPARE_START:
                LogUtil.log("ComponentLoading => callEvent => show => PREPARE_START");
                show();
                break;
            case PlayerType.EventType.PREPARE_COMPLETE:
                LogUtil.log("ComponentLoading => callEvent => show => PREPARE_COMPLETE");
                hide();
                break;
            case PlayerType.EventType.ERROR:
                LogUtil.log("ComponentLoading => callEvent => show => ERROR");
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
        ComponentApiPrepare.super.show();

        try {
            String mediaTitle = getTitle();
            setComponentText(mediaTitle);
        } catch (Exception e) {
        }

        // 网速
        updateNetSpeed();
    }

    @Override
    public void hide() {
        ComponentApiPrepare.super.hide();

        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            setComponentText("");
        } catch (Exception e) {
            LogUtil.log("ComponentLoading => gone => " + e.getMessage());
        }
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_prepare_root;
    }


    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_prepare_bg;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_prepare_name;
    }


    @Override
    public int initViewIdNetSpeed() {
        return R.id.module_mediaplayer_component_prepare_net;
    }
}