
package lib.kalu.mediaplayer.widget.popu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;

/**
 * https://blog.csdn.net/ansondroider/article/details/124790843?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-124790843-blog-101005004.pc_relevant_multi_platform_whitelistv4&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-124790843-blog-101005004.pc_relevant_multi_platform_whitelistv4&utm_relevant_index=2
 */
@SuppressLint("AppCompatCustomView")
public final class PopuView extends TextView {

    private final Rect mRect = new Rect();
    private final RectF mRectF = new RectF();
    private final Path mPath = new Path();

    @ColorInt
    private int mBackgroundColor = Color.WHITE;
    private int mMarginBottom = 0;
    private int mCorners = 0;

    public PopuView(Context context) {
        super(context);
        init(context, null);
    }

    public PopuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PopuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PopuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setMarqueeRepeatLimit(Integer.MAX_VALUE);
        TypedArray typed = null;
        try {
            typed = context.obtainStyledAttributes(attrs, R.styleable.PopuView);
            mBackgroundColor = typed.getColor(R.styleable.PopuView_pv_background_color, Color.WHITE);
            mMarginBottom = typed.getDimensionPixelOffset(R.styleable.PopuView_pv_margin_bottom, 0);
            mCorners = typed.getDimensionPixelOffset(R.styleable.PopuView_pv_corners, 0);
        } catch (Exception e) {
            mBackgroundColor = Color.WHITE;
            mMarginBottom = 0;
        }
        if (null != typed) {
            typed.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1
        canvas.getClipBounds(mRect);
        // 2
        mRectF.left = mRect.left;
        mRectF.right = mRect.right;
        mRectF.top = mRect.top;
        mRectF.bottom = mRect.bottom - mMarginBottom;
        // 3-popu
        Paint paint = getPaint();
        paint.setColor(mBackgroundColor);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        int rx = mCorners;
        canvas.drawRoundRect(mRectF, rx, rx, paint);
        // 4
        int centerX = mRect.centerX();
        int x1 = centerX - rx;
        int x2 = centerX + rx;
        int y1 = (int) mRectF.bottom;
        int y2 = mRect.bottom;
        mPath.reset();
        mPath.moveTo(x1, y1);
        mPath.lineTo(x2, y1);
        mPath.lineTo(centerX, y2);
        mPath.lineTo(x1, y1);
        mPath.close();
        canvas.drawPath(mPath, paint);
        super.onDraw(canvas);
    }

    @Override
    public void setBackground(Drawable background) {
    }
}
