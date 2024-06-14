package lib.kalu.mediaplayer.core.component;

import android.view.KeyEvent;

public interface ComponentApiPause extends ComponentApi {
    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }
}