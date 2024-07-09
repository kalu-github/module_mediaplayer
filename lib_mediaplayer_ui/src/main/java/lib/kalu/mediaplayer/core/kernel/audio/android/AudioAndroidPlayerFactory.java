package lib.kalu.mediaplayer.core.kernel.audio.android;




import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactory;

public class AudioAndroidPlayerFactory implements AudioKernelFactory<AudioAndroidPlayer> {

    private AudioAndroidPlayerFactory() {
    }

    public static AudioAndroidPlayerFactory build() {
        return new AudioAndroidPlayerFactory();
    }

    @Override
    public AudioAndroidPlayer createKernel() {
        return new AudioAndroidPlayer();
    }
}
