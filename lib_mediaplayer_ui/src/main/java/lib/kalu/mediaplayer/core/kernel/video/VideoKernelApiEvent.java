package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface VideoKernelApiEvent {

    default void onUpdateTimeMillis(@NonNull boolean isLooping, @NonNull long max, @NonNull long seek, @NonNull long position, @NonNull long duration) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onMeasure(@PlayerType.KernelType.Value int kernel,
                           int videoWidth,
                           int videoHeight,
                           @PlayerType.RotationType.Value
                           int rotation) {
    }
}
