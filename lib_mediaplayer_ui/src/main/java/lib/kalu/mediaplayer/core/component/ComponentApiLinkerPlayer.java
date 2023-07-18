package lib.kalu.mediaplayer.core.component;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;
import lib.kalu.mediaplayer.widget.player.PlayerView;


@Keep
public interface ComponentApiLinkerPlayer {

    default PlayerLayout getPlayerLayout() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                new Exception("playerView error: null");
            ViewParent parent = playerView.getParent();
            if (null == parent)
                throw new Exception("parent error: null");
            if (!(parent instanceof PlayerLayout))
                throw new Exception("parent error: not instanceof PlayerLayout");
            return (PlayerLayout) parent;
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => getPlayerLayout => " + e.getMessage());
            return null;
        }
    }

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

    default String getUrl() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            String url = playerView.getUrl();
            if (null == url || url.length() == 0)
                throw new Exception("url error: " + url);
            return url;
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => getUrl => " + e.getMessage());
            return null;
        }
    }
    default String getData() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            String data = playerView.getData();
            if (null == data || data.length() == 0)
                throw new Exception("data error: " + data);
            return data;
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => getData => " + e.getMessage());
            return null;
        }
    }
}