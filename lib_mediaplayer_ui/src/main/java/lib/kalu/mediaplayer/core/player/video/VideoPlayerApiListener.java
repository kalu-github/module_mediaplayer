
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerItemsLiatener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

interface VideoPlayerApiListener extends VideoPlayerApiBase {

    default void callWindowListener(@PlayerType.WindowType.Value int state) {

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
                ((ComponentApi) childAt).callWindowEvent(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callWindowListener => " + e.getMessage());
        }

        // listener
        try {
            OnPlayerWindowListener windowListener = getOnPlayerWindowListener();
            if (null != windowListener) {
                windowListener.onWindow(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callWindowListener => " + e.getMessage());
        }
    }

    default void callEventListener(@PlayerType.StateType.Value int state) {

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
                ((ComponentApi) childAt).callEventListener(state);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callEventListener => " + e.getMessage());
        }

        // listener
        try {
            OnPlayerEventListener eventListener = getOnPlayerEventListener();
            if (null != eventListener) {
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
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callEventListener => " + e.getMessage());
        }
    }

    default void callProgressListener(long max, long position, long duration) {

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
                ((ComponentApi) childAt).onUpdateProgress(false, max, position, duration);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiComponent => callProgressListener => " + e.getMessage());
        }

        // listenr
        try {
            OnPlayerProgressListener progressListener = getOnPlayerProgressListener();
            if (null != progressListener) {
                progressListener.onProgress(position, duration);
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiListener => callProgressListener => " + e.getMessage());
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

    default OnPlayerItemsLiatener getOnPlayerItemsListener() {
        return null;
    }

    default void removeOnPlayerItemsListener() {
    }

    default void setOnPlayerItemsListener(OnPlayerItemsLiatener l) {
    }
}
