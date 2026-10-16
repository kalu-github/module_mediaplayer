package lib.kalu.mediaplayer.core.kernel.video.mpv;


import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public class VideoMpvPlayerFactory implements VideoKernelFactory<VideoMpvPlayer> {

    private VideoMpvPlayerFactory() {
    }

    public static VideoMpvPlayerFactory build() {
        return new VideoMpvPlayerFactory();
    }

    @Override
    public VideoMpvPlayer createKernel() {
        return new VideoMpvPlayer();
    }
}
