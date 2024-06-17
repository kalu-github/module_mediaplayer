package lib.kalu.mediaplayer.core.kernel.video;

import androidx.annotation.FloatRange;


import org.json.JSONArray;

import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

interface VideoKernelApiBase {

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

    long[] mConnectTimeout = new long[]{0L};

    default void setConnectTimeout(long v) {
        mConnectTimeout[0] = v;
    }

    default long getConnectTimeout() {
        return mConnectTimeout[0];
    }

    /*****/

    boolean[] mBufferingTimeoutRetry = new boolean[]{false};

    default void setBufferingTimeoutRetry(boolean v) {
        mBufferingTimeoutRetry[0] = v;
    }

    default boolean isBufferingTimeoutRetry() {
        return mBufferingTimeoutRetry[0];
    }

    /*****/

    boolean[] mIjkMediaCodec = new boolean[]{true};

    default void setIjkMediaCodec(boolean v) {
        mIjkMediaCodec[0] = v;
    }

    default boolean isIjkMediaCodec() {
        return mIjkMediaCodec[0];
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


    /************/

    long[] mMaxDuration = new long[]{0L}; // 试播时常

    default long getMaxDuration() {
        return mMaxDuration[0];
    }

    default void setMaxDuration(long v) {
        if (v < 0) {
            v = 0;
        }
        mMaxDuration[0] = v;
    }

    /*****/
    boolean[] mLooping = new boolean[]{false}; // 循环播放

    default void setLooping(boolean v) {
        mLooping[0] = v;
    }

    default boolean isLooping() {
        return mLooping[0];
    }

    /*****/

    boolean[] mLive = new boolean[]{false}; // 直播频道

    default boolean isLive() {
        return mLive[0];
    }

    default void setLive(boolean v) {
        mLive[0] = v;
    }

    /*****/

    boolean[] mMute = new boolean[]{false}; // 是否静音

    default boolean isMute() {
        return mMute[0];
    }

    default void setMute(boolean v) {
        mMute[0] = v;
        setVolume(v ? 0f : 1f, v ? 0f : 1f);
    }

    /*****/

    boolean[] mPrepareAsync = new boolean[]{true};

    default void setPrepareAsync(boolean v) {
        mPrepareAsync[0] = v;
    }

    default boolean isPrepareAsync() {
        return mPrepareAsync[0];
    }

    /*****/
    boolean[] mPlayWhenReady = new boolean[]{true}; // 是否加载完成 立即开播

    default void setPlayWhenReady(boolean v) {
        mPlayWhenReady[0] = v;
    }

    default boolean isPlayWhenReady() {
        return mPlayWhenReady[0];
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

    void setKernelApi(VideoKernelApiEvent eventApi);

    VideoKernelApiEvent getKernelApi();

    void setPlayerApi(VideoPlayerApi playerApi);

    VideoPlayerApi getPlayerApi();

    /*****/

    void setVolume(float left, float right);

    void seekTo(long position);

    boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed);

    @FloatRange(from = 1F, to = 4F)
    float getSpeed();

    void start();

    void pause();

    void stop();

    void release();

    boolean isPlaying();

    long getPosition();

    long getDuration();

    default JSONArray getTrackInfo() {
        return null;
    }

    default boolean switchTrack(int trackId) {
        return false;
    }
}