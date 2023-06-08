package lib.kalu.mediaplayer.core.kernel.video.exo2;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;
import lib.kalu.mediaplayer.core.player.PlayerApi;

@Keep
public final class VideoExoPlayer2Factory implements KernelFactory<VideoExoPlayer2> {

    private VideoExoPlayer2Factory() {
    }

    public static VideoExoPlayer2Factory build() {
        return new VideoExoPlayer2Factory();
    }

    @Override
    public VideoExoPlayer2 createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event) {
        return new VideoExoPlayer2(playerApi, event);
    }
}
