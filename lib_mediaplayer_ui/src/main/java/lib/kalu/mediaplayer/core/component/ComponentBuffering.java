package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;



import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentBuffering extends RelativeLayout implements ComponentApi {

    public ComponentBuffering(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_buffering, this, true);
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        try {
            if(!showSpeed)
                throw new Exception();
            TextView textView = findViewById(R.id.module_mediaplayer_component_buffering_message);
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
//            TextView v2 = findViewById(R.id.module_mediaplayer_component_buffering_unit);
//            v2.setText(unit);
        } catch (Exception e) {
//            MPLogUtil.log("ComponentNet => onUpdateTimeMillis => " + e.getMessage());
        }
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_BUFFERING_START:
                LogUtil.log("ComponentNet => onPlayStateChanged => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT_RETEY_BUFFERING:
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_RELEASE:
            case PlayerType.StateType.STATE_RELEASE_EXCEPTION:
                LogUtil.log("ComponentNet => onPlayStateChanged => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public final void hide() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_buffering_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_buffering_pb).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_buffering_message).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void show() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_buffering_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_buffering_pb).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_buffering_message).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    private boolean showSpeed = false;

    public void enableSpeed(boolean enable) {
        showSpeed = enable;
    }
}
