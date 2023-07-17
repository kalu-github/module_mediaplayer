package lib.kalu.mediaplayer.core.component;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;


@Keep
public interface ComponentApiLinkerPlayer {

    default PlayerLayout getPlayerLayout() {
        try {
            PlayerLayout playerLayout = null;
            View view = (View) this;
            while (true) {
                ViewParent parent = view.getParent();
                if (null == parent) {
                    break;
                } else if (parent instanceof PlayerLayout) {
                    playerLayout = (PlayerLayout) parent;
                    break;
                } else {
                    view = (View) parent;
                }
            }
            if (null == playerLayout)
                new Exception("not find");
            return playerLayout;
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => getPlayerLayout => " + e.getMessage());
            return null;
        }
    }

    default boolean isFull() {
        try {
            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout)
                throw new Exception("playerLayout error: null");
            return playerLayout.isFull();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => isFull => " + e.getMessage());
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout)
                throw new Exception("playerLayout error: null");
            return playerLayout.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    default String getNetSpeed() {
        try {
            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout)
                throw new Exception("playerLayout error: null");
            return playerLayout.getNetSpeed();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => isFull => " + e.getMessage());
            return "0kb/s";
        }
    }

    default void resume() {
        try {
            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout)
                throw new Exception("playerLayout error: null");
            playerLayout.resume();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => resume => " + e.getMessage());
        }
    }

    default void pause() {
        try {
            PlayerLayout playerLayout = getPlayerLayout();
            if (null == playerLayout)
                throw new Exception("playerLayout error: null");
            playerLayout.pause();
        } catch (Exception e) {
            MPLogUtil.log("ComponentApiLinkerPlayer => pause => " + e.getMessage());
        }
    }
}