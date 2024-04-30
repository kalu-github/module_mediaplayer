package lib.kalu.ijkplayer.inter;

import lib.kalu.ijkplayer.IMediaPlayer;

public interface OnInfoListener {
    boolean onInfo(IMediaPlayer mp, int what, int extra);
}
