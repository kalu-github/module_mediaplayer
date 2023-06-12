package lib.kalu.mediaplayer.core.kernel.video.ijk;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;
import lib.kalu.mediaplayer.core.player.PlayerApi;


@Keep
public class VideoIjkPlayerFactory implements KernelFactory<VideoIjkPlayer> {

    private boolean mUseMediaCodec;

    private VideoIjkPlayerFactory(boolean useMediaCodec) {
        mUseMediaCodec = useMediaCodec;
    }

//    private static class Holder {
//        static final IjkMediaPlayer mP = new IjkMediaPlayer();
//    }

    public static VideoIjkPlayerFactory build(boolean useMediaCodec) {
        return new VideoIjkPlayerFactory(useMediaCodec);
    }

    @Override
    public VideoIjkPlayer createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event) {
        return new VideoIjkPlayer(mUseMediaCodec, playerApi, event);
    }
}
