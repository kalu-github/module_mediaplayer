package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public class ComponentPause extends RelativeLayout implements ComponentApi {

    public ComponentPause(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_pause, this, true);
    }

    private long startTime = System.currentTimeMillis();

    @Override
    public boolean dispatchKeyEventComponent(KeyEvent event) {
        // center
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                try {
                    PlayerApi playerApi = getPlayerApi();
                    if (null == playerApi)
                        throw new Exception("playerApi error: null");
                    long position = playerApi.getPosition();
                    if (position <= 0)
                        throw new Exception("position error: " + position);
                    long timeMillis = System.currentTimeMillis();
                    if (timeMillis - startTime < 100)
                        throw new Exception("timeMillis error: " + timeMillis + ", startTime = " + startTime);
                    startTime = timeMillis;
                    playerApi.toggle();
                } catch (Exception e) {
                    MPLogUtil.log("ComponentPause => dispatchKeyEventComponent => " + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_PAUSE:
                MPLogUtil.log("ComponentPause[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_LOADING_START:
            case PlayerType.StateType.STATE_START:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_RESUME_IGNORE:
            case PlayerType.StateType.STATE_RESTAER:
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                MPLogUtil.log("ComponentPause[gone] => playState = " + playState);
                gone();
                break;
        }
    }

    @Override
    public final void gone() {
        try {
            findViewById(R.id.module_mediaplayer_component_pause_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_pause_title).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_pause_seekbar).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_pause_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_pause_title).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_pause_seekbar).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
        onSeekUpdateProgress(position, duration, true);
    }

    @Override
    public final void onSeekUpdateProgress(@NonNull long position, @NonNull long duration, @NonNull boolean updateTime) {
        try {
            if (position < 0 || duration < 0)
                throw new Exception();
            SeekBar seek = findViewById(R.id.module_mediaplayer_component_pause_sb);
            seek.setProgress((int) position);
            seek.setSecondaryProgress((int) position);
            seek.setMax((int) duration);
        } catch (Exception e) {
        }

        try {
            if (!updateTime)
                throw new Exception();
            if (position < 0 || duration < 0)
                throw new Exception();
            // ms => s
            long c = position / 1000;
            long c1 = c / 60;
            long c2 = c % 60;
            StringBuilder builderPosition = new StringBuilder();
            if (c1 < 10) {
                builderPosition.append("0");
            }
            builderPosition.append(c1);
            builderPosition.append(":");
            if (c2 < 10) {
                builderPosition.append("0");
            }
            builderPosition.append(c2);
            String strPosition = builderPosition.toString();

            // ms => s
            StringBuilder builderDuration = new StringBuilder();
            long d = duration / 1000;
            long d1 = d / 60;
            long d2 = d % 60;
            if (d1 < 10) {
                builderDuration.append("0");
            }
            builderDuration.append(d1);
            builderDuration.append(":");
            if (d2 < 10) {
                builderDuration.append("0");
            }
            builderDuration.append(d2);
            String strDuration = builderDuration.toString();

            TextView viewMax = findViewById(R.id.module_mediaplayer_component_pause_max);
            viewMax.setText(strDuration);
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_pause_position);
            viewPosition.setText(strPosition);
        } catch (Exception e) {
        }
    }

    /*************/

    @Override
    public final void setComponentBackgroundColorInt(int value) {
        try {
            setBackgroundColorInt(this, R.id.module_mediaplayer_component_pause_bg, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentBackgroundResource(int resid) {
        try {
            setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_pause_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageResource(int resid) {
        try {
            setImageResource(this, R.id.module_mediaplayer_component_pause_icon, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageUrl(@NonNull String url) {
        try {
            setImageUrl(this, R.id.module_mediaplayer_component_pause_icon, url);
        } catch (Exception e) {
        }
    }

    public final void setComponentTitleText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_pause_title, value);
        } catch (Exception e) {
        }
    }

    public final void setComponentTitleText(@NonNull String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_pause_title, value);
        } catch (Exception e) {
        }
    }

    public final void setComponentTitleTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_pause_title, value);
        } catch (Exception e) {
        }
    }

    public final void setComponentTitleTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_pause_title, color);
        } catch (Exception e) {
        }
    }
}