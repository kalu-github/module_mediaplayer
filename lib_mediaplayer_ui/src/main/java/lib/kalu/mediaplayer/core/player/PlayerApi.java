package lib.kalu.mediaplayer.core.player;

import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.component.ComponentSeek;
import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApi extends PlayerApiBuriedEvent, PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender, PlayerApiExternalMusic {

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
        // action_down => keycode_dpad_right => seek_forward => start
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => start");
            try {
                if (isLive())
                    throw new Exception("living error: true");
                if (!isPlaying())
                    throw new Exception("isPlaying error: false");
                callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_START);
                seekForward(KeyEvent.ACTION_DOWN);
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => start => " + e.getMessage());
            }
            return true;
        }
        // seekForward => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => stop");
                if (isLive())
                    throw new Exception("living error: true");
                if (!isPlaying())
                    throw new Exception("isPlaying error: false");
                callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
                seekForward(KeyEvent.ACTION_UP);
                if (isPlaying())
                    throw new Exception("playing waining: true");
                resume();
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekForward => stop => " + e.getMessage());
            }
            return true;
        }
        // seekRewind => start
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekRewind => start");
                if (isLive())
                    throw new Exception("living error: true");
                if (!isPlaying())
                    throw new Exception("isPlaying error: false");
                callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_START);
                seekRewind(KeyEvent.ACTION_DOWN);
            } catch (Exception e) {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekRewind => start => " + e.getMessage());
            }
            return true;
        }
        // seekRewind => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                MPLogUtil.log("PlayerApi => dispatchKeyEventComponent22 => seekRewind => stop");
                if (isLive())
                    throw new Exception("living error: true");
                if (!isPlaying())
                    throw new Exception("isPlaying error: false");
                callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_STOP);
                seekForward(KeyEvent.ACTION_UP);
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
            if (null == url || url.length() <= 0)
                throw new Exception("url warning: " + url);
            long position = getPosition();
            long duration = getDuration();
            saveBundle(getBaseContext(), url, position, duration);
        } catch (Exception e) {
            MPLogUtil.log("PlayerApi => onSaveBundle => " + e.getMessage());
        }
    }

    default void seekForward(int action) {
        try {
            ComponentSeek seekComponent = findComponent(ComponentSeek.class);
            if (null == seekComponent)
                throw new Exception("seekComponent error: null");
            SeekBar seekBar = seekComponent.findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            if (seekBar.getVisibility() != View.VISIBLE)
                throw new Exception("visabliity error: show");
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
            MPLogUtil.log("ComponentSeek => seekForward => " + e.getMessage());
        }
    }


    default void seekRewind(int action) {
        try {
            ComponentSeek seekComponent = findComponent(ComponentSeek.class);
            if (null == seekComponent)
                throw new Exception("seekComponent error: null");
            SeekBar seekBar = seekComponent.findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            if (seekBar.getVisibility() != View.VISIBLE)
                throw new Exception("visibility error: show");
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
            MPLogUtil.log("ComponentSeek => seekForward => " + e.getMessage());
        }
    }
}
