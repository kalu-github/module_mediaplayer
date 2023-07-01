package lib.kalu.mediaplayer.core.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

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
     * 设置视频宽高
     *
     * @param videoWidth  宽
     * @param videoHeight 高
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 设置视频旋转角度
     *
     * @param degree 角度值
     */
    void setVideoRotation(int degree);

    /**
     * 设置screen scale type
     *
     * @param scaleType 类型
     */
    void setScaleType(@PlayerType.ScaleType.Value int scaleType);

    /**
     * 截图
     *
     * @return
     */
    String screenshot();



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
}