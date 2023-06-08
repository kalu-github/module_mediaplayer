package lib.kalu.mediaplayer.core.player;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import lib.kalu.mediaplayer.util.MPLogUtil;
 interface PlayerApiCache {

    default boolean setCache(Context context, String key, String value) {
        FileOutputStream out = null;
        BufferedWriter writer = null;

        boolean result;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("user@");
            String string1 = key.toString();
            String toLowerCase = string1.toLowerCase();
            builder.append(toLowerCase);
            builder.append("@");
            String string = builder.toString();
            out = context.openFileOutput(string, 0);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(value);
            writer.flush();
            result = true;
        } catch (Exception var22) {
            result = false;
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (Exception var21) {
                }
            }

            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception var20) {
                }
            }

        }

        return result;
    }

    default String getCache(Context context, String key) {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();

        try {
            StringBuilder builder = new StringBuilder();
            builder.append("user@");
            String toLowerCase = key.toLowerCase();
            builder.append(toLowerCase);
            builder.append("@");
            String filename = builder.toString();
            String absolutePath = context.getFilesDir().getAbsolutePath();
            File file = new File(absolutePath + File.separator + filename);
            if (null != file && file.exists()) {
                in = context.openFileInput(filename);
                reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            } else {
                content.append("");
            }
        } catch (Exception var23) {
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception var22) {
                }
            }

            if (null != reader) {
                try {
                    reader.close();
                } catch (Exception var21) {
                }
            }

        }

        String value = content.toString();
        return value;
    }

    default void saveBundle(@NonNull Context context, @NonNull String url, @NonNull long position, @NonNull long duration) {

        if (null == url || url.length() <= 0)
            return;
        try {
            JSONObject object = new JSONObject();
            object.putOpt("url", url);
            object.putOpt("position", position);
            object.putOpt("duration", duration);
            String s = object.toString();
            setCache(context, "save_bundle", s);
        } catch (Exception e) {
            MPLogUtil.log(e.getMessage(), e);
        }
    }
}
