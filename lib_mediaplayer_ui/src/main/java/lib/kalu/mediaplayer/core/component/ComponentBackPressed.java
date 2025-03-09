package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public final class ComponentBackPressed extends RelativeLayout implements ComponentApi {

    public ComponentBackPressed(Context context) {
        super(context);
    }

    @Override
    public int initLayoutId() {
        return 0;
    }

    @Override
    public int initViewIdRoot() {
        return 0;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // stopFull
            if (isFull()) {
                getPlayerView().stopFull();
                return true;
            }
            //  stopFloat();
            else if (isFloat()) {
                getPlayerView().stopFloat();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}