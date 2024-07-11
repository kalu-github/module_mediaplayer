package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;


import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;
import lib.kalu.mediaplayer.widget.seek.SeekBar;

/**
 * 试看
 */
public class ComponentWarningTrySee extends RelativeLayout implements ComponentApi {

    public ComponentWarningTrySee(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_warning_try_see_root;
    }

    @Override
    public int initLayoutIdText() {
        return R.id.module_mediaplayer_component_warning_try_see_title;
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_warning_try_see;
    }

    @Override
    public void callEventListener(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_VIDEO_RENDERING_START:
                LogUtil.log("ComponentTrySee => playState = " + playState);
                try {
                    boolean trySee = isTrySee();
                    if (!trySee)
                        throw new Exception("warning: trySee false");
                    boolean componentShowing = isComponentShowing();
                    if (componentShowing)
                        throw new Exception("warning: componentShowing true");
                    show();
                } catch (Exception e) {
                }
                break;
            case PlayerType.StateType.STATE_TRY_TO_SEE_FINISH:
                LogUtil.log("ComponentTrySee => playState = " + playState);

                try {
                    boolean trySee = isTrySee();
                    if (!trySee)
                        throw new Exception("warning: trySee false");
                    boolean componentShowing = isComponentShowing();
                    if(!componentShowing)
                        throw new Exception("warning: componentShowing false");
                    String mediaTitle = getTitle();
                    setComponentText(mediaTitle + " 试看结束...");
                } catch (Exception e) {
                }

                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
        LogUtil.log("ComponentWarningTrySee => onUpdateProgress");

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
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_warning_try_see_seekbar);
            seekBar.setProgress((int) position);
            seekBar.setMax((int) (max > 0 ? max : duration));
        } catch (Exception e) {
            LogUtil.log("ComponentWarningTrySee => onUpdateProgress => Exception " + e.getMessage());
        }
    }

    @Override
    public void show() {
        ComponentApi.super.show();

        try {
            String mediaTitle = getTitle();
            setComponentText(mediaTitle + " 试看开始...");
        } catch (Exception e) {
        }
    }
}
