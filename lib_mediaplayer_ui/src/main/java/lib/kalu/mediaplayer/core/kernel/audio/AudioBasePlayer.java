package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public abstract class AudioBasePlayer implements AudioKernelApi {

    private AudioKernelApiEvent mAudioKernelApiEvent;
    private AudioPlayerApi mAudioPlayerApi;

    public AudioBasePlayer(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent eventApi) {
        this.mAudioPlayerApi = playerApi;
        this.mAudioKernelApiEvent = eventApi;
    }

    @Override
    public void setEvent(@NonNull AudioKernelApiEvent eventApi) {
        this.mAudioKernelApiEvent = eventApi;
    }

    @Override
    public void onEvent(int kernel, int event) {
        try {
            if (null == mAudioKernelApiEvent || null == mAudioKernelApiEvent)
                throw new Exception("mAudioKernelApiEvent error: null");
        } catch (Exception e) {
            MPLogUtil.log("AudioBasePlayer => onEvent => " + e.getMessage());
        }
    }
}
