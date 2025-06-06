package lib.kalu.mediaplayer.core.kernel.video;

import java.util.HashMap;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

interface VideoKernelApiStartArgs extends VideoKernelApiBase {

    /***************/

    default void clearArgs() {
        setDoWindowing(false);
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

    default long getPlayWhenReadySeekToPosition() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getPlayWhenReadySeekToPosition();
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiBase => getPlayWhenReadySeekToPosition => Exception " + e.getMessage());
            return 0L;
        }
    }

    /***************/

    HashMap<VideoKernelApiBase, StartArgs> mStartArgs = new HashMap<>();

    default void setStartArgs(StartArgs args) {
        mStartArgs.put(this, args);
    }

    default StartArgs getStartArgs() {
        try {
            StartArgs args = mStartArgs.get(this);
            if (null == args)
                throw new Exception("warning: args null");
            return args;
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiStartArgs => getStartArgs => Exception " + e.getMessage());
            return null;
        }
    }

    HashMap<VideoKernelApiBase, Boolean> mDoWindowing = new HashMap<>();

    default void setDoWindowing(boolean v) {
        mDoWindowing.put(this, v);
    }

    default boolean isDoWindowing() {
        try {
            Boolean aBoolean = mDoWindowing.get(this);
            if (null == aBoolean)
                throw new Exception("warning: aBoolean null");
            return aBoolean;
        } catch (Exception e) {
            LogUtil.log("VideoKernelApiStartArgs => isDoWindowing => Exception " + e.getMessage());
            return false;
        }
    }
}