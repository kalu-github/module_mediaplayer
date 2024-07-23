
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
                LogUtil.log("ComponentInit => callEvent => INIT");
                show();
                break;
            case PlayerType.EventType.READY:
                LogUtil.log("ComponentInit => callEvent => READY");
                hide();
                break;
            case PlayerType.EventType.RENDER_FIRST_FRAME:
                LogUtil.log("ComponentInit => callEvent => RENDER_FIRST_FRAME");
                hide();
                break;
            case PlayerType.EventType.ERROR:
                LogUtil.log("ComponentInit => callEvent => ERROR");
                hide();
                break;
            case PlayerType.EventType.INIT_PLAY_WHEN_READY_DELAYED_TIME_START:
                LogUtil.log("ComponentInit => callEvent => INIT_PLAY_WHEN_READY_DELAYED_TIME_START");
                break;
            case PlayerType.EventType.INIT_PLAY_WHEN_READY_DELAYED_TIME_COMPLETE:
                LogUtil.log("ComponentInit => callEvent => INIT_PLAY_WHEN_READY_DELAYED_TIME_COMPLETE");
                break;
            case PlayerType.EventType.START:
                LogUtil.log("ComponentInit => callEvent => START");
                break;
            case PlayerType.EventType.PAUSE:
                LogUtil.log("ComponentInit => callEvent => PAUSE");
                break;
            case PlayerType.EventType.START_PLAY_WHEN_READY_TRUE:
                LogUtil.log("ComponentInit => callEvent => START_PLAY_WHEN_READY_TRUE");
                break;
            case PlayerType.EventType.START_PLAY_WHEN_READY_FALSE:
                LogUtil.log("ComponentInit => callEvent => START_PLAY_WHEN_READY_FALSE");
                break;
        }
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_init_bg;
    }
}