package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;



import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentTry extends RelativeLayout implements ComponentApi {

    public ComponentTry( Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_try, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_TRY_BEGIN:
                LogUtil.log("ComponentTry[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_TRY_COMPLETE:
                Toast.makeText(getContext(), "试看结束", Toast.LENGTH_SHORT).show();
                LogUtil.log("ComponentTry[gone] => playState = " + playState);
                hide();
                break;
            case PlayerType.StateType.STATE_INIT:
                LogUtil.log("ComponentTry[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public final void show() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_try_message).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void hide() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_try_message).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_try_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText( String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_try_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_try_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_try_message, color);
        } catch (Exception e) {
        }
    }
}
