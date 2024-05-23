package lib.kalu.mediaplayer.core.kernel.video.exo2;


import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public final class VideoExo2PlayerFactory implements VideoKernelFactory<VideoExo2Player> {

    private VideoExo2PlayerFactory() {
    }

    public static VideoExo2PlayerFactory build() {
        return new VideoExo2PlayerFactory();
    }

    @Override
    public VideoExo2Player createKernel() {
        return new VideoExo2Player();
    }
}
