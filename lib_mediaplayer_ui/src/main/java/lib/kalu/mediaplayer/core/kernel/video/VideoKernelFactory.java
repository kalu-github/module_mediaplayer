package lib.kalu.mediaplayer.core.kernel.video;




import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;

/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */

public interface VideoKernelFactory<T extends VideoKernelApi> {
    T createKernel( VideoPlayerApi playerApi,  VideoKernelApiEvent event);
}