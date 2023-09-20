package lib.kalu.mediaplayer.core.kernel.video.exo2;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public final class VideoExoPlayer2Factory implements VideoKernelFactory<VideoExoPlayer2> {

    private VideoExoPlayer2Factory() {
    }

    public static VideoExoPlayer2Factory build() {
        return new VideoExoPlayer2Factory();
    }

    @Override
    public VideoExoPlayer2 createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event, @NonNull boolean retryBuffering) {
        return new VideoExoPlayer2(playerApi, event, retryBuffering);
    }
}
