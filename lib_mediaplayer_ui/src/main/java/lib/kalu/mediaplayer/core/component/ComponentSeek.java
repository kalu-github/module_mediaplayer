package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;




import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;


public class ComponentSeek extends RelativeLayout implements ComponentApiSeek {

    public ComponentSeek( Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_seek, this, true);
    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                MPLogUtil.log("ComponentSeek => callPlayerEvent => show => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_FAST_FORWARD_STOP:
            case PlayerType.StateType.STATE_FAST_REWIND_STOP:
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
                MPLogUtil.log("ComponentSeek => callPlayerEvent => gone1 => playState = " + playState);
                hide();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_IGNORE:
            case PlayerType.StateType.STATE_END:
                MPLogUtil.log("ComponentSeek => callPlayerEvent => gone2 => playState = " + playState);
                onUpdateTimeMillis(0, 0, 0, 0);
                hide();
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
            setTag(R.id.module_mediaplayer_component_seek_sb, true);
            
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_seek_ui).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => show => " + e.getMessage());
        }
    }

    @Override
    public final void hide() {
        try {
            
            setTag(R.id.module_mediaplayer_component_seek_sb, false);
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_seek_ui).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public void onUpdateTimeMillis( long seek,  long position,  long duration,  long max) {
        try {
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            Object tag = getTag(R.id.module_mediaplayer_component_seek_sb);
            if (null != tag && ((boolean) tag))
                throw new Exception("seekbar warning: user current action down");
            onUpdateSeekProgress(true, position, duration, max);
        } catch (Exception e) {
//            MPLogUtil.log("ComponentSeek => onUpdateTimeMillis => " + e.getMessage());
        }
    }

    @Override
    public void onUpdateSeekProgress( boolean updateTime,  long position,  long duration,  long max) {

        try {
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekBar error: null");
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => onUpdateSeekProgress => " + e.getMessage());
        }

        try {
            if (!updateTime)
                throw new Exception();
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

            TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
            viewMax.setText(strDuration);
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
            viewPosition.setText(strPosition);
        } catch (Exception e) {
            TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
            viewMax.setText("00:00");
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
            viewPosition.setText("00:00");
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
            MPLogUtil.log("ComponentSeek => findSeekBar => " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isComponentShowing() {
        try {
            int visibility1 = findViewById(R.id.module_mediaplayer_component_seek_bg).getVisibility();
            int visibility2 = findViewById(R.id.module_mediaplayer_component_seek_ui).getVisibility();
            return visibility1 == View.VISIBLE && visibility2 == View.VISIBLE;
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => isComponentShowing => " + e.getMessage());
            return false;
        }
    }
}
