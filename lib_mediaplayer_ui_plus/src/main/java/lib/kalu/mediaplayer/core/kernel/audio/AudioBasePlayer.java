package lib.kalu.mediaplayer.core.kernel.audio;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public abstract class AudioBasePlayer implements AudioKernelApi {

    private AudioKernelApiEvent eventApi;
    private AudioPlayerApi playerApi;

    public AudioBasePlayer(@NonNull AudioPlayerApi playerApi, @NonNull AudioKernelApiEvent eventApi) {
        this.playerApi = playerApi;
        this.eventApi = eventApi;
    }

    public final void setEvent(@NonNull AudioKernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }

    @Override
    public void onUpdateTimeMillis() {
        try {
            if (null == eventApi)
                throw new Exception("eventApi warning: null");
            boolean playing = isPlaying();
            if (!playing)
                throw new Exception("playing warning: false");
            long position = getPosition();
            if (position < 0)
                position = 0;
            long duration = getDuration();
            if (duration < 0)
                duration = 0;
            long seek = getSeek();
            long max = getMax();
            boolean looping = isLooping();
            eventApi.onUpdateTimeMillis(looping, max, seek, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("AudioBasePlayer => onUpdateTimeMillis => " + e.getMessage());
        }
    }

    @Override
    public void onEvent(@PlayerType.KernelType.Value int kernel, int event) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onEvent(kernel, event);
        } catch (Exception e) {
            MPLogUtil.log("AudioBasePlayer => onEvent => " + e.getMessage());
        }
    }
}
