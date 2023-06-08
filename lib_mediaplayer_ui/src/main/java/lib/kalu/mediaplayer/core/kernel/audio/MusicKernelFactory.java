package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;

/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */
@Keep
public interface MusicKernelFactory<T extends MusicKernelApi> {
    T createKernel();
}