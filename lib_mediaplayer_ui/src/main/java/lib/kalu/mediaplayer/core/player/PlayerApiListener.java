
package lib.kalu.mediaplayer.core.player;

import java.util.List;

import lib.kalu.mediaplayer.listener.OnPlayerChangeListener;

 interface PlayerApiListener extends PlayerApiBase {

    default void callProgressListener(long position, long duration) {
        List<OnPlayerChangeListener> listener = getPlayerChangeListener();
        if (null != listener) {
            for (OnPlayerChangeListener l : listener) {
                if (null == l) continue;
                l.onProgress(position, duration);
            }
        }
    }
}
