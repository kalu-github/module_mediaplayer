package lib.kalu.mediaplayer.widget.subtitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public final class StrokeTextView extends TextView {

    private TextPaint mTextPaint;

    public StrokeTextView(Context context) {
        super(context);
        init();
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = getPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 绘制描边
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeWidth(1f);
        mTextPaint.setColor(Color.BLACK);
        getLayout().draw(canvas);

        // 绘制文本
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setStrokeWidth(0);
        mTextPaint.setColor(Color.WHITE);
        super.onDraw(canvas);
    }
}