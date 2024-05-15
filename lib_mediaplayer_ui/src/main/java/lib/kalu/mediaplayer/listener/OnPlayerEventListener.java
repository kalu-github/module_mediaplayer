package lib.kalu.mediaplayer.listener;

import lib.kalu.mediaplayer.config.player.PlayerType;


public interface OnPlayerEventListener {

    /**
     * 播放状态
     *
     * @param state 播放状态，主要是指播放器的各种状态
     */
    default void onEvent(@PlayerType.StateType.Value int state) {
    }
}
