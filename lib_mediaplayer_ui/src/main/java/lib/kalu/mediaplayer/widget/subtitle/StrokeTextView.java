package lib.kalu.mediaplayer.widget.subtitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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

        // 保存当前画笔设置
//        Typeface originalTypeface = getTypeface();

        // 绘制描边
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeWidth(10F);
        setTextColor(Color.BLACK);
//        setTypeface(originalTypeface);
        super.onDraw(canvas);

        // 绘制原始
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeJoin(Paint.Join.ROUND);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(0F);
        setTextColor(Color.WHITE);
//        setTypeface(originalTypeface);
        super.onDraw(canvas);
    }
}