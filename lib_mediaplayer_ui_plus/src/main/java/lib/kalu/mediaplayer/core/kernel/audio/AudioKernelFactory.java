package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;

@Keep
public interface AudioKernelFactory<T extends AudioKernelApi> {
    T createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event);
}