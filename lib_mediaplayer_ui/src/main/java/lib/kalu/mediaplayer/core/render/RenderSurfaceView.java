package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

/**
 * 优点：可以在一个独立的线程中进行绘制，不会影响主线程；使用双缓冲机制，播放视频时画面更流畅
 * 缺点：Surface不在View hierachy中，它的显示也不受View的属性控制，所以不能进行平移，缩放等变换，
 * 也不能放在其它ViewGroup中。SurfaceView 不能嵌套使用。
 * <p>
 * SurfaceView双缓冲
 * 1.SurfaceView在更新视图时用到了两张Canvas，一张frontCanvas和一张backCanvas。
 * 2.每次实际显示的是frontCanvas，backCanvas存储的是上一次更改前的视图，当使用lockCanvas（）获取画布时，
 * 得到的实际上是backCanvas而不是正在显示的frontCanvas，之后你在获取到的backCanvas上绘制新视图，
 * 再unlockCanvasAndPost（canvas）此视图，那么上传的这张canvas将替换原来的frontCanvas作为新的frontCanvas，
 * 原来的frontCanvas将切换到后台作为backCanvas。
 */
public class RenderSurfaceView extends SurfaceView implements RenderApi {

    @Nullable
    private Handler mHandler = null;
    @Nullable
    private KernelApi mKernel;
    @Nullable
    private SurfaceHolder.Callback mSurfaceHolderCallback;

    public RenderSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MPLogUtil.log("RenderSurfaceView => onDetachedFromWindow => " + this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MPLogUtil.log("RenderSurfaceView => onAttachedToWindow => " + this);
    }

    @Override
    public void init() {
        setFocusable(false);
        setFocusableInTouchMode(false);
        addListener();
        //画布透明处理
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void addListener() {
        try {
            if (null != mSurfaceHolderCallback)
                throw new Exception("mSurfaceHolderCallback warning: " + mSurfaceHolderCallback);
            mSurfaceHolderCallback = new SurfaceHolder.Callback() {

                /**
                 * 创建的时候调用该方法
                 * @param holder                        holder
                 */
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    MPLogUtil.log("RenderSurfaceView => addListener => surfaceCreated => width = " + getWidth() + ", height = " + getHeight() + ", mKernel = " + mKernel + ", mHandler = " + mHandler + ", holder = " + holder + ", suface = " + holder.getSurface());
                    updateSurface(false);
                    addHandler();
                    sendMessage(1000);
                }

                /**
                 * 视图改变的时候调用方法
                 * @param holder
                 * @param format
                 * @param width
                 * @param height
                 */
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    MPLogUtil.log("RenderSurfaceView => addListener => surfaceChanged => width = " + width + ", height = " + height + ",surfaceChanged => " + this);
                }

                /**
                 * 销毁的时候调用该方法
                 * @param holder
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    MPLogUtil.log("RenderSurfaceView => addListener => surfaceDestroyed => " + this);
                    clearMessage();
                    updateSurface(true);
                }
            };
            getHolder().addCallback(mSurfaceHolderCallback);
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => addListener => " + e.getMessage());
        }
    }

    @Override
    public void release() {
        setZOrderOnTop(false);
        setZOrderMediaOverlay(false);

        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            Surface surface = surfaceHolder.getSurface();
            if (null == surface)
                throw new Exception("surface error: null");
            clearSurface(surface);
            surface.release();
            surface = null;
            surfaceHolder = null;
            MPLogUtil.log("RenderSurfaceView => release => Surface => succ");
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => release => Surface => " + e.getMessage());
        }

        try {
            if (null == mSurfaceHolderCallback)
                throw new Exception("mSurfaceHolderCallback error: null");
            getHolder().removeCallback(mSurfaceHolderCallback);
            mSurfaceHolderCallback = null;
            MPLogUtil.log("RenderSurfaceView => release => SurfaceHolderCallback => succ");
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => release => SurfaceHolderCallback => " + e.getMessage());
        }

        try {
            if (null == mHandler)
                throw new Exception("mHandler error: null");
            mHandler.removeMessages(9899);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
            MPLogUtil.log("RenderSurfaceView => release => Handler => succ");
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => release => Handler => " + e.getMessage());
        }
    }

    @Override
    public void setKernel(@NonNull KernelApi kernel) {
        this.mKernel = kernel;
    }

    @Override
    public String screenshot() {
        Context context = getContext();
        Bitmap bitmap = getDrawingCache();
        return saveBitmap(context, bitmap);
    }

    @Override
    public void updateBuffer(int delayMillis) {
        try {
            if (null == mHandler) {
                addHandler();
            }
            mHandler.removeMessages(9888);
            mHandler.removeMessages(9877);
            Message message = new Message();
            message.what = 9888;
            message.arg1 = delayMillis;
            mHandler.sendMessage(message);
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => updateBuffer => " + e.getMessage());
        }
    }

    @Override
    public boolean hasFocus() {
        return false;
    }

    @Override
    public boolean hasFocusable() {
        return false;
    }

    @Override
    public boolean hasExplicitFocusable() {
        return false;
    }

    @Override
    public boolean hasWindowFocus() {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    /************/

    /**
     * 记得一定要重新写这个方法，如果角度发生了变化，就重新绘制布局
     * 设置视频旋转角度
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        try {
            float v = getRotation();
            if (v == rotation)
                throw new Exception("rotation warning: " + rotation);
            super.setRotation(rotation);
            requestLayout();
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => setRotation => " + e.getMessage());
        }
    }

    /***************/

//    private void drawBitmap() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Canvas canvas = null;
//                try {
//                    SurfaceHolder holder = getHolder();
//                    if (null == holder)
//                        throw new Exception("holder warning: null");
//                    canvas = holder.lockCanvas();
//                    Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//                    bitmap.eraseColor(Color.RED);//填充颜色
//                    canvas.drawBitmap(bitmap, 0, 0, null);
//                } catch (Exception e) {
//                    MPLogUtil.log("RenderSurfaceView => drawBitmap => " + e.getMessage());
//                }
//                try {
//                    SurfaceHolder holder = getHolder();
//                    if (null == holder)
//                        throw new Exception("holder warning: null");
//                    if (null == canvas)
//                        throw new Exception("canvas warning: null");
//                    //手动try catch一下这个方法，让程序在4.3的手机上不至于崩溃，部分Android13也会崩溃
//                    getHolder().unlockCanvasAndPost(canvas);
//                } catch (Exception e) {
//                    MPLogUtil.log("RenderSurfaceView => drawBitmap => " + e.getMessage());
//                }
//            }
//        }).start();
//    }

    /***************/

    int mVideoWidth = 0;
    int mVideoHeight = 0;
    int mVideoScaleType = 0;
    int mVideoRotation = 0;

    @Override
    public void setVideoSize(@NonNull int videoWidth, @NonNull int videoHeight) {
        try {
            if (videoWidth <= 0 || videoHeight <= 0)
                throw new Exception("videoWidth error: " + videoWidth + ", videoHeight error: " + videoHeight);
            mVideoWidth = videoWidth;
            mVideoHeight = videoHeight;
            requestLayout();
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => setVideoSize => " + e.getMessage());
        }
    }

    @Override
    public void setVideoRotation(@PlayerType.RotationType.Value int videoRotation) {
        this.mVideoRotation = videoRotation;
        requestLayout();
    }

    @Override
    public void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        this.mVideoScaleType = scaleType;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int[] measureSpec = doMeasureSpec(widthMeasureSpec, heightMeasureSpec, mVideoScaleType, mVideoRotation, mVideoWidth, mVideoHeight);
            if (null == measureSpec || measureSpec.length != 2)
                throw new Exception("measureSpec error: " + measureSpec);
            int w = measureSpec[0];
            int h = measureSpec[1];
            int specW = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            int specH = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
            super.onMeasure(specW, specH);
//            setMeasuredDimension(measureSpec[0], measureSpec[1]);
//            getHolder().setFixedSize(measureSpec[0], measureSpec[1]);
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => onMeasure => " + e.getMessage());
        }
    }

    /*****/

    private void updateSurface(boolean clean) {
        try {
            if (null == mKernel)
                throw new Exception("mKernel warning: null");
            if (clean) {
                mKernel.setDisplay(null);
                mKernel.setSurface(null, 0, 0);
            } else {
                mKernel.setSurface(getHolder().getSurface(), 0, 0);
            }
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => updateSurface => " + e.getMessage());
        }
    }

    private void sendMessage(long delayMillis) {
        try {
            if (null == mHandler)
                throw new Exception("mHandler warning: null");
            mHandler.removeMessages(9899);
            mHandler.sendEmptyMessageDelayed(9899, delayMillis);
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => sendMessage => " + e.getMessage());
        }
    }

    private void clearMessage() {
        try {
            if (null == mHandler)
                throw new Exception("mHandler warning: null");
            mHandler.removeMessages(9899);
            mHandler.removeMessages(9888);
            mHandler.removeMessages(9877);
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => clearMessage => " + e.getMessage());
        }
    }

    private void addHandler() {
        try {
            if (null != mHandler)
                throw new Exception("mHandler warning: " + mHandler);
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 9899) {
                        if (null != mKernel) {
                            mKernel.onUpdateTimeMillis();
                            mHandler.sendEmptyMessageDelayed(9899, 100);
                        }
                    } else if (msg.what == 9888) {
                        if (null != mKernel) {
                            mKernel.onUpdateBuffer(PlayerType.StateType.STATE_BUFFERING_START);
                            mHandler.sendEmptyMessageDelayed(9877, msg.arg1);
                        }
                    } else if (msg.what == 9877) {
                        if (null != mKernel) {
                            mKernel.onUpdateBuffer(PlayerType.StateType.STATE_BUFFERING_STOP);
                        }
                    }
                }
            };
        } catch (Exception e) {
            MPLogUtil.log("RenderSurfaceView => addHandler => " + e.getMessage());
        }
    }
}
