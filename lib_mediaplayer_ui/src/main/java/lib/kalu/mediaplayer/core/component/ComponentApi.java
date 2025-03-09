package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public interface ComponentApi {

    @LayoutRes
    int initLayoutId();

    @IdRes
    int initViewIdRoot();

    @IdRes
    default int initViewIdBackground() {
        return -1;
    }

    @IdRes
    default int initViewIdImage() {
        return -1;
    }

    @IdRes
    default int initViewIdText() {
        return -1;
    }

//    default boolean hideDispatchKeyEvent() {
//        return false;
//    }
//
//    default boolean showDispatchKeyEvent() {
//        return false;
//    }

    default void inflate() {
        try {
            ViewGroup viewGroup = (ViewGroup) this;
            Context context = viewGroup.getContext();
            LayoutInflater.from(context).inflate(initLayoutId(), viewGroup, true);
        } catch (Exception e) {
        }
    }

    default void setComponentVisibility(@IdRes int id, int visibility) {
        try {
            View viewById = ((View) this).findViewById(id);
            if (null == viewById)
                throw new Exception("error: viewById null");
            viewById.setVisibility(visibility);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentVisibility => " + e.getMessage());
        }
    }

    default boolean isComponentShowing() {
        try {
            int rootId = initViewIdRoot();
            View viewById = ((View) this).findViewById(rootId);
            if (null == viewById)
                throw new Exception("error: viewById null");
            return viewById.getVisibility() == View.VISIBLE;
        } catch (Exception e) {
            LogUtil.log("ComponentApi => isComponentShowing => " + e.getMessage());
            return false;
        }
    }

    default boolean checkComponentShowingDispatchKeyEvent() {
        return true;
    }

    /******************/

    default void callEvent(int state) {
    }

    default void callWindow(int state) {
    }

    /******************/

    default void setComponentBackgroundColorInt(@ColorInt int v) {
        try {
            int layoutId = initViewIdBackground();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            View view = ((View) this).findViewById(layoutId);
            view.setBackgroundColor(v);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentBackgroundColorInt => " + e.getMessage());
        }
    }

    default void setComponentBackgroundColorRes(@ColorRes int v) {
        try {
            int layoutId = initViewIdBackground();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            View view = ((View) this).findViewById(layoutId);
            int color = ((View) this).getResources().getColor(v);
            view.setBackgroundColor(color);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentBackgroundColorRes => " + e.getMessage());
        }
    }

    default void setComponentBackgroundDrawableRes(@DrawableRes int v) {
        try {
            int layoutId = initViewIdBackground();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            View view = ((View) this).findViewById(layoutId);
            view.setBackgroundResource(v);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentBackgroundDrawableRes => " + e.getMessage());
        }
    }

    /******************/

    default void setComponentImageDrawableRes(@DrawableRes int v) {
        try {
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageResource(v);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentImageDrawableRes => " + e.getMessage());
        }
    }

    default void setComponentImageDrawable(Drawable drawable) {
        try {
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageDrawable(drawable);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentImageDrawable => " + e.getMessage());
        }
    }

    default void setComponentImageBitmap(Bitmap bitmap) {
        try {
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentImageBitmap => " + e.getMessage());
        }
    }

    default void setComponentImageUrl(@NonNull String imgUrl) {
        try {
            if (null == imgUrl || imgUrl.length() <= 0)
                throw new Exception("error: imgUrl null");
            int layoutId = initViewIdImage();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            ImageView imageView = ((View) this).findViewById(layoutId);
            imageView.setImageURI(Uri.parse(imgUrl));
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentImageUrl => " + e.getMessage());
        }
    }

    /******************/

    default void setComponentText(String v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setText(v);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentText => " + e.getMessage());
        }
    }

    default void setComponentText(@StringRes int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setText(v);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentText => " + e.getMessage());
        }
    }

    default void setComponentTextSize(@DimenRes int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            int offset = ((View) this).getResources().getDimensionPixelOffset(v);
            textView.setTextSize(offset);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentTextSize => " + e.getMessage());
        }
    }

    default void setComponentTextSize(float v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setTextSize(v);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentTextSize => " + e.getMessage());
        }
    }

    default void setComponentTextColorInt(@ColorInt int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            textView.setTextColor(v);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentTextColorInt => " + e.getMessage());
        }
    }

    default void setComponentTextColorRes(@ColorRes int v) {
        try {
            int layoutId = initViewIdText();
            if (layoutId == -1)
                throw new Exception("error: layoutId = -1");
            TextView textView = ((View) this).findViewById(layoutId);
            int color = ((View) this).getResources().getColor(v);
            textView.setTextColor(color);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setComponentTextColorRes => " + e.getMessage());
        }
    }

    default void show() {
        try {
            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                throw new Exception("warning: componentShowing true");
            int rootId = initViewIdRoot();
            setComponentVisibility(rootId, View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => show => Exception " + e.getMessage());
        }
    }

    default void hide() {
        try {
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            int rootId = initViewIdRoot();
            setComponentVisibility(rootId, View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => hide => Exception " + e.getMessage());
        }
    }

    /******************/

    default void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {
    }

    /*******************/

    default PlayerView getPlayerView() {
        try {
            PlayerView playerView = null;
            View view = (View) this;
            while (true) {
                ViewParent parent = view.getParent();
                if (null == parent) {
                    break;
                } else if (parent instanceof PlayerView) {
                    playerView = (PlayerView) parent;
                    break;
                } else {
                    view = (View) parent;
                }
            }
            if (null == playerView)
                new Exception("not find");
            return playerView;
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getPlayerView => " + e.getMessage());
            return null;
        }
    }

    default <T extends ComponentApi> T findComponent(java.lang.Class<?> cls) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            ComponentApi component = playerView.findComponent(cls);
            if (null == component)
                throw new Exception("warning: component null");
            return (T) component;
        } catch (Exception e) {
            LogUtil.log("ComponentApi => findComponent => " + e.getMessage());
            return null;
        }
    }

//    default boolean isComponentShowing(Class<?> cls) {
//        try {
//            ComponentApi component = findComponent(cls);
//            if (null == component)
//                throw new Exception("warning: component null");
//            boolean componentShowing = component.isComponentShowing();
//            if (!componentShowing)
//                throw new Exception("warning: componentShowing false");
//            return true;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApi => isComponentApiShowing => " + e.getMessage());
//            return false;
//        }
//    }

    default void superCallEvent(boolean callPlayer, boolean callComponent, @PlayerType.EventType.Value int state) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            playerView.callEvent(callPlayer, callComponent, state);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => superCallEvent => " + e.getMessage());
        }
    }

    default boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFull();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => isFull => " + e.getMessage());
            return false;
        }
    }

    default boolean isFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFloat();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => isFloat => " + e.getMessage());
            return false;
        }
    }

    default boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPlaying();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => isPlaying => " + e.getMessage());
            return false;
        }
    }

    default boolean isPrepared() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPrepared();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => isPrepared => " + e.getMessage());
            return false;
        }
    }

    default String getNetSpeed() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getNetSpeed();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => isFull => " + e.getMessage());
            return "0kb/s";
        }
    }

    default void seekTo(long position) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.seekTo(position);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => seekTo => " + e.getMessage());
        }
    }

    default void resume() {
        resume(true);
    }

    default void resume(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume(callEvent);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => resume => " + e.getMessage());
        }
    }

    default void pause() {
        pause(true);
    }

    default void pause(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause(callEvent);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => pause => " + e.getMessage());
        }
    }

    default void toggle() {
        toggle(true);
    }

    default void toggle(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.toggle(callEvent);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => toggle => " + e.getMessage());
        }
    }

    default void stop() {
        stop(true);
    }

    default void stop(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stop(callEvent, false);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => toggle => " + e.getMessage());
        }
    }

    default long getDuration() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            long duration = playerView.getDuration();
            if (duration < 0)
                throw new Exception("warning: duration<0");
            return duration;
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    default long getPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            long position = playerView.getPosition();
            if (position < 0)
                throw new Exception("warning: position<0");
            return position;
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getPosition => " + e.getMessage());
            return 0L;
        }
    }

    default void setVideoSpeed(@PlayerType.SpeedType.Value int speed) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setSpeed(speed);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setVideoSpeed => " + e.getMessage());
        }
    }

    @PlayerType.SpeedType.Value
    default int getVideoSpeed() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getSpeed();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getVideoSpeed => " + e.getMessage());
            return PlayerType.SpeedType.DEFAULT;
        }
    }

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVideoScaleType(scaleType);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setVideoScaleType => " + e.getMessage());
        }
    }

    @PlayerType.ScaleType.Value
    default int getVideoScaleType() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getVideoScaleType();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getVideoScaleType => " + e.getMessage());
            return PlayerType.ScaleType.DEFAULT;
        }
    }

    default OnPlayerEpisodeListener getOnPlayerEpisodeListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            OnPlayerEpisodeListener onPlayerEpisodeListener = playerView.getOnPlayerEpisodeListener();
            if (null == onPlayerEpisodeListener)
                throw new Exception("warning: onPlayerEpisodeListener null");
            return onPlayerEpisodeListener;
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getOnPlayerEpisodeListener => " + e.getMessage());
            return null;
        }
    }

    default StartArgs getStartArgs() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("error: playerView null");
            StartArgs args = playerView.getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args;
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getStartArgs => " + e.getMessage());
            return null;
        }
    }

    default String getTitle() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            String title = args.getTitle();
            int episodeItemCount = args.getEpisodeItemCount();
            if (episodeItemCount <= 0) {
                return title;
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append(title);
                int index = args.getEpisodePlayingIndex();
                int num = index + 1;
                String s = ((View) this).getResources().getString(R.string.module_mediaplayer_string_title, num);
                builder.append(s);
                return builder.toString();
            }
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getTitle => " + e.getMessage());
            return null;
        }
    }

    default long getTrySeeDuration() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getTrySeeDuration();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getTrySeeDuration => " + e.getMessage());
            return 0L;
        }
    }

    default boolean isPlayWhenReady() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.isPlayWhenReady();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => isPlayWhenReady => " + e.getMessage());
            return true;
        }
    }

    default long getPlayWhenReadyDelayedTime() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getPlayWhenReadyDelayedTime();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getPlayWhenReadyDelayedTime => " + e.getMessage());
            return 0L;
        }
    }

    default long getPlayWhenReadySeekToPosition() {
        try {
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");
            return args.getPlayWhenReadySeekToPosition();
        } catch (Exception e) {
            LogUtil.log("ComponentApi => getPlayWhenReadySeekToPosition => " + e.getMessage());
            return 0L;
        }
    }
}