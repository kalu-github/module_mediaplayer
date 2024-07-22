package lib.kalu.mediaplayer.core.kernel.video;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

interface VideoKernelApiStartArgs extends VideoKernelApiBase {

    /***************/

    default void clearArgs(){
        setDoWindowing(false);
        setDoSeeking(false);
        setMute(false);
        setPrepared(false);
        setVideoSizeChanged(false);
        setStartArgs(null);
    }

    default long getTrySeeDuration() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getTrySeeDuration();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => getTrySeeDuration => Exception " + e.getMessage());
            return 0L;
        }
    }

    default boolean isLive() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.isLive();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => isLive => Exception " + e.getMessage());
            return false;
        }
    }

    default boolean isPlayWhenReady() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.isPlayWhenReady();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => isPlayWhenReady => Exception " + e.getMessage());
            return false;
        }
    }

    default long getPlayWhenReadyDelayedTime() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getPlayWhenReadyDelayedTime();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => getPlayWhenReadyDelayedTime => Exception " + e.getMessage());
            return 0L;
        }
    }

    @PlayerType.NetType.Value
    default int getNetType() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getNetType();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => getNetType => Exception " + e.getMessage());
            return PlayerType.NetType.DEFAULT;
        }
    }

    default long getSeek() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getSeek();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => getSeek => Exception " + e.getMessage());
            return 0L;
        }
    }

    /***************/

     void setStartArgs(StartArgs args);
     StartArgs getStartArgs();

     void setDoWindowing(boolean v);

     boolean isDoWindowing();

     boolean isDoSeeking();

     void setDoSeeking(boolean flag);
     void setLooping(boolean v);

     boolean isLooping();

     boolean isMute();

     void setMute(boolean v) ;

     boolean isPrepared();

     void setPrepared(boolean v);

     boolean isVideoSizeChanged();

     void setVideoSizeChanged(boolean v);
}