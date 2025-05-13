package lib.kalu.mediaplayer.core.kernel.video;


import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.type.PlayerType;


public interface VideoKernelApiEvent {

    default void onUpdateProgress() {
    }

    default void onUpdateSubtitle(int kernel, String language, CharSequence result) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onVideoFormatChanged(@PlayerType.KernelType.Value int kernel,
                                      @PlayerType.RotationType.Value int rotation,
                                      @PlayerType.ScaleType.Value int scaleType,
                                      int width, int height, int bitrate) {
    }
}
