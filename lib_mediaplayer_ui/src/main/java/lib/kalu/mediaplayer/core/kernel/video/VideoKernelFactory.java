package lib.kalu.mediaplayer.core.kernel.video;


/**
 * @description: 1.继承{@link VideoPlayerImpl}扩展自己的播放器。
 * @date: 2021-05-12 14:42
 */

public interface VideoKernelFactory<T extends VideoKernelApi> {
    T createKernel();
}