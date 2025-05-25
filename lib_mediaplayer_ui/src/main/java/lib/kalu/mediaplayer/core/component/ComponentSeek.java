package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.Arrays;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentSeek extends RelativeLayout implements ComponentApi {

    public ComponentSeek(Context context) {
        super(context);
        inflate();
        lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtil.log("ComponentSeek => dispatchKeyEvent => action =  " + event.getAction() + ", keyCode = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount());
        try {
            // 试看
            long trySeeDuration = getTrySeeDuration();
            if (trySeeDuration > 0L)
                throw new Exception("warning: trySeeDuration > 0L");
            // seekForward => start
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                onUpdateProgress(true, -2, -2, -2);
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                int repeatCount = event.getRepeatCount();
                actionDown(repeatCount, KeyEvent.KEYCODE_DPAD_RIGHT);
                return true;
            }
            // seekForward => stop
            else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                onUpdateProgress(true, -1, -1, -1);
                return true;
            }
            // seekRewind => start
            else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                onUpdateProgress(true, -2, -2, -2);
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_SEEK_SHOW);
                show();
                int repeatCount = event.getRepeatCount();
                actionDown(repeatCount, KeyEvent.KEYCODE_DPAD_LEFT);
                return true;
            }
            // seekRewind => stop
            else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                onUpdateProgress(true, -1, -1, -1);
                return true;
            }

            throw new Exception("warning: not find");
        } catch (Exception e) {
            LogUtil.log("ComponentSeek => dispatchKeyEvent => Exception " + e.getMessage());
            return false;
        }
    }

    @Override
    public void callEvent(int playState) {
        switch (playState) {
//            case PlayerType.EventType.SEEK_START_FORWARD:
//                LogUtil.log("ComponentSeek => callEvent => SEEK_START_FORWARD");
//                show();
//                break;
//            case PlayerType.EventType.SEEK_START_REWIND:
//                LogUtil.log("ComponentSeek => callEvent => SEEK_START_REWIND");
//                show();
//                break;
//            case PlayerType.EventType.SEEK_FINISH:
//                LogUtil.log("ComponentSeek => callEvent => SEEK_FINISH");
//                hide();
//                long duration = getDuration();
//                long position = getPosition();
//                long trySeeDuration = getTrySeeDuration();
//                onUpdateProgress(false, trySeeDuration, position, duration);
//                break;
//            case PlayerType.EventType.INIT:
//            case PlayerType.EventType.ERROR:
//            case PlayerType.EventType.COMPLETE:
//                onUpdateProgress(false, 0, 0, 0);
//                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long progress, long duration) {
        LogUtil.log("ComponentSeek => onUpdateProgress => isFromUser = " + isFromUser + ", trySeeDuration = " + trySeeDuration + ", progress = " + progress + ", duration = " + duration);


        lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);

        if (isFromUser && progress == -2 && duration == -2) {
            seekBar.setHovered(true);
            show();
        } else if (isFromUser && progress == -1 && duration == -1) {
            seekBar.setHovered(false);
            hide();
        } else if (isFromUser) {
            seekBar.setMax((int) duration);
            seekBar.setProgress((int) progress);
            seekBar.setTextInfo(progress, duration);
        } else {
            seekBar.setMax((int) duration);
            boolean hovered = seekBar.isHovered();
            if (!hovered) {
                seekBar.setProgress((int) progress);
            }
            seekBar.setTextInfo(progress, duration);
        }
    }

    /**********/

    private void actionDown(int repeatCount, int keyCode) {

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration <=0");
            // click
            if (repeatCount == 0) {
                lib.kalu.mediaplayer.widget.seek.SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
                int progress = seekBar.getProgress();
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

    private void updateTimeMillis() {
        try {
            long millis = System.currentTimeMillis();
            ((View) this).setTag(millis);
        } catch (Exception e) {
        }
    }

    private void clearTimeMillis() {
        try {
            ((View) this).setTag(null);
        } catch (Exception e) {
        }
    }

    private long getCastTimeMillis() {
        try {
            Object tag = ((View) this).getTag();
            if (null == tag)
                throw new Exception("warning: tag null");
            long start = (long) tag;
            long millis = System.currentTimeMillis();
            return Math.abs(millis - start);
        } catch (Exception e) {
            return 0L;
        }
    }
}
