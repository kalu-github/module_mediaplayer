package lib.kalu.mediaplayer.widget.loading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.MPLogUtil;

@SuppressLint("AppCompatCustomView")
@Keep
public class MPLoadingView extends View {

    private int mDelayMillis = 120;
    private int mLoop = 0;

    private int mCount = 8;
    private float mRadius = 0f;
    private float mRate = 0f;
    private Paint mPaint = new Paint();
    private Rect mRect = new Rect();

    @ColorInt
    private int mColorBackground = Color.TRANSPARENT;
    @ColorInt
    private int mColorRound = Color.GRAY;

    private Handler mHandler = null;

    public MPLoadingView(Context context) {
        super(context);
        init(null);
    }

    public MPLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MPLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MPLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = getContext().getApplicationContext().obtainStyledAttributes(attrs, R.styleable.MPLoadingView);
            mCount = typedArray.getInt(R.styleable.MPLoadingView_lv_count, 8);
            mDelayMillis = typedArray.getInt(R.styleable.MPLoadingView_lv_delay_millis, 120);
            mRate = typedArray.getFloat(R.styleable.MPLoadingView_lv_rate, 0.9f);
            mRadius = typedArray.getDimension(R.styleable.MPLoadingView_lv_radius, 0f);
            mColorBackground = typedArray.getColor(R.styleable.MPLoadingView_lv_color_background, Color.TRANSPARENT);
            mColorRound = typedArray.getColor(R.styleable.MPLoadingView_lv_color_round, Color.GRAY);
        } catch (Exception e) {
        }
        if (null != typedArray) {
            typedArray.recycle();
        }
    }

    @Override
    public final void setVisibility(int visibility) {
        setEnabled(visibility == View.VISIBLE);
        super.setVisibility(visibility);
        if (visibility != View.VISIBLE) {
            cleanMessage();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        setEnabled(visibility == View.VISIBLE);
        super.onVisibilityChanged(changedView, visibility);
        MPLogUtil.log("MPLoadingView => onVisibilityChanged => visibility = " + visibility);
        if (visibility != View.VISIBLE) {
            cleanMessage();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        setEnabled(visibility == View.VISIBLE);
        super.onWindowVisibilityChanged(visibility);
        MPLogUtil.log("MPLoadingView => onWindowVisibilityChanged => visibility = " + visibility);
        if (visibility != View.VISIBLE) {
            cleanMessage();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        setEnabled(false);
        super.onDetachedFromWindow();
        clearHandler();
    }


    @Override
    protected void onAttachedToWindow() {
        setEnabled(true);
        super.onAttachedToWindow();
        checkHandler();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clearLoading(canvas);
        drawLoading(canvas);
    }

    private void clearLoading(@NonNull Canvas canvas) {
        try {
            mPaint.reset();
            mPaint.setColor(Color.TRANSPARENT);
            int width = getWidth();
            int height = getHeight();
            mRect.set(0, 0, width, height);
            canvas.drawRect(mRect, mPaint);
//            // 方法一：
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC_OVER);
//            //  方法二：
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            //  方法三：
//            Paint paint = new Paint();
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            canvas.drawPaint(paint);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        } catch (Exception e) {
            MPLogUtil.log("MPLoadingView => clearLoading => " + e.getMessage());
        }
    }

    private void drawLoading(@NonNull Canvas canvas) {
        try {
            boolean enabled = isEnabled();
            if (!enabled)
                throw new Exception("enabled warning: false");
            int visibility = getVisibility();
            MPLogUtil.log("MPLoadingView => onDraw => mLoop = " + mLoop + ", mCount = " + mCount + ", visibility = " + visibility);
            if (visibility != View.VISIBLE)
                throw new Exception("visibility warning: " + visibility);
            // 循环次数
            if (mLoop + 1 >= mCount) {
                mLoop = 0;
            }
            // 画笔
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeWidth(0f);
            mPaint.setFakeBoldText(true);

            float cx = getWidth() * 0.5f;
            float cy = getHeight() * 0.2f;
            float radius;
            if (mRadius == 0f) {
                radius = Math.max(cx, cy) / 5;
            } else {
                radius = mRadius;
            }
            float angle = 360 / mCount;

            mPaint.setColor(mColorBackground);
            canvas.drawCircle(cx, cy, Math.min(cx, cy), mPaint);

            // 椭圆
            int length = mLoop + mCount;
            for (int i = mLoop; i < length; i++) {
                if (i == mLoop) {
                    if (mLoop != 0) {
                        canvas.save();
                        canvas.rotate(angle * (i % mCount), cx, cx);
                    }
                    mPaint.setColor(mColorRound);
                } else {
                    try {
                        float r = ((mColorRound >> 16) & 0xff) / 255.0f;
                        float g = ((mColorRound >> 8) & 0xff) / 255.0f;
                        float b = ((mColorRound) & 0xff) / 255.0f;
                        int a = (255 / 11) * (i - mLoop);
                        int color = ((int) (a * 255.0f + 0.5f) << 24) |
                                ((int) (r * 255.0f + 0.5f) << 16) |
                                ((int) (g * 255.0f + 0.5f) << 8) |
                                (int) (b * 255.0f + 0.5f);
                        mPaint.setColor(color);
                    } catch (Exception e) {
                    }
                }
                radius = radius * mRate;
                canvas.drawCircle(cx, cy, radius, mPaint);
                canvas.save();
                canvas.rotate(angle, cx, cx);
            }

            // delay
            mLoop = mLoop + 1;
            // update
            updateMessage();
        } catch (Exception e) {
            MPLogUtil.log("MPLoadingView => drawLoading => " + e.getMessage());
        }
    }

    private void cleanMessage() {
        try {
            if (null == mHandler)
                throw new Exception("mHandler warning: null");
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            MPLogUtil.log("MPLoadingView => cleanMessage => " + e.getMessage());
        }
    }

    private void checkHandler() {
        try {
            if (null != mHandler)
                throw new Exception("mHandler warning: null");
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    cleanMessage();
                    try {
                        int visibility = getVisibility();
                        if (visibility != View.VISIBLE)
                            throw new Exception();
                        if (msg.what != 9001)
                            throw new Exception("msg.what error: " + msg.what);
                        postInvalidate();
                    } catch (Exception e) {
                        MPLogUtil.log("MPLoadingView => checkHandler => handleMessage => " + e.getMessage());
                    }
                }
            };
        } catch (Exception e) {
            MPLogUtil.log("MPLoadingView => checkHandler => " + e.getMessage());
        }
    }

    private void clearHandler() {
        try {
            if (null == mHandler)
                throw new Exception("mHandler warning: null");
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        } catch (Exception e) {
            MPLogUtil.log("MPLoadingView => clearHandler => " + e.getMessage());
        }
    }

    private void updateMessage() {
        checkHandler();
        try {
            mHandler.sendEmptyMessageDelayed(9001, mDelayMillis);
            MPLogUtil.log("MPLoadingView => updateMessage => succ");
        } catch (Exception e) {
            MPLogUtil.log("MPLoadingView => updateMessage => " + e.getMessage());
        }
    }

    /*************/

    @Keep
    public void setDelayMillis(@NonNull int delayMillis) {
        this.mDelayMillis = delayMillis;
    }

    @Keep
    public void setCount(@NonNull int count) {
        this.mCount = count;
    }

    @Keep
    public void setRate(@NonNull float rate) {
        this.mRate = rate;
    }

    @Keep
    public void setRadius(@DimenRes int resId) {
        try {
            float dimension = getResources().getDimension(resId);
            this.mRadius = dimension;
        } catch (Exception e) {
            MPLogUtil.log("MPLoadingView => setRadius => " + e.getMessage());
        }
    }
}
