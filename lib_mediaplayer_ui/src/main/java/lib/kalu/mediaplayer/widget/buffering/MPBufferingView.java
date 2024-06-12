package lib.kalu.mediaplayer.widget.buffering;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MPBufferingView extends View {
    public MPBufferingView(Context context) {
        super(context);
    }
    public MPBufferingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public MPBufferingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @SuppressLint("NewApi")
    public MPBufferingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
