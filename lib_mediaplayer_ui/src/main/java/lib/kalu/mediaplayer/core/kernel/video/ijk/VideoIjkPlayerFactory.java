package lib.kalu.mediaplayer.core.kernel.video.ijk;


import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;



public class VideoIjkPlayerFactory implements VideoKernelFactory<VideoIjkPlayer> {

    public static VideoIjkPlayerFactory build() {
        return new VideoIjkPlayerFactory();
    }

    @Override
    public VideoIjkPlayer createKernel() {
        return new VideoIjkPlayer();
    }
}
