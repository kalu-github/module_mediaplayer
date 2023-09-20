package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public interface AudioKernelApiEvent {

    default void setEvent(@NonNull AudioKernelApiEvent eventApi) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }
}
