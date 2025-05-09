package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;

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

    default boolean toggleTrackLanguageSubtitle(String language) {
        return false;
    }

    default boolean toggleTrackLanguageAudio(String language) {
        return false;
    }

    default boolean toggleTrackRoleFlagSubtitle(int roleFlag) {
        return false;
    }

    default boolean toggleTrackRoleFlagAudio(int roleFlag) {
        return false;
    }

    default boolean toggleTrackRoleFlagVideo(int roleFlag) {
        return false;
    }

    default boolean toggleTrack(int groupIndex, int trackIndex) {
        return false;
    }

    default JSONArray getTrackInfo(int type) {
        return null;
    }

    default JSONArray getTrackInfoAll() {
        return getTrackInfo(-1);
    }

    default JSONArray getTrackInfoVideo() {
        return getTrackInfo(1);
    }

    default JSONArray getTrackInfoAudio() {
        return getTrackInfo(2);
    }

    default JSONArray getTrackInfoSubtitle() {
        return getTrackInfo(3);
    }

    default void callPlayWhenReadyDelayedTimeComplete(Context context) {
    }
}