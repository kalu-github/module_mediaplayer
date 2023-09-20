package lib.kalu.mediaplayer.core.kernel.video.vlc;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public class VideoVlcPlayerFactory implements VideoKernelFactory<VideoVlcPlayer> {

    private VideoVlcPlayerFactory() {
    }

    public static VideoVlcPlayerFactory build() {
        return new VideoVlcPlayerFactory();
    }

    @Override
    public VideoVlcPlayer createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event, @NonNull boolean retryBuffering) {
        return new VideoVlcPlayer(playerApi, event, retryBuffering);
    }
}
