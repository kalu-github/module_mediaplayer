
package lib.kalu.mediaplayer.core.player;

import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiListener extends PlayerApiBase {

    default void callProgressListener(long position, long duration) {
        try {
            OnPlayerChangeListener listener = getPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onProgress(position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiListener => callProgressListener => " + e.getMessage());
        }
    }
}
