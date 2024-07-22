
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
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


    default void callEvent(@PlayerType.EventType.Value int state) {
        callEvent(true, true, state);
    }

    default void callEvent(boolean callPlayer, boolean callComponent, @PlayerType.EventType.Value int state) {

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
             if (state == PlayerType.EventType.START) {
                eventListener.onStart();
            } else if (state == PlayerType.EventType.COMPLETE) {
                eventListener.onComplete();
            } else if (state == PlayerType.EventType.PAUSE) {
                eventListener.onPause();
            } else if (state == PlayerType.EventType.RESUME) {
                eventListener.onResume();
            } else if (state == PlayerType.EventType.ERROR) {
                eventListener.onError(null);
            } else if (state == PlayerType.EventType.BUFFERING_START) {
                eventListener.onBufferingStart();
            } else if (state == PlayerType.EventType.BUFFERING_STOP) {
                eventListener.onBufferingStop();
            } else if (state == PlayerType.EventType.LOADING_START) {
                eventListener.onLoadingStart();
            } else if (state == PlayerType.EventType.LOADING_STOP) {
                eventListener.onLoadingStop();
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiBase => callEvent => " + e.getMessage());
        }
    }

    default void callProgress(long trySeeDuration, long position, long duration) {

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
                ((ComponentApi) childAt).onUpdateProgress(false, trySeeDuration, position, duration);
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

    /**************/

    OnPlayerEventListener[] mOnPlayerEventListener = new OnPlayerEventListener[]{null};

    default OnPlayerEventListener getOnPlayerEventListener() {
        return this.mOnPlayerEventListener[0];
    }

    default void setOnPlayerEventListener(OnPlayerEventListener l) {
        this.mOnPlayerEventListener[0] = l;
    }

    default void clearOnPlayerEventListener() {
        this.mOnPlayerEventListener[0] = null;
    }

    /**************/

    OnPlayerProgressListener[] mOnPlayerProgressListener = new OnPlayerProgressListener[]{null};

    default OnPlayerProgressListener getOnPlayerProgressListener() {
        return this.mOnPlayerProgressListener[0];
    }

    default void setOnPlayerProgressListener(OnPlayerProgressListener l) {
        this.mOnPlayerProgressListener[0] = l;
    }

    default void clearOnPlayerProgressListener() {
        this.mOnPlayerProgressListener[0] = null;
    }

    /**************/
    OnPlayerWindowListener[] mOnPlayerWindowListener = new OnPlayerWindowListener[]{null};

    default OnPlayerWindowListener getOnPlayerWindowListener() {
        return this.mOnPlayerWindowListener[0];
    }

    default void setOnPlayerWindowListener(OnPlayerWindowListener l) {
        this.mOnPlayerWindowListener[0] = l;
    }

    default void clearOnPlayerWindowListener() {
        this.mOnPlayerWindowListener[0] = null;
    }

    /***********/

    OnPlayerEpisodeListener[] mOnPlayerItemsLiatener = new OnPlayerEpisodeListener[]{null};

    default OnPlayerEpisodeListener getOnPlayerEpisodeListener() {
        return this.mOnPlayerItemsLiatener[0];
    }

    default void setOnPlayerEpisodeListener(OnPlayerEpisodeListener l) {
        this.mOnPlayerItemsLiatener[0] = l;
    }

    default void clearOnPlayerEpisodeListener() {
        this.mOnPlayerItemsLiatener[0] = null;
    }

    default void clearOnPlayerListener() {
        clearOnPlayerEventListener();
        clearOnPlayerWindowListener();
        clearOnPlayerProgressListener();
        clearOnPlayerEpisodeListener();
    }
}
