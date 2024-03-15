package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;




import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */

public interface VideoKernelApi extends VideoKernelApiBase, VideoKernelApiEvent {

    void onUpdateTimeMillis();

    
    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser, boolean isMainThread);

    void createDecoder( Context context,  boolean logger,  int seekParameters);

    void startDecoder( Context context,  String url,  boolean prepareAsync);

    default void initOptionsIjk() {
    }

    default void initOptionsExo( Context context,  com.google.android.exoplayer2.ExoPlayer.Builder exoBuilder) {
    }

    default void initOptionsMediax( Context context,  androidx.media3.exoplayer.ExoPlayer.Builder exoBuilder) {
    }

    default void initDecoder( Context context,  String playUrl,  StartBuilder bundle) {

        MPLogUtil.log("VideoKernelApi => initDecoder => playUrl = " + playUrl);
        long seek = bundle.getSeek();
        MPLogUtil.log("VideoKernelApi => initDecoder => seek = " + seek);
        setSeek(seek);
        long max = bundle.getMax();
        MPLogUtil.log("VideoKernelApi => initDecoder => max = " + max);
        setMax(max);
        boolean mute = bundle.isMute();
        MPLogUtil.log("VideoKernelApi => initDecoder => mute = " + mute);
        setMute(mute);
        boolean loop = bundle.isLooping();
        MPLogUtil.log("VideoKernelApi => initDecoder => loop = " + loop);
        setLooping(loop);
        boolean live = bundle.isLive();
        MPLogUtil.log("VideoKernelApi => initDecoder => live = " + live);
        setLive(live);
        boolean playWhenReady = bundle.isPlayWhenReady();
        MPLogUtil.log("VideoKernelApi => initDecoder => playWhenReady = " + playWhenReady);
        setPlayWhenReady(playWhenReady);
        boolean prepareAsync = bundle.isPrepareAsync();
        MPLogUtil.log("VideoKernelApi => initDecoder => prepareAsync = " + prepareAsync);
        startDecoder(context, playUrl, prepareAsync);

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

    default void update( long seek,  long max,  boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    void setDisplay(SurfaceHolder surfaceHolder);

    void setSurface( Surface surface, int w, int h);
}