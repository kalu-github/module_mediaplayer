package lib.kalu.mediaplayer.core.kernel.video.base;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public abstract class BasePlayer implements KernelApi {

    private KernelApiEvent eventApi;
    private PlayerApi musicApi;

    public BasePlayer(@NonNull PlayerApi musicApi, @NonNull KernelApiEvent eventApi) {
        this.musicApi = musicApi;
        this.eventApi = eventApi;
    }

    @Override
    public void onUpdateTimeMillis() {
        try {
            if (null == eventApi)
                throw new Exception("eventApi error: null");
            boolean playing = isPlaying();
            if (!playing)
                throw new Exception("playing error: false");
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
            MPLogUtil.log("BasePlayer => onUpdateTimeMillis => " + e.getMessage());
        }
    }

    @Override
    public void onEvent(int kernel, int event) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onEvent(kernel, event);
        } catch (Exception e) {
            MPLogUtil.log("BasePlayer => onEvent => " + e.getMessage());
        }
    }

    @Override
    public boolean isExternalMusicPlayWhenReady() {
        try {
            return musicApi.isExternalMusicPlayWhenReady();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicLooping() {
        try {
            return musicApi.isExternalMusicLooping();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicSeek() {
        try {
            return musicApi.isExternalMusicSeek();
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String getExternalMusicPath() {
        try {
            return musicApi.getExternalMusicPath();
        } catch (Exception e) {
            return null;
        }
    }

    public final void setEvent(@NonNull KernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }
}
