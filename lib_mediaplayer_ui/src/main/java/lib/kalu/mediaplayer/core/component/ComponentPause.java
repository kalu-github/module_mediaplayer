package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;
import lib.kalu.mediaplayer.widget.player.PlayerView;
import lib.kalu.mediaplayer.widget.seek.SeekBar;
import lib.kalu.mediaplayer.widget.subtitle.model.Time;

public class ComponentPause extends RelativeLayout implements ComponentApiPause {

    public ComponentPause(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_pause;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                boolean prepared = isPrepared();
                if (!prepared)
                    throw new Exception("warning: prepared false");
                boolean playing = isPlaying();
                if (playing)
                    throw new Exception("warning: playing true");
                resume();
                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentPause => dispatchKeyEvent => Exception1 " + e.getMessage());
            }
        }
        // keycode_enter || keycode_dpad_center
        else if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER)) {
            try {
                boolean menuShowing = isComponentShowing(ComponentApiMenu.class);
                if (menuShowing)
                    throw new Exception("warning: ComponentApiMenu true");
                boolean bufferingShowing = isComponentShowing(ComponentApiBuffering.class);
                if (bufferingShowing)
                    throw new Exception("warning: ComponentApiBuffering true");
                boolean seekShowing = isComponentShowing(ComponentApiSeek.class);
                if (seekShowing)
                    throw new Exception("warning: ComponentApiSeek true");
                boolean warningPlayInfoShowing = isComponentShowing(ComponentApiWarningPlayInfo.class);
                if (warningPlayInfoShowing)
                    throw new Exception("warning: ComponentApiWarningPlayInfo true");
                toggle();
                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentPause => dispatchKeyEvent => Exception2 " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.PAUSE:
                LogUtil.log("ComponentPause[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.INIT:
            case PlayerType.StateType.RESUME:
            case PlayerType.StateType.FAST_FORWARD_START:
            case PlayerType.StateType.FAST_REWIND_START:
                LogUtil.log("ComponentPause[gone] => playState = " + playState);
                hide();
                break;
            case PlayerType.StateType.COMPONENT_MENU_SHOW:
                LogUtil.log("ComponentPause[show] => playState = " + playState);
                try {
                    boolean componentShowing = isComponentShowing();
                    if (!componentShowing)
                        throw new Exception("warning: componentShowing false");
                    setTag(true);
                    hide();
                } catch (Exception e) {
                    LogUtil.log("ComponentPause => callEventListener => hide => Exception2 " + playState);
                }
                break;
            case PlayerType.StateType.COMPONENT_MENU_HIDE:
                LogUtil.log("ComponentPause[gone] => playState = " + playState);
                try {
                    boolean componentShowing = isComponentShowing();
                    if (componentShowing)
                        throw new Exception("warning: componentShowing true");
                    Object tag = getTag();
                    if (null == tag)
                        throw new Exception("warning: tag null");
                    setTag(null);
                    show();
                } catch (Exception e) {
                    LogUtil.log("ComponentPause => callEventListener => show => Exception2 " + playState);
                }
                break;
        }
    }


    @Override
    public void hide() {

        try {
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration>0L)
                throw new Exception("warning: trySee true");
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            ComponentApiPause.super.hide();
        } catch (Exception e) {
            LogUtil.log("ComponentPause => hide => Exception " + e.getMessage());
        }

        try {
            setComponentText("");
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {

        try {
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration>0L)
                throw new Exception("warning: trySee true");
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            ComponentApiPause.super.show();
        } catch (Exception e) {
            LogUtil.log("ComponentPause => show => Exception " + e.getMessage());
        }

        try {
            for (int i = 0; i < 2; i++) {
                long duration = getDuration();
                if (duration <= 0)
                    throw new Exception("warning: duration<=0");
                long position = getPosition();
                long trySeeDuration = getTrySeeDuration();
                SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_pause_sb);
                seekBar.setProgress((int) position);
                seekBar.setMax((int) (trySeeDuration > 0L ? trySeeDuration : duration));
            }
        } catch (Exception e) {
        }

        try {
            String mediaTitle = getTitle();
            setComponentText(mediaTitle);
        } catch (Exception e) {
        }
    }

    /*************/

    @Override
    public int initLayoutIdComponentBackground() {
        return R.id.module_mediaplayer_component_pause_bg;
    }

    @Override
    public int initLayoutIdText() {
        return R.id.module_mediaplayer_component_pause_title;
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_pause_root;
    }
}