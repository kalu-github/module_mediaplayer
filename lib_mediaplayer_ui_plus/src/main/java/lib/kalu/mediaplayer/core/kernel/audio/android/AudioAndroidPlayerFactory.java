package lib.kalu.mediaplayer.core.kernel.audio.android;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;

@Keep
public class AudioAndroidPlayerFactory implements AudioKernelFactory<AudioAndroidPlayer> {

    private AudioAndroidPlayerFactory() {
    }

    public static AudioAndroidPlayerFactory build() {
        return new AudioAndroidPlayerFactory();
    }

    @Override
    public AudioAndroidPlayer createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event) {
        return new AudioAndroidPlayer(playerApi, event);
    }
}
