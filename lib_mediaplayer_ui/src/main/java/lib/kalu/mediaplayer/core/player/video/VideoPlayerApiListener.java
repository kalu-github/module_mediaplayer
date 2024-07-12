
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

interface VideoPlayerApiListener extends VideoPlayerApiBase, VideoPlayerApiBuried {

    default void callWindow(@PlayerType.WindowType.Value int state) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callWindow(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callWindow => " + e.getMessage());
        }

        // listener
        try {
            OnPlayerWindowListener windowListener = getOnPlayerWindowListener();
            if (null != windowListener) {
                windowListener.onWindow(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callWindow => " + e.getMessage());
        }

        // 埋点
        onBuriedWindow(state);
    }


    default void callEvent(@PlayerType.StateType.Value int state) {
        callEvent(true, true, state);
    }

    default void callEvent(boolean callPlayer, boolean callComponent, @PlayerType.StateType.Value int state) {

        // component
        try {
            if (!callComponent)
                throw new Exception("warning: callComponent false");
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).callEvent(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callEvent => " + e.getMessage());
        }

        // listener
        try {
            if (!callPlayer)
                throw new Exception("warning: callPlayer false");
            OnPlayerEventListener eventListener = getOnPlayerEventListener();
            if (null == eventListener)
                throw new Exception("warning: eventListener null");
            eventListener.onEvent(state);
            if (state == PlayerType.StateType.STATE_RESTAER) {
                eventListener.onRestart();
            } else if (state == PlayerType.StateType.STATE_START) {
                eventListener.onStart();
            } else if (state == PlayerType.StateType.STATE_END) {
                eventListener.onComplete();
            } else if (state == PlayerType.StateType.STATE_PAUSE) {
                eventListener.onPause();
            } else if (state == PlayerType.StateType.STATE_RESUME) {
                eventListener.onResume();
            } else if (state == PlayerType.StateType.STATE_BUFFERING_START) {
                eventListener.onBufferingStart();
            } else if (state == PlayerType.StateType.STATE_BUFFERING_STOP) {
                eventListener.onBufferingStop();
            } else if (state == PlayerType.StateType.STATE_LOADING_START) {
                eventListener.onLoadingStart();
            } else if (state == PlayerType.StateType.STATE_LOADING_STOP) {
                eventListener.onLoadingStop();
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callEvent => " + e.getMessage());
        }
    }

    default void callProgress(long maxDuration, long position, long duration) {

        // component
        try {
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateProgress(false, maxDuration, position, duration);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiComponent => callProgress => " + e.getMessage());
        }

        // listenr
        try {
            OnPlayerProgressListener progressListener = getOnPlayerProgressListener();
            if (null != progressListener) {
                progressListener.onProgress(position, duration);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiListener => callProgress => " + e.getMessage());
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

    default OnPlayerEpisodeListener getOnPlayerEpisodeListener() {
        return null;
    }

    default void removeOnPlayerEpisodeListener() {
    }

    default void setOnPlayerEpisodeListener(OnPlayerEpisodeListener l) {
    }
}
