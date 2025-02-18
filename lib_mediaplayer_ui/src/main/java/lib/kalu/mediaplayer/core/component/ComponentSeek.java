package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentSeek extends RelativeLayout implements ComponentApiSeek {

    public ComponentSeek(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_seek_root;
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_seek;
    }

    @Override
    public void initSeekBarChangeListener() {
        try {
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("warning: null == seekBar");
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    try {
                        hide();
                        int progress = seekBar.getProgress();
                        seekTo(progress);
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => initSeekBarChangeListener => " + e.getMessage());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        boolean adShowing = isComponentShowing(ComponentApiAD.class);
        if (adShowing)
            return false;

        // 试看
        try {
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySee true");
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => dispatchKeyEvent => Exception1 " + e.getMessage());
            return false;
        }

        // seekForward => start
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                boolean bufferingShowing = isComponentShowing(ComponentApiBuffering.class);
                if (bufferingShowing)
                    throw new Exception("warning: ComponentApiBuffering true");
                boolean warningPlayInfoShowing = isComponentShowing(ComponentApiWarningPlayInfo.class);
                if (warningPlayInfoShowing)
                    throw new Exception("warning: ComponentApiWarningPlayInfo true");
                boolean menuShowing = isComponentShowing(ComponentApiMenu.class);
                if (menuShowing)
                    throw new Exception("warning: ComponentApiMenu true");
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                int repeatCount = event.getRepeatCount();
                actionDown(repeatCount, KeyEvent.KEYCODE_DPAD_RIGHT);
                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentSeek => dispatchKeyEvent => Exception2 " + e.getMessage());
            }
        }
        // seekForward => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                boolean menuShowing = isComponentShowing(ComponentApiMenu.class);
                if (menuShowing)
                    throw new Exception("warning: ComponentApiMenu true");
                onUpdateProgress(true, -1, -1, -1);
                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentSeek => dispatchKeyEvent => Exception3 " + e.getMessage());
            }
        }
        // seekRewind => start
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                boolean bufferingShowing = isComponentShowing(ComponentApiBuffering.class);
                LogUtil.log("ComponentSeek => dispatchKeyEvent => KEYCODE_DPAD_LEFT => bufferingShowing = " + bufferingShowing);
                if (bufferingShowing)
                    throw new Exception("warning: ComponentApiBuffering true");
                boolean warningPlayInfoShowing = isComponentShowing(ComponentApiWarningPlayInfo.class);
                LogUtil.log("ComponentSeek => dispatchKeyEvent => KEYCODE_DPAD_LEFT => warningPlayInfoShowing = " + warningPlayInfoShowing);
                if (warningPlayInfoShowing)
                    throw new Exception("warning: ComponentApiWarningPlayInfo true");
                boolean menuShowing = isComponentShowing(ComponentApiMenu.class);
                LogUtil.log("ComponentSeek => dispatchKeyEvent => KEYCODE_DPAD_LEFT => menuShowing = " + menuShowing);
                if (menuShowing)
                    throw new Exception("warning: ComponentApiMenu true");
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                show();
                int repeatCount = event.getRepeatCount();
                actionDown(repeatCount, KeyEvent.KEYCODE_DPAD_LEFT);
                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentSeek => dispatchKeyEvent => Exception4 " + e.getMessage());
            }
        }
        // seekRewind => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                boolean menuShowing = isComponentShowing(ComponentApiMenu.class);
                if (menuShowing)
                    throw new Exception("warning: ComponentApiMenu true");
                onUpdateProgress(true, -1, -1, -1);
                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentSeek => dispatchKeyEvent => Exception5 " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.SEEK_START_FORWARD:
                LogUtil.log("ComponentSeek => callEvent => SEEK_START_FORWARD");
                show();
                break;
            case PlayerType.EventType.SEEK_START_REWIND:
                LogUtil.log("ComponentSeek => callEvent => SEEK_START_REWIND");
                show();
                break;
            case PlayerType.EventType.SEEK_FINISH:
                LogUtil.log("ComponentSeek => callEvent => SEEK_FINISH");
                hide();
                long duration = getDuration();
                long position = getPosition();
                long trySeeDuration = getTrySeeDuration();
                onUpdateProgress(false, trySeeDuration, position, duration);
                break;
            case PlayerType.EventType.INIT:
            case PlayerType.EventType.ERROR:
            case PlayerType.EventType.COMPLETE:
                onUpdateProgress(false, 0, 0, 0);
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long progress, long duration) {
        //  LogUtil.log("ComponentSeek => onUpdateProgress => isFromUser = " + isFromUser + ", trySeeDuration = " + trySeeDuration + ", progress = " + progress + ", duration = " + duration);

        try {
//            if (progress < 0L) {
//                progress = 0L;
//            }
//            if (duration < 0L) {
//                duration = 0L;
//            }

            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (isFromUser && trySeeDuration == -1 && progress == -1 && duration == -1) {
                long position = getPosition();
                int seek = seekBar.getProgress();
//                LogUtil.log("ComponentSeek11 => onUpdateProgress11 => seek = " + seek + ", position = " + position);
                long range = Math.abs(position - seek);
                if (range > 1000L) {
                    clearTimeMillis();
                    superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_HIDE);
                    seekTo(seek);
                    setHovered(false);
                    hide();
                } else {
                    updateTimeMillis();
                }
            } else if (isFromUser) {
                long position = getPosition();
//                LogUtil.log("ComponentSeek11 => onUpdateProgress22 => progress = " + progress + ", position = " + position);
                seekBar.setProgressHovered((int) position);
                seekBar.setProgress((int) progress);
                seekBar.setMax((int) (trySeeDuration > 0 ? trySeeDuration : duration));
                setHovered(true);
                int visibility = seekBar.getVisibility();
                if (visibility == View.VISIBLE) {
                    show();
                }
            } else {
                long castTimeMillis = getCastTimeMillis();
                if (castTimeMillis > 1000L) {
                    hide();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {
        try {
            boolean bufferingShowing = isComponentShowing(ComponentApiBuffering.class);
            if (bufferingShowing)
                throw new Exception("warning: ComponentApiBuffering true");
            boolean warningPlayInfoShowing = isComponentShowing(ComponentApiWarningPlayInfo.class);
            if (warningPlayInfoShowing)
                throw new Exception("warning: ComponentApiWarningPlayInfo true");
            boolean menuShowing = isComponentShowing(ComponentApiMenu.class);
            if (menuShowing)
                throw new Exception("warning: ComponentApiMenu true");
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            ComponentApiSeek.super.show();
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => show => Exception1 " + e.getMessage());
        }
    }

    @Override
    public void hide() {
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            ComponentApiSeek.super.hide();
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => hide => Exception " + e.getMessage());
        }
    }

    /**********/

    @Override
    public void actionDown(int repeatCount, int keyCode) {

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration <=0");
            // click
            if (repeatCount == 0) {
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                LogUtil.log("ComponentSeek => actionDown => progress = " + progress);
                if (progress >= duration)
                    throw new Exception("warning: range >= duration");
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 8000 : -8000);
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 4000 : -4000);
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1000 : -1000);
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 400 : -400);
                }
                // 时间太短了
                else {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 100 : -100);
                }
                if (progress >= duration) {
                    progress = (int) duration;
                }

                long trySeeDuration = getTrySeeDuration();
                onUpdateProgress(true, trySeeDuration, progress, duration);
            }
            // long click
            else {
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
                if (progress >= duration)
                    throw new Exception("warning: progress > duration");
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 60000 : -60000);
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 30000 : -30000);
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 10000 : -10000);
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 5000 : -5000);
                }
                // 时间太短了
                else {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1000 : -1000);
                }
                if (progress >= duration) {
                    progress = (int) duration;
                }
                long trySeeDuration = getTrySeeDuration();
                onUpdateProgress(true, trySeeDuration, progress, duration);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => startInitMsg => Exception " + e.getMessage());
        }
    }
}
