package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.seek.SeekBar;

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
                boolean warningTrySeeShowing = isComponentShowing(ComponentApiWarningTrySee.class);
                if (warningTrySeeShowing)
                    throw new Exception("warning: ComponentApiWarningTrySee true");
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
            case PlayerType.EventType.PAUSE:
                LogUtil.log("ComponentPause[show] => PAUSE");
                show();
                break;
            case PlayerType.EventType.RESUME:
                LogUtil.log("ComponentPause[hide] => RESUME");
                hide();
                break;
            case PlayerType.EventType.SEEK_COMPONENT_SHOW:
                LogUtil.log("ComponentPause[hide] => SEEK_COMPONENT_SHOW");
                hide();
                break;
            case PlayerType.EventType.SEEK_COMPONENT_HIDE:
                LogUtil.log("ComponentPause[show] => SEEK_COMPONENT_HIDE");
                boolean playing = isPlaying();
                if (!playing) {
                    long trySeeDuration = getTrySeeDuration();
                    long position = getPosition();
                    long duration = getDuration();
                    onUpdateProgress(false, trySeeDuration, position, duration);
                    show();
                }
                break;
            case PlayerType.EventType.START_PLAY_WHEN_READY_FALSE:
                LogUtil.log("ComponentPause => callEvent => START_PLAY_WHEN_READY_FALSE");
                try {
                    boolean componentShowing = isComponentShowing();
                    if (componentShowing)
                        throw new Exception("warning: componentShowing true");
                    show();
                } catch (Exception e) {
                    LogUtil.log("ComponentPause => callEvent => Exception[START_PLAY_WHEN_READY_NO] " + e.getMessage());
                }
                break;
            case PlayerType.EventType.COMPONENT_MENU_SHOW:
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
            case PlayerType.EventType.COMPONENT_MENU_HIDE:
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
            if (trySeeDuration > 0L)
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
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySee true");
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            ComponentApiPause.super.show();
        } catch (Exception e) {
            LogUtil.log("ComponentPause => show => Exception " + e.getMessage());
        }


        try {
            String mediaTitle = getTitle();
            setComponentText(mediaTitle);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {
        try {
            if (duration <= 0) {
                duration = 0L;
            }
            if (position < 0) {
                position = 0L;
            }
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_pause_sb);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (trySeeDuration > 0L ? trySeeDuration : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public int initViewIdBackground() {
        return R.id.module_mediaplayer_component_pause_bg;
    }

    @Override
    public int initViewIdText() {
        return R.id.module_mediaplayer_component_pause_title;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_pause_root;
    }
}