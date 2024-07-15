package lib.kalu.mediaplayer.core.kernel.video.ff;


import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public class VideoFFmpegPlayerFactory implements VideoKernelFactory<VideoFFmpegPlayer> {

    private VideoFFmpegPlayerFactory() {
    }

    public static VideoFFmpegPlayerFactory build() {
        return new VideoFFmpegPlayerFactory();
    }

    @Override
    public VideoFFmpegPlayer createKernel() {
        return new VideoFFmpegPlayer();
    }
}
