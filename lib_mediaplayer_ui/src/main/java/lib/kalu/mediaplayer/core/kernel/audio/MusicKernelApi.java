package lib.kalu.mediaplayer.core.kernel.audio;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;


@Keep
public interface MusicKernelApi {

    void setMusicListener(@NonNull OnMusicPlayerChangeListener listener);

    void createDecoder(@NonNull Context context);

    void setDataSource(@NonNull Context context, @NonNull String musicUrl);

    void start(long position, OnMusicPlayerChangeListener l);

    void stop();

    void pause();

    void release();

    void addListener(long position);

    void removeListener(boolean clear);

    void setLooping(boolean v);

    void setVolume(float v);

    boolean isPlaying();

//    boolean isEnable();
//
//    void setEnable(boolean v);

    void seekTo(long v);

    long getDuration();

    long getPosition();

    default void setSeekParameters() {
    }
}