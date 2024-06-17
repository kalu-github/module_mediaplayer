package lib.kalu.mediaplayer.core.kernel.video;

import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.LogUtil;

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
    public void onUpdateProgress() {
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
            eventApi.onUpdateProgress(position, duration);
        } catch (Exception e) {
        }
    }

    @Override
    public void onEvent(int kernel, int event) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onEvent(kernel, event);
        } catch (Exception e) {
            LogUtil.log("VideoBasePlayer => onEvent => " + e.getMessage());
        }
    }

    @Override
    public void onUpdateSizeChanged(int kernel, int videoWidth, int videoHeight, int rotation) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onUpdateSizeChanged(kernel, videoWidth, videoHeight, rotation);
        } catch (Exception e) {
            LogUtil.log("VideoBasePlayer => onUpdateSizeChanged => " + e.getMessage());
        }
    }

    public final void setEvent(VideoKernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }
}
