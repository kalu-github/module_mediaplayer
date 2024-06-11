
package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentInit extends RelativeLayout implements ComponentApi {

    public ComponentInit( Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_init, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_INIT:
                LogUtil.log("ComponentInit[show] => playState = " + playState);
                show();
                break;
            default:
                LogUtil.log("ComponentInit[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public final void hide() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void show() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_init_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_init_bg).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    /*************/

    @Override
    public final void setComponentBackgroundColorInt(int value) {
        try {
            setBackgroundColorInt(this, R.id.module_mediaplayer_component_init_bg, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentBackgroundResource(int resid) {
        try {
            setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_init_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageResource(int resid) {
        try {
            setImageResource(this, R.id.module_mediaplayer_component_init_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageUrl( String url) {
        try {
            setImageUrl(this, R.id.module_mediaplayer_component_init_bg, url);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_init_txt, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText( String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_init_txt, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_init_txt, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_init_txt, color);
        } catch (Exception e) {
        }
    }
}