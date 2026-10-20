package lib.kalu.mpvplayer;

/** SDK listeners are delivered on the Android main thread. */
public interface PlayerListener {
    default void onPlaybackStateChanged(MpvPlayer player, MpvPlayer.PlaybackState state) {}
    default void onPrepared(MpvPlayer player) {}
    default void onPlaybackStarted(MpvPlayer player) {}
    default void onPaused(MpvPlayer player) {}
    default void onResumed(MpvPlayer player) {}
    default void onStopped(MpvPlayer player) {}
    default void onBufferingUpdate(MpvPlayer player, int percent) {}
    default void onBufferingChanged(MpvPlayer player, boolean buffering) {}
    default void onFirstFrame(MpvPlayer player, long elapsedMs) {}
    default void onVideoSizeChanged(MpvPlayer player, int width, int height) {}
    default void onSeekComplete(MpvPlayer player) {}
    default void onProgress(MpvPlayer player, long positionMs, long durationMs, long bufferedPositionMs) {}
    default void onCompletion(MpvPlayer player) {}
    default void onError(MpvPlayer player, PlayerError error) {}
    default void onRetry(MpvPlayer player, int attempt, long delayMs) {}
    default void onRecoveryResult(MpvPlayer player, boolean recovered) {}
    default void onSubtitle(MpvPlayer player, String text) {}
    default void onReleased(MpvPlayer player) {}
}
