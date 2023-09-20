package lib.kalu.mediaplayer.core.player.audio;

import android.view.View;

import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.A;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.listener.OnPlayerAudioChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface AudioPlayerApiBase {

    OnPlayerAudioChangeListener[] mOnPlayerChangeListener = new OnPlayerAudioChangeListener[1];

    default boolean hasPlayerChangeListener() {
        return null != mOnPlayerChangeListener && null != mOnPlayerChangeListener[0];
    }

    default OnPlayerAudioChangeListener getPlayerChangeListener() {
        return mOnPlayerChangeListener[0];
    }

    default void cleanPlayerChangeListener() {
        mOnPlayerChangeListener[0] = null;
        try {
            ((View) this).setOnKeyListener(null);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiBase => cleanPlayerChangeListener => " + e.getMessage());
        }
    }

    default void setOnPlayerChangeListener(@NonNull OnPlayerAudioChangeListener l) {
        mOnPlayerChangeListener[0] = null;
        mOnPlayerChangeListener[0] = l;
    }

    default void callPlayerEvent(@PlayerType.StateType.Value int state) {
        // listener
        try {
            boolean hasListener = hasPlayerChangeListener();
            if (!hasListener)
                throw new Exception("not find PlayerChangeListener");
            OnPlayerAudioChangeListener listener = getPlayerChangeListener();
            if (null == listener)
                throw new Exception("listener error: null");
            listener.onChange(state);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiBase => callPlayerEvent => " + e.getMessage());
        }
    }

    default void setPlayWhenReady(@NonNull boolean playWhenReady) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.setPlayWhenReady(playWhenReady);
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiBase => setPlayWhenReady => " + e.getMessage());
        }
    }

    default boolean isPlayWhenReady() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            return audioKernel.isPlayWhenReady();
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiBase => isPlayWhenReady => " + e.getMessage());
            return false;
        }
    }

    AudioKernelApi getAudioKernel();

    void setAudioKernel(@NonNull AudioKernelApi kernel);

    void start(@NonNull String url);

    void start(@NonNull StartBuilder builder, @NonNull String playUrl);

    void start(@NonNull StartBuilder builder, @NonNull String playUrl, @NonNull boolean retryBuffering);
}
