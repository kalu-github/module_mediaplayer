package lib.kalu.mediaplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;



import java.io.File;
import java.io.FileOutputStream;

public final class BitmapUtil {

    public static String saveDrawable( Context context,  Drawable drawable) {
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            return saveBitmap(context, bitmap);
        } catch (Exception e) {
            MPLogUtil.log("BitmapUtil => saveDrawable => " + e.getMessage());
            return null;
        }
    }

    public static String saveBitmap( Context context,  Bitmap bitmap) {
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
            MPLogUtil.log("BitmapUtil => saveBitmap => " + e.getMessage());
            return null;
        }
    }

    public static String saveScreenshot( Context context,  Bitmap bitmap) {
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
            MPLogUtil.log("BitmapUtil => saveBitmap => " + e.getMessage());
            return null;
        }
    }
}
