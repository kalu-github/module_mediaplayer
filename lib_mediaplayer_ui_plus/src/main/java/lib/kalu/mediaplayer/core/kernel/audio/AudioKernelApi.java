package lib.kalu.mediaplayer.core.kernel.audio;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.util.MPLogUtil;


/**
 * @description: 播放器 - 抽象接口
 * @date: 2021-05-12 09:40
 */
@Keep
public interface AudioKernelApi extends AudioKernelApiBase, AudioKernelApiEvent {

    void onUpdateTimeMillis();

    @NonNull
    <T extends Object> T getPlayer();

    void releaseDecoder(boolean isFromUser, boolean isMainThread);

    void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters);

    void startDecoder(@NonNull Context context, @NonNull String url, @NonNull boolean prepareAsync);

    default void initOptionsIjk() {
    }

    default void initOptionsExo(@NonNull Context context, @NonNull ExoPlayer.Builder exoBuilder) {
    }

    default void initDecoder(@NonNull Context context, @NonNull String playUrl, @NonNull StartBuilder bundle) {

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
        boolean playWhenReady = bundle.isPlayWhenReady();
        MPLogUtil.log("VideoKernelApi => initDecoder => playWhenReady = " + playWhenReady);
        setPlayWhenReady(playWhenReady);
        boolean prepareAsync = bundle.isPrepareAsync();
        MPLogUtil.log("VideoKernelApi => initDecoder => prepareAsync = " + prepareAsync);
        startDecoder(context, playUrl, prepareAsync);
    }

    default void update(@NonNull long seek, @NonNull long max, @NonNull boolean loop) {
        setSeek(seek);
        setMax(max);
        setLooping(loop);
    }
}