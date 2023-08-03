package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface KernelApi extends KernelApiBase,
        KernelApiEvent {

    void onUpdateTimeMillis();

    void onUpdateBuffer(@PlayerType.StateType.Value int status);

    @NonNull
    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser, boolean isMainThread);

    void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters);

    void startDecoder(@NonNull Context context, @NonNull String url);

    default void setOptions() {
    }

    default void initDecoder(@NonNull Context context, @NonNull String playUrl, @NonNull StartBuilder bundle) {

        MPLogUtil.log("KernelApi => initDecoder => playUrl = " + playUrl);
        long seek = bundle.getSeek();
        MPLogUtil.log("KernelApi => initDecoder => seek = " + seek);
        setSeek(seek);
        long max = bundle.getMax();
        MPLogUtil.log("KernelApi => initDecoder => max = " + max);
        setMax(max);
        boolean mute = bundle.isMute();
        MPLogUtil.log("KernelApi => initDecoder => mute = " + mute);
        setMute(mute);
        boolean loop = bundle.isLoop();
        MPLogUtil.log("KernelApi => initDecoder => loop = " + loop);
        setLooping(loop);
        boolean live = bundle.isLive();
        MPLogUtil.log("KernelApi => initDecoder => live = " + live);
        setLive(live);
        boolean playWhenReady = bundle.isPlayWhenReady();
        MPLogUtil.log("KernelApi => initDecoder => playWhenReady = " + playWhenReady);
        setPlayWhenReady(playWhenReady);

        // 2
        startDecoder(context, playUrl);

//        String musicUrl = bundle.getExternalMusicUrl();
//        MPLogUtil.log("KernelApi => update => musicUrl = " + musicUrl);
//        setExternalMusicPath(musicUrl);
//        boolean musicLoop = bundle.isExternalMusicLoop();
//        MPLogUtil.log("KernelApi => update => musicLoop = " + musicLoop);
//        setExternalMusicLooping(musicLoop);
//        boolean musicPlayWhenReady = bundle.isExternalMusicPlayWhenReady();
//        MPLogUtil.log("KernelApi => update => musicPlayWhenReady = " + musicPlayWhenReady);
//        setisExternalMusicPlayWhenReady(musicPlayWhenReady);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }

    void setDisplay(SurfaceHolder surfaceHolder);

    void setSurface(@NonNull Surface surface, int w, int h);

    void setPlayWhenReady(boolean playWhenReady);

    boolean isPlayWhenReady();
}