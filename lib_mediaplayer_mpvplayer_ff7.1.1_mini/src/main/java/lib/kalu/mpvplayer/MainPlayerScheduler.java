package lib.kalu.mpvplayer;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

final class MainPlayerScheduler implements PlayerScheduler {
    private final Handler handler = new Handler(Looper.getMainLooper());
    public long now() { return SystemClock.elapsedRealtime(); }
    public void post(Runnable action,long delayMs) { handler.postDelayed(action,delayMs); }
    public void cancelAll() { handler.removeCallbacksAndMessages(null); }
}
