package lib.kalu.mediaplayer.widget.line;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;

public class UnderLineRadioButton extends RadioButton {
    public UnderLineRadioButton(Context context) {
        super(context);
    }

    public UnderLineRadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UnderLineRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public UnderLineRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean hasFocus = hasFocus();
        if (hasFocus)
            return;

        // 下划线
        boolean selected = isSelected();
        if (!selected)
            return;

        TextPaint paint = getPaint();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int textHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
        float textWidth = paint.measureText(String.valueOf(getText()));
        int offset = getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_under_line);
        int width = getWidth();
        int height = getHeight();
        int startX = (int) (width / 2 - textWidth / 2);
        int endX = (int) (width / 2 + textWidth / 2);
        int startY = height / 2 + textHeight / 2 + offset;
        canvas.drawLine(startX, startY, endX, startY, paint);
    }
}
