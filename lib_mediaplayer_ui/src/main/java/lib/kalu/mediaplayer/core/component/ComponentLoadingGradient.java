package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public class ComponentLoadingGradient extends RelativeLayout implements ComponentApi {

    public ComponentLoadingGradient(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_loading_gradient, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_LOADING_START:
                LogUtil.log("ComponentLoading => callEventListener => show => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_RELEASE:
            case PlayerType.StateType.STATE_RELEASE_EXCEPTION:
                LogUtil.log("ComponentLoading => callEventListener => gone => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public final void show() {

        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            String mediaTitle = tags.getTitle();
            setComponentText(mediaTitle);
        } catch (Exception e) {
        }


        try {
            findViewById(R.id.module_mediaplayer_component_loading_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentLoading => show => " + e.getMessage());
        }
    }

    @Override
    public final void hide() {

        try {
            setComponentText("");
        } catch (Exception e) {
        }

        try {
            findViewById(R.id.module_mediaplayer_component_loading_root).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentLoading => gone => " + e.getMessage());
        }
    }

    @Override
    public boolean isComponentShowing() {
        try {
            int visibility = findViewById(R.id.module_mediaplayer_component_loading_gradient_root).getVisibility();
            return visibility == View.VISIBLE;
        } catch (Exception e) {
            return false;
        }
    }

    /*************/

    @Override
    public int initLayoutIdComponentBackground() {
        return R.id.module_mediaplayer_component_loading_gradient_bg;
    }

    @Override
    public int initLayoutIdText() {
        return R.id.module_mediaplayer_component_loading_gradient_message;
    }
}