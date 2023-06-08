package lib.kalu.mediaplayer.core.player;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiExternalMusic extends PlayerApiBase {

    default boolean isExternalMusicPlayWhenReady() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_play_when_ready);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => isExternalMusicPlayWhenReady => " + e.getMessage());
            return false;
        }
    }

    default void setExternalMusicPlayWhenReady(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_play_when_ready, v);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicPlayWhenReady => " + e.getMessage());
        }
    }

    default void setExternalMusicLooping(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_looping, v);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicLooping => " + e.getMessage());
        }
    }

    default boolean isExternalMusicLooping() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_looping);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => isExternalMusicLooping => " + e.getMessage());
            return false;
        }
    }

    default boolean isExternalMusicSeek() {
        try {
            return (boolean) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_seek);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => isExternalMusicSeek => " + e.getMessage());
            return true;
        }
    }

    default void setExternalMusicSeek(boolean v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_seek, v);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicSeek => " + e.getMessage());
        }
    }

    default String getExternalMusicPath() {
        try {
            return (String) ((View) this).getTag(R.id.module_mediaplayer_id_player_external_music_url);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => getExternalMusicPath => " + e.getMessage());
            return null;
        }
    }

    default void setExternalMusicUrl(String v) {
        try {
            ((View) this).setTag(R.id.module_mediaplayer_id_player_external_music_url, v);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => setExternalMusicUrl => " + e.getMessage());
        }
    }


    default void updateExternalMusicData(@NonNull StartBuilder data) {
        try {
            if (null == data)
                throw new Exception("data error: null");
            boolean externalEnable = data.isExternalEnable();
            if (!externalEnable)
                throw new Exception("externalEnable warning: false");
            String musicUrl = data.getExternalMusicUrl();
            if (null == musicUrl || musicUrl.length() <= 0)
                throw new Exception("musicUrl error: " + musicUrl);
            setExternalMusicUrl(musicUrl);
            boolean musicLoop = data.isExternalMusicLoop();
            setExternalMusicLooping(musicLoop);
            boolean musicPlayWhenReady = data.isExternalMusicPlayWhenReady();
            setExternalMusicPlayWhenReady(musicPlayWhenReady);
            boolean musicSeek = data.isExternalMusicSeek();
            setExternalMusicSeek(musicSeek);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => updateExternalMusicData => " + e.getMessage());
        }
    }

    default void stopExternalMusic(boolean release) {
        try {
            KernelApi kernel = getKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_external_enable);
            if (null == tag)
                throw new Exception("tag error: null");
            if (!(tag instanceof Boolean))
                throw new Exception("tag error: " + tag);
            if (!((boolean) tag))
                throw new Exception("externalEnable warning: " + tag);
            setExternalMusicPlayWhenReady(false);
            kernel.stopExternalMusic(release);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => stopExternalMusic => " + e.getMessage());
        }
    }

    default void startExternalMusic(@NonNull Context context) {
        startExternalMusic(context, null);
    }

    default void startExternalMusic(@NonNull Context context, @Nullable StartBuilder data) {
        try {
            if (null == data)
                throw new Exception("data error: null");
            KernelApi kernel = getKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_external_enable);
            if (null == tag)
                throw new Exception("tag error: null");
            if (!(tag instanceof Boolean))
                throw new Exception("tag error: " + tag);
            if (!((boolean) tag))
                throw new Exception("externalEnable warning: " + tag);
            updateExternalMusicData(data);
            setExternalMusicPlayWhenReady(true);
            kernel.startExternalMusic(context);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => startExternalMusic => " + e.getMessage());
        }
    }

    default void pauseExternalMusic() {
        try {
            KernelApi kernel = getKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_external_enable);
            if (null == tag)
                throw new Exception("tag error: null");
            if (!(tag instanceof Boolean))
                throw new Exception("tag error: " + tag);
            if (!((boolean) tag))
                throw new Exception("externalEnable warning: " + tag);
            kernel.pauseExternalMusic();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => pauseExternalMusic => " + e.getMessage());
        }
    }

    default void resetExternalMusic() {
        try {
            KernelApi kernel = getKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_external_enable);
            if (null == tag)
                throw new Exception("tag error: null");
            if (!(tag instanceof Boolean))
                throw new Exception("tag error: " + tag);
            if (!((boolean) tag))
                throw new Exception("externalEnable warning: " + tag);
            kernel.resetExternalMusic();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => resetExternalMusic => " + e.getMessage());
        }
    }

    default void resumeExternalMusic() {
        try {
            KernelApi kernel = getKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_external_enable);
            if (null == tag)
                throw new Exception("tag error: null");
            if (!(tag instanceof Boolean))
                throw new Exception("tag error: " + tag);
            if (!((boolean) tag))
                throw new Exception("externalEnable warning: " + tag);
            kernel.resumeExternalMusic();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => resumeExternalMusic => " + e.getMessage());
        }
    }

    default boolean isExternalMusicPlaying() {
        try {
            KernelApi kernel = getKernel();
            if (null == kernel)
                throw new Exception("kernel error: null");
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_external_enable);
            if (null == tag)
                throw new Exception("tag error: null");
            if (!(tag instanceof Boolean))
                throw new Exception("tag error: " + tag);
            if (!((boolean) tag))
                throw new Exception("externalEnable warning: " + tag);
            return kernel.isExternalMusicPlaying();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => isExternalMusicPlaying => " + e.getMessage());
            return false;
        }
    }

    default void checkExternalMusic(@NonNull Context context) {

        try {
            Object tag = ((View) this).getTag(R.id.module_mediaplayer_id_player_external_enable);
            if (null == tag)
                throw new Exception("tag error: null");
            if (!(tag instanceof Boolean))
                throw new Exception("tag error: " + tag);
            if (!((boolean) tag))
                throw new Exception("externalEnable warning: " + tag);

            // reset music player
            pauseExternalMusic();
            resetExternalMusic();

            String musicPath = getExternalMusicPath();
            if (null == musicPath || musicPath.length() <= 0)
                throw new Exception("musicPath error: " + musicPath);

            boolean playWhenReady = isExternalMusicPlayWhenReady();
            if (playWhenReady) {
                boolean musicLooping = isExternalMusicLooping();
                if (musicLooping) {
                    startExternalMusic(context, null);
                } else {
                    stopExternalMusic(false);
                }
            } else {
                stopExternalMusic(false);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiExternalMusic => checkExternalMusic => " + e.getMessage());
        }
    }
}
