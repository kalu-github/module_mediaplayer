package lib.kalu.ijkplayer.inter;

import lib.kalu.ijkplayer.IMediaPlayer;

public interface OnErrorListener {
    boolean onError(IMediaPlayer mp, int what, int extra);
}