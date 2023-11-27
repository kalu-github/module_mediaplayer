package lib.kalu.mediaplayer.core.kernel.video.mediax;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public final class VideoMediaxPlayerFactory implements VideoKernelFactory<VideoMediaxPlayer> {

    private VideoMediaxPlayerFactory() {
    }

    public static VideoMediaxPlayerFactory build() {
        return new VideoMediaxPlayerFactory();
    }

    @Override
    public VideoMediaxPlayer createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event) {
        return new VideoMediaxPlayer(playerApi, event);
    }
}
