package lib.kalu.mediaplayer.core.kernel.audio.exo2;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;

@Keep
public final class AudioExoPlayer2Factory implements AudioKernelFactory<AudioExoPlayer2> {

    private AudioExoPlayer2Factory() {
    }

    public static AudioExoPlayer2Factory build() {
        return new AudioExoPlayer2Factory();
    }

    @Override
    public AudioExoPlayer2 createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event, @NonNull boolean retryBuffering) {
        return new AudioExoPlayer2(playerApi, event, retryBuffering);
    }
}
