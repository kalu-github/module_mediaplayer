package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentBuffering extends RelativeLayout implements ComponentApiBuffering {

    public ComponentBuffering(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_buffering;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_buffering_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.BUFFERING_START:
            case PlayerType.StateType.SEEK_START:
                LogUtil.log("ComponentBuffering[show] => callEvent => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.BUFFERING_STOP:
            case PlayerType.StateType.SEEK_FINISH:
            case PlayerType.StateType.INIT:
            case PlayerType.StateType.ERROR:
            case PlayerType.StateType.RELEASE:
            case PlayerType.StateType.RELEASE_EXCEPTION:
                LogUtil.log("ComponentBuffering[hide] => callEvent => playState = " + playState);
                hide();
                break;
        }
    }
}
