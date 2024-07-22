package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
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
            case PlayerType.EventType.PLAY_WHEN_READY_FALSE:
                LogUtil.log("ComponentWarningPlayInfo => callEvent => PLAY_WHEN_READY_FALSE");
                hide();
                break;
            case PlayerType.EventType.RENDER_FIRST_FRAME:
                LogUtil.log("ComponentWarningPlayInfo => callEvent => RENDER_FIRST_FRAME");
                show();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {

        // 自动隐藏
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            long seek = getSeek();
            long cast = position - seek;
            if (cast < 2000L)
                throw new Exception("warning: cast < 2000");
            hide();
        } catch (Exception e) {
        }

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
            seekBar.setMax((int) (trySeeDuration > 0 ? trySeeDuration : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {
//        LogUtil.log("ComponentWarningPlayInfo => show");

        try {
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySee true");
            // 1
            ComponentApiWarningPlayInfo.super.show();
            // 2. 标题
            TextView titleView = findViewById(R.id.module_mediaplayer_component_warning_play_info_title);
            titleView.setText(getTitle());
            // 3. 播放记录提示
            boolean showWarningPlayInfoRecord = isShowWarningPlayInfoRecord();
            if (showWarningPlayInfoRecord) {
                long seek = getSeek();
                if (seek > 0L) {
                    String millis = TimeUtil.formatTimeMillis(seek);
                    String string = getResources().getString(R.string.module_mediaplayer_string_play_record, millis);
                    TextView textView = findViewById(R.id.module_mediaplayer_component_warning_play_info_record);
                    textView.setText(string);
                }
            }
        } catch (Exception e) {
        }

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration<=0");
            long position = getPosition();
            long trySeeDuration = getTrySeeDuration();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_play_info_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (trySeeDuration > 0L ? trySeeDuration : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public void hide() {
//        LogUtil.log("ComponentWarningPlayInfo => hide");

        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            // 1
            ComponentApiWarningPlayInfo.super.hide();
            // 2. 标题
            TextView titleView = findViewById(R.id.module_mediaplayer_component_warning_play_info_title);
            titleView.setText("");
            // 3. 播放记录提示
            TextView recordView = findViewById(R.id.module_mediaplayer_component_warning_play_info_record);
            recordView.setText("");
        } catch (Exception e) {
            LogUtil.log("ComponentWarningPlayInfo => hide => Exception " + e.getMessage());
        }
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_warning_play_info_root;
    }
}
