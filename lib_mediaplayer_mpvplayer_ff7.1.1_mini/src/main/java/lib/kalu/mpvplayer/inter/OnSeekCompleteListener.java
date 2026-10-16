package lib.kalu.mpvplayer.inter;

import lib.kalu.mpvplayer.MpvPlayer;

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
    void onSeekComplete(MpvPlayer mp);
}