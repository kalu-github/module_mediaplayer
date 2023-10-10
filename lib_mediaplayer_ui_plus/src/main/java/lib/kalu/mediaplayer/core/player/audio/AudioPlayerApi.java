package lib.kalu.mediaplayer.core.player.audio;

import android.content.Context;

import androidx.annotation.NonNull;

public interface AudioPlayerApi extends AudioPlayerApiKernel {

    default boolean switchExternalAudio(@NonNull boolean enable) {
        return false;
    }
}
