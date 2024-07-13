package lib.kalu.mediaplayer.core.kernel.video.ijk;




import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;



public class VideoIjkPlayerFactory implements VideoKernelFactory<VideoIjkPlayer> {

    public static VideoIjkPlayerFactory build() {
        return new VideoIjkPlayerFactory();
    }

    @Override
    public VideoIjkPlayer createKernel() {
        return new VideoIjkPlayer();
    }
}
