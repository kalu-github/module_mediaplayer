package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentPrepareGradient extends RelativeLayout implements ComponentApi {

    public ComponentPrepareGradient(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_prepare_gradient;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.INIT:
                LogUtil.log("ComponentLoadingGradient => callEvent => INIT");
                show();
                break;
            case PlayerType.EventType.VIDEO_RENDERING_START:
                LogUtil.log("ComponentLoadingGradient => callEvent => VIDEO_RENDERING_START");
                hide();
                break;
            case PlayerType.EventType.ERROR:
                LogUtil.log("ComponentLoadingGradient => callEvent => ERROR");
                hide();
                break;
        }
    }

    @Override
    public void onUpdateSpeed(int kernel, CharSequence result) {
        TextView textView = findViewById(R.id.module_mediaplayer_component_prepare_gradient_net);
        textView.setText(result);
    }

    @Override
    public void show() {
        try {
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            // 1
            ComponentApi.super.show();
            // 2
            setComponentText(getTitle());
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
            ComponentApi.super.hide();
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

}