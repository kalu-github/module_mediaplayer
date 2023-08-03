package lib.kalu.mediaplayer.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.vlc.util.VlcLogUtil;

public final class MPLogUtil {

    private static String mTag = "MP_COMMON";
    private static boolean mLog = false;

    public static void setLogger(@NonNull PlayerBuilder config) {

        int videoKernel = config.getVideoKernel();
        boolean log = config.isLog();
        if (videoKernel == PlayerType.KernelType.VLC) {
            try {
                Class<?> clazz = Class.forName("lib.kalu.vlc.util.VlcLogUtil");
                if (null != clazz) {
                    VlcLogUtil.setLogger(log);
                    log("setLogger => vlc succ");
                } else {
                    log("setLogger => vlc fail");
                }
            } catch (Exception e) {
                log("setLogger => vlc exception");
            }
        } else if (videoKernel == PlayerType.KernelType.IJK) {
            try {
                Class<?> clazz = Class.forName("lib.kalu.ijkplayer.util.IjkLogUtil");
                if (null != clazz) {
                    lib.kalu.ijkplayer.util.IjkLogUtil.setLogger(log);
                    log("setLogger => ijk succ");
                } else {
                    log("setLogger => ijk fail");
                }
            } catch (Exception e) {
                log("setLogger => ijk exception");
            }
        } else if (videoKernel == PlayerType.KernelType.EXO_V1) {
            try {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer.util.ExoLogUtil");
                if (null != clazz) {
                    lib.kalu.exoplayer.util.ExoLogUtil.setLogger(log);
                    log("setLogger => exo succ");
                } else {
                    log("setLogger => exo fail");
                }
            } catch (Exception e) {
                log("setLogger => exo exception");
            }
        } else if (videoKernel == PlayerType.KernelType.EXO_V2) {
            try {
                Class<?> clazz = Class.forName("lib.kalu.exoplayer2.util.ExoLogUtil");
                if (null != clazz) {
                    lib.kalu.exoplayer2.util.ExoLogUtil.setLogger(log);
                    log("setLogger => exo succ");
                } else {
                    log("setLogger => exo fail");
                }
            } catch (Exception e) {
                log("setLogger => exo exception");
            }
        }

        MPLogUtil.mLog = log;
    }

    public static boolean isLog() {
        return mLog;
    }

    public static void log(@NonNull String message) {
        log(message, null);
    }

    public static void log(@NonNull String message, @Nullable Throwable throwable) {

        if (!mLog)
            return;

        if (null == message || message.length() == 0)
            return;

        Log.e(mTag, message, throwable);
    }
}
