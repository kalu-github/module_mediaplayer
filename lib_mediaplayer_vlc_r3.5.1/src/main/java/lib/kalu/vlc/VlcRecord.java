package lib.kalu.vlc;

import org.videolan.libvlc.MediaPlayer;

import java.io.File;

/**
 * vlc input_file --sout="#transcode{vfilter=adjust{gamma=1.5},vcodec=theo,vb=2000,scale=0.67,acodec=vorb,ab=128,channels=2}:standard{access=file,mux=ogg,dst="output_file.ogg"}"
 *
 * @author Created by yyl on 2018/5/23.
 * https://github.com/mengzhidaren
 */
public class VlcRecord {

    static {
        synchronized (VlcRecord.class) {
            try {
                System.loadLibrary("vlc-record");
            } catch (Exception e) {
            }
        }
    }

    public static boolean startRecord(MediaPlayer mediaPlayer, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            return mediaPlayer.record(path);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean stopRecord(MediaPlayer mediaPlayer) {
        try {
            return mediaPlayer.record(null);
        } catch (Exception e) {
            return false;
        }
    }

    public native boolean isRecording(MediaPlayer mediaPlayer);

    public native boolean isSuportRecord(MediaPlayer mediaPlayer);


    /**
     * * Take a snapshot of the current video window.
     * <p>
     * If i_width AND i_height is 0, original size is used.
     * If i_width XOR i_height is 0, original aspect-ratio is preserved.
     * <p>
     * \param p_mi media player instance
     * \param num number of video output (typically 0 for the first/only one)
     * \param psz_filepath the path of a file or a folder to save the screenshot into
     * \param i_width the snapshot's width
     * \param i_height the snapshot's height
     * \return 0 on success, -1 if the video was not found
     */
    public native int snapshot(MediaPlayer mediaPlayer, String path, int width, int height);
}
