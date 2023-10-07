package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface AudioKernelApiEvent {

    default void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }
}
