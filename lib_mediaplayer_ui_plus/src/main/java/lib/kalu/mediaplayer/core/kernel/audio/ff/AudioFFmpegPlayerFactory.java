package lib.kalu.mediaplayer.core.kernel.audio.ff;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;

@Keep
public class AudioFFmpegPlayerFactory implements AudioKernelFactory<AudioFFmpegPlayer> {

    private AudioFFmpegPlayerFactory() {
    }

    public static AudioFFmpegPlayerFactory build() {
        return new AudioFFmpegPlayerFactory();
    }

    @Override
    public AudioFFmpegPlayer createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event) {
        return new AudioFFmpegPlayer(playerApi, event);
    }
}
