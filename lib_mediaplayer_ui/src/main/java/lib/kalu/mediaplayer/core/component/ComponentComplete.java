package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentComplete extends RelativeLayout implements ComponentApi {

    public ComponentComplete(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_complete;
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_complete_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.END:
                LogUtil.log("ComponentComplete[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.INIT:
                LogUtil.log("ComponentComplete[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    /*************/

    @Override
    public int initLayoutIdComponentBackground() {
        return R.id.module_mediaplayer_component_complete_bg;
    }

    @Override
    public int initLayoutIdImage() {
        return R.id.module_mediaplayer_component_complete_icon;
    }
}
