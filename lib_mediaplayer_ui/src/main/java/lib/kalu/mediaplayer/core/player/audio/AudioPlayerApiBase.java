package lib.kalu.mediaplayer.core.player.audio;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface AudioPlayerApiBase extends AudioPlayerApiListener {

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
