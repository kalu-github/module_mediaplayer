package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

public interface VideoKernelApi extends VideoKernelApiBase, VideoKernelApiEvent {

    void onUpdateProgress();

    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser);

    void createDecoder(Context context);

    void startDecoder(Context context, boolean prepareAsync, String url, Object... o);

    default void initOptions(Context context, Object... o) {
    }

    void setKernelApi(VideoKernelApiEvent eventApi);

    VideoKernelApiEvent getKernelApi();

    void setPlayerApi(VideoPlayerApi playerApi);

    VideoPlayerApi getPlayerApi();

    default void update(long seek, long max, boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    default void initDecoder(Context context, String playUrl, StartArgs args, Object... o) {

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
        LogUtil.log("VideoKernelApi => initDecoder => prepareAsync = " + prepareAsync + ", playWhenReady = " + playWhenReady + ", live = " + live + ", loop = " + loop + ", mute = " + mute + ", max = " + max + ", seek = " + seek + ", playUrl = " + playUrl);
        startDecoder(context, prepareAsync, playUrl, o);

//        String musicUrl = bundle.getExternalMusicUrl();
//        MPLogUtil.log("VideoKernelApi => update => musicUrl = " + musicUrl);
//        setExternalMusicPath(musicUrl);
//        boolean musicLoop = bundle.isExternalMusicLoop();
//        MPLogUtil.log("VideoKernelApi => update => musicLoop = " + musicLoop);
//        setExternalMusicLooping(musicLoop);
//        boolean musicPlayWhenReady = bundle.isExternalMusicPlayWhenReady();
//        MPLogUtil.log("VideoKernelApi => update => musicPlayWhenReady = " + musicPlayWhenReady);
//        setisExternalMusicPlayWhenReady(musicPlayWhenReady);
    }

    /***********/

//    default void setDisplay(SurfaceHolder surfaceHolder){
//    }

    void setSurface(Surface surface, int w, int h);

    /***********/

    android.os.Handler[] mHandlerOpenUrl = new android.os.Handler[1];

    default void loppingOpenUrlTimeout(boolean reset, long start, long timeout) {
        try {
            boolean prepared = isPrepared();
            if (prepared) {
                stopCheckOpenUrlTimeout();
            } else {
                long current = System.currentTimeMillis();
                long cast = current - start;
                if (cast >= timeout)
                    throw new Exception("warning: cast >= timeout");
                mHandlerOpenUrl[0].sendEmptyMessageDelayed(1234, 1000);
            }
        } catch (Exception e) {
            mHandlerOpenUrl[0].removeCallbacksAndMessages(null);
            mHandlerOpenUrl[0] = null;
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.ANDROID, PlayerType.EventType.EVENT_ERROR_NET);
            getPlayerApi().stop(false);
            if (reset) {
                getPlayerApi().release();
            }
        }
    }

    default void startCheckOpenUrlTimeout(boolean reset, long start, long timeout) {
        stopCheckOpenUrlTimeout();
        mHandlerOpenUrl[0] = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 1234) {
                    loppingOpenUrlTimeout(reset, start, timeout);
                }
            }
        };
        mHandlerOpenUrl[0].sendEmptyMessageDelayed(1234, 1000);
    }

    default void stopCheckOpenUrlTimeout() {
        if (null != mHandlerOpenUrl[0]) {
            mHandlerOpenUrl[0].removeCallbacksAndMessages(null);
            mHandlerOpenUrl[0] = null;
        }
    }

    /***********/

    android.os.Handler[] mHandlerLoadBuffering = new android.os.Handler[1];

    default void loppingLoadBufferingTimeout(boolean reset, boolean retry, long timeout) {
        try {
            if (!retry)
                throw new Exception("warning: retry false");
            long seek;
            try {
                if (isLive()) {   // 直播
                    seek = 0;
                } else {  // 点播
                    seek = getPosition();
                }
            } catch (Exception e) {
                seek = 0;
            }
            if (seek < 0) {
                seek = 0;
            }

            getPlayerApi().stop(false);
            if (reset) {
                getPlayerApi().release();
            }

            getPlayerApi().restart(seek, true, true);
            getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_START);
            mHandlerLoadBuffering[0].sendEmptyMessageDelayed(4321, timeout);
        } catch (Exception e) {
            mHandlerLoadBuffering[0].removeCallbacksAndMessages(null);
            mHandlerLoadBuffering[0] = null;
//            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_STOP);
//            callPlayerEvent(PlayerType.StateType.STATE_BUFFERING_TIMEOUT);
            getPlayerApi().stop(false);
            if (reset) {
                getPlayerApi().release();
            }
        }
    }

    default void startCheckLoadBufferingTimeout(boolean reset, boolean retry, long timeout) {
        stopCheckLoadBufferingTimeout();
        mHandlerLoadBuffering[0] = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 4321) {
                    loppingLoadBufferingTimeout(reset, retry, timeout);
                }
            }
        };
        mHandlerLoadBuffering[0].sendEmptyMessageDelayed(4321, timeout);
    }

    default void stopCheckLoadBufferingTimeout() {
        if (null != mHandlerLoadBuffering[0]) {
            mHandlerLoadBuffering[0].removeCallbacksAndMessages(null);
            mHandlerLoadBuffering[0] = null;
        }
    }

    /***********/
}