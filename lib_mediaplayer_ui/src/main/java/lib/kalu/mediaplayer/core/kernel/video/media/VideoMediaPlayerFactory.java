package lib.kalu.mediaplayer.core.kernel.video.media;




import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public final class VideoMediaPlayerFactory implements VideoKernelFactory<VideoMediaPlayer> {

    private VideoMediaPlayerFactory() {
    }

    public static VideoMediaPlayerFactory build() {
        return new VideoMediaPlayerFactory();
    }

    @Override
    public VideoMediaPlayer createKernel() {
        return new VideoMediaPlayer();
    }
}
