package lib.kalu.mediaplayer.core.kernel.vlc;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.VideoPlayerApi;

@Keep
public class VideoVlcPlayerFactory implements VideoKernelFactory<VideoVlcPlayer> {

    private VideoVlcPlayerFactory() {
    }

    public static VideoVlcPlayerFactory build() {
        return new VideoVlcPlayerFactory();
    }

    @Override
    public VideoVlcPlayer createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event) {
        return new VideoVlcPlayer(playerApi, event);
    }
}
