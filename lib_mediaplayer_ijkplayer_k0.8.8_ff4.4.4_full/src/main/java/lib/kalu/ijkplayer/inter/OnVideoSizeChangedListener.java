package lib.kalu.ijkplayer.inter;

import lib.kalu.ijkplayer.IMediaPlayer;

public interface OnVideoSizeChangedListener {
    void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den);
}