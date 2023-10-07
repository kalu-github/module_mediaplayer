package lib.kalu.mediaplayer.core.player;

import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.SpeedUtil;

interface VideoPlayerApiDevice extends VideoPlayerApiBase {

    void setScreenKeep(boolean enable);

    default String getNetSpeed() {
        try {
            String speed = SpeedUtil.getNetSpeed(getBaseContext());
            MPLogUtil.log("VideoPlayerApiDevice => getNetSpeed => speed = " + speed);
            return speed;
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiDevice => getNetSpeed => " + e.getMessage());
            return "0kb/s";
        }
    }
}
