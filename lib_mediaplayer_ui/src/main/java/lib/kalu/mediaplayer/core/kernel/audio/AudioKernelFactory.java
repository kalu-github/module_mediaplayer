package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */
@Keep
public interface AudioKernelFactory<T extends AudioKernelApi> {
    T createKernel(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent event, @NonNull boolean retryBuffering);
}