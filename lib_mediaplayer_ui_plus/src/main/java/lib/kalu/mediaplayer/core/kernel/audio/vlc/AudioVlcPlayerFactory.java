package lib.kalu.mediaplayer.core.kernel.audio.vlc;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;

@Keep
public class AudioVlcPlayerFactory implements AudioKernelFactory<AudioVlcPlayer> {

    private AudioVlcPlayerFactory() {
    }

    public static AudioVlcPlayerFactory build() {
        return new AudioVlcPlayerFactory();
    }

    @Override
    public AudioVlcPlayer createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event) {
        return new AudioVlcPlayer(playerApi, event);
    }
}
