package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;
import lib.kalu.mediaplayer.widget.seek.SeekBar;

public class ComponentWarningPlayInfo extends RelativeLayout implements ComponentApiWarningPlayInfo {

    public ComponentWarningPlayInfo(Context context) {
        super(context);
        inflate();
        setComponentShowPlayInfoRecord(false);
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
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_VIDEO_RENDERING_START:
                LogUtil.log("ComponentWarningPlayInfo => playState = " + playState);
                try {
                    boolean componentShowing = isComponentShowing();
                    if (componentShowing)
                        throw new Exception("warning: componentShowing false");
                    boolean trySee = isTrySee();
                    if (trySee)
                        throw new Exception("warning: trySee true");
                    show();
                } catch (Exception d) {
                }
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
        ComponentApiWarningPlayInfo.super.show();

        // 播放记录提示
        try {
            boolean showWarningPlayInfoRecord = isShowWarningPlayInfoRecord();
            if (!showWarningPlayInfoRecord)
                throw new Exception("warning: showWarningPlayInfoRecord false");
            long seek = getSeek();
            if (seek <= 0L)
                throw new Exception("warning: seek <=0");
            String millis = TimeUtil.formatTimeMillis(seek);
            String string = getResources().getString(R.string.module_mediaplayer_string_play_record, millis);
            TextView textView = findViewById(R.id.module_mediaplayer_component_warning_play_info_record);
            textView.setText(string);
        } catch (Exception e) {
        }

        try {
            String mediaTitle = getTitle();
            TextView textView = findViewById(R.id.module_mediaplayer_component_warning_play_info_title);
            textView.setText(mediaTitle);
        } catch (Exception e) {
        }

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration<=0");
            long position = getPosition();
            long max = getMaxDuration();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_play_info_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
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
        ComponentApiWarningPlayInfo.super.hide();

        // 播放记录提示
        try {
            TextView textView = findViewById(R.id.module_mediaplayer_component_warning_play_info_record);
            textView.setText("");
        } catch (Exception e) {
        }
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_warning_play_info_root;
    }
}
