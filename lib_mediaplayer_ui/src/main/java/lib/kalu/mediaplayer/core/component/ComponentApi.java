package lib.kalu.mediaplayer.core.component;

import android.net.Uri;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import lib.kalu.mediaplayer.util.MPLogUtil;


@Keep
public interface ComponentApi extends ComponentApiLinkerPlayer {

    default void callPlayerEvent(@NonNull int playState) {
    }

    default void callWindowEvent(int state) {
    }

    /*************/

    default void setImageResource(@NonNull View layout, @IdRes int id, @DrawableRes int value) {
        try {
            ImageView imageView = layout.findViewById(id);
            imageView.setImageResource(value);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setImageResource => " + e.getMessage());
        }
    }

    default void setImageUrl(@NonNull View layout, @IdRes int id, @NonNull String url) {
        try {
            ImageView imageView = layout.findViewById(id);
            imageView.setImageURI(Uri.parse(url));
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setImageUrl => " + e.getMessage());
        }
    }

    default void setTextColor(@NonNull View layout, @IdRes int id, @ColorInt int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setTextColor(value);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setTextColor => " + e.getMessage());
        }
    }

    default void setTextSize(@NonNull View layout, @IdRes int id, @DimenRes int value) {
        try {
            TextView view = layout.findViewById(id);
            int offset = layout.getResources().getDimensionPixelOffset(value);
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setTextSize => " + e.getMessage());
        }
    }

    default void setText(@NonNull View layout, @IdRes int id, @StringRes int value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setText => " + e.getMessage());
        }
    }

    default void setText(@NonNull View layout, @IdRes int id, @NonNull String value) {
        try {
            TextView view = layout.findViewById(id);
            view.setText(value);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setText => " + e.getMessage());
        }
    }

    default void setCompoundDrawablesWithIntrinsicBounds(@NonNull View layout, @IdRes int id, @DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
        try {
            TextView view = layout.findViewById(id);
            view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setCompoundDrawablesWithIntrinsicBounds => " + e.getMessage());
        }
    }

    default void setDimens(@NonNull View layout, @IdRes int id, @DimenRes int value) {
        try {
            View view = layout.findViewById(id);
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            int offset = layout.getResources().getDimensionPixelOffset(value);
            layoutParams.width = offset;
            layoutParams.height = offset;
            view.setLayoutParams(layoutParams);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setDimens => " + e.getMessage());
        }
    }

    default void setBackgroundColorInt(@NonNull View layout, @IdRes int id, @ColorInt int value) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundColor(value);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setBackgroundColorInt => " + e.getMessage());
        }
    }

    default void setBackgroundColorRes(@NonNull View layout, @IdRes int id, @ColorRes int resId) {
        try {
            View view = layout.findViewById(id);
            int color = view.getResources().getColor(resId);
            view.setBackgroundColor(color);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setBackgroundColorRes => " + e.getMessage());
        }
    }

    default void setBackgroundDrawableRes(@NonNull View layout, @IdRes int id, @DrawableRes int resId) {
        try {
            View view = layout.findViewById(id);
            view.setBackgroundResource(resId);
        } catch (Exception e) {
            MPLogUtil.log("ComponentApi => setBackgroundDrawableRes => " + e.getMessage());
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

    default void setComponentImageUrl(@NonNull String url) {
    }

    /******************/

    default void setComponentText(@NonNull String value) {
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

    default void gone() {
    }

    /******************/

    default void onUpdateSeekProgress(@NonNull long position, @NonNull long duration, @NonNull boolean updateTime) {
    }

    default void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
    }

    default boolean dispatchKeyEventComponent(KeyEvent event) {
        return false;
    }
}