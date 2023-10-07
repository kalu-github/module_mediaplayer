package lib.kalu.mediaplayer.core.kernel.ijk;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.VideoPlayerApi;


@Keep
public class VideoIjkPlayerFactory implements VideoKernelFactory<VideoIjkPlayer> {

    private boolean mUseMediaCodec;

    private VideoIjkPlayerFactory(boolean useMediaCodec) {
        mUseMediaCodec = useMediaCodec;
    }

    public static VideoIjkPlayerFactory build(boolean useMediaCodec) {
        return new VideoIjkPlayerFactory(useMediaCodec);
    }

    @Override
    public VideoIjkPlayer createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event) {
        return new VideoIjkPlayer(mUseMediaCodec, playerApi, event);
    }
}
