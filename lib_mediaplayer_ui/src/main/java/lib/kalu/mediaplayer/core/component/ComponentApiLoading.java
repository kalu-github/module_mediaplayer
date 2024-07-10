package lib.kalu.mediaplayer.core.component;

import android.view.View;
import android.widget.TextView;

public interface ComponentApiLoading extends ComponentApi {

    boolean[] mShowNetSpeed = new boolean[]{false};

    default void setComponentShowNetSpeed(boolean v) {
        mShowNetSpeed[0] = v;
    }

    default boolean isShowNetSpeed() {
        return mShowNetSpeed[0];
    }

    int initLayoutIdNetSpeed();

    default void updateNetSpeed() {
        // 网速
        try {
            boolean showNetSpeed = isShowNetSpeed();
            if (!showNetSpeed)
                throw new Exception("warning: showNetSpeed false");
            boolean componentShowing = isComponentShowing();
            if (!componentShowing)
                throw new Exception("warning: componentShowing false");
            int netSpeedId = initLayoutIdNetSpeed();
            TextView textView = ((View) this).findViewById(netSpeedId);
            if (null == textView)
                throw new Exception("textView error: null");
            int viewVisibility = textView.getVisibility();
            if (viewVisibility != View.VISIBLE)
                throw new Exception("viewVisibility warning: " + viewVisibility);
            String speed = getNetSpeed();
            textView.setText(speed);
        } catch (Exception e) {
        }
    }
}