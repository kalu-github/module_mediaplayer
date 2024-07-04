package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


public class ComponentSeek extends RelativeLayout implements ComponentApiSeek {

    public ComponentSeek(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_seek, this, true);
    }

    @Override
    public boolean enableDispatchKeyEvent() {
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        // 试看
        try {
            StartArgs tags = getPlayerView().getTags();
            boolean trySee = tags.isTrySee();
            if (trySee)
                return false;
        } catch (Exception e) {
        }

        // seekForward => start
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                int repeatCount = event.getRepeatCount();
                startInitMsg(repeatCount, KeyEvent.KEYCODE_DPAD_RIGHT);
                return true;
            }
        }
        // seekForward => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                startDelayedMsg(KeyEvent.KEYCODE_DPAD_RIGHT);
                return true;
            }
        }
        // seekRewind => start
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                int repeatCount = event.getRepeatCount();
                startInitMsg(repeatCount, KeyEvent.KEYCODE_DPAD_LEFT);
                return true;
            }
        }
        // seekRewind => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                startDelayedMsg(KeyEvent.KEYCODE_DPAD_LEFT);
                return true;
            }
        }

        return false;
    }


    @Override
    public void seekToPosition(int keyCode, int position) {
        try {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                getPlayerView().callEventListener(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
                getPlayerView().seekTo(position);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                getPlayerView().callEventListener(PlayerType.StateType.STATE_FAST_REWIND_STOP);
                getPlayerView().seekTo(position);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => seekToPosition => " + e.getMessage());
        }
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                LogUtil.log("ComponentSeek => callEventListener => show => playState = " + playState);
                setUserTouch(true);
                show();
                break;
            case PlayerType.StateType.STATE_FAST_FORWARD_STOP:
            case PlayerType.StateType.STATE_FAST_REWIND_STOP:
                LogUtil.log("ComponentSeek => callEventListener => gone => playState = " + playState);
                setUserTouch(false);
                hide();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_END:
                onUpdateProgress(false, 0, 0, 0);
                break;
        }
    }

    @Override
    public void callWindowEvent(int windowState) {
        switch (windowState) {
            default:
                hide();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
//        LogUtil.log("ComponentSeek => onUpdateProgress => isFromUser = " + isFromUser + ", max = " + max + ", position = " + position + ", duration = " + duration);

        // 进度条
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

            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findSeekBar();
            // 拖动进度条
            if (isFromUser) {
                seekBar.setProgress((int) position);
            }
            // 默认
            else {
                seekBar.setPlayPosition(position);
            }

            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
        }
    }

    @Override
    public void setUserTouch(boolean status) {
        try {
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekBar error: null");
            seekBar.setTag(status ? true : null);
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => setUserTouch => " + e.getMessage());
        }
    }

    @Override
    public lib.kalu.mediaplayer.widget.seek.SeekBar findSeekBar() {
        try {
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekBar error: null");
            return seekBar;
        } catch (Exception e) {
//            LogUtil.log("ComponentSeek => findSeekBar => " + e.getMessage());
            return null;
        }
    }

    @Override
    public int getSeekBarMax() {
        try {
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findSeekBar();
            return seekBar.getMax();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getSeekBarProgress() {
        try {
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findSeekBar();
            return seekBar.getProgress();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void initSeekBarChangeListener() {
        try {
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("warning: null == seekBar");
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    setUserTouch(true);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    setUserTouch(false);
                    seekToStopTrackingTouch();
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => initSeekBarChangeListener => " + e.getMessage());
        }
    }

    @Override
    public void seekToStopTrackingTouch() {
        try {
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("warning: null == seekBar");
            long max = seekBar.getMax();
            long playPosition = seekBar.getPlayPosition();
            seekBar.clearPlayPosition();
            if (playPosition > max) {
                playPosition = max;
            } else if (playPosition <= 0) {
                playPosition = 0;
            }
            getPlayerView().resume();
            getPlayerView().seekTo(playPosition);
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => seekToStopTrackingTouch => " + e.getMessage());
        }
    }

    @Override
    public boolean isComponentShowing() {
        try {
            int visibility = findViewById(R.id.module_mediaplayer_component_seek_root).getVisibility();
            return visibility == View.VISIBLE;
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => isComponentShowing => " + e.getMessage());
            return false;
        }
    }

    @Override
    public final void show() {

        try {
            long position = getPosition();
            long duration = getDuration();
            lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findSeekBar();
            seekBar.setMax((int) duration);
            seekBar.setProgress((int) position);
            seekBar.setPlayPosition((int) position);
        } catch (Exception e) {
        }

        try {
            findViewById(R.id.module_mediaplayer_component_seek_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => show => " + e.getMessage());
        }
        try {
            setTag(R.id.module_mediaplayer_component_seek_sb, true);
        } catch (Exception e) {
        }
    }

    @Override
    public final void hide() {
        try {
            findViewById(R.id.module_mediaplayer_component_seek_root).setVisibility(View.GONE);
        } catch (Exception e) {
        }
        try {
            setTag(R.id.module_mediaplayer_component_seek_sb, false);
        } catch (Exception e) {
        }
    }
}
