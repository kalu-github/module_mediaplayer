
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.util.LogUtil;

interface VideoPlayerApiListener extends VideoPlayerApiBase {

    default void callPlayerWindow(@PlayerType.WindowType.Value int state) {

        // listener
        try {
            OnPlayerWindowListener windowListener = getOnPlayerWindowListener();
            if (null == windowListener)
                throw new Exception("windowListener error: null");
            windowListener.onWindow(state);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callPlayerWindow => " + e.getMessage());
        }

        // component
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callWindowEvent(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callPlayerWindow => " + e.getMessage());
        }
    }

    default void callPlayerEvent(@PlayerType.StateType.Value int state) {
        // listener
        try {
            OnPlayerEventListener eventListener = getOnPlayerEventListener();
            if (null == eventListener)
                throw new Exception("eventListener error: null");
            eventListener.onEvent(state);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callPlayerEvent => " + e.getMessage());
        }

        // component
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callPlayerEvent(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callPlayerEvent => " + e.getMessage());
        }
    }

    default void callUpdateProgressPlayer(long position, long duration) {
        try {
            OnPlayerProgressListener progressListener = getOnPlayerProgressListener();
            if (null == progressListener)
                throw new Exception("progressListener error: null");
            progressListener.onProgress(position, duration);
        } catch (Exception e) {
//            MPLogUtil.log("VideoPlayerApiListener => callPlayerProgress => " + e.getMessage());
        }
    }

    default OnPlayerEventListener getOnPlayerEventListener() {
        return null;
    }

    default void removeOnPlayerEventListener() {
    }

    default void setOnPlayerEventListener(OnPlayerEventListener l) {
    }

    default OnPlayerProgressListener getOnPlayerProgressListener() {
        return null;
    }

    default void removeOnPlayerProgressListener() {
    }

    default void setOnPlayerProgressListener(OnPlayerProgressListener l) {
    }

    default OnPlayerWindowListener getOnPlayerWindowListener() {
        return null;
    }

    default void removeOnPlayerWindowListener() {
    }

    default void setOnPlayerWindowListener(OnPlayerWindowListener l) {
    }
}
