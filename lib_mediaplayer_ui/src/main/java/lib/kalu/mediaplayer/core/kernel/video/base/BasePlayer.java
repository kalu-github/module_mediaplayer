package lib.kalu.mediaplayer.core.kernel.video.base;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public abstract class BasePlayer implements KernelApi {

    private KernelApiEvent eventApi;
    private PlayerApi playerApi;

    public BasePlayer(@NonNull PlayerApi playerApi, @NonNull KernelApiEvent eventApi) {
        this.playerApi = playerApi;
        this.eventApi = eventApi;
    }

    @Override
    public void onUpdateBuffer(int status) {
        try {
            if (null == playerApi)
                throw new Exception("playerApi error: null");
            boolean playing = isPlaying();
            if (!playing)
                throw new Exception("playing error: false");
            playerApi.callPlayerEvent(status);
        } catch (Exception e) {
            MPLogUtil.log("BasePlayer => onUpdateBuffer => " + e.getMessage());
        }
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
    public void onMeasure(int kernel, int videoWidth, int videoHeight, int rotation) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onMeasure(kernel, videoWidth, videoHeight, rotation);
        } catch (Exception e) {
            MPLogUtil.log("BasePlayer => onMeasure => " + e.getMessage());
        }
    }

    @Override
    public boolean isExternalMusicPlayWhenReady() {
        try {
            return playerApi.isExternalMusicPlayWhenReady();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicLooping() {
        try {
            return playerApi.isExternalMusicLooping();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isExternalMusicSeek() {
        try {
            return playerApi.isExternalMusicSeek();
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String getExternalMusicPath() {
        try {
            return playerApi.getExternalMusicPath();
        } catch (Exception e) {
            return null;
        }
    }

    public final void setEvent(@NonNull KernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }
}
