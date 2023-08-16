package lib.kalu.mediaplayer.core.kernel.video.android;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.KernelFactory;
import lib.kalu.mediaplayer.core.player.PlayerApi;

@Keep
public class VideoAndroidPlayerFactory implements KernelFactory<VideoAndroidPlayer> {

    private VideoAndroidPlayerFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static VideoAndroidPlayerFactory build() {
        return new VideoAndroidPlayerFactory();
    }

    @Override
    public VideoAndroidPlayer createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event, @NonNull boolean retryBuffering) {
        return new VideoAndroidPlayer(playerApi, event, retryBuffering);
    }
}
