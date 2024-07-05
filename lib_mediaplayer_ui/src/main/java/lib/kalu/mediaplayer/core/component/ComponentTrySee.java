package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public class ComponentTrySee extends RelativeLayout implements ComponentApi {

    public ComponentTrySee(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_try_see, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_TRY_TO_SEE_FINISH:
                LogUtil.log("ComponentTrySee => playState = " + playState);
                try {
                    PlayerView playerView = getPlayerView();
                    if (null == playerView)
                        throw new Exception("error: playerView null");
                    StartArgs tags = playerView.getTags();
                    if (null == tags)
                        throw new Exception("error: tags null");
                    boolean trySee = tags.isTrySee();
                    if (!trySee)
                        throw new Exception("warning: trySee false");
                    setComponentText("试看结束...");
                    show();
                } catch (Exception e) {
                    hide();
                }
                break;
            case PlayerType.StateType.STATE_INIT:
                LogUtil.log("ComponentTrySee => playState = " + playState);
                try {
                    PlayerView playerView = getPlayerView();
                    if (null == playerView)
                        throw new Exception("error: playerView null");
                    StartArgs tags = playerView.getTags();
                    if (null == tags)
                        throw new Exception("error: tags null");
                    boolean trySee = tags.isTrySee();
                    if (!trySee)
                        throw new Exception("warning: trySee false");
                    setComponentText("试看中...");
                    show();
                } catch (Exception e) {
                    hide();
                }
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        try {
            if (position < 0 || duration < 0)
                throw new Exception();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_try_see_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
        }

        // position
        try {
            String optString = TimeUtil.formatTimeMillis(position, (max > 0 ? max : duration));
            TextView textView = findViewById(R.id.module_mediaplayer_component_try_see_position);
            textView.setText(optString);
        } catch (Exception e) {
        }

        // duration
        try {
            String optString = TimeUtil.formatTimeMillis(max > 0 ? max : duration);
            TextView textView = findViewById(R.id.module_mediaplayer_component_try_see_duration);
            textView.setText(optString);
        } catch (Exception e) {
        }
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_try_see_root;
    }

    @Override
    public int initLayoutIdText() {
        return R.id.module_mediaplayer_component_try_see_message;
    }
}
