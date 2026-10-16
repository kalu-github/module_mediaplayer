package lib.kalu.mpvplayer.inter;

import lib.kalu.mpvplayer.MpvPlayer;

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
    void onVideoSizeChanged(MpvPlayer mp, int width, int height);
}