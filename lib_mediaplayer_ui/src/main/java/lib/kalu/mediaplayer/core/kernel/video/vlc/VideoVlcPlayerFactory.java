package lib.kalu.mediaplayer.core.kernel.video.vlc;


import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public class VideoVlcPlayerFactory implements VideoKernelFactory<VideoVlcPlayer> {

    private VideoVlcPlayerFactory() {
    }

    public static VideoVlcPlayerFactory build() {
        return new VideoVlcPlayerFactory();
    }

    @Override
    public VideoVlcPlayer createKernel() {
        return new VideoVlcPlayer();
    }
}
