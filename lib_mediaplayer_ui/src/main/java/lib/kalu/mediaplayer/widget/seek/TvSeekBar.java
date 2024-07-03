package lib.kalu.mediaplayer.widget.seek;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class TvSeekBar extends SeekBar {
    public TvSeekBar(Context context) {
        super(context);
    }

    public TvSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TvSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TvSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int width = getWidth();
        int height = getHeight();

        int max = getMax();
        int progress = getProgress();
        int w = width * progress / max;

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawText(progress + "", w, height / 3, paint);
    }
}
