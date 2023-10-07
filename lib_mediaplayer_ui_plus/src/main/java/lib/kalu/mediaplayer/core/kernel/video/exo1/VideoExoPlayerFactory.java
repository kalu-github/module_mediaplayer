package lib.kalu.mediaplayer.core.kernel.video.exo1;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public final class VideoExoPlayerFactory implements VideoKernelFactory<VideoExoPlayer> {

    private VideoExoPlayerFactory() {
    }

    public static VideoExoPlayerFactory build() {
        return new VideoExoPlayerFactory();
    }

    @Override
    public VideoExoPlayer createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event) {
        return new VideoExoPlayer(playerApi, event);
    }
}
