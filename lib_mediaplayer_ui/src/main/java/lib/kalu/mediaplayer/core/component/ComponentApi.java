package lib.kalu.mediaplayer.core.component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;


import androidx.annotation.StringRes;

import java.io.File;

import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public interface ComponentApi {

    default void callEventListener(int playState) {
    }

    default void callWindowEvent(int state) {
    }

    /*************/

    default void setImageResource(View layout, @IdRes int id, @DrawableRes int value) {
        try {
            ImageView imageView = layout.findViewById(id);
            imageView.setImageResource(value);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setImageResource => " + e.getMessage());
        }
    }

    default void setImageUrl(View layout, @IdRes int id, String url) {
        try {
            ImageView imageView = layout.findViewById(id);
            imageView.setImageURI(Uri.parse(url));
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setImageUrl => " + e.getMessage());
        }
    }

    default void setImageFile(View layout, @IdRes int id, String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists())
                throw new Exception("error: file not exists");
            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            ImageView imageView = layout.findViewById(id);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setImageFile => " + e.getMessage());
        }
    }

    default void setTextColor(View layout, @IdRes int id, @ColorInt int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setTextColor(value);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setTextColor => " + e.getMessage());
        }
    }

    default void setTextSize(View layout, @IdRes int id, @DimenRes int value) {
        try {
            TextView view = layout.findViewById(id);
            int offset = layout.getResources().getDimensionPixelOffset(value);
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setTextSize => " + e.getMessage());
        }
    }

    default void setText(View layout, @IdRes int id, @StringRes int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setText => " + e.getMessage());
        }
    }

    default void setText(View layout, @IdRes int id, String value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setText => " + e.getMessage());
        }
    }

    default void setCompoundDrawablesWithIntrinsicBounds(View layout, @IdRes int id, @DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        try {
            TextView view = layout.findViewById(id);
            view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setCompoundDrawablesWithIntrinsicBounds => " + e.getMessage());
        }
    }

    default void setDimens(View layout, @IdRes int id, @DimenRes int value) {
        try {
            View view = layout.findViewById(id);
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            int offset = layout.getResources().getDimensionPixelOffset(value);
            layoutParams.width = offset;
            layoutParams.height = offset;
            view.setLayoutParams(layoutParams);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setDimens => " + e.getMessage());
        }
    }

    default void setBackgroundColorInt(View layout, @IdRes int id, @ColorInt int value) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundColor(value);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setBackgroundColorInt => " + e.getMessage());
        }
    }

    default void setBackgroundColorRes(View layout, @IdRes int id, @ColorRes int resId) {
        try {
            View view = layout.findViewById(id);
            int color = view.getResources().getColor(resId);
            view.setBackgroundColor(color);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setBackgroundColorRes => " + e.getMessage());
        }
    }

    default void setBackgroundDrawableRes(View layout, @IdRes int id, @DrawableRes int resId) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundResource(resId);
        } catch (Exception e) {
            LogUtil.log("ComponentApi => setBackgroundDrawableRes => " + e.getMessage());
        }
    }

    /******************/

    default boolean isComponentShowing() {
        return false;
    }

    /******************/

    default void setComponentBackgroundColorInt(@ColorInt int value) {
    }

    default void setComponentBackgroundResource(@DrawableRes int resid) {
    }

    default void setComponentImageResource(@DrawableRes int resid) {
    }

    default void setComponentImageUrl(String url) {
    }

    default void setComponentImageFile(String filepath) {
    }

    /******************/

    default void setComponentText(String value) {
    }

    default void setComponentText(@StringRes int value) {
    }

    default void setComponentTextSize(@DimenRes int value) {
    }

    default void setComponentTextColor(@ColorInt int color) {
    }

    /******************/

    default void show() {
    }

    default void hide() {
    }

    /******************/

    default void onUpdateProgress(boolean isFromUser, long max, long position, long duration) {
    }

    default boolean dispatchKeyEventComponent(KeyEvent event) {
        return false;
    }

    /*******************/

    default PlayerLayout getPlayerLayout() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                new Exception("playerView error: null");
            ViewParent parent = playerView.getParent();
            if (null == parent)
                throw new Exception("parent error: null");
            if (!(parent instanceof PlayerLayout))
                throw new Exception("parent error: not instanceof PlayerLayout");
            return (PlayerLayout) parent;
        } catch (Exception e) {
            LogUtil.log("ComponentApiLinkerPlayer => getPlayerLayout => " + e.getMessage());
            return null;
        }
    }

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
            LogUtil.log("ComponentApiLinkerPlayer => getPlayerView => " + e.getMessage());
            return null;
        }
    }

    default boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFull();
        } catch (Exception e) {
            LogUtil.log("ComponentApiLinkerPlayer => isFull => " + e.getMessage());
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
            LogUtil.log("ComponentApiLinkerPlayer => isPlaying => " + e.getMessage());
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
            LogUtil.log("ComponentApiLinkerPlayer => isFull => " + e.getMessage());
            return "0kb/s";
        }
    }

    default void resume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume();
        } catch (Exception e) {
            LogUtil.log("ComponentApiLinkerPlayer => resume => " + e.getMessage());
        }
    }

    default void pause() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause();
        } catch (Exception e) {
            LogUtil.log("ComponentApiLinkerPlayer => pause => " + e.getMessage());
        }
    }

    default String getUrl() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            String url = playerView.getUrl();
            if (null == url || url.length() == 0)
                throw new Exception("url error: " + url);
            return url;
        } catch (Exception e) {
            LogUtil.log("ComponentApiLinkerPlayer => getUrl => " + e.getMessage());
            return null;
        }
    }

    default String getData() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            String data = playerView.getData();
            if (null == data || data.length() == 0)
                throw new Exception("data error: " + data);
            return data;
        } catch (Exception e) {
            LogUtil.log("ComponentApiLinkerPlayer => getData => " + e.getMessage());
            return null;
        }
    }
}