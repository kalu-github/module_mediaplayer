
package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentInit extends RelativeLayout implements ComponentApi {

    public ComponentInit( Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_init;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_init_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.INIT:
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
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_init_bg;
    }
}