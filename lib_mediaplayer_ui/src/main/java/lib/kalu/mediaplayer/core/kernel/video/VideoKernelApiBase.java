package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.FloatRange;



import org.json.JSONArray;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

interface VideoKernelApiBase {

    void setVolume(float left, float right);

    boolean isMute();

    void setMute(boolean v);

    void seekTo( long position);

    boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed);

    @FloatRange(from = 1F, to = 4F)
    float getSpeed();

    long getSeek();

    void setSeek(long seek);

    long getMax();

    void setMax(long max);

    boolean isLive();

    void setLive( boolean v);

    void setLooping(boolean loop);

    boolean isLooping();

    boolean isPrepared();

    void start();

    void pause();

    void stop();

    void release();

    boolean isPlaying();

    long getPosition();

    long getDuration();

    void setPlayWhenReady(boolean playWhenReady);

    boolean isPlayWhenReady();

    default JSONArray getTrackInfo() {
        return null;
    }

    default boolean switchTrack( int trackId) {
        return false;
    }
}