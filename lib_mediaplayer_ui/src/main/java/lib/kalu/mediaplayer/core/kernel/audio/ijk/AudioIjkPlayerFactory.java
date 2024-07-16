package lib.kalu.mediaplayer.core.kernel.audio.ijk;


import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;

public class AudioIjkPlayerFactory implements AudioKernelFactory<AudioIjkPlayer> {

    public static AudioIjkPlayerFactory build() {
        return new AudioIjkPlayerFactory();
    }

    @Override
    public AudioIjkPlayer createKernel() {
        return new AudioIjkPlayer();
    }
}
