package lib.kalu.mpvplayer.inter;

import lib.kalu.mpvplayer.MpvPlayer;

/**
 * Interface definition for a callback to be invoked when playback of
 * a media source has completed.
 */
public interface OnCompletionListener {
    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    void onCompletion(MpvPlayer mp);
}