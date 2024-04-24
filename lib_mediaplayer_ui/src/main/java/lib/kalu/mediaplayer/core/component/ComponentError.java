package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentError extends RelativeLayout implements ComponentApi {

    public ComponentError(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_error, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_IGNORE:
                MPLogUtil.log("ComponentError[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_START_SEEK:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_RESUME_IGNORE:
            case PlayerType.StateType.STATE_RESTAER:
                MPLogUtil.log("ComponentError[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public final void show() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_error_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_error_ui).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void hide() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_error_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_error_ui).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    /*************/

    @Override
    public final void setComponentBackgroundColorInt(int value) {
        try {
            setBackgroundColorInt(this, R.id.module_mediaplayer_component_error_bg, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentBackgroundResource(int resid) {
        try {
            setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_error_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageResource(int resid) {
        try {
            setImageResource(this, R.id.module_mediaplayer_component_error_icon, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageUrl( String url) {
        try {
            setImageUrl(this, R.id.module_mediaplayer_component_error_icon, url);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_error_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText( String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_error_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_error_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_error_message, color);
        } catch (Exception e) {
        }
    }
}
