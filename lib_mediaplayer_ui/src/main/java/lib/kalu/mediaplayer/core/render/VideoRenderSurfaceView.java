package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;

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
    private VideoKernelApi mKernel;
    @Nullable
    private SurfaceHolder.Callback mSurfaceHolderCallback;

    public VideoRenderSurfaceView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startUpdateProgress();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopUpdateProgress();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            startUpdateProgress();
        } else {
            stopUpdateProgress();
        }
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
//                    LogUtil.log("VideoRenderSurfaceView => addListener => surfaceCreated => width = " + getWidth() + ", height = " + getHeight() + ", mKernel = " + mKernel + ", mHandler = " + mHandler + ", holder = " + holder + ", suface = " + holder.getSurface());
                    setSurface(false);
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
                    LogUtil.log("VideoRenderSurfaceView => addListener => surfaceChanged => width = " + width + ", height = " + height + ",surfaceChanged => " + this);
                }

                /**
                 * 销毁的时候调用该方法
                 * @param holder
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    LogUtil.log("VideoRenderSurfaceView => addListener => surfaceDestroyed => " + this);
                    setSurface(true);
                }
            };
            getHolder().addCallback(mSurfaceHolderCallback);
        } catch (Exception e) {
            LogUtil.log("VideoRenderSurfaceView => addListener => " + e.getMessage());
        }
    }

    @Override
    public void setSurface(boolean release) {
        try {
            if (null == mKernel)
                throw new Exception("mKernel warning: null");
            if (release) {
//                mKernel.setDisplay(null);
                mKernel.setSurface(null, 0, 0);
            } else {
                mKernel.setSurface(getHolder().getSurface(), 0, 0);
            }
        } catch (Exception e) {
            LogUtil.log("VideoRenderSurfaceView => setSurface => " + e.getMessage());
        }
    }

    @Override
    public void reset() {
        LogUtil.log("VideoRenderSurfaceView => reset =>");
        setSurface(false);
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
            LogUtil.log("VideoRenderSurfaceView => release => removeSurface => succ");
        } catch (Exception e) {
            LogUtil.log("VideoRenderSurfaceView => release => removeSurface => " + e.getMessage());
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
            LogUtil.log("VideoRenderSurfaceView => release => removeCallback => succ");
        } catch (Exception e) {
            LogUtil.log("VideoRenderSurfaceView => release => removeCallback => " + e.getMessage());
        }
    }

    @Override
    public void setKernel(VideoKernelApi kernel) {
        this.mKernel = kernel;
    }

    @Override
    public VideoKernelApi getKernel() {
        return this.mKernel;
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
            LogUtil.log("VideoRenderSurfaceView => setRotation => " + e.getMessage());
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
        LogUtil.log("VideoRenderSurfaceView => setVideoFormat => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", videoRotation = " + videoRotation);

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
        LogUtil.log("VideoRenderSurfaceView => setVideoSize => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);

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
        LogUtil.log("VideoRenderSurfaceView => setVideoRotation => videoRotation = " + videoRotation);
        if (mVideoRotation != videoRotation) {
            this.mVideoRotation = videoRotation;
            requestLayout();
        }
    }

    @Override
    public void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        LogUtil.log("VideoRenderSurfaceView => setVideoScaleType => scaleType = " + scaleType);
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
            LogUtil.log("VideoRenderSurfaceView => onMeasure => " + e.getMessage());
        }
    }
}
