package lib.kalu.mediaplayer.core.kernel.exo2;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.VideoPlayerApi;

@Keep
public final class VideoExoPlayer2Factory implements VideoKernelFactory<VideoExoPlayer2> {

    private VideoExoPlayer2Factory() {
    }

    public static VideoExoPlayer2Factory build() {
        return new VideoExoPlayer2Factory();
    }

    @Override
    public VideoExoPlayer2 createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event) {
        return new VideoExoPlayer2(playerApi, event);
    }
}
