
package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;

public final class ComponentInit extends RelativeLayout implements ComponentApi {

    public ComponentInit(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_init, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_INIT:
                MPLogUtil.log("ComponentInit[show] => playState = " + playState);
                show();
                break;
            default:
                MPLogUtil.log("ComponentInit[gone] => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.GONE);
        }catch (Exception e){
        }
    }

    @Override
    public void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.VISIBLE);
        }catch (Exception e){
        }
    }

    /*************/

    @Override
    public void setComponentBackgroundColorInt(int value) {
        try {
            setBackgroundColorInt(this, R.id.module_mediaplayer_component_init_bg, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentBackgroundResource(int resid) {
        try {
            setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_init_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentImageResource(int resid) {
        try {
            setImageResource(this, R.id.module_mediaplayer_component_init_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentImageUrl(@NonNull String url) {
        try {
            setImageUrl(this, R.id.module_mediaplayer_component_init_bg, url);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_init_txt, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(@NonNull String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_init_txt, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_init_txt, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_init_txt, color);
        } catch (Exception e) {
        }
    }
}