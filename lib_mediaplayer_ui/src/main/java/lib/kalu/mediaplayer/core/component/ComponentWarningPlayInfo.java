package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;
import lib.kalu.mediaplayer.widget.seek.SeekBar;

public class ComponentWarningPlayInfo extends RelativeLayout implements ComponentApi {

    public ComponentWarningPlayInfo(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_warning_play_info;
    }

    @Override
    public boolean enableDispatchKeyEvent() {
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            boolean componentShowing = isComponentShowing();
            if (componentShowing) {
                hide();
            }
        }
        return false;
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_VIDEO_RENDERING_START:
                LogUtil.log("ComponentWarningPlayInfo => playState = " + playState);
                show();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            if (position < 0) {
                position = 0;
            }
            if (duration < 0) {
                duration = 0;
            }
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_play_info_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {
        ComponentApi.super.show();

        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            String mediaTitle = tags.getTitle();
            TextView textView = findViewById(R.id.module_mediaplayer_component_warning_play_info_title);
            textView.setText(mediaTitle);
        } catch (Exception e) {
        }

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration<=0");
            long position = getPosition();
            long maxDuration = getMaxDuration();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_play_info_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (maxDuration > 0 ? maxDuration : duration));
        } catch (Exception e) {
        }

        // 4s后隐藏
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, 2000);
    }

    @Override
    public void hide() {
        ComponentApi.super.hide();

        try {
            ViewGroup viewGroup = (ViewGroup) getParent();
            viewGroup.removeView(this);
        } catch (Exception e) {
        }
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_warning_play_info_root;
    }
}
