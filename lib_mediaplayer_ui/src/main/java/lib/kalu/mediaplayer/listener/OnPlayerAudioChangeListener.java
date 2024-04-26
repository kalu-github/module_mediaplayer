package lib.kalu.mediaplayer.listener;

import lib.kalu.mediaplayer.config.player.PlayerType;


public interface OnPlayerAudioChangeListener {

    default void onChange(@PlayerType.StateType.Value int state) {
    }

    default void onProgress( long position,  long duration) {
    }
}
