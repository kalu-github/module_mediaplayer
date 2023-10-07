package lib.kalu.mediaplayer.core.kernel.audio.ijk;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;


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
    public AudioIjkPlayer createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event) {
        return new AudioIjkPlayer(mUseMediaCodec, playerApi, event);
    }
}
