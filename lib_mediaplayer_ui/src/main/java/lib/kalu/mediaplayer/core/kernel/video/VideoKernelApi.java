package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.view.Surface;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

public interface VideoKernelApi extends VideoKernelApiCheck, VideoKernelApiBase, VideoKernelApiEvent {

    void onUpdateProgress();

    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser);

    void createDecoder(Context context);

    void startDecoder(Context context, StartArgs args);

    default void initOptions(Context context, StartArgs args) {
    }

    default void update(long seek, long max, boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    default void clear() {
        mDoWindowing[0] = false;
        mConnectTimeout[0] = 0L;
        mBufferingTimeoutRetry[0] = false;
        mSeek[0] = 0L;
        mMax[0] = 0L;
        mLooping[0] = false;
        mLive[0] = false;
        mMute[0] = false;
        mPrepareAsync[0] = true;
        mPlayWhenReady[0] = true;
        mPrepared[0] = false;
        mIjkMediaCodec[0] = true;
        stopCheckPreparedPlaying();
        stopCheckConnectTimeout();
        stopCheckBufferingTimeout();
    }

    default void initDecoder(Context context, StartArgs args) {

        clear();
        LogUtil.log("VideoKernelApi => initDecoder => " + args.toString());

        try {

            long connectTimout = args.getConnectTimout();
            setConnectTimeout(connectTimout);
            boolean bufferingTimeoutRetry = args.isBufferingTimeoutRetry();
            setBufferingTimeoutRetry(bufferingTimeoutRetry);

            long seek = args.getSeek();
            setSeek(seek);
            long max = args.getMax();
            setMax(max);
            boolean mute = args.isMute();
            setMute(mute);
            boolean loop = args.isLooping();
            setLooping(loop);
            boolean live = args.isLive();
            setLive(live);
            boolean playWhenReady = args.isPlayWhenReady();
            setPlayWhenReady(playWhenReady);
            boolean prepareAsync = args.isPrepareAsync();
            setPrepareAsync(prepareAsync);
        } catch (Exception e) {
        }


        initOptions(context, args);
        startDecoder(context, args);
    }

    /***********/

//    default void setDisplay(SurfaceHolder surfaceHolder){
//    }

    void setSurface(Surface surface, int w, int h);
}