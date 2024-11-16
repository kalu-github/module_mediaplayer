package lib.kalu.mediaplayer.core.player.video;

import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.SpeedUtil;

public interface VideoPlayerApiDevice extends VideoPlayerApiBase {

    void setScreenKeep(boolean enable);

    default String getNetSpeed() {
        try {
            String speed = SpeedUtil.getNetSpeed(getBaseContext());
            LogUtil.log("VideoPlayerApiDevice => getNetSpeed => speed = " + speed);
            return speed;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApiDevice => getNetSpeed => " + e.getMessage());
            return "0kb/s";
        }
    }
}
