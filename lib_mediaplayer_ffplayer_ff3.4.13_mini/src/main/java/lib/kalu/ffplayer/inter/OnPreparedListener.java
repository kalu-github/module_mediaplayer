package lib.kalu.ffplayer.inter;

import lib.kalu.ffplayer.FFmpegPlayer;

/**
 * Interface definition for a callback to be invoked when the media
 * source is ready for playback.
 */
public interface OnPreparedListener {
    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    void onPrepared(FFmpegPlayer mp);
}