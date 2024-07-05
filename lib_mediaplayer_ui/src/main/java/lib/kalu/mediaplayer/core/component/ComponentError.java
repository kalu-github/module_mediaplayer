package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentError extends RelativeLayout implements ComponentApi {

    public ComponentError(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_error;
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_error_root;
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_ERROR:
                LogUtil.log("ComponentError[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_RESTAER:
                LogUtil.log("ComponentError[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public int initLayoutIdComponentBackground() {
        return R.id.module_mediaplayer_component_error_bg;
    }

    @Override
    public int initLayoutIdImage() {
        return R.id.module_mediaplayer_component_error_icon;
    }
}
