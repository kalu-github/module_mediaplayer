package lib.kalu.mediaplayer.core.render;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;


public interface VideoRenderApiBase {

    void setKernel(VideoKernelApi player);

    VideoKernelApi getKernel();
}