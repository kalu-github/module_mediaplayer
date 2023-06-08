package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentComplete extends RelativeLayout implements ComponentApi {

    public ComponentComplete(@NonNull Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_complete, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_END:
                MPLogUtil.log("ComponentComplete[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
                MPLogUtil.log("ComponentComplete[gone] => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_complete_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_complete_message).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_complete_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_complete_message).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    /*************/

    @Override
    public void setComponentBackgroundColorInt(int value) {
        try {
            setBackgroundColorInt(this, R.id.module_mediaplayer_component_complete_bg, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentBackgroundResource(int resid) {
        try {
            setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_complete_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentImageResource(int resid) {
        try {
            setImageResource(this, R.id.module_mediaplayer_component_complete_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentImageUrl(@NonNull String url) {
        try {
            setImageUrl(this, R.id.module_mediaplayer_component_complete_bg, url);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_complete_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(@NonNull String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_complete_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_complete_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_complete_message, color);
        } catch (Exception e) {
        }
    }
}
