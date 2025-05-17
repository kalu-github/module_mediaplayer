package lib.kalu.exoplayer2.util;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary;

@Keep
public final class ExoLogUtil {

    private static String mTag = "MP_EXO";
    private static boolean mLog = false;

    public static void setLogger(boolean enable) {
        mLog = enable;
        FfmpegLibrary.ffmpegLogger(mLog);
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
