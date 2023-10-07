package lib.kalu.mediaplayer.listener;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface OnPlayerAudioChangeListener {

    default void onChange(@PlayerType.StateType.Value int state) {
    }

    default void onProgress(@NonNull long position, @NonNull long duration) {
    }
}
