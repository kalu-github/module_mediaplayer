package lib.kalu.mediaplayer.core.kernel.video.ff;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;
import lib.kalu.mediaplayer.core.player.PlayerApi;

@Keep
public class VideoFFmpegPlayerFactory implements KernelFactory<VideoFFmpegPlayer> {

    private VideoFFmpegPlayerFactory() {
    }

    public static VideoFFmpegPlayerFactory build() {
        return new VideoFFmpegPlayerFactory();
    }

    @Override
    public VideoFFmpegPlayer createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event) {
        return new VideoFFmpegPlayer(playerApi, event);
    }
}
