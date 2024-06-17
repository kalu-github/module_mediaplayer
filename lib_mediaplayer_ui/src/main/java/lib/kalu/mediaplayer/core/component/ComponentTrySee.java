package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public class ComponentTrySee extends RelativeLayout implements ComponentApi {

    public ComponentTrySee(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_try_see, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_TRY_TO_SEE_FINISH:
                LogUtil.log("ComponentTrySee => playState = " + playState);
                try {
                    PlayerView playerView = getPlayerView();
                    if (null == playerView)
                        throw new Exception("error: playerView null");
                    StartArgs tags = playerView.getTags();
                    if (null == tags)
                        throw new Exception("error: tags null");
                    boolean trySee = tags.isTrySee();
                    if (!trySee)
                        throw new Exception("warning: trySee false");
                    setComponentText("试看结束...");
                    show();
                } catch (Exception e) {
                    hide();
                }
                break;
            case PlayerType.StateType.STATE_INIT:
                LogUtil.log("ComponentTrySee => playState = " + playState);
                try {
                    PlayerView playerView = getPlayerView();
                    if (null == playerView)
                        throw new Exception("error: playerView null");
                    StartArgs tags = playerView.getTags();
                    if (null == tags)
                        throw new Exception("error: tags null");
                    boolean trySee = tags.isTrySee();
                    if (!trySee)
                        throw new Exception("warning: trySee false");
                    setComponentText("试看中...");
                    show();
                } catch (Exception e) {
                    hide();
                }
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        try {
            if (position < 0 || duration < 0)
                throw new Exception();
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_try_see_seekbar);
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

            TextView viewMax = findViewById(R.id.module_mediaplayer_component_try_see_duration);
            viewMax.setText(strDuration);
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_try_see_position);
            viewPosition.setText(strPosition);
        } catch (Exception e) {
        }
    }

    @Override
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_try_see_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void hide() {
        try {
            findViewById(R.id.module_mediaplayer_component_try_see_root).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_try_see_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText(String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_try_see_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_try_see_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_try_see_message, color);
        } catch (Exception e) {
        }
    }
}
