package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentLoading extends RelativeLayout implements ComponentApi {

    public ComponentLoading(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_loading, this, true);
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_LOADING_START:
                LogUtil.log("ComponentLoading => callEventListener => show => playState = " + playState);
                show();
                break;
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_RELEASE:
            case PlayerType.StateType.STATE_RELEASE_EXCEPTION:
//            default:
                LogUtil.log("ComponentLoading => callEventListener => gone => playState = " + playState);
                hide();
                break;
        }
    }

    @Override
    public void callWindowEvent(int state) {
        switch (state) {
            case PlayerType.WindowType.FLOAT:
            case PlayerType.WindowType.NORMAL:
            case PlayerType.WindowType.FULL:
                try {
                    int visibility = findViewById(R.id.module_mediaplayer_component_loading_pb).getVisibility();
                    if (visibility == View.VISIBLE) {
                        try {
                            boolean full = isFull();
                            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(full ? View.VISIBLE : View.INVISIBLE);
                        } catch (Exception e) {
                            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.INVISIBLE);
                        }
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public final void show() {
        long duration;
        try {
            duration = getPlayerView().getDuration();
        } catch (Exception e) {
            duration = 0;
        }
        try {
            
            if (duration <= 0) {
                findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.VISIBLE);
            boolean full = isFull();
            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(full ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentLoading => show => " + e.getMessage());
        }
    }

    @Override
    public final void hide() {
        try {
            
            findViewById(R.id.module_mediaplayer_component_loading_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_loading_pb).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_loading_message).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentLoading => gone => " + e.getMessage());
        }
    }

    /*************/

    @Override
    public final void setComponentBackgroundColorInt(int value) {
        try {
            setBackgroundColorInt(this, R.id.module_mediaplayer_component_loading_bg, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentBackgroundResource(int resid) {
        try {
            setBackgroundDrawableRes(this, R.id.module_mediaplayer_component_loading_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageResource(int resid) {
        try {
            setImageResource(this, R.id.module_mediaplayer_component_loading_bg, resid);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentImageUrl(String url) {
        try {
            setImageUrl(this, R.id.module_mediaplayer_component_loading_bg, url);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentImageFile(String filepath) {
        try {
            setImageFile(this, R.id.module_mediaplayer_component_loading_bg, filepath);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_component_loading_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentText(String value) {
        try {
            setText(this, R.id.module_mediaplayer_component_loading_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_component_loading_message, value);
        } catch (Exception e) {
        }
    }

    @Override
    public final void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_component_loading_message, color);
        } catch (Exception e) {
        }
    }
}
