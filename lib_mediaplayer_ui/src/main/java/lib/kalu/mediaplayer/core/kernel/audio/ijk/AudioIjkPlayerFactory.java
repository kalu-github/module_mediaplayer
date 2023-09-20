package lib.kalu.mediaplayer.core.kernel.audio.ijk;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;


@Keep
public class AudioIjkPlayerFactory implements AudioKernelFactory<AudioIjkPlayer> {

    private boolean mUseMediaCodec;

    private AudioIjkPlayerFactory(boolean useMediaCodec) {
        mUseMediaCodec = useMediaCodec;
    }

    public static AudioIjkPlayerFactory build(boolean useMediaCodec) {
        return new AudioIjkPlayerFactory(useMediaCodec);
    }

    @Override
    public AudioIjkPlayer createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event, @NonNull boolean retryBuffering) {
        return new AudioIjkPlayer(mUseMediaCodec, playerApi, event);
    }
}
