package lib.kalu.mediaplayer.core.kernel.video;

import org.json.JSONArray;

import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.type.PlayerType;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

interface VideoKernelApiBase {

    void setKernelApi(VideoKernelApiEvent eventApi);

    VideoKernelApiEvent getKernelApi();

    void setPlayerApi(VideoPlayerApi playerApi);

    VideoPlayerApi getPlayerApi();

    /*****/

    void setVolume(float left, float right);

    void seekTo(long position);

    boolean setSpeed(float speed);

    void setSpeed(@PlayerType.SpeedType.Value int speed);

    @PlayerType.SpeedType.Value
    int getSpeed();

    void start();

    void pause();

    void stop();

    void release();

    boolean isPlaying();

    long getPosition();

    long getDuration();

    default JSONArray getTrackInfo() {
        return null;
    }

    default boolean switchTrack(int trackId) {
        return false;
    }
}