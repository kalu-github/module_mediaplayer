package lib.kalu.mediaplayer.core.kernel.video.android;


import lib.kalu.mediaplayer.core.kernel.video.VideoKernelFactory;


public class VideoAndroidPlayerFactory implements VideoKernelFactory<VideoAndroidPlayer> {

    private VideoAndroidPlayerFactory() {
    }

//    private static class Holder {
//        static final AndroidMediaPlayer mP = new AndroidMediaPlayer();
//    }

    public static VideoAndroidPlayerFactory build() {
        return new VideoAndroidPlayerFactory();
    }

    @Override
    public VideoAndroidPlayer createKernel() {
        return new VideoAndroidPlayer();
    }
}
