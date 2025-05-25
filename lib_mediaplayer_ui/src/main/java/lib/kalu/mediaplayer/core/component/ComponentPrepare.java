package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentPrepare extends RelativeLayout implements ComponentApi {

    public ComponentPrepare(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_prepare;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.INIT:
                LogUtil.log("ComponentLoading => callEvent => show => INIT");
                show();
                break;
            case PlayerType.EventType.VIDEO_RENDERING_START:
                LogUtil.log("ComponentLoading => callEvent => show => VIDEO_RENDERING_START");
                hide();
                break;
            case PlayerType.EventType.ERROR:
                LogUtil.log("ComponentLoading => callEvent => show => ERROR");
                hide();
                break;
        }
    }

    @Override
    public void onUpdateSpeed(int kernel, CharSequence result) {
        TextView textView = findViewById(R.id.module_mediaplayer_component_prepare_net);
        textView.setText(result);
    }

    @Override
    public void show() {
        ComponentApi.super.show();

        try {
            String mediaTitle = getTitle();
            setComponentText(mediaTitle);
        } catch (Exception e) {
        }
    }

    @Override
    public void hide() {
        ComponentApi.super.hide();

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


    public int initViewIdTextNetSpeed() {
        return R.id.module_mediaplayer_component_prepare_net;
    }
}