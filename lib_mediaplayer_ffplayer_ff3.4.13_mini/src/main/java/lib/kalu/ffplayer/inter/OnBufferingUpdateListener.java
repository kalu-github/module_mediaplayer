package lib.kalu.ffplayer.inter;

import lib.kalu.ffplayer.FFmpegPlayer;

/**
 * Interface definition of a callback to be invoked indicating buffering
 * status of a media resource being streamed over the network.
 */
public interface OnBufferingUpdateListener {
    /**
     * Called to update status in buffering a media stream received through
     * progressive HTTP download. The received buffering percentage
     * indicates how much of the content has been buffered or played.
     * For example a buffering update of 80 percent when half the content
     * has already been played indicates that the next 30 percent of the
     * content to play has been buffered.
     *
     * @param mp      the MediaPlayer the update pertains to
     * @param percent the percentage (0-100) of the content
     *                that has been buffered or played thus far
     */
    void onBufferingUpdate(FFmpegPlayer mp, int percent);
}