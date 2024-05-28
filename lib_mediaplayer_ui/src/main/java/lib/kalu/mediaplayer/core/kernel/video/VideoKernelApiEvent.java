package lib.kalu.mediaplayer.core.kernel.video;


import lib.kalu.mediaplayer.type.PlayerType;


public interface VideoKernelApiEvent {

    default void onUpdateProgress(boolean isLooping, long max, long position, long duration) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onUpdateSizeChanged(@PlayerType.KernelType.Value int kernel, int videoWidth, int videoHeight, @PlayerType.RotationType.Value int rotation) {
    }
}
