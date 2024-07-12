package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentSurfaceCover extends RelativeLayout implements ComponentApi {

    public ComponentSurfaceCover(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_surface_cover;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_START:
                LogUtil.log("ComponentSurfaceCover => callEventListener => show => playState = " + playState);
                hide();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_RELEASE:
            case PlayerType.StateType.STATE_ERROR:
                LogUtil.log("ComponentSurfaceCover => callEventListener => gone => playState = " + playState);
                show();
                break;
        }
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_surface_cover_root;
    }
}
