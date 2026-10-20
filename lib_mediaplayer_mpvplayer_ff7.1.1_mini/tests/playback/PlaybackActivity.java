package lib.kalu.mpvplayer.smoke;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import lib.kalu.mpvplayer.MpvPlayer;

/** Diagnostic app built against the packaged AAR, not a mock player. */
public final class PlaybackActivity extends Activity implements TextureView.SurfaceTextureListener {
    public static final String URL = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
    public volatile int prepared, rendering, sizes, seeks, completed, changedFrames, pcmPeak;
    public volatile long position;
    public volatile String failure;
    public MpvPlayer player;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextureView texture;
    private TextView status;
    private Surface surface;
    private Visualizer visualizer;
    private int session, updates;
    private long lastHash;
    private boolean hasHash, savedFrame;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        status = new TextView(this);
        status.setText("Loading public MP4…\nAudio PCM and speaker output are not yet verified.");
        layout.addView(status);
        texture = new TextureView(this);
        texture.setSurfaceTextureListener(this);
        layout.addView(texture, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(layout);
    }

    private void event(String name) {
        Log.i("MpvPlaybackTest", name);
        if (Looper.myLooper() != Looper.getMainLooper()) failure = "Callback off main thread: " + name;
    }

    private void failed(Throwable error) {
        failure = Log.getStackTraceString(error);
        Log.e("MpvPlaybackTest", "Playback failed", error);
        status.setText(failure);
    }

    @Override public void onSurfaceTextureAvailable(SurfaceTexture value, int width, int height) {
        try {
            surface = new Surface(value);
            player = new MpvPlayer();
            // A dedicated AudioTrack session lets Visualizer measure only this player's PCM.
            // Test-only reflection avoids adding diagnostics to the public player API.
            session = ((AudioManager)getSystemService(AUDIO_SERVICE)).generateAudioSessionId();
            if (session <= 0) throw new IllegalStateException("Cannot allocate an audio session");
            Method set = MpvPlayer.class.getDeclaredMethod("setProperty", String.class, String.class);
            set.setAccessible(true);
            set.invoke(player, "audiotrack-session-id", Integer.toString(session));
            player.setOnPreparedListener(p -> {
                event("onPrepared"); prepared++;
                try { p.start(); handler.postDelayed(sampleAudio, 1000); }
                catch (RuntimeException e) { failed(e); }
            });
            player.setOnVideoSizeChangedListener((p, w, h) -> {
                event("onVideoSizeChanged " + w + "x" + h);
                if (w > 0 && h > 0) sizes++;
            });
            player.setOnInfoListener((p, what, extra) -> {
                event("onInfo " + what + " " + extra);
                if (what == MpvPlayer.MEDIA_INFO_VIDEO_RENDERING_START) rendering++;
                return true;
            });
            player.setOnSeekCompleteListener(p -> { event("onSeekComplete"); seeks++; });
            player.setOnCompletionListener(p -> { event("onCompletion"); completed++; });
            player.setOnBufferingUpdateListener((p, percent) -> event("onBufferingUpdate " + percent));
            player.setOnErrorListener((p, what, extra) -> {
                failed(new IllegalStateException("onError " + what + " " + extra)); return true;
            });
            player.setSurface(surface);
            player.setSurfaceSize(width, height);
            String url = getIntent().getStringExtra("url");
            player.setDataSource(url == null ? URL : url);
            player.prepareAsync();
        } catch (Exception | LinkageError e) { failed(e); }
    }

    private final Runnable sampleAudio = new Runnable() {
        @Override public void run() {
            if (player == null || failure != null) return;
            try {
                if (visualizer == null) {
                    visualizer = new Visualizer(session);
                    visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                    visualizer.setScalingMode(Visualizer.SCALING_MODE_AS_PLAYED);
                    if (visualizer.setEnabled(true) != Visualizer.SUCCESS)
                        throw new IllegalStateException("Cannot enable audio PCM capture");
                }
                byte[] samples = new byte[visualizer.getCaptureSize()];
                if (visualizer.getWaveForm(samples) != Visualizer.SUCCESS)
                    throw new IllegalStateException("Cannot capture player audio PCM");
                int low = 255, high = 0, peak = 0;
                for (byte sample : samples) {
                    int value = sample & 255;
                    low = Math.min(low, value); high = Math.max(high, value);
                    peak = Math.max(peak, Math.abs(value - 128));
                }
                if (high - low >= 3) pcmPeak = Math.max(pcmPeak, peak);
                position = player.getCurrentPositionLong();
                status.setText("prepared=" + prepared + " firstFrame=" + rendering + " size=" + sizes
                    + "\nmovingFrames=" + changedFrames + " PCM peak=" + pcmPeak + " position=" + position
                    + "\nSpeaker audibility still requires listening / loopback recording.");
                handler.postDelayed(this, 250);
            } catch (RuntimeException e) { failed(e); }
        }
    };

    @Override public void onSurfaceTextureUpdated(SurfaceTexture value) {
        if (++updates % 10 != 0) return;
        Bitmap frame = texture.getBitmap(64, 36);
        if (frame == null) return;
        int[] pixels = new int[64 * 36];
        frame.getPixels(pixels, 0, 64, 0, 0, 64, 36);
        frame.recycle();
        int low = 765, high = 0;
        long hash = 1;
        for (int pixel : pixels) {
            int light = ((pixel >> 16) & 255) + ((pixel >> 8) & 255) + (pixel & 255);
            low = Math.min(low, light); high = Math.max(high, light);
            hash = 31 * hash + pixel;
        }
        // Reject a flat black/solid surface; require distinct actual pixel frames.
        if (high - low > 45) {
            if (hasHash && hash != lastHash) changedFrames++;
            lastHash = hash; hasHash = true;
            if (changedFrames >= 3 && !savedFrame) {
                Bitmap evidence = texture.getBitmap();
                if (evidence != null) {
                    try (FileOutputStream out = new FileOutputStream(new File(getFilesDir(), "video-frame.png"))) {
                        evidence.compress(Bitmap.CompressFormat.PNG, 100, out);
                        savedFrame = true;
                    } catch (Exception e) { failed(e); }
                    finally { evidence.recycle(); }
                }
            }
        }
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture value, int width, int height) {
        if (player != null) {
            try { player.setSurfaceSize(width, height); }
            catch (RuntimeException e) { failed(e); }
        }
    }
    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture value) {
        closePlayer(); return true;
    }
    private void closePlayer() {
        handler.removeCallbacksAndMessages(null);
        if (visualizer != null) { visualizer.release(); visualizer = null; }
        if (player != null) { player.release(); player = null; }
        if (surface != null) { surface.release(); surface = null; }
    }
    @Override public void onDestroy() { closePlayer(); super.onDestroy(); }
}
