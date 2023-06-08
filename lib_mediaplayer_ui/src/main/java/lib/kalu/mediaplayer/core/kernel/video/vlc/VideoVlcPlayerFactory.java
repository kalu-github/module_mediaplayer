package lib.kalu.mediaplayer.core.kernel.video.vlc;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;
import lib.kalu.mediaplayer.core.player.PlayerApi;

@Keep
public class VideoVlcPlayerFactory implements KernelFactory<VideoVlcPlayer> {

    private VideoVlcPlayerFactory() {
    }

    public static VideoVlcPlayerFactory build() {
        return new VideoVlcPlayerFactory();
    }

    @Override
    public VideoVlcPlayer createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event) {
        return new VideoVlcPlayer(playerApi, event);
    }
}
