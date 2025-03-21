package lib.kalu.mediaplayer.core.kernel.video.media3;




import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public final class VideoMedia3PlayerFactory implements VideoKernelFactory<VideoMedia3Player> {

    private VideoMedia3PlayerFactory() {
    }

    public static VideoMedia3PlayerFactory build() {
        return new VideoMedia3PlayerFactory();
    }

    @Override
    public VideoMedia3Player createKernel() {
        return new VideoMedia3Player();
    }
}
