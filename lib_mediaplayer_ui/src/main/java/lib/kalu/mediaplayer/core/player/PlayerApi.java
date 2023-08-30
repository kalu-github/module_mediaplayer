package lib.kalu.mediaplayer.core.player;

import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApiSeek;
import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApi extends PlayerApiBuriedEvent, PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender {

    default boolean dispatchKeyEventPlayer(@NonNull KeyEvent event) {
        boolean isFloat = isFloat();
        boolean isFull = isFull();
        MPLogUtil.log("PlayerApi => dispatchKeyEventPlayer => action = " + event.getAction() + ", code = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount() + ", isFloat = " + isFloat + ", isFull = " + isFull);
        dispatchKeyEventComponents(event);
        // action_down => keycode_back
        if (isFloat && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            stopFloat();
            return true;
        }
        // action_down => keycode_back
        else if (isFull && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            stopFull();
            return true;
        }
//        // keycode_dpad_left
//        else if (isFull && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//            return true;
//        }
//        // keycode_dpad_right
//        else if (isFull && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            return true;
//        }
        // keycode_dpad_up
        else if (isFull && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            return true;
        }
        // keycode_dpad_down
        else if (isFull && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            return true;
        }
        // action_up => keycode_enter
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getRepeatCount() == 0) {
            toggle();
            return true;
        }
        // action_up => keycode_dpad_center
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER && event.getRepeatCount() == 0) {
            toggle();
            return true;
        }
        // action_down => keycode_dpad_right => seek_forward => start1
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getRepeatCount() == 0) {
//            MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => start1 => repeatCount = " + event.getRepeatCount());
            try {
                if (!isPrepared())
                    throw new Exception("isPrepared error: false");
                if (isLive())
                    throw new Exception("living error: true");
                callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_START);
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => start1 => " + e.getMessage());
            }
            return true;
        }
        // action_down => keycode_dpad_right => seek_forward => start2
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && event.getRepeatCount() >= 1) {
            try {
                if (!isPrepared())
                    throw new Exception("isPrepared error: false");
                if (isLive())
                    throw new Exception("living error: true");
                seekForward(KeyEvent.ACTION_DOWN);
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => start2 => " + e.getMessage());
            }
            return true;
        }
        // seekForward => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                if (!isPrepared())
                    throw new Exception("isPrepared error: false");
                if (isLive())
                    throw new Exception("living error: true");
                boolean checkSeekBar = checkSeekBar();
                if (!checkSeekBar)
                    throw new Exception("checkSeekBar error: false");
                seekForward(KeyEvent.ACTION_UP);
                callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
                if (isPlaying())
                    throw new Exception("playing waining: true");
                resume();
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => stop => " + e.getMessage());
            }
            return true;
        }
        // seekRewind => start1
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getRepeatCount() == 0) {
            try {
                if (!isPrepared())
                    throw new Exception("isPrepared error: false");
                if (isLive())
                    throw new Exception("living error: true");
                callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_START);
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekRewind => start1 => " + e.getMessage());
            }
            return true;
        }
        // seekRewind => start2
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && event.getRepeatCount() >= 1) {
            try {
                if (!isPrepared())
                    throw new Exception("isPrepared error: false");
                if (isLive())
                    throw new Exception("living error: true");
                seekRewind(KeyEvent.ACTION_DOWN);
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekRewind => start2 => " + e.getMessage());
            }
            return true;
        }
        // seekRewind => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                if (!isPrepared())
                    throw new Exception("isPrepared error: false");
                if (isLive())
                    throw new Exception("living error: true");
                boolean checkSeekBar = checkSeekBar();
                if (!checkSeekBar)
                    throw new Exception("checkSeekBar error: false");
                seekRewind(KeyEvent.ACTION_UP);
                callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_STOP);
                if (isPlaying())
                    throw new Exception("playing waining: true");
                resume();
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekRewind => stop => " + e.getMessage());
            }
            return true;
        }
        // android
        else {
            return false;
        }
    }

    default void checkOnWindowVisibilityChanged(int visibility) {
        try {
            String url = getUrl();
            if (null == url || url.length() == 0)
                throw new Exception("url error: " + url);
            boolean playing = isPlaying();
            boolean windowVisibilityChangedRelease = isWindowVisibilityChangedRelease();
            // show
            if (visibility == View.VISIBLE) {
                if (playing)
                    return;
                if (windowVisibilityChangedRelease) {
                    restart();
                } else {
                    resume(false);
                }
            }
            // hide
            else {
                if (windowVisibilityChangedRelease) {
                    release();
                } else {
                    pause(true);
                }
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => checkOnWindowVisibilityChanged => " + e.getMessage());
        }
    }

    default void checkOnDetachedFromWindow(@NonNull boolean releaseTag) {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            release(releaseTag, false);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => checkOnDetachedFromWindow => " + e.getMessage());
        }
    }

    default void checkOnAttachedToWindow() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url warning: " + url);
            boolean playing = isPlaying();
            MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => url = " + url + ", playing = " + playing + ", this = " + this);
            if (playing)
                throw new Exception("playing warning: true");
            restart();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => checkOnAttachedToWindow => " + e.getMessage());
        }
    }

    default void onSaveBundle() {
        try {
            String url = getUrl();
            if (null == url || url.length() == 0)
                throw new Exception("url warning: " + url);
            long position = getPosition();
            long duration = getDuration();
            saveBundle(getBaseContext(), url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => onSaveBundle => " + e.getMessage());
        }
    }

    /**
     * 快进
     *
     * @param action
     */
    default void seekForward(int action) {
        try {
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            boolean checkSeekBar = checkSeekBar();
            if (!checkSeekBar)
                throw new Exception("checkSeekBar error: false");
            int max = seekBar.getMax();
            int progress = seekBar.getProgress();
            if (max <= 0)
                throw new Exception("max error: " + max);
            // action_down
            if (action == KeyEvent.ACTION_DOWN) {
                if (progress >= max)
                    throw new Exception("error: not progress>=max");
                int next = progress + Math.abs(max) / 200;
                if (next > max) {
                    next = max;
                }
                callUpdateSeekProgress(next, max);
            }
            // action_up
            else if (action == KeyEvent.ACTION_UP) {
                if (progress >= max) {
                    progress = max;
                }
                seekTo(progress);
            }
            // error
            else {
                throw new Exception("error: not find");
            }

        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => seekForward => " + e.getMessage());
        }
    }


    /**
     * 快退
     *
     * @param action
     */
    default void seekRewind(int action) {
        try {
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            boolean checkSeekBar = checkSeekBar();
            if (!checkSeekBar)
                throw new Exception("checkSeekBar error: false");
            int max = seekBar.getMax();
            int progress = seekBar.getProgress();
            if (max <= 0)
                throw new Exception("error: max <= 0 || progress <= 0");
            // action_down
            if (action == KeyEvent.ACTION_DOWN) {
                if (progress <= 0)
                    throw new Exception("progress warning: " + progress);
                int next = progress - Math.abs(max) / 200;
                if (next < 0) {
                    next = 0;
                }
                callUpdateSeekProgress(next, max);
            }
            // action_up
            else if (action == KeyEvent.ACTION_UP) {
                if (progress < 0) {
                    progress = 0;
                }
                seekTo(progress);
            }
            // error
            else {
                throw new Exception("error: not find");
            }

        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => seekForward => " + e.getMessage());
        }
    }

    default boolean checkSeekBar() {
        try {
            ComponentApiSeek seekComponent = findSeekComponent();
            if (null == seekComponent)
                throw new Exception("seekComponent error: null");
            MPLogUtil.log("PlayerApi => checkSeekBar => seekComponent = " + seekComponent);
            return seekComponent.isComponentShowing();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => checkSeekBar => " + e.getMessage());
            return false;
        }
    }

    default SeekBar findSeekBar() {
        try {
            ComponentApiSeek seekComponent = findSeekComponent();
            if (null == seekComponent)
                throw new Exception("seekComponent error: null");
            SeekBar seekBar = seekComponent.findSeekBar();
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            return seekBar;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => findSeekBar => " + e.getMessage());
            return null;
        }
    }
}
