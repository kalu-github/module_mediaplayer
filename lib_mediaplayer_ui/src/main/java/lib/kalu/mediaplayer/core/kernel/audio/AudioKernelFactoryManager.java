package lib.kalu.mediaplayer.core.kernel.audio;

import lib.kalu.mediaplayer.core.kernel.audio.android.AudioAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.ijk.AudioIjkPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.android.VideoAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.exo1.VideoExoPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.exo2.VideoExo2PlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.ff.VideoFFmpegPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.ijk.VideoIjkPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.media3.VideoMedia3PlayerFactory;
import lib.kalu.mediaplayer.core.kernel.video.vlc.VideoVlcPlayerFactory;
import lib.kalu.mediaplayer.type.PlayerType;

public final class AudioKernelFactoryManager {

    public static AudioKernelFactory getFactory(@PlayerType.KernelType int type) {
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return AudioIjkPlayerFactory.build(false);
        }
        // ijk_mediacodec
        else if (type == PlayerType.KernelType.IJK_MEDIACODEC) {
            return AudioIjkPlayerFactory.build(true);
        }
        // android
        else {
            return AudioAndroidPlayerFactory.build();
        }
    }

    public static AudioKernelApi getKernel(@PlayerType.KernelType.Value int kernelType) {
        return getFactory(kernelType).createKernel();
    }
}
