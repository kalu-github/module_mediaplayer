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

    default boolean isExoUseOkhttp() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.isExoUseOkhttp();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => isExoUseOkhttp => Exception " + e.getMessage());
            return false;
        }
    }


    @PlayerType.KernelType.Value
    default int getKernelType() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getKernelType();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => getKernelType => Exception " + e.getMessage());
            return PlayerType.KernelType.ANDROID;
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

    // 快进
    long[] mSeek = new long[]{0L};

    default long getSeek() {
        try {
            if (mSeek[0] <= 0L)
                throw new Exception("warning: mSeek <= 0");
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration <= 0");
            if (mSeek[0] >= duration)
                throw new Exception("warning: mSeek >= duration");
            return mSeek[0];
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => getSeek => " + e.getMessage());
            mSeek[0] = 0L;
            return mSeek[0];
        }
    }

    default void setSeek(long seek) {
        mSeek[0] = seek;
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