package lib.kalu.mediaplayer.core.kernel.audio.exo1;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;

@Keep
public final class AudioExoPlayerFactory implements AudioKernelFactory<AudioExoPlayer> {

    private AudioExoPlayerFactory() {
    }

    public static AudioExoPlayerFactory build() {
        return new AudioExoPlayerFactory();
    }

    @Override
    public AudioExoPlayer createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event) {
        return new AudioExoPlayer(playerApi, event);
    }
}
