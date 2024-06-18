
package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentInit extends RelativeLayout implements ComponentApi {

    public ComponentInit( Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_init, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_INIT:
                LogUtil.log("ComponentInit[show] => playState = " + playState);
                show();
                break;
            default:
                LogUtil.log("ComponentInit[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public final void hide() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void show() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    /*************/

    @Override
    public int initLayoutIdComponentBackground() {
        return R.id.module_mediaplayer_component_init_bg;
    }
}