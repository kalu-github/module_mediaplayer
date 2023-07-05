package lib.kalu.mediaplayer.core.player;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.util.MPLogUtil;

public interface PlayerApi extends PlayerApiBuriedEvent, PlayerApiBase, PlayerApiKernel, PlayerApiDevice, PlayerApiComponent, PlayerApiCache, PlayerApiRender, PlayerApiExternalMusic {

    default boolean dispatchKeyEventPlayer(@NonNull KeyEvent event) {
        MPLogUtil.log("PlayerApi => dispatchKeyEventPlayer => action = " + event.getAction() + ", code = " + event.getKeyCode());
        dispatchKeyEventComponentAll(event);
        // float
        if (isFloat()) {
            // keycode_back => stopFloat
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK && isFloat()) {
                stopFloat();
                return true;
            } else {
                return false;
            }
        }
        // full
        else if (isFull()) {
            // keycode_back => stopfull
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                stopFull();
                return true;
            }
            // keycode_dpad_center
            else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    toggle();
                }
                return true;
            }
            // keycode_dpad_left
            else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                return true;
            }
            // keycode_dpad_right
            else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return true;
            }
            // keycode_dpad_up
            else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                return true;
            }
            // keycode_dpad_down
            else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                return true;
            } else {
                return false;
            }
        } else {
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
}
