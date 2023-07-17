package lib.kalu.mediaplayer.core.component;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;


@Keep
public interface ComponentApiLinkerPlayer {

    default PlayerView getPlayerView() {
        try {
            PlayerView playerView = null;
            View view = (View) this;
            while (true) {
                ViewParent parent = view.getParent();
                if (null == parent) {
                    break;
                } else if (parent instanceof PlayerView) {
                    playerView = (PlayerView) parent;
                    break;
                } else {
                    view = (View) parent;
                }
            }
            if (null == playerView)
                new Exception("not find");
            return playerView;
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => getPlayerView => " + e.getMessage());
            return null;
        }
    }

    default boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFull();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => isFull => " + e.getMessage());
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    default String getNetSpeed() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getNetSpeed();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => isFull => " + e.getMessage());
            return "0kb/s";
        }
    }

    default void resume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => resume => " + e.getMessage());
        }
    }

    default void pause() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => pause => " + e.getMessage());
        }
    }
}