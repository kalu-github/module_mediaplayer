
package lib.kalu.mediaplayer.core.player.audio;

import lib.kalu.mediaplayer.listener.OnPlayerAudioChangeListener;
import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface AudioPlayerApiListener extends AudioPlayerApiBase {

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
