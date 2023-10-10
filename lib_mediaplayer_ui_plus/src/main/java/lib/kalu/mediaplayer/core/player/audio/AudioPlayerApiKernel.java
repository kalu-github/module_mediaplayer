package lib.kalu.mediaplayer.core.player.audio;

import androidx.annotation.NonNull;

import java.io.File;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApi;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelFactoryManager;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

interface AudioPlayerApiKernel extends AudioPlayerApiBase {

    default void createAudioKernel(@NonNull StartBuilder builder, @NonNull PlayerBuilder playerBuilder) {
        try {
            releaseAudio();
            if (null == builder)
                throw new Exception("builder error: null");
            String audioPath = builder.getExternalAudioPath();
            if (null == audioPath || audioPath.length() == 0)
                throw new Exception("audioPath warning: " + audioPath);
            File audioFile = new File(audioPath);
            if (!audioFile.exists() || audioFile.isDirectory())
                throw new Exception("audioFile warning: not exists");
            int audioKernel = PlayerManager.getInstance().getConfig().getExtrAudioKernel();
            AudioKernelApi audioKernelApi = AudioKernelFactoryManager.getKernel((AudioPlayerApi) this, audioKernel, new AudioKernelApiEvent() {
            });
            setAudioKernel(audioKernelApi);
            createAudioDecoder(playerBuilder);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => createAudioKernel => " + e.getMessage());
        }
    }

    default void initAudioKernel(@NonNull StartBuilder builder) {
        try {
            if (null == builder)
                throw new Exception("builder error: null");
            String audioPath = builder.getExternalAudioPath();
            if (null == audioPath || audioPath.length() == 0)
                throw new Exception("audioPath warning: " + audioPath);
            File audioFile = new File(audioPath);
            if (!audioFile.exists() || audioFile.isDirectory())
                throw new Exception("audioFile warning: not exists");
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.initDecoder(getBaseContextAudio(), audioPath, builder);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => initAudioKernel => " + e.getMessage());
        }
    }

    default void createAudioDecoder(@NonNull PlayerBuilder builder) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            boolean log = builder.isLog();
            int seekParameters = builder.getExoSeekParameters();
            audioKernel.createDecoder(getBaseContextAudio(), log, seekParameters);
        } catch (Exception e) {
            MPLogUtil.log("VideoPlayerApiKernel => createAudioDecoder => " + e.getMessage());
        }
    }

    default boolean setMuteAudio(@NonNull boolean enable) {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.setMute(enable);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => setMuteAudio => " + e.getMessage());
            return false;
        }
    }

    default boolean releaseAudio() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.release(false);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => releaseAudio => " + e.getMessage());
            return false;
        }
    }

    default boolean startAudio() {
        try {
            VideoKernelApi videoKernel = ((PlayerView) this).getVideoKernel();
            if (null == videoKernel)
                throw new Exception("videoKernel error: null");
            long position = videoKernel.getPosition();
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            long duration = audioKernel.getDuration();
            if (position > duration) {
                position = duration;
            }
            audioKernel.seekTo(position);
            audioKernel.start();
            return true;
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => startAudio => " + e.getMessage());
            return false;
        }
    }

    default boolean stopAudio() {
        try {
            AudioKernelApi audioKernel = getAudioKernel();
            if (null == audioKernel)
                throw new Exception("audioKernel error: null");
            audioKernel.stop(false);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("AudioPlayerApiKernel => startAudio => " + e.getMessage());
            return false;
        }
    }
}
