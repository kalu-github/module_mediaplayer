package lib.kalu.mediaplayer.core.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public interface ComponentApiMenu extends ComponentApi {

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    default int getEpisodeCount() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            int episodeCount = tags.getEpisodeCount();
            if (episodeCount <= 0)
                throw new Exception("warning: episodeCount " + episodeCount);
            return episodeCount;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeCount => " + e.getMessage());
            return -1;
        }
    }

    default int getEpisodePlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("error: tags null");
            int episodePlaying = tags.getEpisodePlaying();
            if (episodePlaying < 0)
                throw new Exception("warning: episodePlaying " + episodePlaying);
            return episodePlaying;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodePlaying => " + e.getMessage());
            return -1;
        }
    }

    default void scrollNextItem(int action) {
    }

    default void updateTabCheckedChange(boolean requestFocus) {
    }

    default void updateTabChecked(int id) {
    }

    default void toggleScale(int focusId) {
    }

    default void toggleSpeed(int focusId) {

    }

    default void toggleEpisode(int focusId) {

    }

    default void callEpisodeClickListener(int pos) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: null == playerView");
            OnPlayerEpisodeListener listener = playerView.getOnPlayerEpisodeListener();
            if (null == listener)
                throw new Exception("error: null == listener");
            listener.onEpisode(pos);
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => callEpisodeClickListener => " + e.getMessage());
        }
    }

    Handler[] mHandlerDelayedMsg = new Handler[]{null};

    default void stopDelayedMsg() {
        if (null != mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0].removeMessages(1000);
            mHandlerDelayedMsg[0] = null;
        }
    }

    default void startDelayedMsg() {
        if (null == mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0] = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == 1000) {
                        hide();
                    }
                }
            };
        }
        mHandlerDelayedMsg[0].removeMessages(1000);
        mHandlerDelayedMsg[0].sendEmptyMessageDelayed(1000, 4000);
    }
}