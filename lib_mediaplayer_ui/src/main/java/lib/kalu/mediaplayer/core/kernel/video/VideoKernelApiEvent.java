package lib.kalu.mediaplayer.core.kernel.video;




import lib.kalu.mediaplayer.config.player.PlayerType;


public interface VideoKernelApiEvent {

    default void onUpdateTimeMillis( boolean isLooping,  long max,  long seek,  long position,  long duration) {
    }

    default void onEvent(@PlayerType.KernelType.Value int kernel, @PlayerType.EventType.Value int event) {
    }

    default void onUpdateSizeChanged(@PlayerType.KernelType.Value int kernel, int videoWidth, int videoHeight, @PlayerType.RotationType.Value int rotation) {
    }
}
