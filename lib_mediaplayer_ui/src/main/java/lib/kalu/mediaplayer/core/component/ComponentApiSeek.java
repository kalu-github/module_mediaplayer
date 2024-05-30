package lib.kalu.mediaplayer.core.component;

import android.widget.SeekBar;

public interface ComponentApiSeek extends ComponentApi {

    void setUserTouch(boolean status);

    SeekBar findSeekBar();

    void initSeekBarChangeListener();

    void seekToStopTrackingTouch();
}