package lib.kalu.mediaplayer.core.kernel.audio.ff;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public class AudioFFmpegPlayerFactory implements AudioKernelFactory<AudioFFmpegPlayer> {

    private AudioFFmpegPlayerFactory() {
    }

    public static AudioFFmpegPlayerFactory build() {
        return new AudioFFmpegPlayerFactory();
    }

    @Override
    public AudioFFmpegPlayer createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event, @NonNull boolean retryBuffering) {
        return new AudioFFmpegPlayer(playerApi, event, retryBuffering);
    }
}
