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
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_complete, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_END:
                LogUtil.log("ComponentComplete[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
                LogUtil.log("ComponentComplete[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_complete_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_complete_ui).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentComplete => show => " + e.getMessage());
        }
    }

    @Override
    public final void hide() {
        try {
            findViewById(R.id.module_mediaplayer_component_complete_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_complete_ui).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentComplete => hide => " + e.getMessage());
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
