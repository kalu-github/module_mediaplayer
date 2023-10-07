package lib.kalu.mediaplayer.core.kernel.video.ff;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public class VideoFFmpegPlayerFactory implements VideoKernelFactory<VideoFFmpegPlayer> {

    private VideoFFmpegPlayerFactory() {
    }

    public static VideoFFmpegPlayerFactory build() {
        return new VideoFFmpegPlayerFactory();
    }

    @Override
    public VideoFFmpegPlayer createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event) {
        return new VideoFFmpegPlayer(playerApi, event);
    }
}
