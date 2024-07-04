package lib.kalu.mediaplayer.widget.seek;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.util.TimeUtil;

public final class SeekBar extends android.widget.SeekBar {

    private Paint mPaint = null;
    private int mTextColor = Color.WHITE;
    private int mTextSize = 10;
    private int mTextGravity = 2;
    private int mTextDurationPaddingLeft = 0;
    private int mTextDurationPaddingRight = 0;
    private int mTextProgressPaddingLeft = 0;
    private int mTextProgressPaddingRight = 0;
    private int mMode = 2;

    public SeekBar(Context context) {
        super(context);
        init(null);
    }

    public SeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 计算 paddingLeft paddingRight
        try {
            long duration = getMax();
            if (duration <= 0)
                throw new Exception("warning: duration <= 0");

            if (null == mPaint) {
                mPaint = new Paint();
            }
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            mPaint.setColor(mTextColor);

            String text = TimeUtil.formatTimeMillis(duration);
            float textWidth = mPaint.measureText(text);
//            LogUtil.log("SeekBar => onMeasure => text = " + text + ", textWidth = " + textWidth);

            int left = (int) (textWidth + mTextProgressPaddingLeft + mTextProgressPaddingRight);
            int right = (int) (textWidth + mTextDurationPaddingLeft + mTextDurationPaddingRight);
            int top = getPaddingTop();
            int bottom = getPaddingBottom();
            setPadding(left, top, right, bottom);
        } catch (Exception e) {
            LogUtil.log("SeekBar => onMeasure => Exception " + e.getMessage());
        }
        super.onMeasure(widthMeasureSpec, -2);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        // 进度条
        try {
            long duration = getMax();
            if (duration <= 0)
                throw new Exception("warning: duration <= 0");
            super.onDraw(canvas);
        } catch (Exception e) {
        }

        // 时长
        try {
            long duration = getMax();
            if (duration <= 0)
                throw new Exception("warning: duration <= 0");

            if (null == mPaint) {
                mPaint = new Paint();
            }
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            mPaint.setColor(mTextColor);

            String text = TimeUtil.formatTimeMillis(duration);
//            float textWidth = mPaint.measureText(text);
//            LogUtil.log("SeekBar => onDraw => duration = " + duration + ", text = " + text + ", textWidth = " + textWidth);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float textHeight = fontMetrics.bottom - fontMetrics.top;


            int width = getWidth();
            int right = getPaddingRight();
            int x = (width - right + mTextDurationPaddingLeft);

            int y;
            // top
            if (mTextGravity == 1) {
                y = (int) (textHeight * 0.8);
            }
            // center
            else if (mTextGravity == 2) {
                Rect rect = getProgressDrawable().getBounds();
                int paddingTop = getPaddingTop();
                int height = rect.height();
                int top = rect.top;
                y = paddingTop + top + height * 2;
            }
            // bottom
            else {
                int height = getHeight();
                y = (int) (height - fontMetrics.descent);
            }

            canvas.drawText(text, x, y, mPaint);

        } catch (Exception e) {
        }

        // 进度
        try {

            long duration = getMax();
            if (duration <= 0)
                throw new Exception("warning: duration <= 0");

            if (null == mPaint) {
                mPaint = new Paint();
            }
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            mPaint.setColor(mTextColor);

            long progress;
            if (mMode == 2 || playPosition <= 0) {
                progress = getProgress();
            } else {
                progress = playPosition;
            }
            if (progress < 0) {
                progress = 0;
            }

            String text = TimeUtil.formatTimeMillis(progress, duration);
//            LogUtil.log("SeekBar => onDraw => duration = " + duration + ", progress = " + progress + ", text = " + text + ", playPosition = " + playPosition + ", mMode = " + mMode);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float textHeight = fontMetrics.bottom - fontMetrics.top;

            int y;
            if (mTextGravity == 1) {
                y = (int) (textHeight * 0.8);
            } else if (mTextGravity == 2) {
                Rect rect = getProgressDrawable().getBounds();
                int paddingTop = getPaddingTop();
                int height = rect.height();
                int top = rect.top;
                y = paddingTop + top + height * 2;
            } else {
                int height = getHeight();
                y = (int) (height - fontMetrics.descent);
            }

            int left = getPaddingLeft();
            float textWidth = mPaint.measureText(text);
            int x = (int) (left - textWidth - mTextProgressPaddingRight);

            canvas.drawText(text, x, y, mPaint);

        } catch (Exception e) {
        }

        // 快进快退 提示
        try {
            if (mMode == 2)
                throw new Exception("warning: mMode = 2");

            long duration = getMax();
            if (duration <= 0)
                throw new Exception("warning: duration <= 0");

            long progress = getProgress();
            if (progress < 0) {
                progress = 0;
            }

            if (null == mPaint) {
                mPaint = new Paint();
            }
            mPaint.setAntiAlias(true);
            mPaint.setTextSize((float) (mTextSize * 1.1));
            mPaint.setColor(mTextColor);

            String text = TimeUtil.formatTimeMillis(progress, duration);
            float textWidth = mPaint.measureText(text);

            Rect rect = getProgressDrawable().getBounds();
            int width = rect.width();
//            int paddingLeft = getPaddingLeft();

            int range = (int) (width * progress / duration);
            int x = (int) (range + mTextProgressPaddingLeft + mTextProgressPaddingRight + textWidth * 0.5);

            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float textHeight = fontMetrics.bottom - fontMetrics.top;

            int top = getPaddingTop();
            int y = (int) (top - textHeight * 0.5);

            canvas.drawText(text, x, y, mPaint);
        } catch (Exception e) {
        }
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBar);
            mMode = a.getInteger(R.styleable.SeekBar_sb_mode, 2);
            mTextColor = a.getColor(R.styleable.SeekBar_sb_text_color, Color.WHITE);
            mTextGravity = a.getInteger(R.styleable.SeekBar_sb_text_gravity, 2);
            mTextSize = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_size, 10);
            mTextDurationPaddingLeft = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_duration_padding_left, 0);
            mTextDurationPaddingRight = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_duration_padding_right, 0);
            mTextProgressPaddingLeft = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_progress_padding_left, 0);
            mTextProgressPaddingRight = a.getDimensionPixelOffset(R.styleable.SeekBar_sb_text_progress_padding_right, 0);
//            mMinHeight = a.getDimensionPixelSize(R.styleable.ProgressBar_minHeight, mMinHeight);
//            mMaxHeight = a.getDimensionPixelSize(R.styleable.ProgressBar_maxHeight, mMaxHeight);
        } catch (Exception e) {
        }
        if (null != a) {
            a.recycle();
        }

        setMax(0);
        setProgress(0);
        setPlayPosition(0);
        setThumbOffset(0);
    }


    private long playPosition;

    public long getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(long playPosition) {
        if (this.playPosition < playPosition) {
            this.playPosition = playPosition;
        }
    }

    public void clearPlayPosition() {
        this.playPosition = 0;
    }
}
