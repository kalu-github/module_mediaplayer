package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.audio.android.AudioAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.exo1.AudioExoPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.exo2.AudioExoPlayer2Factory;
import lib.kalu.mediaplayer.core.kernel.audio.ff.AudioFFmpegPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.ijk.AudioIjkPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.vlc.AudioVlcPlayerFactory;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

/**
 * @description: 工具类
 * @date: 2021-05-12 14:41
 */
@Keep
public final class AudioKernelFactoryManager {

    public static AudioKernelFactory getFactory(@PlayerType.AudioKernelType int type) {
        // ijk
        if (type == PlayerType.AudioKernelType.AUDIO_IJK) {
            return AudioIjkPlayerFactory.build(false);
        }
        // ijk_mediacodec
        else if (type == PlayerType.AudioKernelType.AUDIO_IJK_MEDIACODEC) {
            return AudioIjkPlayerFactory.build(true);
        }
        // exo1
        else if (type == PlayerType.AudioKernelType.AUDIO_EXO_V1) {
            return AudioExoPlayerFactory.build();
        }
        // exo2
        else if (type == PlayerType.AudioKernelType.AUDIO_EXO_V2) {
            return AudioExoPlayer2Factory.build();
        }
        // vlc
        else if (type == PlayerType.AudioKernelType.AUDIO_VLC) {
            return AudioVlcPlayerFactory.build();
        }
        // ffplayer
        else if (type == PlayerType.AudioKernelType.AUDIO_FFPLAYER) {
            return AudioFFmpegPlayerFactory.build();
        }
        // android
        else {
            return AudioAndroidPlayerFactory.build();
        }
    }

    public static AudioKernelApi getKernel(@NonNull AudioPlayerApi playerApi, @NonNull boolean retryBuffering, @PlayerType.VideoKernelType.Value int kernelType, @NonNull AudioKernelApiEvent event) {
        return getFactory(kernelType).createKernel(playerApi, event, retryBuffering);
    }
}
