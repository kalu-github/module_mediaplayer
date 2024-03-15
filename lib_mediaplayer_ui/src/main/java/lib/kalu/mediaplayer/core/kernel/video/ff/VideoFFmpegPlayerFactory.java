package lib.kalu.mediaplayer.core.kernel.video.ff;




import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;


public class VideoFFmpegPlayerFactory implements VideoKernelFactory<VideoFFmpegPlayer> {

    private VideoFFmpegPlayerFactory() {
    }

    public static VideoFFmpegPlayerFactory build() {
        return new VideoFFmpegPlayerFactory();
    }

    @Override
    public VideoFFmpegPlayer createKernel( VideoPlayerApi playerApi,  VideoKernelApiEvent event) {
        return new VideoFFmpegPlayer(playerApi, event);
    }
}
