package lib.kalu.mediaplayer.core.kernel.video.yfy;




import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;


public class VideoYfyPlayerFactory implements VideoKernelFactory<VideoYfyPlayer> {

    private VideoYfyPlayerFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static VideoYfyPlayerFactory build() {
        return new VideoYfyPlayerFactory();
    }

    @Override
    public VideoYfyPlayer createKernel(VideoPlayerApi playerApi, VideoKernelApiEvent event) {
        return new VideoYfyPlayer(playerApi, event);
    }
}
