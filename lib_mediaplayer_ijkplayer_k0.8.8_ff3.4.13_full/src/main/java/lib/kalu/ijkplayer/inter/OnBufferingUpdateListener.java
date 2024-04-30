package lib.kalu.ijkplayer.inter;

import lib.kalu.ijkplayer.IMediaPlayer;

public interface OnBufferingUpdateListener {
    void onBufferingUpdate(IMediaPlayer mp, int percent);
}
