package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.audio.android.AudioAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.ijk.AudioIjkPlayerFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class AudioKernelFactoryManager {

    public static AudioKernelFactory getFactory(@PlayerType.KernelType int type) {
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return AudioIjkPlayerFactory.build(false);
        }
//        // ijk_mediacodec
//        else if (type == PlayerType.KernelType.IJK_MEDIACODEC) {
//            return VideoIjkPlayerFactory.build(true);
//        }
//        // exo1
//        else if (type == PlayerType.KernelType.EXO_V1) {
//            return VideoExoPlayerFactory.build();
//        }
//        // exo2
//        else if (type == PlayerType.KernelType.EXO_V2) {
//            return VideoExoPlayer2Factory.build();
//        }
//        // vlc
//        else if (type == PlayerType.KernelType.VLC) {
//            return VideoVlcPlayerFactory.build();
//        }
//        // ffplayer
//        else if (type == PlayerType.KernelType.FFPLAYER) {
//            return VideoFFmpegPlayerFactory.build();
//        }
        // android
        else {
            return AudioAndroidPlayerFactory.build();
        }
    }

    public static AudioKernelApi getKernel(@NonNull AudioPlayerApi playerApi, @NonNull boolean retryBuffering, @PlayerType.KernelType.Value int kernelType, @NonNull AudioKernelApiEvent event) {
        return getFactory(kernelType).createKernel(playerApi, event, retryBuffering);
    }
}
