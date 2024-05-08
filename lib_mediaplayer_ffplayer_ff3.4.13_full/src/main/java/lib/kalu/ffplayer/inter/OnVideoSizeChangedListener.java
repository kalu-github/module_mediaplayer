package lib.kalu.ffplayer.inter;

import lib.kalu.ffplayer.FFmpegPlayer;

/**
 * Interface definition of a callback to be invoked when the
 * video size is first known or updated
 */
public interface OnVideoSizeChangedListener {
    /**
     * Called to indicate the video size
     *
     * @param mp     the MediaPlayer associated with this callback
     * @param width  the width of the video
     * @param height the height of the video
     */
    void onVideoSizeChanged(FFmpegPlayer mp, int width, int height);
}