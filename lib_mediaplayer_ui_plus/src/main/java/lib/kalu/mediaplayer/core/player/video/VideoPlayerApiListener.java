
package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface VideoPlayerApiListener extends VideoPlayerApiBase {

    default void callProgressListener(long position, long duration) {
        try {
            OnPlayerChangeListener listener = getOnPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onProgress(position, duration);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiListener => callProgressListener => " + e.getMessage());
        }
    }
}
