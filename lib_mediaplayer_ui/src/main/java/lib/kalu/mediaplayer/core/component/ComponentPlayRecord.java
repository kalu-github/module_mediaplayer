package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

/**
 * 续播
 */
public class ComponentPlayRecord extends RelativeLayout implements ComponentApi {
    public ComponentPlayRecord(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_play_record, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        // 续播
        if (playState == PlayerType.StateType.STATE_SEEK_PLAY_RECORD) {
            LogUtil.log("ComponentPlayRecord => playStatus = " + playState);
            show();
        }
    }

    @Override
    public final void hide() {
        try {
            findViewById(R.id.module_mediaplayer_component_play_record_root).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentPlayRecord => hide => Exception " + e.getMessage());
        }
    }

    @Override
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_play_record_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentPlayRecord => show => Exception " + e.getMessage());
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    PlayerView playerView = getPlayerView();
                    StartArgs tags = playerView.getTags();
                    long seek = tags.getSeek();
                    playerView.seekTo(seek);
                } catch (Exception e) {
                } finally {
                    hide();
                }
            }
        }, 2000);
    }
}
