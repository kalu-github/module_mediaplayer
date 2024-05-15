package lib.kalu.mediaplayer.listener;


public interface OnPlayerProgressListener {

    default void onProgress(long position, long duration) {
    }
}
