package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.android.VideoAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.exo1.VideoExoPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.exo2.VideoExoPlayer2Factory;
import lib.kalu.mediaplayer.core.kernel.video.exo2.VideoMediaxPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.ff.VideoFFmpegPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.ijk.VideoIjkPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.vlc.VideoVlcPlayerFactory;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class VideoKernelFactoryManager {

    public static VideoKernelFactory getFactory(@PlayerType.KernelType int type) {
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return VideoIjkPlayerFactory.build(false);
        }
        // ijk_mediacodec
        else if (type == PlayerType.KernelType.IJK_MEDIACODEC) {
            return VideoIjkPlayerFactory.build(true);
        }
        // exo1
        else if (type == PlayerType.KernelType.EXO_V1) {
            return VideoExoPlayerFactory.build();
        }
        // exo2
        else if (type == PlayerType.KernelType.EXO_V2) {
            return VideoExoPlayer2Factory.build();
        }
        // meiax
        else if (type == PlayerType.KernelType.MEDIAX) {
            return VideoMediaxPlayerFactory.build();
        }
        // vlc
        else if (type == PlayerType.KernelType.VLC) {
            return VideoVlcPlayerFactory.build();
        }
        // ffplayer
        else if (type == PlayerType.KernelType.FFPLAYER) {
            return VideoFFmpegPlayerFactory.build();
        }
        // android
        else {
            return VideoAndroidPlayerFactory.build();
        }
    }

    public static VideoKernelApi getKernel(@NonNull VideoPlayerApi playerApi, @PlayerType.KernelType.Value int kernelType, @NonNull VideoKernelApiEvent event) {
        return getFactory(kernelType).createKernel(playerApi, event);
    }
}
