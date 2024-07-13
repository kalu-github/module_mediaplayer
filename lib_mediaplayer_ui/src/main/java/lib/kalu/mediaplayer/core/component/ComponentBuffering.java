package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentBuffering extends RelativeLayout implements ComponentApiBuffering {

    public ComponentBuffering(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_buffering;
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
//        try {
//            if (!showSpeed)
//                throw new Exception();
//            TextView textView = findViewById(R.id.module_mediaplayer_component_buffering_message);
//            if (null == textView)
//                throw new Exception("textView error: null");
//            int viewVisibility = textView.getVisibility();
//            if (viewVisibility != View.VISIBLE)
//                throw new Exception("viewVisibility warning: " + viewVisibility);
//            String speed = getNetSpeed();
//            textView.setText(speed);
////            int length = speed.length();
////            int start = length - 4;
////            String unit = speed.substring(start, length);
////            String num = speed.substring(0, start);
////            v1.setText(num);
////            TextView v2 = findViewById(R.id.module_mediaplayer_component_buffering_unit);
////            v2.setText(unit);
//        } catch (Exception e) {
////            MPLogUtil.log("ComponentNet => onUpdateTimeMillis => " + e.getMessage());
//        }
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_buffering_root;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.BUFFERING_START:
            case PlayerType.StateType.SEEK_START:
                LogUtil.log("ComponentBuffering[show] => callEvent => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.INIT:
            case PlayerType.StateType.BUFFERING_STOP:
            case PlayerType.StateType.SEEK_FINISH:
            case PlayerType.StateType.ERROR:
            case PlayerType.StateType.RELEASE:
            case PlayerType.StateType.RELEASE_EXCEPTION:
                LogUtil.log("ComponentBuffering[hide] => callEvent => playState = " + playState);
                hide();
                break;
        }
    }
}
