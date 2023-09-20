package lib.kalu.mediaplayer.core.kernel.video.android;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

@Keep
public class VideoAndroidPlayerFactory implements VideoKernelFactory<VideoAndroidPlayer> {

    private VideoAndroidPlayerFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static VideoAndroidPlayerFactory build() {
        return new VideoAndroidPlayerFactory();
    }

    @Override
    public VideoAndroidPlayer createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event, @NonNull boolean retryBuffering) {
        return new VideoAndroidPlayer(playerApi, event, retryBuffering);
    }
}
