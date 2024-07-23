package lib.kalu.mediaplayer.listener;

import lib.kalu.mediaplayer.type.PlayerType;


public interface OnPlayerEventListener {

    void onComplete();

    void onStart();

    void onError(String info);

    default void onPause() {
    }

    default void onResume() {
    }

    default void onBufferingStart() {
    }

    default void onBufferingStop() {
    }

    default void onPrepareStart() {
    }

    default void onPrepareComplete() {
    }

    /**
     * 播放状态
     *
     * @param state 播放状态，主要是指播放器的各种状态
     */
    default void onEvent(@PlayerType.EventType.Value int state) {
    }
}
