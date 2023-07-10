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

    /**
     * 设置视频宽高
     *
     * @param width  宽
     * @param height 高
     */
    default void setVideoSize(int width, int height) {
        mVideoWidth[0] = width;
        mVideoHeight[0] = height;
    }


    /**
     * 设置screen scale type
     *
     * @param scaleType 类型
     */
    default void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        mVideoScaleType[0] = scaleType;
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     * 借鉴于网络
     */
    default int[] doMeasureSpec(int widthMeasureSpec, int heightMeasureSpec) {

        MPLogUtil.log("RenderApi => doMeasureSpec => mVideoRotation = " + mVideoRotation[0] + ", mVideoScaleType = " + mVideoScaleType[0] + ", mVideoWidth = " + mVideoWidth[0] + ", mVideoHeight = " + mVideoHeight[0]);

        if (mVideoRotation[0] == 90 || mVideoRotation[0] == 270) {
            // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);


        try {
            if (mVideoHeight[0] == 0 || mVideoWidth[0] == 0)
                throw new Exception("mVideoHeight error: " + mVideoHeight + ", mVideoWidth = " + mVideoWidth);
            switch (mVideoScaleType[0]) {
                // 4:3
                case PlayerType.ScaleType.SCREEN_SCALE_4_3:
                    if (height > width / 4 * 3) {
                        height = width / 4 * 3;
                    } else {
                        width = height / 3 * 4;
                    }
                    break;
                // 16:9
                case PlayerType.ScaleType.SCREEN_SCALE_16_9:
                    if (height > width / 16 * 9) {
                        height = width / 16 * 9;
                    } else {
                        width = height / 9 * 16;
                    }
                    break;
                // 原始类型，指视频的原始类型
                case PlayerType.ScaleType.SCREEN_SCALE_ORIGINAL:
                    width = mVideoWidth[0];
                    height = mVideoHeight[0];
                    break;
                //默认正常类型
                case PlayerType.ScaleType.SCREEN_SCALE_DEFAULT:
                default:
                    if (mVideoWidth[0] * height < width * mVideoHeight[0]) {
                        width = height * mVideoWidth[0] / mVideoHeight[0];
                    } else if (mVideoWidth[0] * height > width * mVideoHeight[0]) {
                        height = width * mVideoHeight[0] / mVideoWidth[0];
                    }
                    break;
                //充满整个控件视图
                case PlayerType.ScaleType.SCREEN_SCALE_MATCH_PARENT:
                    width = widthMeasureSpec;
                    height = heightMeasureSpec;
                    break;
                //剧中裁剪类型
                case PlayerType.ScaleType.SCREEN_SCALE_CENTER_CROP:
                    if (mVideoWidth[0] * height > width * mVideoHeight[0]) {
                        width = height * mVideoWidth[0] / mVideoHeight[0];
                    } else {
                        height = width * mVideoHeight[0] / mVideoWidth[0];
                    }
                    break;
            }
            return new int[]{width, height};
        } catch (Exception e) {
            MPLogUtil.log("RenderApi => doMeasureSpec => " + e.getMessage(), e);
            return new int[]{width, height};
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
            MPLogUtil.log("saveBitmap => " + e.getMessage(), e);
            return null;
        }
    }
}