package lib.kalu.ffplayer.inter;

import lib.kalu.ffplayer.FFmpegPlayer;

/**
 * Interface definition of a callback to be invoked when there
 * has been an error during an asynchronous operation (other errors
 * will throw exceptions at method call time).
 */
public interface OnErrorListener {
    /**
     * Called to indicate an error.
     *
     * @param mp    the MediaPlayer the error pertains to
     * @param what  the type of error that has occurred:
     *              <ul>
     *              <li>{@link #MEDIA_ERROR_UNKNOWN}
     *              <li>{@link #MEDIA_ERROR_SERVER_DIED}
     *              </ul>
     * @param extra an extra code, specific to the error. Typically
     *              implementation dependant.
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    boolean onError(FFmpegPlayer mp, int what, int extra);
}
