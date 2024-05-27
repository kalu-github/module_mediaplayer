package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentSurfaceCover extends RelativeLayout implements ComponentApi {

    public ComponentSurfaceCover(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_surface_cover, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_START:
                LogUtil.log("ComponentSurfaceCover => callPlayerEvent => show => playState = " + playState);
                hide();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_RELEASE:
            case PlayerType.StateType.STATE_ERROR:
                LogUtil.log("ComponentSurfaceCover => callPlayerEvent => gone => playState = " + playState);
                show();
                break;
        }
    }

    @Override
    public final void show() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_surface_cover_bg).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentSurfaceCover => show => " + e.getMessage());
        }
    }

    @Override
    public final void hide() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_surface_cover_bg).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentSurfaceCover => hide => " + e.getMessage());
        }
    }
}
