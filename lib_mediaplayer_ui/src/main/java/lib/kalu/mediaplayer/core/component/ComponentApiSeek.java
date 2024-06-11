package lib.kalu.mediaplayer.core.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface ComponentApiSeek extends ComponentApi {

    void setUserTouch(boolean status);

    SeekBar findSeekBar();

    void initSeekBarChangeListener();

    void seekToStopTrackingTouch();

    void seekToPosition(int keyCode, int position);

    Handler[] mHandlerDelayedMsg = new Handler[1];

    default void startInitMsg(int repeatCount, int keyCode) {
        if (null != mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0].removeMessages(1000);
            mHandlerDelayedMsg[0] = null;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            long max = getPlayerView().getMax();
            SeekBar seekBar = findSeekBar();
            int duration = seekBar.getMax();
            if (repeatCount == 0) {
                Object tag = seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                if (null == tag) {
                    int progress = seekBar.getProgress();
                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, progress);
                    getPlayerView().callEventListener(PlayerType.StateType.STATE_FAST_FORWARD_START);
                } else {
                    int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                    range += 1000;
                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                    if (range >= duration) {
                        range = duration;
                    }
                    onUpdateProgress(true, max, range, duration);
                }
            } else {
                int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                range += 10000;
                seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                onUpdateProgress(true, max, range, duration);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            long max = getPlayerView().getMax();
            SeekBar seekBar = findSeekBar();
            int duration = seekBar.getMax();
            if (repeatCount == 0) {
                Object tag = seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                if (null == tag) {
                    int progress = seekBar.getProgress();
                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, progress);
                    getPlayerView().callEventListener(PlayerType.StateType.STATE_FAST_REWIND_START);
                } else {
                    int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                    range -= 1000;
                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                    if (range <= 0) {
                        range = 0;
                    }
                    onUpdateProgress(true, max, range, duration);
                }
            } else {
                int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                range -= 10000;
                seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                onUpdateProgress(true, max, range, duration);
            }
        }
    }

    default void startDelayedMsg(int keyCode) {
        if (null == mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0] = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    SeekBar seekBar = findSeekBar();
                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, null);
                    int progress = seekBar.getProgress();
                    seekToPosition(keyCode, progress);
                }
            };
        }
        mHandlerDelayedMsg[0].sendEmptyMessageDelayed(1000, 1000);
    }
}