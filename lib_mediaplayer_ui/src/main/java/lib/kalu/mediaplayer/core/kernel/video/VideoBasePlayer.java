package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.Nullable;

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
            LogUtil.log("VideoBasePlayer => onUpdateProgress => " + e.getMessage());
        }
    }

    @Override
    public void onUpdateProgress(long position, long duration) {
        try {
            if (null == eventApi)
                throw new Exception("eventApi warning: null");
//            boolean playing = isPlaying();
//            if (!playing)
//                throw new Exception("playing warning: false");
            if (position < 0)
                position = 0;
            if (duration < 0)
                duration = 0;
            eventApi.onUpdateProgress(position, duration);
        } catch (Exception e) {
            LogUtil.log("VideoBasePlayer => onUpdateProgress => " + e.getMessage());
        }
    }


    @Override
    public void onUpdateSubtitle(int kernel, String language, CharSequence result) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onUpdateSubtitle(kernel, language, result);
        } catch (Exception e) {
            LogUtil.log("VideoBasePlayer => onUpdateSubtitle => " + e.getMessage());
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
    public void onVideoFormatChanged(int kernel, int rotation, int scaleType, int width, int height, int bitrate) {
        try {
            if (null == eventApi || null == eventApi)
                throw new Exception("eventApi error: null");
            eventApi.onVideoFormatChanged(kernel, rotation, scaleType, width, height, bitrate);
        } catch (Exception e) {
            LogUtil.log("VideoBasePlayer => onVideoFormatChanged => " + e.getMessage());
        }
    }

    public final void setEvent(VideoKernelApiEvent eventApi) {
        this.eventApi = eventApi;
    }
}
