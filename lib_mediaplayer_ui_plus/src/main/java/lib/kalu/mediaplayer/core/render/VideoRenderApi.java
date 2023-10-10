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
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;

import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public interface VideoRenderApi {

    void init();

    void addListener();

    void release();

    void setLayoutParams(@NonNull ViewGroup.LayoutParams params);

    void setScaleX(float v);

    /**
     * 关联AbstractPlayer
     *
     * @param player player
     */
    void setKernel(@NonNull VideoKernelApi player);

    /**
     * 截图
     *
     * @return
     */
    String screenshot();

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
    default int[] doMeasureSpec(@NonNull int widthMeasureSpec,
                                @NonNull int heightMeasureSpec,
                                @PlayerType.ScaleType.Value int videoScaleType,
                                @PlayerType.RotationType.Value int videoRotation,
                                @NonNull int videoWidth,
                                @NonNull int videoHeight) {

        if (videoScaleType == 0) {
            videoScaleType = PlayerManager.getInstance().getConfig().getScaleType();
        }

        if (videoRotation == 90 || videoRotation == 270) {
            // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        int screenWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        MPLogUtil.log("VideoRenderApi => doMeasureSpec => measureWidth => screenWidth = " + screenWidth + ", screenHeight = " + screenHeight + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", videoScaleType = " + videoScaleType + ", videoRotation = " + videoRotation);

        try {
            // 填充屏幕, 裁剪
            if (videoWidth > 0 && videoHeight > 0 && videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_SCREEN_CROP) {
                if (videoWidth * screenHeight > screenWidth * videoHeight) {
                    int newScreenWidth = screenHeight * videoWidth / videoHeight;
                    return new int[]{newScreenWidth, screenHeight};
                } else {
                    int newScreenHeight = screenWidth * videoHeight / videoWidth;
                    return new int[]{screenWidth, newScreenHeight};
                }
            }
            // 视频尺寸
            else if (videoWidth > 0 && videoHeight > 0 && videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_VIDEO_ORIGINAL) {
                return new int[]{videoWidth, videoHeight};
            }
            // 16:9
            else if (videoWidth > 0 && videoHeight > 0 && videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_16_9) {
                if (screenHeight > screenWidth / 16 * 9) {
                    int newScreenHeight = screenWidth / 16 * 9;
                    return new int[]{screenWidth, newScreenHeight};
                } else {
                    int newScreenWidth = screenHeight / 9 * 16;
                    return new int[]{newScreenWidth, screenHeight};
                }
            }
            // 4:3
            else if (videoWidth > 0 && videoHeight > 0 && videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_4_3) {
                if (screenHeight > screenWidth / 4 * 3) {
                    int newScreenHeight = screenWidth / 4 * 3;
                    return new int[]{screenWidth, newScreenHeight};
                } else {
                    int newScreenWidth = screenHeight / 3 * 4;
                    return new int[]{newScreenWidth, screenHeight};
                }
            }
            // 错误1
            else if (videoScaleType == PlayerType.ScaleType.SCREEN_SCALE_SCREEN_MATCH) {
                throw new Exception("not need crop");
            }
            // 错误2
            else if (videoWidth < 0 || videoHeight < 0) {
                throw new Exception("videoWidth error: " + videoWidth + ", videoHeight error: " + videoHeight);
            }
            // 错误3
            else {
                throw new Exception("not find");
            }
        } catch (Exception e) {
            return new int[]{screenWidth, screenHeight};
        }
    }

    default String saveBitmap(@NonNull Context context, @NonNull Bitmap bitmap) {
        try {
            // 1
            File dir = context.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 2
            File file = new File(dir, "screenshot.jpg");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            // 3
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            // 4
            bitmap.recycle();
            bitmap = null;
            // 5
            String path = file.getAbsolutePath();
            return path;
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