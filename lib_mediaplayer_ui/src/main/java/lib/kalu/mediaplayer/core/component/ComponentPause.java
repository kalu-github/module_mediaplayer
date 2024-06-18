package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;
import lib.kalu.mediaplayer.widget.subtitle.model.Time;

public class ComponentPause extends RelativeLayout implements ComponentApiPause {

    public ComponentPause(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_pause, this, true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // toggle
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                toggle();
                return true;
            }
        }
        // toggle
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                toggle();
                return true;
            }
        }
        return false;
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_PAUSE:
                LogUtil.log("ComponentPause[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                LogUtil.log("ComponentPause[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public boolean isComponentShowing() {
        return findViewById(R.id.module_mediaplayer_component_pause_root).getVisibility() == View.VISIBLE;
    }

    @Override
    public final void hide() {

        try {
            TextView textView = findViewById(R.id.module_mediaplayer_component_pause_title);
            textView.setText("");
        } catch (Exception e) {
        }

        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            boolean trySee = tags.isTrySee();
            if (trySee)
                throw new Exception("warning: trySee true");
            findViewById(R.id.module_mediaplayer_component_pause_root).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentPause => hide => Exception " + e.getMessage());
        }
    }

    @Override
    public final void show() {

        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            String mediaTitle = tags.getMediaTitle();
            TextView textView = findViewById(R.id.module_mediaplayer_component_pause_title);
            textView.setText(mediaTitle);
        } catch (Exception e) {
        }


        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            boolean trySee = tags.isTrySee();
            if (trySee)
                throw new Exception("warning: trySee true");
            findViewById(R.id.module_mediaplayer_component_pause_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentPause => show => Exception " + e.getMessage());
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        try {
            if (position < 0 || duration < 0)
                throw new Exception();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_pause_sb);
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
        }

        // position
        try {
            String optString = TimeUtil.formatTimeMillis(position, (max > 0 ? max : duration));
            TextView textView = findViewById(R.id.module_mediaplayer_component_pause_position);
            textView.setText(optString);
        } catch (Exception e) {
        }

        // duration
        try {
            String optString = TimeUtil.formatTimeMillis(max > 0 ? max : duration);
            TextView textView = findViewById(R.id.module_mediaplayer_component_pause_duration);
            textView.setText(optString);
        } catch (Exception e) {
        }
    }

    /*************/

    @Override
    public int initLayoutIdComponentBackground() {
        return R.id.module_mediaplayer_component_pause_bg;
    }
}