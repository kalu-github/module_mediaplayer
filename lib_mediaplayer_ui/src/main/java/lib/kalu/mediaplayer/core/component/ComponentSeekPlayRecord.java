package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;

/**
 * 续播,提示
 */
public class ComponentSeekPlayRecord extends RelativeLayout implements ComponentApi {
    public ComponentSeekPlayRecord(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_seek_play_record, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        // 续播
        if (playState == PlayerType.StateType.STATE_START) {
            LogUtil.log("ComponentSeekPlayRecord => playStatus = " + playState);
            show();
        }
    }

    @Override
    public final void hide() {

        try {
            ViewGroup viewGroup = (ViewGroup) getParent();
            viewGroup.removeView(this);
        } catch (Exception e) {
        }

//        setComponentText("");
//        try {
//            findViewById(R.id.module_mediaplayer_component_seek_play_record_root).setVisibility(View.GONE);
//        } catch (Exception e) {
//            LogUtil.log("ComponentSeekPlayRecord => hide => Exception " + e.getMessage());
//        }
    }

    @Override
    public final void show() {

        try {
            long seek = getPlayerView().getSeek();
            String optString = TimeUtil.formatTimeMillis(seek);
            String string = getResources().getString(R.string.module_mediaplayer_string_play_record, optString);
            setComponentText(string);
        } catch (Exception e) {
        }

        try {
            findViewById(R.id.module_mediaplayer_component_seek_play_record_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentPlayRecord => show => Exception " + e.getMessage());
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, 2000);
    }

    @Override
    public int initLayoutIdText() {
        return R.id.module_mediaplayer_component_seek_play_record_root;
    }
}
