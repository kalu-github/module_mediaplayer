package lib.kalu.mediaplayer.core.kernel.video;

public interface VideoKernelApiSubtitle {
    default boolean setSubtitleOffsetMs(int offset) {
        return false;
    }

    default boolean addSubtitleTrack(String url) {
        return false;
    }
}
