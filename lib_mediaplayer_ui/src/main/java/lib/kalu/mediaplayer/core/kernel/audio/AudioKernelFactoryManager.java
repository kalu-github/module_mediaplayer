package lib.kalu.mediaplayer.core.kernel.audio;

import lib.kalu.mediaplayer.core.kernel.audio.android.AudioAndroidPlayerFactory;
import lib.kalu.mediaplayer.core.kernel.audio.ijk.AudioIjkPlayerFactory;
import lib.kalu.mediaplayer.type.PlayerType;

public final class AudioKernelFactoryManager {

    public static AudioKernelFactory getFactory(@PlayerType.KernelType int type) {
        // ijk
        if (type == PlayerType.KernelType.IJK) {
            return AudioIjkPlayerFactory.build();
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
