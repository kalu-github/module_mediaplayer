package lib.kalu.mediaplayer.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.mediaplayer.R;

public class MultiSegmentProgressBar extends View {

    private Bitmap mThumbIcon = null;
    private float mThumbRadius = 0f;
    private int mThumbColor = Color.BLUE;
    private int mThumbColorGradient = 0;

    private float mCorner = 0f;
    private int mProgress = 0;
    private int mMax = 100;
    private List<int[]> mSegments;

    private int mBackgroundColor = Color.BLACK;
    private int mProgressColor = Color.RED;
    private int mBufferColor = Color.GRAY;

    private int mWidth;
    private int mHeight;
    private int mRealHeight;


    private RectF mRectBackground;
    private RectF mRectBuffer;
    private RectF mRectProgress;

    private Paint mBackgroundPaint;
    private Paint mProgressPaint;
    private Paint mBufferPaint;
    private Paint mThumbPaint;

    public MultiSegmentProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public MultiSegmentProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MultiSegmentProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray typed = null;
        try {
            typed = context.obtainStyledAttributes(attrs, R.styleable.MultiSegmentProgressBar);
            mBackgroundColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_background_color, mBackgroundColor);
            mBufferColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_buffer_color, mBufferColor);
            mProgressColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_progress_color, mProgressColor);
            mCorner = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_corner, mCorner);
            mThumbRadius = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_thumb_radius, mThumbRadius);
            mThumbColor = typed.getColor(R.styleable.MultiSegmentProgressBar_ms_thumb_color, mThumbColor);
            mThumbColorGradient = typed.getResourceId(R.styleable.MultiSegmentProgressBar_ms_thumb_color_gradient, mThumbColorGradient);
            //
            int resourceId = typed.getResourceId(R.styleable.MultiSegmentProgressBar_ms_thumb_icon, -1);
            Log.e("MultiSegmentProgressBar", "init -> resourceId = " + resourceId);
            if (resourceId != -1) {

                float reqWidth = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_thumb_icon_width, 0);
                float reqHeight = typed.getDimension(R.styleable.MultiSegmentProgressBar_ms_thumb_icon_height, 0);
                Log.e("MultiSegmentProgressBar", "init -> reqWidth = " + reqWidth + ", reqHeight = " + reqHeight);

                if (reqWidth > 0 && reqHeight > 0) {

                    Drawable drawable = getResources().getDrawable(resourceId);
                    Log.e("MultiSegmentProgressBar", "init -> drawable = " + drawable);

                    // 创建与 Drawable 尺寸相同的 Bitmap
                    Bitmap bitmap = Bitmap.createBitmap(
                            (int) reqWidth,
                            (int) reqHeight,
                            Bitmap.Config.ARGB_8888
                    );

                    // 创建 Canvas 并绘制 Drawable
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);


                    mThumbIcon = bitmap;
                    Log.e("MultiSegmentProgressBar", "init -> mThumbIcon = " + mThumbIcon);
                }
            }
        } catch (Exception e) {
            Log.e("MultiSegmentProgressBar", "init -> Exception " + e.getMessage(), e);
        }
        if (null != typed) {
            typed.recycle();
        }

        // 缓存段列表
        mSegments = new ArrayList<>();

        // 初始化画笔
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStyle(Paint.Style.FILL);

        mBufferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBufferPaint.setColor(mBufferColor);
        mBufferPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mThumbRadius <= 0) {
            mRealHeight = MeasureSpec.getSize(heightMeasureSpec);
            int measureSpec = MeasureSpec.makeMeasureSpec(mRealHeight * 2, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, measureSpec);
        } else {
            int thumbHeight = (int) (mThumbRadius * 2);
            mRealHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (mRealHeight < thumbHeight) {
                int measureSpec = MeasureSpec.makeMeasureSpec(thumbHeight, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, measureSpec);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.e("MultiSegmentProgressBar", "onDraw -> mWidth = " + mWidth + ", mHeight = " + mHeight + ", mRealHeight = " + mRealHeight + ", mProgress = " + mProgress + ", mMax = " + mMax + ", mThumbIcon = " + mThumbIcon);
        // 绘制背景
        drawBackground(canvas);

        // 绘制缓存段
        drawBuffer(canvas);

        // 绘制播放进度
        drawProgress(canvas);

        // 绘制手柄
        drawThumb(canvas);
    }

    private void drawBackground(Canvas canvas) {
        try {
            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;
            if (null == mRectBackground) {
                float left = 0f;
                float top = mHeight * 0.5F - mRealHeight * 0.5F;
                float right = mWidth;
                float bottom = mHeight * 0.5F + mRealHeight * 0.5F;
                mRectBackground = new RectF(left, top, right, bottom);
            }
            canvas.drawRoundRect(mRectBackground, mCorner, mCorner, mBackgroundPaint);
        } catch (Exception e) {
        }
    }

    private void drawBuffer(Canvas canvas) {
        try {
            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;

            // 绘制缓存段
            for (int[] segment : mSegments) {
                if (null == segment)
                    continue;
                if (segment.length != 2)
                    continue;
                int start = segment[0];
                int end = segment[1];

                if (null == mRectBuffer) {
                    mRectBuffer = new RectF();
                } else {
                    mRectBuffer.setEmpty();
                }
                float left = start * mWidth / mMax;
                float top = mHeight * 0.5F - mRealHeight * 0.5F;
                float right = end * mWidth / mMax;
                float bottom = mHeight * 0.5F + mRealHeight * 0.5F;
                mRectBuffer.set(left, top, right, bottom);
                canvas.drawRoundRect(mRectBuffer, mCorner, mCorner, mBufferPaint);
            }
        } catch (Exception e) {
        }
    }

    private void drawProgress(Canvas canvas) {
        try {
            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;

            if (null == mRectProgress) {
                mRectProgress = new RectF();
            } else {
                mRectProgress.setEmpty();
            }
            float left = 0f;
            float top = mHeight * 0.5F - mRealHeight * 0.5F;
            float right = mProgress * mWidth / mMax;
            float bottom = mHeight * 0.5F + mRealHeight * 0.5F;
            mRectProgress.set(left, top, right, bottom);
            canvas.drawRoundRect(mRectProgress, mCorner, mCorner, mProgressPaint);
        } catch (Exception e) {
        }
    }

    private void drawThumb(Canvas canvas) {
        try {
            if (mWidth <= 0)
                return;
            if (mHeight <= 0)
                return;

            if (null == mThumbPaint) {
                mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mThumbPaint.setStyle(Paint.Style.FILL);
                mThumbPaint.setColor(mThumbColor);
            }

            float radius;
            if (mThumbRadius <= 0f) {
                radius = mHeight * 0.5f;
            } else {
                radius = mThumbRadius;
            }

            float cx = mProgress * mWidth / mMax;
            if (cx < mThumbRadius) {
                cx = mThumbRadius;
            } else if (cx + mThumbRadius > mWidth) {
                cx = mWidth - mThumbRadius;
            }
            float cy = radius;

            if (mThumbColorGradient != 0) {
                int[] ints = getResources().getIntArray(mThumbColorGradient);
                LinearGradient linearGradient = new LinearGradient(
                        cx - mThumbRadius, cy - mThumbRadius,                 // 起点坐标 (x1, y1)
                        cx + mThumbRadius, cy + mThumbRadius,             // 终点坐标 (x2, y2)
                        ints,
                        null,                // 颜色分布位置（null表示均匀分布）
                        Shader.TileMode.CLAMP // 边缘处理模式
                );
                mThumbPaint.setShader(linearGradient);
            }

            canvas.drawCircle(cx, cy, radius, mThumbPaint);

            //
            if (null != mThumbIcon) {
                int width = mThumbIcon.getWidth();
                int height = mThumbIcon.getHeight();
                float left = cx - width * 0.5F;
                float top = cy - height * 0.5f;
                canvas.drawBitmap(mThumbIcon, left, top, mThumbPaint);
            }
        } catch (Exception e) {
        }
    }

    public final void setProgress(int progress, int max) {
        this.mProgress = progress;
        this.mMax = max;
        invalidate();
    }

    public final void setMax(int max) {
        this.mMax = max;
        invalidate();
    }

    public final void addSegment(int start, int end) {

        try {
            if (start >= end)
                return;
            if (start >= mMax)
                return;
            if (end > mMax) {
                end = mMax;
            }
            // 添加新的缓存段
            if (mSegments.isEmpty()) {
                mSegments.add(new int[]{start, end});
            } else {
                int size = mSegments.size();
                int index = size - 1;
                int[] segment = mSegments.get(index);
                int before = segment[1];
                if (start > before) {
                    mSegments.add(new int[]{start, end});
                } else {
                    segment[1] = end;
                }
            }

        } catch (Exception e) {
        }

        // 刷新视图
        invalidate();
    }

    public final void clearSegments() {
        mSegments.clear();
        invalidate();
    }
}