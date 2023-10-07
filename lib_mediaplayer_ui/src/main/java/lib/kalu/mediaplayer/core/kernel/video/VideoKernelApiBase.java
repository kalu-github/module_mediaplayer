package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
interface VideoKernelApiBase {

    void setVolume(float left, float right);

    boolean isMute();

    void setMute(boolean v);

    void seekTo(@NonNull long position);

    void setSpeed(float speed);

    float getSpeed();

    long getSeek();

    void setSeek(long seek);

    long getMax();

    void setMax(long max);

    boolean isLive();

    void setLive(@NonNull boolean v);

    void setLooping(boolean loop);

    boolean isLooping();

    boolean isPrepared();

    void release(boolean isMainThread);

    void start();

    void pause();

    void stop(boolean isMainThread);

    boolean isPlaying();

    long getPosition();

    long getDuration();

    void setPlayWhenReady(boolean playWhenReady);

    boolean isPlayWhenReady();
}