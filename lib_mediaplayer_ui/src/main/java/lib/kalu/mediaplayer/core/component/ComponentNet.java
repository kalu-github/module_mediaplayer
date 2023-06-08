package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class ComponentNet extends RelativeLayout implements ComponentApi {

    public ComponentNet(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_net, this, true);
    }

    @Override
    public void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
        boolean showing = isShowing();
        MPLogUtil.log("ComponentNet => onUpdateTimeMillis => showing = " + showing);
        if (!showing)
            return;
        updateSpeed();
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_BUFFERING_START:
                MPLogUtil.log("ComponentNet => onPlayStateChanged => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                MPLogUtil.log("ComponentNet => onPlayStateChanged => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void callWindowEvent(int state) {
        switch (state) {
            case PlayerType.WindowType.FLOAT:
            case PlayerType.WindowType.NORMAL:
            case PlayerType.WindowType.FULL:
                try {
                    int visibility = findViewById(R.id.module_mediaplayer_component_net).getVisibility();
                    if (visibility == View.VISIBLE) {
                        try {
                            PlayerApi playerApi = getPlayerApi();
                            boolean full = playerApi.isFull();
                            TextView v1 = findViewById(R.id.module_mediaplayer_component_net_txt);
                            v1.setVisibility(full ? View.VISIBLE : View.INVISIBLE);
                            TextView v2 = findViewById(R.id.module_mediaplayer_component_net_unit);
                            v2.setVisibility(full ? View.VISIBLE : View.INVISIBLE);
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_net).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_net).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
        try {
            PlayerApi playerApi = getPlayerApi();
            boolean full = playerApi.isFull();
            TextView v1 = findViewById(R.id.module_mediaplayer_component_net_txt);
            v1.setVisibility(full ? View.VISIBLE : View.INVISIBLE);
            TextView v2 = findViewById(R.id.module_mediaplayer_component_net_unit);
            v2.setVisibility(full ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
        }
    }

    private void updateSpeed() {
        try {
            String speed = getPlayerApi().getNetSpeed();
            int length = speed.length();
            int start = length - 4;
            String unit = speed.substring(start, length);
            String num = speed.substring(0, start);
            TextView v1 = findViewById(R.id.module_mediaplayer_component_net_txt);
            v1.setText(num);
            TextView v2 = findViewById(R.id.module_mediaplayer_component_net_unit);
            v2.setText(unit);
        } catch (Exception e) {
        }
    }

    private boolean isShowing() {
        try {
            return findViewById(R.id.module_mediaplayer_component_net).getVisibility() == View.VISIBLE;
        } catch (Exception e) {
            return false;
        }
    }
}
