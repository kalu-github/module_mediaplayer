package lib.kalu.mediaplayer.widget.gradual;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.LogUtil;

public final class GradientView extends View {

    private int colorStart;
    private int colorCenter;
    private int colorEnd;
    private Paint mPaint = null;
    private ValueAnimator mValueAnimator = null;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = null;
    private AccelerateDecelerateInterpolator mAccelerateDecelerateInterpolator = null;

    public GradientView(Context context) {
        super(context);
        init(null);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            start();
        } else {
            close();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != View.VISIBLE) {
            close();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        close();
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        close();
//    }

    @Override
    protected void onDraw(Canvas canvas) {

        try {
            int width = getWidth();
            int height = getHeight();
            int startH = (int) (height * 0.4);
            int endH = (int) (height * 0.6);
            int startW = (int) (width * 0.05);
            int endW = (int) (width * 0.95);

            Path path = new Path();
            path.moveTo(0, startH);
            path.lineTo(startW, 0);
            path.lineTo(endW, 0);
            path.lineTo(width, startH);
            path.lineTo(width, endH);
            path.lineTo(endW, height);
            path.lineTo(startW, height);
            path.lineTo(0, endH);
            path.lineTo(0, startH);
            path.close();

            if (null == mPaint) {
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            }

            LinearGradient backGradient = new LinearGradient(width, 0, 0, 0, new int[]{colorStart, colorCenter, colorCenter, colorCenter, colorEnd}, new float[]{0, 0.4f, 0.5f, 0.6f, 1f}, Shader.TileMode.CLAMP);
            mPaint.setShader(backGradient);

//            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
            canvas.drawPath(path, mPaint);
        } catch (Exception e) {
        }
    }

    private void start() {
        try {
            if (null == mValueAnimator) {
                mValueAnimator = ValueAnimator.ofInt(100, 255);
            }
            mValueAnimator.setDuration(1000);
            // 循环次数，INFINITE表示无限循坏
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            // 重复类型，有REVERSE和RESTART两种类型，与setRepeatCount配合使用，reverse                                                                       表示倒叙回放，restart表示重新开始
            mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);

            if (null == mAccelerateDecelerateInterpolator) {
                mAccelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
            }
            mValueAnimator.setInterpolator(mAccelerateDecelerateInterpolator);
            if (null == mAnimatorUpdateListener) {
                mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
//                        int value = (int) animation.getAnimatedValue();
//                        for (int i = 0; i < 3; i++) {
//                            int color = i == 0 ? colorStart : i == 1 ? colorCenter : colorEnd;
//                            String A, R, G, B;
//                            StringBuffer sb = new StringBuffer();
//                            A = Integer.toHexString(value);
//                            R = Integer.toHexString(Color.red(color));
//                            G = Integer.toHexString(Color.green(color));
//                            B = Integer.toHexString(Color.blue(color));
//                            //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
//                            R = R.length() == 1 ? "0" + R : R;
//                            G = G.length() == 1 ? "0" + G : G;
//                            B = B.length() == 1 ? "0" + B : B;
//                            sb.append("0x");
//                            sb.append(A);
//                            sb.append(R);
//                            sb.append(G);
//                            sb.append(B);
//                            String string = sb.toString();
//                            if (i == 0) {
//                                colorStart = Color.parseColor(string);
//                            } else if (i == 1) {
//                                colorCenter = Color.parseColor(string);
//                            } else {
//                                colorEnd = Color.parseColor(string);
//                            }
//                        }

//                        int value = (int) animation.getAnimatedValue();
//                        colorStart = (colorStart & 0x00FFFFFF) | (value << 24);
//                        colorEnd = (colorEnd & 0x00FFFFFF) | (value << 24);
//                        colorCenter = (colorCenter & 0x00FFFFFF) | (value << 24);

                        int value = (int) animation.getAnimatedValue();
                        colorStart = Color.rgb(255, value, 255 - value);
                        colorEnd = Color.rgb(value, 0, 255 - value);

                        invalidate();
                    }
                };
            }
            mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
            mValueAnimator.start();
        } catch (Exception e) {
            LogUtil.log("GradientView => start => Exception " + e.getMessage());
        }
    }

    private void close() {
        try {
            if (null == mValueAnimator) throw new Exception("Warning: mValueAnimator null");
            mValueAnimator.end();
            mValueAnimator.cancel();
            mValueAnimator = null;
            mAnimatorUpdateListener = null;
            mAccelerateDecelerateInterpolator = null;
        } catch (Exception e) {
            LogUtil.log("GradientView => close => Exception " + e.getMessage());
        }
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = getContext().getApplicationContext().obtainStyledAttributes(attrs, R.styleable.GradientView);
            colorStart = typedArray.getColor(R.styleable.GradientView_ev_color_start, 0);
            colorEnd = typedArray.getColor(R.styleable.GradientView_ev_color_end, 0);
            colorCenter = typedArray.getColor(R.styleable.GradientView_ev_color_center, 0);
        } catch (Exception e) {
        }
        if (null != typedArray) {
            typedArray.recycle();
        }
        if (colorStart == 0) {
            colorStart = getResources().getColor(R.color.module_mediaplayer_color_aaf85a55);
        }
        if (colorEnd == 0) {
            colorEnd = getResources().getColor(R.color.module_mediaplayer_color_aaf85a55);
        }
        if (colorCenter == 0) {
            colorCenter = getResources().getColor(R.color.module_mediaplayer_color_f85a55);
        }
    }
}