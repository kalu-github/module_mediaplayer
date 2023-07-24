package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.io.File;
import java.io.FileOutputStream;

import lib.kalu.mediaplayer.config.player.PlayerManager;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public interface RenderApi {

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
            videoScaleType = PlayerManager.getInstance().getConfig().getVideoScaleType();
        }

        if (videoRotation == 90 || videoRotation == 270) {
            // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        int screenWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        MPLogUtil.log("RenderApi => doMeasureSpec => measureWidth => screenWidth = " + screenWidth + ", screenHeight = " + screenHeight + ", videoWidth = " + videoWidth + ", videoHeight = " + videoHeight + ", videoScaleType = " + videoScaleType + ", videoRotation = " + videoRotation);

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
            MPLogUtil.log("RenderApi => saveBitmap => " + e.getMessage());
            return null;
        }
    }
}