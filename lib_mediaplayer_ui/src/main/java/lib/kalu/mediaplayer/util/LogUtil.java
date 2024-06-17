package lib.kalu.mediaplayer.util;

import android.util.Log;


import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.args.PlayerArgs;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.vlc.util.VlcLogUtil;

public final class LogUtil {

    private static String mTag = "MP_COMMON";
    private static boolean mLog = true;

    public static void setLogger(boolean v) {
        mLog = v;
    }

    public static boolean isLog() {
        return mLog;
    }

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, @Nullable Throwable throwable) {

        if (!mLog)
            return;

        if (null == message || message.length() == 0)
            return;

        Log.e(mTag, message, throwable);
    }
}
