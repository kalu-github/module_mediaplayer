package lib.kalu.mediaplayer.core.kernel.audio.exo2;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.core.kernel.audio.MusicKernelFactory;

@Keep
public final class MusicExoPlayer2Factory implements MusicKernelFactory<MusicExoPlayer2> {

    private MusicExoPlayer2Factory() {
    }

    public static MusicExoPlayer2Factory build() {
        return new MusicExoPlayer2Factory();
    }

    @Override
    public MusicExoPlayer2 createKernel() {
        return new MusicExoPlayer2();
    }
}
