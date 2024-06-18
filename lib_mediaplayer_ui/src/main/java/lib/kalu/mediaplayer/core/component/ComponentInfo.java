package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

public class ComponentInfo extends RelativeLayout implements ComponentApi {

    public ComponentInfo(Context context) {
        super(context);
        setEnabled(false);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_info, this, true);
    }

    @Override
    public boolean enableDispatchKeyEvent() {
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            boolean componentShowing = isComponentShowing();
            if (componentShowing) {
                hide();
                return true;
            }
        }
        return false;
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_START:
                LogUtil.log("ComponentPlayInfo => playState = " + playState);
                show();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        boolean enabled = isEnabled();
        if (!enabled)
            return;

        try {
            if (position < 0 || duration < 0)
                throw new Exception();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_info_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
        }

        // position
        try {
            String optString = TimeUtil.formatTimeMillis(position, (max > 0 ? max : duration));
            TextView textView = findViewById(R.id.module_mediaplayer_component_info_position);
            textView.setText(optString);
        } catch (Exception e) {
        }

        // duration
        try {
            String optString = TimeUtil.formatTimeMillis(max > 0 ? max : duration);
            TextView textView = findViewById(R.id.module_mediaplayer_component_info_duration);
            textView.setText(optString);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isComponentShowing() {
        try {
            return findViewById(R.id.module_mediaplayer_component_info_root).getVisibility() == View.VISIBLE;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public final void show() {
        setEnabled(true);
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            String mediaTitle = tags.getMediaTitle();
            TextView textView = findViewById(R.id.module_mediaplayer_component_info_title);
            textView.setText(mediaTitle);
        } catch (Exception e) {
        }


        try {
            findViewById(R.id.module_mediaplayer_component_info_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }

        // 2s后隐藏
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, 2000);
    }

    @Override
    public final void hide() {
        setEnabled(false);
        try {
            findViewById(R.id.module_mediaplayer_component_info_root).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }
}
