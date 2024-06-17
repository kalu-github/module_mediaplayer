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
import lib.kalu.mediaplayer.widget.player.PlayerView;

public interface ComponentApiSeek extends ComponentApi {

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    void setUserTouch(boolean status);

    SeekBar findSeekBar();

    void initSeekBarChangeListener();

    void seekToStopTrackingTouch();

    void seekToPosition(int keyCode, int position);

    Handler[] mHandlerDelayedMsg = new Handler[]{null};

    default void startInitMsg(int repeatCount, int keyCode) {
        if (null != mHandlerDelayedMsg[0]) {
            mHandlerDelayedMsg[0].removeMessages(1000);
            mHandlerDelayedMsg[0] = null;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            SeekBar seekBar = findSeekBar();
            int duration = seekBar.getMax();
            if (duration > 0) {
                long maxDuration = getPlayerView().getMaxDuration();
                if (repeatCount == 0) {
                    Object tag = seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                    if (null == tag) {
                        int progress = seekBar.getProgress();
                        seekBar.setTag(R.id.module_mediaplayer_id_seek_position, progress);
                        getPlayerView().callEventListener(PlayerType.StateType.STATE_FAST_FORWARD_START);
                    } else {
                        int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                        if (range >= duration)
                            return;
                        // >=2H 2 * 60 * 60 * 1000
                        if (duration >= 7200000) {
                            range += 8000;
                        }
                        // >=1H 60*60*1000
                        else if (duration >= 3600000) {
                            range += 4000;
                        }
                        // >=30MIN 30*60*1000
                        else if (duration >= 1800000) {
                            range += 1000;
                        }
                        // 10MIN 10*60*1000
                        else if (duration >= 600000) {
                            range += 400;
                        }
                        // 时间太短了
                        else {
                            range += 100;
                        }
                        if (range >= duration) {
                            range = duration;
                        }
                        seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                        onUpdateProgress(true, maxDuration, range, duration);
                    }
                } else {
                    int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                    if (range >= duration) {
                        return;
                    }
                    // >=2H 2 * 60 * 60 * 1000
                    if (duration >= 7200000) {
                        range += 60000;
                    }
                    // >=1H 60*60*1000
                    else if (duration >= 3600000) {
                        range += 30000;
                    }
                    // >=30MIN 30*60*1000
                    else if (duration >= 1800000) {
                        range += 10000;
                    }
                    // 10MIN 10*60*1000
                    else if (duration >= 600000) {
                        range += 5000;
                    }
                    // 时间太短了
                    else {
                        range += 1000;
                    }
                    if (range >= duration) {
                        range = duration;
                    }
                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                    onUpdateProgress(true, maxDuration, range, duration);
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            SeekBar seekBar = findSeekBar();
            int duration = seekBar.getMax();
            if (duration > 0) {
                long maxDuration = getPlayerView().getMaxDuration();
                if (repeatCount == 0) {
                    Object tag = seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                    if (null == tag) {
                        int progress = seekBar.getProgress();
                        seekBar.setTag(R.id.module_mediaplayer_id_seek_position, progress);
                        getPlayerView().callEventListener(PlayerType.StateType.STATE_FAST_REWIND_START);
                    } else {
                        int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                        if (range <= 0)
                            return;
                        // >=2H 2 * 60 * 60 * 1000
                        if (duration >= 7200000) {
                            range -= 8000;
                        }
                        // >=1H 60*60*1000
                        else if (duration >= 3600000) {
                            range -= 4000;
                        }
                        // >=30MIN 30*60*1000
                        else if (duration >= 1800000) {
                            range -= 1000;
                        }
                        // 10MIN 10*60*1000
                        else if (duration >= 600000) {
                            range -= 400;
                        }
                        // 时间太短了
                        else {
                            range -= 100;
                        }
                        if (range <= 0) {
                            range = 0;
                        }
                        seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                        onUpdateProgress(true, maxDuration, range, duration);
                    }
                } else {
                    int range = (int) seekBar.getTag(R.id.module_mediaplayer_id_seek_position);
                    if (range <= 0)
                        return;
                    // >=2H 2 * 60 * 60 * 1000
                    if (duration >= 7200000) {
                        range -= 60000;
                    }
                    // >=1H 60*60*1000
                    else if (duration >= 3600000) {
                        range -= 30000;
                    }
                    // >=30MIN 30*60*1000
                    else if (duration >= 1800000) {
                        range -= 10000;
                    }
                    // 10MIN 10*60*1000
                    else if (duration >= 600000) {
                        range -= 5000;
                    }
                    // 时间太短了
                    else {
                        range -= 1000;
                    }
                    if (range <= 0) {
                        range = 0;
                    }
                    seekBar.setTag(R.id.module_mediaplayer_id_seek_position, range);
                    onUpdateProgress(true, maxDuration, range, duration);
                }
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