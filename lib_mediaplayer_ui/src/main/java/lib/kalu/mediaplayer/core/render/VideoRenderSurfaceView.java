package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
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
public class VideoRenderSurfaceView extends SurfaceView implements VideoRenderApi {

    @Nullable
    private Handler mHandler = null;
    @Nullable
    private VideoKernelApi mKernel;
    @Nullable
    private SurfaceHolder.Callback mSurfaceHolderCallback;

    public VideoRenderSurfaceView(Context context) {
        super(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
        MPLogUtil.log("VideoRenderSurfaceView => onDetachedFromWindow => " + this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
        MPLogUtil.log("VideoRenderSurfaceView => onAttachedToWindow => " + this);
    }

    @Override
    public void init() {
        setFocusable(false);
        setFocusableInTouchMode(false);
        setWillNotDraw(true); //禁止onDraw
        setZOrderOnTop(true); //画布透明处理
        setZOrderMediaOverlay(true); //画面置顶
//        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        addListener();
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
                    MPLogUtil.log("VideoRenderSurfaceView => addListener => surfaceCreated => width = " + getWidth() + ", height = " + getHeight() + ", mKernel = " + mKernel + ", mHandler = " + mHandler + ", holder = " + holder + ", suface = " + holder.getSurface());
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
                    MPLogUtil.log("VideoRenderSurfaceView => addListener => surfaceChanged => width = " + width + ", height = " + height + ",surfaceChanged => " + this);
                }

                /**
                 * 销毁的时候调用该方法
                 * @param holder
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    MPLogUtil.log("VideoRenderSurfaceView => addListener => surfaceDestroyed => " + this);
                    clearMessage();
                    updateSurface(true);
                }
            };
            getHolder().addCallback(mSurfaceHolderCallback);
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderSurfaceView => addListener => " + e.getMessage());
        }
    }

    @Override
    public void release() {

        // step1
        try {
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            Surface surface = surfaceHolder.getSurface();
            if (null == surface)
                throw new Exception("surface error: null");
//            clearSurface(surface);
            surface.release();
            MPLogUtil.log("VideoRenderSurfaceView => release => removeSurface => succ");
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderSurfaceView => release => removeSurface => " + e.getMessage());
        }

        // step2
        try {
            if (null == mSurfaceHolderCallback)
                throw new Exception("mSurfaceHolderCallback error: null");
            SurfaceHolder surfaceHolder = getHolder();
            if (null == surfaceHolder)
                throw new Exception("surfaceHolder error: null");
            surfaceHolder.removeCallback(mSurfaceHolderCallback);
            mSurfaceHolderCallback = null;
            MPLogUtil.log("VideoRenderSurfaceView => release => removeCallback => succ");
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderSurfaceView => release => removeCallback => " + e.getMessage());
        }

        // step3
        try {
            if (null == mHandler)
                throw new Exception("mHandler error: null");
            mHandler.removeMessages(9899);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
            MPLogUtil.log("VideoRenderSurfaceView => release => Handler => succ");
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderSurfaceView => release => Handler => " + e.getMessage());
        }
    }

    @Override
    public void setKernel(VideoKernelApi kernel) {
        this.mKernel = kernel;
    }


    @Override
    public String screenshot(String url, long position) {
        return null;
//        try {
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever.setDataSource(url);
//            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(position);
//            mediaMetadataRetriever.release();
//            return saveBitmap(getContext().getApplicationContext(), bitmap);
//        } catch (Exception e) {
//            return null;
//        }

//        Canvas canvasH = getHolder().lockCanvas(null);//获取画布
//
//        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        paint.setColor(Color.BLUE);
//        canvas.drawRect(new RectF(0, 0, getWidth(), getHeight()), paint);
//
//        getHolder().unlockCanvasAndPost(canvasH);//解锁画布，提交画好的图像

//        return saveBitmap(getContext().getApplicationContext(), bitmap);

//        Context context = getContext();
//        Bitmap bitmap = getDrawingCache();
//        return saveBitmap(context, bitmap);
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
            MPLogUtil.log("VideoRenderSurfaceView => updateBuffer => " + e.getMessage());
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
            MPLogUtil.log("VideoRenderSurfaceView => setRotation => " + e.getMessage());
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
//                    MPLogUtil.log("VideoRenderSurfaceView => drawBitmap => " + e.getMessage());
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
//                    MPLogUtil.log("VideoRenderSurfaceView => drawBitmap => " + e.getMessage());
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
    public void setVideoFormat(int videoWidth, int videoHeight, int videoRotation) {
        MPLogUtil.log("VideoRenderSurfaceView => setVideoFormat => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", videoRotation = " + videoRotation);

        boolean update = false;
        if (mVideoRotation != videoRotation) {
            update = true;
            this.mVideoRotation = videoRotation;
        }

        if (videoWidth != 0 && mVideoWidth != videoWidth) {
            update = true;
            this.mVideoWidth = videoWidth;
        }
        if (videoHeight != 0 && mVideoHeight != videoHeight) {
            update = true;
            this.mVideoHeight = videoHeight;
        }
        if (update) {
            requestLayout();
        }
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        MPLogUtil.log("VideoRenderSurfaceView => setVideoSize => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);

        boolean update = false;
        if (videoWidth != 0 && mVideoWidth != videoWidth) {
            update = true;
            this.mVideoWidth = videoWidth;
        }
        if (videoHeight != 0 && mVideoHeight != videoHeight) {
            update = true;
            this.mVideoHeight = videoHeight;
        }
        if (update) {
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(@PlayerType.RotationType.Value int videoRotation) {
        MPLogUtil.log("VideoRenderSurfaceView => setVideoRotation => videoRotation = " + videoRotation);
        if (mVideoRotation != videoRotation) {
            this.mVideoRotation = videoRotation;
            requestLayout();
        }
    }

    @Override
    public void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        MPLogUtil.log("VideoRenderSurfaceView => setVideoScaleType => scaleType = " + scaleType);
        if (mVideoScaleType != scaleType) {
            this.mVideoScaleType = scaleType;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
            int[] measureSpec = doMeasureSpec(screenWidth, screenHeight, mVideoWidth, mVideoHeight, mVideoScaleType, mVideoRotation);
            if (null == measureSpec || measureSpec.length != 2)
                throw new Exception("measureSpec error: " + measureSpec);
            int width = measureSpec[0];
            int height = measureSpec[1];
            int specW = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int specH = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(specW, specH);
//            getHolder().setFixedSize(measureSpec[0], measureSpec[1]);
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderSurfaceView => onMeasure => " + e.getMessage());
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
            MPLogUtil.log("VideoRenderSurfaceView => updateSurface => " + e.getMessage());
        }
    }

    private void sendMessage(long delayMillis) {
        try {
            if (null == mHandler)
                throw new Exception("mHandler warning: null");
            mHandler.removeMessages(9899);
            mHandler.sendEmptyMessageDelayed(9899, delayMillis);
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderSurfaceView => sendMessage => " + e.getMessage());
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
            MPLogUtil.log("VideoRenderSurfaceView => clearMessage => " + e.getMessage());
        }
    }

    private void addHandler() {
        try {
            if (null != mHandler)
                throw new Exception("mHandler warning: " + mHandler);
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
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
            MPLogUtil.log("VideoRenderSurfaceView => addHandler => " + e.getMessage());
        }
    }
}
