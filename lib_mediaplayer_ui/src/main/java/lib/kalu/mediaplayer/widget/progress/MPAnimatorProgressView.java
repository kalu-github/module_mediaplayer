package lib.kalu.mediaplayer.widget.progress;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;

/**
 * @author: gengzhibo
 * @date: 2021/6/1
 */
public class MPAnimatorProgressView extends View {
    public MPAnimatorProgressView(Context context) {
        this(context, null);
    }

    public MPAnimatorProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPAnimatorProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("CustomViewStyleable")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimationProgress);
        viewBackGroundColor = typedArray.getColor(R.styleable.AnimationProgress_ap_background_color, 0xFFCECECE);
        viewProgressColor = typedArray.getColor(R.styleable.AnimationProgress_ap_progress_color, 0xFF000000);
        viewLineColor = typedArray.getColor(R.styleable.AnimationProgress_ap_line_color, 0xFFFFFFFF);

        progress = typedArray.getInt(R.styleable.AnimationProgress_ap_progress, 0);
        offsetLine = typedArray.getDimensionPixelOffset(R.styleable.AnimationProgress_ap_line_space, 20);
        paintWidth = typedArray.getDimensionPixelOffset(R.styleable.AnimationProgress_ap_line_width, 10);
        animatorTime = typedArray.getInt(R.styleable.AnimationProgress_ap_animator_time, 1000);
        animatorProgressTime = typedArray.getInt(R.styleable.AnimationProgress_ap_animator_progress_time, 10);

        typedArray.recycle();
        startAnimator();
    }


    //当前view 宽度和高度
    private int width = 0;
    private int height = 0;
    //动画偏移量
    private float offsetAnimator = 0;


    //下面为可自定义编辑的参数
    //背景颜色
    private int viewBackGroundColor;
    //线条颜色
    private int viewLineColor;
    //进度条颜色
    private int viewProgressColor;
    //线条见间隔
    private int offsetLine;
    //执行动画需要的时间
    private int animatorTime;
    //进度变化动画需要的时间
    private int animatorProgressTime;
    //画笔/线条的宽度
    private int paintWidth;


    //当前进度
    private float progress = 0;


    //设置对应的属性
    public void setViewBackGroundColor(int viewBackGroundColor) {
        this.viewBackGroundColor = viewBackGroundColor;
        invalidate();
    }

    public void setViewLineColor(int viewLineColor) {
        this.viewLineColor = viewLineColor;
        invalidate();
    }

    public void setViewProgressColor(int viewProgressColor) {
        this.viewProgressColor = viewProgressColor;
        invalidate();
    }

    public void setOffsetLine(int offsetLine) {
        this.offsetLine = offsetLine;
        invalidate();
        startAnimator();
    }

    public void setPaintWidth(int paintWidth) {
        this.paintWidth = paintWidth;
        invalidate();
    }

    public void setAnimatorTime(int animatorTime) {
        this.animatorTime = animatorTime;
        invalidate();
        startAnimator();
    }

    public void setAnimatorProgressTime(int animatorProgressTime) {
        this.animatorProgressTime = animatorProgressTime;
        invalidate();
        startAnimator();
    }

    /**
     * @description 设置进度
     */
    public void setProgress(int progress) {
        if (progress != this.progress) {
            invalidate();
            startProgressAnimator((int) this.progress, progress);
        }
    }

    @Override
    /**
     * @description 测量当前view宽高
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    /**
     * @description 绘制相关动画
     */
    protected void onDraw(Canvas canvas) {


        //绘制背景
        drawViewBackground(canvas);
        //绘制进度
        drawProgress(canvas);
    }

    /**
     * @param startProgress
     * @param endProgress
     * @description 进度变化动画
     */
    public void startProgressAnimator(int startProgress, int endProgress) {
        //动画差值范围
        ValueAnimator va = ValueAnimator.ofFloat(startProgress, endProgress);
        va.setDuration(animatorProgressTime);
        //线性取值
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                progress = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }

        });


        va.start();
    }


    /**
     * @description 开始动画
     */
    public void startAnimator() {
        //动画差值范围
        ValueAnimator va = ValueAnimator.ofFloat(0f, offsetLine);
        //循环播放
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setDuration(animatorTime);
        //线性取值
        va.setInterpolator(new LinearInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                offsetAnimator = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        va.start();
    }

    /**
     * @description 绘制进度
     */
    private void drawProgress(Canvas canvas) {
        //展示图层
        Bitmap src = progressBitmapSrc();
        //遮罩图层
        Bitmap dst = progressBitmapDst();

        Paint paint = new Paint();

        canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        //绘制遮罩层
        canvas.drawBitmap(dst, 0, 0, paint);
        //设置遮罩模式为SRC_IN显示原图层中原图层与遮罩层相交部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, 0, 0, paint);
        paint.setXfermode(null);
    }


    /**
     * @description 绘制源图层
     */
    private Bitmap progressBitmapSrc() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //绘制图层背景
        getProgressBackground(canvas);
        getProgressLines(canvas);

        return bitmap;
    }

    /**
     * @description 绘制线条动画
     */
    private void getProgressLines(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(viewLineColor);

        //设置画笔宽度
        paint.setStrokeWidth(paintWidth);

        //设置画笔圆形边界
        paint.setStrokeCap(Paint.Cap.ROUND);
        //todo 动画效果展示优化
        canvas.translate(-offsetLine * 2, 0);
        //offsetAnimator 为动画展示时使用, 每次移动一定的长度,
        //通过连续的移动来实现视觉上的动画效果
        canvas.translate(offsetAnimator, 0);
        canvas.save();

        int indexOffset = 0;

        //依次绘制线条
        do {
            //绘制右上到左下的线条
            canvas.drawLine(20, -20, -offsetLine - 20, height + 20, paint);
            indexOffset += offsetLine;
            //偏移位置
            canvas.translate(offsetLine, 0);
        } while (indexOffset < width + offsetLine * 4);

        canvas.restore();
    }


    /**
     * @description 绘制图层背景
     */
    private void getProgressBackground(Canvas canvas) {
        //设置画笔
        Paint paint = new Paint();
        paint.setColor(viewProgressColor);

        //确定两个圆形的中心位置及半径
        int leftCirclePoint = height / 2;
        int rightCirclePoint = width - leftCirclePoint;
        int radius = height / 2;

        //绘制左侧和右侧的圆形填充
        canvas.drawCircle(leftCirclePoint, height >> 1, radius, paint);
        canvas.drawCircle(rightCirclePoint, height >> 1, radius, paint);

        //绘制中心的矩形填充
        Rect rect = new Rect(leftCirclePoint, 0, rightCirclePoint, height);
        canvas.drawRect(rect, paint);
    }


    /**
     * @description 绘制遮罩图层
     */
    private Bitmap progressBitmapDst() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        //如果当前进度为0, 则不展示任何内容
        if (progress == 0) {
            return bitmap;
        }
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);


        //遮罩图层宽度与当前progress进度有关
        int leftCirclePoint = height / 2;
        int rightCirclePoint = leftCirclePoint + (int) ((width - height) / 1f * progress / 100f);

        int radius = height / 2;

        canvas.drawCircle(leftCirclePoint, height >> 1, radius, paint);
        canvas.drawCircle(rightCirclePoint, height >> 1, radius, paint);

        Rect rect = new Rect(leftCirclePoint, 0, rightCirclePoint, height);
        canvas.drawRect(rect, paint);


        return bitmap;
    }


    /**
     * @description 绘制背景图层
     */
    private void drawViewBackground(Canvas canvas) {
        canvas.save();

        Paint paint = new Paint();
        paint.setColor(viewBackGroundColor);

        int leftCirclePoint = height / 2;
        int rightCirclePoint = width - leftCirclePoint;
        int radius = height / 2;

        canvas.drawCircle(leftCirclePoint, height >> 1, radius, paint);
        canvas.drawCircle(rightCirclePoint, height >> 1, radius, paint);

        Rect rect = new Rect(leftCirclePoint, 0, rightCirclePoint, height);
        canvas.drawRect(rect, paint);
    }
}