package lib.kalu.ffplayer.inter;

import lib.kalu.ffplayer.FFmpegPlayer;

public interface OnInfoListener {
    /**
     * Called to indicate an info or a warning.
     *
     * @param mp    the MediaPlayer the info pertains to.
     * @param what  the type of info or warning.
     *              <ul>
     *              <li>{@link #MEDIA_INFO_UNKNOWN}
     *              <li>{@link #MEDIA_INFO_VIDEO_TRACK_LAGGING}
     *              <li>{@link #MEDIA_INFO_BUFFERING_START}
     *              <li>{@link #MEDIA_INFO_BUFFERING_END}
     *              <li>{@link #MEDIA_INFO_BAD_INTERLEAVING}
     *              <li>{@link #MEDIA_INFO_NOT_SEEKABLE}
     *              <li>{@link #MEDIA_INFO_METADATA_UPDATE}
     *              </ul>
     * @param extra an extra code, specific to the info. Typically
     *              implementation dependant.
     * @return True if the method handled the info, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the info to be discarded.
     */
    boolean onInfo(FFmpegPlayer mp, int what, int extra);

}
