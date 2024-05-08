package lib.kalu.ffplayer.inter;

import lib.kalu.ffplayer.FFmpegPlayer;

/**
 * Interface definition of a callback to be invoked indicating
 * the completion of a seek operation.
 */
public interface OnSeekCompleteListener {
    /**
     * Called to indicate the completion of a seek operation.
     *
     * @param mp the MediaPlayer that issued the seek operation
     */
    void onSeekComplete(FFmpegPlayer mp);
}