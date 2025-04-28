package lib.kalu.mediaplayer.core.kernel.video.mediax;




import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public final class VideoMediaxPlayerFactory implements VideoKernelFactory<VideoMediaxPlayer> {

    private VideoMediaxPlayerFactory() {
    }

    public static VideoMediaxPlayerFactory build() {
        return new VideoMediaxPlayerFactory();
    }

    @Override
    public VideoMediaxPlayer createKernel() {
        return new VideoMediaxPlayer();
    }
}
