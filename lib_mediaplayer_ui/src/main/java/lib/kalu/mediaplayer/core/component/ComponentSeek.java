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


public class ComponentSeek extends RelativeLayout implements ComponentApiSeek {

    public ComponentSeek(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_seek, this, true);
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
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_seek_ui).setVisibility(View.VISIBLE);
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
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_seek_ui).setVisibility(View.GONE);
        } catch (Exception e) {
        }
        try {
            setTag(R.id.module_mediaplayer_component_seek_sb, false);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
//        LogUtil.log("ComponentSeek => onUpdateProgress => isFromUser = " + isFromUser + ", max = " + max + ", position = " + position + ", duration = " + duration);

        // 进度条
        try {
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("warning: null == seekBar");
            Object tag = seekBar.getTag();
            if (null != tag && !isFromUser)
                throw new Exception("warning: user touch");
            if (null == tag && !isFromUser) {
                int visibility = seekBar.getVisibility();
                if (visibility != View.VISIBLE) {
                    throw new Exception("warning: visibility != View.VISIBLE");
                }
            }
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
        }

        // 进度时间
        try {
            TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
            if (null == viewMax)
                throw new Exception("warning: null == viewMax");
            Object tag = viewMax.getTag();
            if (null != tag && !isFromUser)
                throw new Exception("warning: user touch");
            if (null == tag && !isFromUser) {
                int visibility = viewMax.getVisibility();
                if (visibility != View.VISIBLE) {
                    throw new Exception("warning: visibility != View.VISIBLE");
                }
            }
            // ms => s
            StringBuilder builder = new StringBuilder();
            long d = (max > 0 ? max : duration) / 1000;
            long d1 = d / 60;
            long d2 = d % 60;
            if (d1 < 10) {
                builder.append("0");
            }
            builder.append(d1);
            builder.append(":");
            if (d2 < 10) {
                builder.append("0");
            }
            builder.append(d2);

            String s = builder.toString();
            viewMax.setText(s);
        } catch (Exception e) {
        }

        // 总时间
        try {
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
            if (null == viewPosition)
                throw new Exception("warning: null == viewPosition");
            Object tag = viewPosition.getTag();
            if (null != tag && !isFromUser)
                throw new Exception("warning: user touch");
            if (null == tag && !isFromUser) {
                int visibility = viewPosition.getVisibility();
                if (visibility != View.VISIBLE) {
                    throw new Exception("warning: visibility != View.VISIBLE");
                }
            }

            // ms => s
            long c = position / 1000;
            long c1 = c / 60;
            long c2 = c % 60;
            StringBuilder builder = new StringBuilder();
            if (c1 < 10) {
                builder.append("0");
            }
            builder.append(c1);
            builder.append(":");
            if (c2 < 10) {
                builder.append("0");
            }
            builder.append(c2);
            String s = builder.toString();
            viewPosition.setText(s);
        } catch (Exception e) {
        }
    }

    @Override
    public void setUserTouch(boolean status) {
        try {
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekBar error: null");
            seekBar.setTag(status ? true : null);
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => setUserTouch => " + e.getMessage());
        }
        try {
            TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
            if (null == viewMax)
                throw new Exception("warning: null == viewMax");
            viewMax.setTag(status ? true : null);
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => setUserTouch => " + e.getMessage());
        }
        try {
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
            if (null == viewPosition)
                throw new Exception("warning: null == viewPosition");
            viewPosition.setTag(status ? true : null);
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => setUserTouch => " + e.getMessage());
        }
    }

    @Override
    public SeekBar findSeekBar() {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekBar error: null");
            return seekBar;
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => findSeekBar => " + e.getMessage());
            return null;
        }
    }

    @Override
    public void initSeekBarChangeListener() {
        try {
            SeekBar seekBar = findSeekBar();
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
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("warning: null == seekBar");
            int duration = seekBar.getMax();
            int progress = seekBar.getProgress();
            if (progress > duration) {
                progress = duration;
            }
            getPlayerView().seekTo(progress);
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => seekToStopTrackingTouch => " + e.getMessage());
        }
    }

    @Override
    public boolean isComponentShowing() {
        try {
            int visibility1 = findViewById(R.id.module_mediaplayer_component_seek_bg).getVisibility();
            int visibility2 = findViewById(R.id.module_mediaplayer_component_seek_ui).getVisibility();
            return visibility1 == View.VISIBLE && visibility2 == View.VISIBLE;
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => isComponentShowing => " + e.getMessage());
            return false;
        }
    }
}
