package lib.kalu.mediaplayer.core.kernel.video;

import android.content.Context;

import lib.kalu.mediaplayer.core.kernel.audio.MusicPlayerManager;
import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface KernelApiExternalMusic extends KernelApiBase {

    boolean isExternalMusicPlayWhenReady();

    boolean isExternalMusicLooping();

    boolean isExternalMusicSeek();

    String getExternalMusicPath();

    default void stopExternalMusic(boolean release) {
        try {
            String musicPath = getExternalMusicPath();
            if (null == musicPath || musicPath.length() <= 0)
                throw new Exception("musicPath warning: " + musicPath);
            // 1 视频
            setMute(false);
            setVolume(1F, 1F);
            // 2 音频
            MusicPlayerManager.release(release);
        } catch (Exception e) {
            MPLogUtil.log("KernelApiExternalMusic => stopExternalMusic => " + e.getMessage());
        }
    }

    default void startExternalMusic(Context context) {

        try {
            String musicPath = getExternalMusicPath();
            if (null == musicPath || musicPath.length() <= 0)
                throw new Exception("musicPath warning: " + musicPath);
            // 1 视频
            setMute(true);
            setVolume(0F, 0F);
            pause();

            // 2 音频
            long position = 0;
            boolean musicSeek = isExternalMusicSeek();
            if (musicSeek) {
                position = getPosition() - getSeek();
                if (position <= 0) {
                    position = 0;
                }
            }
            // 3
            OnMusicPlayerChangeListener l = new OnMusicPlayerChangeListener() {
                @Override
                public void onStart() {
                    start();
                }

                @Override
                public void onEnd() {
                    setVolume(1F, 1F);
                }

                @Override
                public void onError() {
                    setVolume(1F, 1F);
                }
            };
            MusicPlayerManager.start(context, position, musicPath, l);
        } catch (Exception e) {
            MPLogUtil.log("KernelApiExternalMusic => startExternalMusic => " + e.getMessage());
        }
    }

    default void pauseExternalMusic() {
        MusicPlayerManager.pause();
    }

    default void resetExternalMusic() {
        MusicPlayerManager.reset();
    }

    default void resumeExternalMusic() {
        MusicPlayerManager.resume();
    }

    default boolean isExternalMusicPlaying() {
        return MusicPlayerManager.isPlaying();
    }

    default boolean isExternalMusicRelease() {
        return MusicPlayerManager.isRelease();
    }
}
