package lib.kalu.mediaplayer.core.kernel.audio.ijk;


import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;

public class AudioIjkPlayerFactory implements AudioKernelFactory<AudioIjkPlayer> {

    private boolean mUseMediaCodec;

    private AudioIjkPlayerFactory(boolean useMediaCodec) {
        mUseMediaCodec = useMediaCodec;
    }

    public static AudioIjkPlayerFactory build(boolean useMediaCodec) {
        return new AudioIjkPlayerFactory(useMediaCodec);
    }

    @Override
    public AudioIjkPlayer createKernel() {
        return new AudioIjkPlayer(mUseMediaCodec);
    }
}
