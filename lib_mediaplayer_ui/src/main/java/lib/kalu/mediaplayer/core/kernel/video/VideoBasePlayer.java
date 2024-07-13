package lib.kalu.mediaplayer.core.kernel.video;

import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public abstract class VideoBasePlayer implements VideoKernelApi {

    private VideoKernelApiEvent eventApi;
    private VideoPlayerApi playerApi;
    private int mVideoSpeed = PlayerType.SpeedType.DEFAULT;

    @Override
    public void setSpeed(@PlayerType.SpeedType.Value int speed) {
        this.mVideoSpeed = speed;
        switch (mVideoSpeed) {
            case PlayerType.SpeedType._0_5:
                setSpeed(0.5F);
                break;
            case PlayerType.SpeedType._1_5:
                setSpeed(1.5F);
                break;
            case PlayerType.SpeedType._2_0:
                setSpeed(2.0F);
                break;
            case PlayerType.SpeedType._2_5:
                setSpeed(2.5F);
                break;
            case PlayerType.SpeedType._3_0:
                setSpeed(3.0F);
                break;
            case PlayerType.SpeedType._3_5:
                setSpeed(3.5F);
                break;
            case PlayerType.SpeedType._4_0:
                setSpeed(4.0F);
                break;
            case PlayerType.SpeedType._4_5:
                setSpeed(4.5F);
                break;
            case PlayerType.SpeedType._5_0:
                setSpeed(5.0F);
                break;
            default:
                setSpeed(1.0F);
                break;
        }
    }

    @PlayerType.SpeedType.Value
    @Override
    public int getSpeed() {
        return this.mVideoSpeed;
    }

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
//            boolean playing = isPlaying();
//            if (!playing)
//                throw new Exception("playing warning: false");
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
