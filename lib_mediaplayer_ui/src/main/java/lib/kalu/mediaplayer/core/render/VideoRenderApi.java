package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;


import java.io.File;
import java.io.FileOutputStream;

import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;


public interface VideoRenderApi {

    void init();

    void addListener();

    void release();

    void setLayoutParams(ViewGroup.LayoutParams params);

    void setScaleX(float v);

    /**
     * 关联AbstractPlayer
     *
     * @param player player
     */
    void setKernel(VideoKernelApi player);

    /**
     * 截图
     *
     * @return
     */
    String screenshot(String url, long position);

    /**
     *
     */
    void updateBuffer(int delayMillis);

    /******************/

    /**
     * 设置视频旋转角度
     *
     * @param videoRotation 角度值
     */
    void setVideoRotation(@PlayerType.RotationType.Value int videoRotation);

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType);

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     * 借鉴于网络
     */
    default int[] doMeasureSpec(int screenWidth, int screenHeight, int videoWidth, int videoHeight,
                                @PlayerType.ScaleType.Value int videoScaleType,
                                @PlayerType.RotationType.Value int videoRotation) {
        MPLogUtil.log("VideoRenderApi => doMeasureSpec => screenWidth = " + screenWidth + ", screenHeight = " + screenHeight + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", videoScaleType = " + videoScaleType + ", videoRotation = " + videoRotation);

        if (videoScaleType == 0) {
            videoScaleType = PlayerManager.getInstance().getConfig().getScaleType();
        }

        // 软解码时处理旋转信息，交换宽高
        if (videoRotation == 90 || videoRotation == 270) {
//            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
//            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
//            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        try {
            // 裁剪显示
            if (videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_SCREEN_CROP) {
                if (videoWidth > 0 && videoHeight > 0) {
                    int v1 = videoWidth * screenHeight;
                    int v2 = screenWidth * videoHeight;
                    if (v1 > v2) {
                        int realH = screenHeight;
                        int realW = screenHeight * videoWidth / videoHeight;
                        return new int[]{realW, realH};
                    } else {
                        int realW = screenWidth;
                        int realH = screenWidth * videoHeight / videoWidth;
                        return new int[]{realW, realH};
                    }
                }
            }
            // 视频尺寸
            else if (videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_VIDEO_ORIGINAL) {
                if (videoWidth > 0 && videoHeight > 0) {
                    return new int[]{videoWidth, videoHeight};
                }
            }
            // 16:9
            else if (videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_16_9) {
                if (videoWidth > 0 && videoHeight > 0) {
                    double realH = ((double) screenWidth) / 16 * 9;
                    if (screenHeight > realH) {
                        return new int[]{screenWidth, (int) realH};
                    } else {
                        double realW = ((double) screenHeight) / 9 * 16;
                        return new int[]{(int) realW, screenHeight};
                    }
                }
            }
            // 4:3
            else if (videoWidth > 0 && videoHeight > 0 && videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_4_3) {
                if (videoWidth > 0 && videoHeight > 0) {
                    double realH = ((double) screenWidth) / 4 * 3;
                    if (screenHeight > realH) {
                        return new int[]{screenWidth, (int) realH};
                    } else {
                        double realW = ((double) screenHeight) / 3 * 4;
                        return new int[]{(int) realW, screenHeight};
                    }
                }
            }
            // 填充屏幕 => 竖直方向拉伸至屏幕
            else if (videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_SCREEN_MATCH) {
                double v1 = ((double) screenWidth) / ((double) screenHeight);
                double v2 = ((double) videoWidth) / ((double) videoWidth);

                // 视频宽高比>屏幕宽高比, 以屏幕宽度为基准缩放
                if (v2 > v1) {
                    double realW = ((double) screenWidth);
                    double realH = (((double) screenWidth) / ((double) videoWidth)) * ((double) videoHeight);
                    return new int[]{(int) realW, (int) realH};
                }
                // 视频宽高比<屏幕宽高比, 以屏幕高度为基准缩放
                else if (v2 < v1) {
                    double realH = ((double) screenHeight);
                    double realW = (((double) screenHeight) / ((double) videoHeight)) * ((double) videoWidth);
                    return new int[]{(int) realW, (int) realH};
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return new int[]{screenWidth, screenHeight};
        }
    }

    default String saveBitmap(Context context, Bitmap bitmap) {
        try {
            // 1
            File dir = context.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 2
            File screenshotDir = new File(dir, "mp_screenshot");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            // 3
            File[] files = screenshotDir.listFiles();
            for (File file : files) {
                if (null == file)
                    continue;
                if (file.exists()) {
                    file.delete();
                }
            }
            // 4
            String screenshotName = System.nanoTime() + ".jpg";
            File screenshotFile = new File(screenshotDir, screenshotName);
            if (screenshotFile.exists()) {
                screenshotFile.delete();
            }
            screenshotFile.createNewFile();
            // 5
            FileOutputStream fos = new FileOutputStream(screenshotFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            // 6
            bitmap.recycle();
            bitmap = null;
            // 5
            return screenshotFile.getAbsolutePath();
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderApi => saveBitmap => " + e.getMessage());
            return null;
        }
    }

    /********/

    default void clearSurface(Surface surface) {
        try {
            if (null == surface)
                throw new Exception("surface error: null");
            Paint paint = new Paint();
            paint.setColor(0xff000000);
            Canvas canvas = surface.lockCanvas(null);
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            canvas.drawRect(0, 0, 0 + canvas.getWidth(), 0 + canvas.getHeight(), paint);
            surface.unlockCanvasAndPost(canvas);
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderApi => clearSurface => " + e.getMessage());
        }
    }

    default void clearSurfaceGLES(Surface surface) {
        try {
            if (null == surface)
                throw new Exception("surface error: null");
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
                throw new Exception("sdkVersion warning: " + android.os.Build.VERSION.SDK_INT);
            EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            int[] version = new int[2];
            EGL14.eglInitialize(display, version, 0, version, 1);
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_NONE, 0,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(display, attribList, 0, configs, 0, configs.length, numConfigs, 0);

            EGLConfig config = configs[0];
            EGLContext context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, new int[]{
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            }, 0);

            EGLSurface eglSurface = EGL14.eglCreateWindowSurface(display, config, surface,
                    new int[]{
                            EGL14.EGL_NONE
                    }, 0);

            EGL14.eglMakeCurrent(display, eglSurface, eglSurface, context);
            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            EGL14.eglSwapBuffers(display, eglSurface);
            EGL14.eglDestroySurface(display, eglSurface);
            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(display, context);
            EGL14.eglTerminate(display);
        } catch (Exception e) {
            MPLogUtil.log("VideoRenderApi => clearSurfaceGLES => " + e.getMessage());
        }
    }
}