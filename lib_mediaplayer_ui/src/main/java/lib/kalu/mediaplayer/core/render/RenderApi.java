package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.io.File;
import java.io.FileOutputStream;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public interface RenderApi {

    /**
     *
     */
    void init();

    void addListener();

    /**
     * 释放资源
     */
    void releaseListener();

//    /**
//     * 获取真实的RenderView
//     *
//     * @return view
//     */
//    View getReal();

    void setLayoutParams(@NonNull ViewGroup.LayoutParams params);

    void setScaleX(float v);

    /**
     * 关联AbstractPlayer
     *
     * @param player player
     */
    void setKernel(@NonNull KernelApi player);

    /**
     * 截图
     *
     * @return
     */
    String screenshot();


    /**
     * 清屏
     */
    void clearCanvas();

    /**
     * 刷屏
     */
    void updateCanvas();

    /**
     *
     */
    void updateBuffer(int delayMillis);

    /******************/

    int[] mVideoWidth = new int[1];
    int[] mVideoHeight = new int[1];
    int[] mVideoScaleType = new int[1];
    int[] mVideoRotation = new int[1];

    /**
     * 设置视频旋转角度
     *
     * @param videoRotation 角度值
     */
    default void setVideoRotation(@PlayerType.RotationType.Value int videoRotation) {
        mVideoRotation[0] = videoRotation;
    }

    default void setVideoSize(int videoWidth, int videoHeight) {
        try {
            if (videoWidth <= 0 || videoHeight <= 0)
                throw new Exception("videoWidth error: " + videoWidth + ", videoHeight error: " + videoHeight);
            mVideoWidth[0] = videoWidth;
            mVideoHeight[0] = videoHeight;
        } catch (Exception e) {
            MPLogUtil.log("RenderApi => setVideoSize => " + e.getMessage());
        }
    }

    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        mVideoScaleType[0] = scaleType;
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     * 借鉴于网络
     */
    default int[] doMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {

        if (mVideoRotation[0] == 90 || mVideoRotation[0] == 270) {
            // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        int measureWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        MPLogUtil.log("RenderApi => doMeasureSpec => measureWidth => step1 => measureWidth = " + measureWidth + ", measureHeight = " + measureHeight);

        if (mVideoHeight[0] == 0 || mVideoWidth[0] == 0) {
        } else {
            switch (mVideoScaleType[0]) {
                // 默认类型
                default:
                    if (mVideoWidth[0] * measureHeight < measureWidth * mVideoHeight[0]) {
                        measureWidth = measureHeight * mVideoWidth[0] / mVideoHeight[0];
                    } else if (mVideoWidth[0] * measureHeight > measureWidth * mVideoHeight[0]) {
                        measureHeight = measureWidth * mVideoHeight[0] / mVideoWidth[0];
                    }
                    break;
                // 裁剪类型
                case PlayerType.ScaleType.SCREEN_SCALE_CENTER_CROP:
                    if (mVideoWidth[0] * measureHeight > measureWidth * mVideoHeight[0]) {
                        measureWidth = measureHeight * mVideoWidth[0] / mVideoHeight[0];
                    } else {
                        measureHeight = measureWidth * mVideoHeight[0] / mVideoWidth[0];
                    }
                    break;
                // 4:3
                case PlayerType.ScaleType.SCREEN_SCALE_4_3:
                    if (measureHeight > measureWidth / 4 * 3) {
                        measureHeight = measureWidth / 4 * 3;
                    } else {
                        measureWidth = measureHeight / 3 * 4;
                    }
                    break;
                // 16:9
                case PlayerType.ScaleType.SCREEN_SCALE_16_9:
                    if (measureHeight > measureWidth / 16 * 9) {
                        measureHeight = measureWidth / 16 * 9;
                    } else {
                        measureWidth = measureHeight / 9 * 16;
                    }
                    break;
                // 原始类型，指视频的原始类型
                case PlayerType.ScaleType.SCREEN_SCALE_ORIGINAL:
                    measureWidth = mVideoWidth[0];
                    measureHeight = mVideoHeight[0];
                    break;
                //充满整个控件视图
                case PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT:
                    measureWidth = widthMeasureSpec;
                    measureHeight = heightMeasureSpec;
                    break;
            }
        }
        MPLogUtil.log("RenderApi => doMeasureSpec => measureWidth => step2 => measureWidth = " + measureWidth + ", measureHeight = " + measureHeight);
        return new int[]{measureWidth, measureHeight};
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
            MPLogUtil.log("RenderApi => saveBitmap => " + e.getMessage());
            return null;
        }
    }
}