package lib.kalu.mediaplayer.core.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface ComponentApiSeek extends ComponentApi {

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    void setUserTouch(boolean status);

    <T extends android.widget.SeekBar> T findSeekBar();

    int getSeekBarMax();

    int getSeekBarProgress();

    void initSeekBarChangeListener();

    void seekToStopTrackingTouch();

    void seekToPosition(int keyCode, int position);

    Handler[] mHandlerDelayedMsg = new Handler[]{null};

    default void startInitMsg(int repeatCount, int keyCode) {
        if (null != mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0].removeMessages(1000);
            mHandlerDelayedMsg[0] = null;
        }

        try {
            long duration = getDuration();
            if (duration <= 0)
                throw new Exception("warning: duration <=0");
            boolean componentShowing = isComponentShowing();
            // gone
            if (!componentShowing) {
                superCallEvent(false, true, keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? PlayerType.EventType.SEEK_START_FORWARD : PlayerType.EventType.SEEK_START_REWIND);
            }
            // click
            else if (repeatCount == 0) {
                int progress = getSeekBarProgress();
                if (progress >= duration)
                    throw new Exception("warning: range >= duration");
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 8000 : -8000);
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 4000 : -4000);
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1000 : -1000);
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 400 : -400);
                }
                // 时间太短了
                else {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 100 : -100);
                }
                if (progress >= duration) {
                    progress = (int) duration;
                }

                long trySeeDuration = getTrySeeDuration();
                onUpdateProgress(true, trySeeDuration, progress, duration);
            }
            // long click
            else {
                int progress = getSeekBarProgress();
                if (progress >= duration)
                    throw new Exception("warning: progress > duration");
                // >=2H 2 * 60 * 60 * 1000
                if (duration >= 7200000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 60000 : -60000);
                }
                // >=1H 60*60*1000
                else if (duration >= 3600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 30000 : -30000);
                }
                // >=30MIN 30*60*1000
                else if (duration >= 1800000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 10000 : -10000);
                }
                // 10MIN 10*60*1000
                else if (duration >= 600000) {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 5000 : -5000);
                }
                // 时间太短了
                else {
                    progress += (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1000 : -1000);
                }
                if (progress >= duration) {
                    progress = (int) duration;
                }
                long trySeeDuration = getTrySeeDuration();
                onUpdateProgress(true, trySeeDuration, progress, duration);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentApiSeek => startInitMsg => Exception " + e.getMessage());
        }
    }

    default void startDelayedMsg(int keyCode) {
        if (null == mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0] = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    View seekBar = findSeekBar();
//                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, null);
                    int progress = getSeekBarProgress();
                    seekToPosition(keyCode, progress);
                }
            };
        }
        mHandlerDelayedMsg[0].sendEmptyMessageDelayed(1000, 1000);
    }
}