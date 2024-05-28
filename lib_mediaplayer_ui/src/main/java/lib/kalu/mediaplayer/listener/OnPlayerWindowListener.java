package lib.kalu.mediaplayer.listener;

import lib.kalu.mediaplayer.type.PlayerType;


public interface OnPlayerWindowListener {

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     *
     * @param state 播放模式
     */
     void onWindow(@PlayerType.WindowType.Value int state);
}
