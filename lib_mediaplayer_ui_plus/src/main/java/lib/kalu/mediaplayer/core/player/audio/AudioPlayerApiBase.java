package lib.kalu.mediaplayer.core.player.audio;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApi;

interface AudioPlayerApiBase {

    AudioKernelApi getAudioKernel();

    void setAudioKernel(@NonNull AudioKernelApi kernel);

    default Context getBaseContextAudio() {
        return ((View) this).getContext().getApplicationContext();
    }
}
