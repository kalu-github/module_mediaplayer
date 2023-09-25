
package lib.kalu.mediaplayer.core.player.audio;

import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.listener.OnPlayerAudioChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface AudioPlayerApiListener {

    OnPlayerAudioChangeListener[] mOnPlayerChangeListener = new OnPlayerAudioChangeListener[1];

    default boolean hasPlayerChangeListener() {
        return null != mOnPlayerChangeListener && null != mOnPlayerChangeListener[0];
    }

    default OnPlayerAudioChangeListener getPlayerChangeListener() {
        return mOnPlayerChangeListener[0];
    }

    default void cleanPlayerChangeListener() {
        mOnPlayerChangeListener[0] = null;
        try {
            ((View) this).setOnKeyListener(null);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiBase => cleanPlayerChangeListener => " + e.getMessage());
        }
    }

    default void setOnPlayerChangeListener(@NonNull OnPlayerAudioChangeListener l) {
        mOnPlayerChangeListener[0] = null;
        mOnPlayerChangeListener[0] = l;
    }

    default void callPlayerEvent(@PlayerType.StateType.Value int state) {
        // listener
        try {
            boolean hasListener = hasPlayerChangeListener();
            if (!hasListener)
                throw new Exception("not find PlayerChangeListener");
            OnPlayerAudioChangeListener listener = getPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onChange(state);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiBase => callPlayerEvent => " + e.getMessage());
        }
    }

    default void callProgressListener(long position, long duration) {
        try {
            OnPlayerAudioChangeListener playerChangeListener = getPlayerChangeListener();
            if (null == playerChangeListener)
                throw new Exception("listener error: null");
            playerChangeListener.onProgress(position, duration);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => callProgressListener => " + e.getMessage());
        }
    }
}
