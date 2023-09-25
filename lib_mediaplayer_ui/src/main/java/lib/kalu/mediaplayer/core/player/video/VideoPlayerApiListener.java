
package lib.kalu.mediaplayer.core.player.video;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface VideoPlayerApiListener {

    OnPlayerChangeListener[] mOnPlayerChangeListener = new OnPlayerChangeListener[1];

    default boolean hasPlayerChangeListener() {
        return null != mOnPlayerChangeListener && null != mOnPlayerChangeListener[0];
    }

    default OnPlayerChangeListener getPlayerChangeListener() {
        return mOnPlayerChangeListener[0];
    }

    default void cleanPlayerChangeListener() {
        mOnPlayerChangeListener[0] = null;
        try {
            ((View) this).setOnKeyListener(null);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiBase => cleanPlayerChangeListener => " + e.getMessage());
        }
    }

    default void setOnPlayerChangeListener(@NonNull OnPlayerChangeListener l) {
        mOnPlayerChangeListener[0] = null;
        mOnPlayerChangeListener[0] = l;
    }

    default void callChangeListener(@PlayerType.StateType.Value int state) {
        // listener
        try {
            boolean hasListener = hasPlayerChangeListener();
            if (!hasListener)
                throw new Exception("not find PlayerChangeListener");
            OnPlayerChangeListener listener = getPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onChange(state);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiListener => callChangeListener => " + e.getMessage());
        }
    }

    default void callWindowListener(@PlayerType.WindowType.Value int state) {

        // listener
        try {
            boolean hasListener = hasPlayerChangeListener();
            if (!hasListener)
                throw new Exception("not find PlayerChangeListener");
            OnPlayerChangeListener listener = getPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onWindow(state);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiListener => callWindowListener => " + e.getMessage());
        }
    }

    default void callProgressListener(long position, long duration) {
        try {
            OnPlayerChangeListener listener = getPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onProgress(position, duration);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiListener => callProgressListener => " + e.getMessage());
        }
    }
}
