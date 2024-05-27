package lib.kalu.mediaplayer.core.player.video;

import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;


import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.component.ComponentApiSeek;
import lib.kalu.mediaplayer.util.LogUtil;

public interface VideoPlayerApi extends VideoPlayerApiBuriedEvent, VideoPlayerApiBase, VideoPlayerApiKernel, VideoPlayerApiDevice, VideoPlayerApiComponent, VideoPlayerApiCache, VideoPlayerApiRender {

    default boolean dispatchKeyEventPlayer(KeyEvent event) {
        boolean isFloat = isFloat();
        boolean isFull = isFull();
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
        // seekForward => start
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int repeatCount = event.getRepeatCount();
            seekForward(KeyEvent.ACTION_DOWN, repeatCount);
            return true;
        }
        // seekForward => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int repeatCount = event.getRepeatCount();
            seekForward(KeyEvent.ACTION_UP, repeatCount);
            return true;
        }
        // seekRewind => start
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            int repeatCount = event.getRepeatCount();
            seekRewind(KeyEvent.ACTION_DOWN, repeatCount);
            return true;
        }
        // seekRewind => stop
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            int repeatCount = event.getRepeatCount();
            seekRewind(KeyEvent.ACTION_UP, repeatCount);
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
            LogUtil.log("VideoPlayerApi => checkOnWindowVisibilityChanged => " + e.getMessage());
        }
    }

    default void checkOnDetachedFromWindow(boolean releaseTag) {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url error: " + url);
            release(releaseTag, false);
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => checkOnDetachedFromWindow => " + e.getMessage());
        }
    }

    default void checkOnAttachedToWindow() {
        try {
            String url = getUrl();
            if (null == url || url.length() <= 0)
                throw new Exception("url warning: " + url);
            boolean playing = isPlaying();
            LogUtil.log("VideoPlayerApi => checkOnAttachedToWindow => url = " + url + ", playing = " + playing + ", this = " + this);
            if (playing)
                throw new Exception("playing warning: true");
            restart();
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => checkOnAttachedToWindow => " + e.getMessage());
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
            LogUtil.log("VideoPlayerApi => onSaveBundle => " + e.getMessage());
        }
    }

    /**
     * 快进
     */
    default void seekForward(int action, int repeatCount) {
        try {
            boolean live = isLive();
            if (live)
                throw new Exception("warning: live true");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            if (action == KeyEvent.ACTION_DOWN && repeatCount == 1) {
                callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_START);
            } else if (action == KeyEvent.ACTION_DOWN && repeatCount > 1) {
                boolean seekBarShowing = isSeekBarShowing();
                if (!seekBarShowing)
                    throw new Exception("warning: seekBarShowing false");
                int duration = seekBar.getMax();
                int progress = seekBar.getProgress();
                if (duration <= 0)
                    throw new Exception("duration error: " + duration);
                if (progress >= duration)
                    throw new Exception("error: not progress>=max");
                int range = (progress / 100);
                if (range < 1000) {
                    range = 1000;
                }
                progress += range;
                if (progress > duration) {
                    progress = duration;
                }
                long max = getMax();
                callUpdateProgress(max, progress, duration);
            } else if (action == KeyEvent.ACTION_DOWN) {
                throw new Exception("warning: repeatCount <1");
            } else if (action == KeyEvent.ACTION_UP) {
                boolean seekBarShowing = isSeekBarShowing();
                if (!seekBarShowing)
                    throw new Exception("warning: seekBarShowing false");
                int duration = seekBar.getMax();
                int progress = seekBar.getProgress();
                if (duration <= 0)
                    throw new Exception("duration error: " + duration);
                if (progress >= duration) {
                    progress = duration;
                }
                seekTo(progress);
                callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
                boolean playing = isPlaying();
                if (!playing) {
                    resume();
                }
            } else {
                throw new Exception("error: not find");
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => seekForward => " + e.getMessage());
        }
    }

    /**
     * 快退
     */
    default void seekRewind(int action, int repeatCount) {
        try {
            boolean live = isLive();
            if (live)
                throw new Exception("warning: live true");
            boolean prepared = isPrepared();
            if (!prepared)
                throw new Exception("warning: prepared false");
            SeekBar seekBar = findSeekBar();
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            if (action == KeyEvent.ACTION_DOWN && repeatCount == 1) {
                callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_START);
            } else if (action == KeyEvent.ACTION_DOWN && repeatCount > 1) {
                int duration = seekBar.getMax();
                int progress = seekBar.getProgress();
                if (duration <= 0)
                    throw new Exception("error: max <= 0 || progress <= 0");
                if (progress <= 0)
                    throw new Exception("progress warning: " + progress);
                int range = (progress / 100);
                if (range < 1000) {
                    range = 1000;
                }
                progress -= range;
                if (progress < 0) {
                    progress = 0;
                }
                long max = getMax();
                callUpdateProgress(max, progress, duration);
            } else if (action == KeyEvent.ACTION_DOWN) {
                throw new Exception("warning: repeatCount <1");
            } else if (action == KeyEvent.ACTION_UP) {
                boolean seekBarShowing = isSeekBarShowing();
                if (!seekBarShowing)
                    throw new Exception("warning: seekBarShowing false");
                int progress = seekBar.getProgress();
                if (progress < 0) {
                    progress = 0;
                }
                seekTo(progress);
                callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_STOP);
                boolean playing = isPlaying();
                if (!playing) {
                    resume();
                }
            } else {
                throw new Exception("error: not find");
            }
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => seekRewind => " + e.getMessage());
        }
    }

    default boolean isSeekBarShowing() {
        try {
            ComponentApiSeek seekComponent = findSeekComponent();
            if (null == seekComponent)
                throw new Exception("seekComponent error: null");
            boolean componentShowing = seekComponent.isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => isSeekBarShowing => " + e.getMessage());
            return false;
        }
    }

    default SeekBar findSeekBar() {
        try {
            ComponentApiSeek seekComponent = findSeekComponent();
            if (null == seekComponent)
                throw new Exception("warning: seekComponent null");
            SeekBar seekBar = seekComponent.findSeekBar();
            if (null == seekBar)
                throw new Exception("error: seekBar null");
            return seekBar;
        } catch (Exception e) {
            LogUtil.log("VideoPlayerApi => findSeekBar => " + e.getMessage());
            return null;
        }
    }
}
