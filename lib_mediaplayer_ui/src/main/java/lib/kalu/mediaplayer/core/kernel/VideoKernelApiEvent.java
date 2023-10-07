package lib.kalu.mediaplayer.core.kernel;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface VideoKernelApiEvent {

    default void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {
    }

    default void onEvent(@PlayerType.VideoKernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onMeasure(@PlayerType.VideoKernelType.Value int kernel,
                           int videoWidth,
                           int videoHeight,
                           @PlayerType.RotationType.Value
                           int rotation) {
    }
}
