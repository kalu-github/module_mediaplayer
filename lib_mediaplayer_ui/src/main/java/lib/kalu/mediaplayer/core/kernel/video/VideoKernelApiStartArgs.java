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

    StartArgs[] mStartArgs = new StartArgs[]{null};

    default void setStartArgs(StartArgs args) {
        mStartArgs[0] = args;
    }

    default StartArgs getStartArgs() {
        return mStartArgs[0];
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

    // 正在切换window
    boolean[] mDoWindowing = new boolean[]{false};

    default void setDoWindowing(boolean v) {
        mDoWindowing[0] = v;
    }

    default boolean isDoWindowing() {
        return mDoWindowing[0];
    }

    /*****/

    // 起播快进
    boolean[] mDoSeeking = new boolean[]{false};

    default boolean isDoSeeking() {
        return mDoSeeking[0];
    }

    default void setDoSeeking(boolean flag) {
        mDoSeeking[0] = flag;
    }

    /*****/

    boolean[] mLooping = new boolean[]{false};

    default void setLooping(boolean v) {
        mLooping[0] = v;
    }

    default boolean isLooping() {
        return mLooping[0];
    }

    /*****/

    boolean[] mMute = new boolean[]{false}; // 是否静音

    default boolean isMute() {
        return mMute[0];
    }

    default void setMute(boolean v) {
        mMute[0] = v;
    }

    /*****/

    boolean[] mPrepared = new boolean[]{false};

    default boolean isPrepared() {
        return mPrepared[0];
    }

    default void setPrepared(boolean v) {
        mPrepared[0] = v;
    }

    /*****/

    boolean[] mVideoSizeChanged = new boolean[]{false};

    default boolean isVideoSizeChanged() {
        return mVideoSizeChanged[0];
    }

    default void setVideoSizeChanged(boolean v) {
        mVideoSizeChanged[0] = v;
    }
}