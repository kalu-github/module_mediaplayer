package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentPause extends RelativeLayout implements ComponentApiPause {

    public ComponentPause(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_pause, this, true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // toggle
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                toggle();
                return true;
            }
        }
        // toggle
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            boolean isShowing = isComponentMenuShowing();
            if (!isShowing) {
                toggle();
                return true;
            }
        }
        return false;
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_PAUSE:
                LogUtil.log("ComponentPause[show] => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_RESUME:
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                LogUtil.log("ComponentPause[gone] => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public boolean isComponentShowing() {
        int visibility1 = findViewById(R.id.module_mediaplayer_component_pause_bg).getVisibility();
        int visibility2 = findViewById(R.id.module_mediaplayer_component_pause_title).getVisibility();
        int visibility3 = findViewById(R.id.module_mediaplayer_component_pause_seekbar).getVisibility();
        return visibility1 == View.VISIBLE && visibility2 == View.VISIBLE && visibility3 == View.VISIBLE;
    }

    @Override
    public final void hide() {
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

            findViewById(R.id.module_mediaplayer_component_pause_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_pause_title).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_pause_seekbar).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        try {
            if (position < 0 || duration < 0)
                throw new Exception();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_pause_sb);
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
        }

        try {
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
            long d = (max > 0 ? max : duration) / 1000;
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
    public final void setComponentImageUrl(String url) {
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

    public final void setComponentTitleText(String value) {
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