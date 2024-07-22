package lib.kalu.mediaplayer.core.kernel.video;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
public interface VideoKernelApiHandler extends VideoKernelApiBase, VideoKernelApiEvent, VideoKernelApiStartArgs {

    default void clearHandler() {
        stopPlayWhenReadyDelayedTime();
        stopCheckBufferingTimeout();
        stopCheckPreparedPlaying();
        stopCheckConnectTimeout();
    }

    /***********/

    void startPlayWhenReadyDelayedTime(long delayedTime);

    void stopPlayWhenReadyDelayedTime();

    /***********/

    void startCheckPreparedPlaying();

    void stopCheckPreparedPlaying();

    /***********/

    void startCheckConnectTimeout(long timeout);

    void stopCheckConnectTimeout();

    /***********/

    void startCheckBufferingTimeout(boolean bufferingTimeoutRetry, long timeout);

    void stopCheckBufferingTimeout();
}