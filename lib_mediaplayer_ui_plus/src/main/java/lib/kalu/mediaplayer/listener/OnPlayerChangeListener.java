package lib.kalu.mediaplayer.listener;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface OnPlayerChangeListener {

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     *
     * @param state 播放模式
     */
    default void onWindow(@PlayerType.WindowType.Value int state) {
    }

    /**
     * 播放状态
     *
     * @param state 播放状态，主要是指播放器的各种状态
     */
    default void onChange(@PlayerType.StateType.Value int state) {
    }

    default void onProgress(@NonNull long position, @NonNull long duration) {
    }
}
