package lib.kalu.mediaplayer.buried;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@Keep
public interface BuriedEvent {

    void onPlaying(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onExit(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onCompletion(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onError(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onPause(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onResume(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onSeek(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onWindowFull(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onWindowFloat(@NonNull String url, @NonNull long position, @NonNull long duration);

    void onWindowSimple(@NonNull String url, @NonNull long position, @NonNull long duration);
}