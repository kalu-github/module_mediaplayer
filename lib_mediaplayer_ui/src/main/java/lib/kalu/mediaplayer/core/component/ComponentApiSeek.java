package lib.kalu.mediaplayer.core.component;

import android.widget.SeekBar;

import androidx.annotation.Keep;


@Keep
public interface ComponentApiSeek extends ComponentApi {

    SeekBar findSeekBar();

    boolean isShowing();
}