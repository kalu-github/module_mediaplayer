package lib.kalu.mediaplayer.core.render;

import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;


public interface VideoRenderApiBase {

    void setVideoKernel(VideoKernelApi player);

    VideoKernelApi getVideoKernel();
}