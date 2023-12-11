package lib.kalu.mediaplayer.core.kernel.video.media3;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public final class VideoMedia3PlayerFactory implements VideoKernelFactory<VideoMedia3Player> {

    private VideoMedia3PlayerFactory() {
    }

    public static VideoMedia3PlayerFactory build() {
        return new VideoMedia3PlayerFactory();
    }

    @Override
    public VideoMedia3Player createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event) {
        return new VideoMedia3Player(playerApi, event);
    }
}
