package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.LogUtil;


/**
 * <pre>
 *     desc  : 重写TextureView，适配视频的宽高和旋转
 *     revise: 1.继承View，具有view的特性，比如移动，旋转，缩放，动画等变化。支持截图
 *             8.必须在硬件加速的窗口中使用，占用内存比SurfaceView高，在5.0以前在主线程渲染，5.0以后有单独的渲染线程。
 * </pre>
 */
public class VideoRenderTextureView extends TextureView implements VideoRenderApi {

    @Nullable
    private VideoKernelApi mKernel;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private SurfaceTextureListener mSurfaceTextureListener;

    public VideoRenderTextureView(Context context) {
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
//        setDrawingCacheEnabled(true);
        setFocusable(false);
        setFocusableInTouchMode(false);
        addListener();
    }

    @Override
    public void addListener() {
        mSurfaceTextureListener = new SurfaceTextureListener() {
            /**
             * SurfaceTexture准备就绪
             * @param surfaceTexture            surface
             * @param width                     WIDTH
             * @param height                    HEIGHT
             */
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                LogUtil.log("VideoRenderTextureView => onSurfaceTextureAvailable => " + this);
//                VideoRenderTextureView.this.mSurfaceTexture = surfaceTexture;
//                setSurfaceTexture(VideoRenderTextureView.this.mSurfaceTexture);
//                setSurface(true);
                mSurfaceTexture = surfaceTexture;
                setSurface(true);
            }

            /**
             * SurfaceTexture即将被销毁
             * @param surface                   surface
             */
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                LogUtil.log("VideoRenderTextureView => onSurfaceTextureDestroyed => " + this);
                return false;
            }

            /**
             * SurfaceTexture缓冲大小变化
             * @param surface                   surface
             * @param width                     WIDTH
             * @param height                    HEIGHT
             */
            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                LogUtil.log("VideoRenderTextureView => onSurfaceTextureSizeChanged => " + this);
            }

            /**
             * SurfaceTexture通过updateImage更新
             * @param surface                   surface
             */
            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                LogUtil.log("VideoRenderTextureView => onSurfaceTextureUpdated => " + this);
            }
        };
        setSurfaceTextureListener(mSurfaceTextureListener);
    }

    @Override
    public void setSurface(boolean release) {
        if (release) {
            if (mSurface != null) {
                mSurface.release();
                mSurface = null;
            }
            mSurface = new Surface(mSurfaceTexture);
        }
        if (mKernel != null) {
            int w = getWidth();
            int h = getHeight();
            mKernel.setSurface(mSurface, w, h);
        }
    }

    @Override
    public void reset() {
        LogUtil.log("VideoRenderTextureView => reset =>");
        setSurface(false);
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (null != mSurfaceTextureListener) {
            mSurfaceTextureListener = null;
        }
    }

    @Override
    public void setKernel(VideoKernelApi player) {
        this.mKernel = player;
    }

    @Override
    public VideoKernelApi getKernel() {
        return this.mKernel;
    }

    @Override
    public String screenshot(String url, long position) {
        Context context = getContext();
        Bitmap bitmap = getBitmap();
//        //设置缓存
//        setDrawingCacheEnabled(true);
//        buildDrawingCache();
//        //从缓存中获取当前屏幕的图片
//        Context context = getContext();
//        Bitmap bitmap = getDrawingCache();
        return saveBitmap(context, bitmap);
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

    /****************/

    /************/

    /**
     * 记得一定要重新写这个方法，如果角度发生了变化，就重新绘制布局
     * 设置视频旋转角度
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    /***************/

    int mVideoWidth = 0;
    int mVideoHeight = 0;
    int mVideoScaleType = 0;
    int mVideoRotation = 0;

    @Override
    public void setVideoFormat(int videoWidth, int videoHeight, int videoRotation) {
        LogUtil.log("VideoRenderTextureView => setVideoFormat => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", videoRotation = " + videoRotation);

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
        LogUtil.log("VideoRenderTextureView => setVideoSize => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);

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
        LogUtil.log("VideoRenderTextureView => setVideoRotation => videoRotation = " + videoRotation);
        if (mVideoRotation != videoRotation) {
            this.mVideoRotation = videoRotation;
            requestLayout();
        }
    }

    @Override
    public void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        LogUtil.log("VideoRenderTextureView => setVideoScaleType => scaleType = " + scaleType);
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
            LogUtil.log("VideoRenderTextureView => onMeasure => width = " + width + ", height = " + height);
            int specW = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int specH = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(specW, specH);
//            getHolder().setFixedSize(measureSpec[0], measureSpec[1]);
        } catch (Exception e) {
            LogUtil.log("VideoRenderTextureView => onMeasure => " + e.getMessage());
        }
    }
}