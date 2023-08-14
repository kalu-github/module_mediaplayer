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

public class ComponentNet extends RelativeLayout implements ComponentApi {

    public ComponentNet(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_net, this, true);
    }

    @Override
    public void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
        try {
            TextView textView = findViewById(R.id.module_mediaplayer_component_net_message);
            if (null == textView)
                throw new Exception("textView error: null");
            int viewVisibility = textView.getVisibility();
            if (viewVisibility != View.VISIBLE)
                throw new Exception("viewVisibility warning: " + viewVisibility);
            String speed = getNetSpeed();
            textView.setText(speed);
//            int length = speed.length();
//            int start = length - 4;
//            String unit = speed.substring(start, length);
//            String num = speed.substring(0, start);
//            v1.setText(num);
//            TextView v2 = findViewById(R.id.module_mediaplayer_component_net_unit);
//            v2.setText(unit);
        } catch (Exception e) {
            MPLogUtil.log("ComponentNet => onUpdateTimeMillis => " + e.getMessage());
        }
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
    public final void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_net_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_net_pb).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_net_message).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_net_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_net_pb).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_net_message).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }
}
