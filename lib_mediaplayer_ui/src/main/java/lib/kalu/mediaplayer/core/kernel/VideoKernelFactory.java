package lib.kalu.mediaplayer.core.kernel;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.player.VideoPlayerApi;

/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */
@Keep
public interface VideoKernelFactory<T extends VideoKernelApi> {
    T createKernel(@NonNull VideoPlayerApi playerApi, @NonNull VideoKernelApiEvent event);
}