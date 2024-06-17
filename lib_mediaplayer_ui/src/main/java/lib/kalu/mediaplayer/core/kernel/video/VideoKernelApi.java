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

public interface VideoKernelApi extends VideoKernelApiHandler, VideoKernelApiBase, VideoKernelApiEvent {

    void onUpdateProgress();

    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser);

    void createDecoder(Context context);

    void startDecoder(Context context, boolean prepareAsync, String url, Object... o);

    default void initOptions(Context context, Object... o) {
    }

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
}