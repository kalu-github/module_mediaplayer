package lib.kalu.mediaplayer.core.kernel.video;

import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

public abstract class VideoBasePlayer implements VideoKernelApi {

    private VideoKernelApiEvent eventApi;
    private VideoPlayerApi playerApi;

    @Override
    public void setPlayerApi(VideoPlayerApi playerApi) {
        this.playerApi = playerApi;
    }

    @Override
    public VideoPlayerApi getPlayerApi() {
        return this.playerApi;
    }

    @Override
    public void setKernelApi(VideoKernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }

    @Override
    public VideoKernelApiEvent getKernelApi() {
        return this.eventApi;
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
//            MPLogUtil.log("VideoBasePlayer => onUpdateTimeMillis => " + e.getMessage());
        }
    }

    @Override
    public void onEvent(int kernel, int event) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onEvent(kernel, event);
        } catch (Exception e) {
            MPLogUtil.log("VideoBasePlayer => onEvent => " + e.getMessage());
        }
    }

    @Override
    public void onUpdateSizeChanged(int kernel, int videoWidth, int videoHeight, int rotation) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onUpdateSizeChanged(kernel, videoWidth, videoHeight, rotation);
        } catch (Exception e) {
            MPLogUtil.log("VideoBasePlayer => onUpdateSizeChanged => " + e.getMessage());
        }
    }

    public final void setEvent(VideoKernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }
}
