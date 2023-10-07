package lib.kalu.mediaplayer.core.kernel;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.android.VideoAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.exo1.VideoExoPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.exo2.VideoExoPlayer2Factory;
import lib.kalu.mediaplayer.core.kernel.ff.VideoFFmpegPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.ijk.VideoIjkPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.vlc.VideoVlcPlayerFactory;
import lib.kalu.mediaplayer.core.player.VideoPlayerApi;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class VideoKernelFactoryManager {

    public static VideoKernelFactory getFactory(@PlayerType.VideoKernelType int type) {
        // ijk
        if (type == PlayerType.VideoKernelType.VIDEO_IJK) {
            return VideoIjkPlayerFactory.build(false);
        }
        // ijk_mediacodec
        else if (type == PlayerType.VideoKernelType.VIDEO_IJK_MEDIACODEC) {
            return VideoIjkPlayerFactory.build(true);
        }
        // exo1
        else if (type == PlayerType.VideoKernelType.VIDEO_EXO_V1) {
            return VideoExoPlayerFactory.build();
        }
        // exo2
        else if (type == PlayerType.VideoKernelType.VIDEO_EXO_V2) {
            return VideoExoPlayer2Factory.build();
        }
        // vlc
        else if (type == PlayerType.VideoKernelType.VIDEO_VLC) {
            return VideoVlcPlayerFactory.build();
        }
        // ffplayer
        else if (type == PlayerType.VideoKernelType.VIDEO_FFPLAYER) {
            return VideoFFmpegPlayerFactory.build();
        }
        // android
        else {
            return VideoAndroidPlayerFactory.build();
        }
    }

    public static VideoKernelApi getKernel(@NonNull VideoPlayerApi playerApi, @PlayerType.VideoKernelType.Value int kernelType, @NonNull VideoKernelApiEvent event) {
        return getFactory(kernelType).createKernel(playerApi, event);
    }
}
