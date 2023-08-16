package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.player.PlayerApi;

/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */
@Keep
public interface KernelFactory<T extends KernelApi> {
    T createKernel(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent event,@NonNull boolean retryBuffering);
}