package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public class ComponentLoadingGradient extends RelativeLayout implements ComponentApiLoading {

    public ComponentLoadingGradient(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_loading_gradient;
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_LOADING_START:
                LogUtil.log("ComponentLoadingGradient => callEventListener => show => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_RELEASE:
            case PlayerType.StateType.STATE_RELEASE_EXCEPTION:
                LogUtil.log("ComponentLoadingGradient => callEventListener => gone => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {

        // 网速
        updateComponentNetSpeed();
    }

    @Override
    public void show() {
        ComponentApiLoading.super.show();

        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
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

        // 网速
        updateComponentNetSpeed();
    }

    @Override
    public void hide() {
        ComponentApiLoading.super.hide();

        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            setComponentText("");
        } catch (Exception e) {
            LogUtil.log("ComponentLoadingGradient => gone => " + e.getMessage());
        }
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_loading_gradient_root;
    }

    /*************/


    @Override
    public int initLayoutIdComponentBackground() {
        return R.id.module_mediaplayer_component_loading_gradient_bg;
    }

    @Override
    public int initLayoutIdText() {
        return R.id.module_mediaplayer_component_loading_gradient_name;
    }

    @Override
    public int initLayoutIdNetSpeed() {
        return R.id.module_mediaplayer_component_loading_gradient_net;
    }
}