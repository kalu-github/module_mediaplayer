package lib.kalu.mediaplayer.core.kernel.audio.ijk;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.core.kernel.audio.MusicKernelFactory;

@Keep
public final class MusicIjkPlayerFactory implements MusicKernelFactory<MusicIjkPlayer> {

    private MusicIjkPlayerFactory() {
    }

    public static MusicIjkPlayerFactory build() {
        return new MusicIjkPlayerFactory();
    }

    @Override
    public MusicIjkPlayer createKernel() {
        return new MusicIjkPlayer();
    }
}
