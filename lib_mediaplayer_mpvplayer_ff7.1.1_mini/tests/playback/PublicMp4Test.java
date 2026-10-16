package lib.kalu.mpvplayer.smoke;

import android.content.Intent;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lib.kalu.mpvplayer.MpvPlayer;

@SuppressWarnings("deprecation")
public final class PublicMp4Test extends InstrumentationTestCase {
    private interface Condition { boolean satisfied(); }
    private void await(PlaybackActivity activity, String label, Condition condition) throws Exception {
        long deadline = SystemClock.elapsedRealtime() + 60000;
        while (!condition.satisfied() && activity.failure == null && SystemClock.elapsedRealtime() < deadline)
            Thread.sleep(100);
        assertNull(activity.failure, activity.failure);
        assertTrue("Timed out: " + label, condition.satisfied());
    }
    public void testPublicMp4CallbacksPixelsAndAudioPcm() throws Exception {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), PlaybackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PlaybackActivity activity = (PlaybackActivity)getInstrumentation().startActivitySync(intent);
        try {
            await(activity, "prepared callback", () -> activity.prepared == 1);
            await(activity, "video size callback", () -> activity.sizes > 0);
            await(activity, "first video rendering callback", () -> activity.rendering == 1);
            await(activity, "three changing non-flat pixel frames", () -> activity.changedFrames >= 3);
            await(activity, "non-silent PCM on this player's AudioTrack session", () -> activity.pcmPeak >= 3);
            await(activity, "playback clock advances", () -> activity.position > 3000);
            getInstrumentation().runOnMainSync(() -> activity.player.seekTo(15000));
            await(activity, "seek complete callback", () -> activity.seeks == 1);
            assertEquals("Seeking must not repeat first-frame callback", 1, activity.rendering);
            getInstrumentation().runOnMainSync(() -> {
                long duration = activity.player.getDurationLong();
                assertTrue("Public MP4 duration", duration > 20000);
                activity.player.seekTo(duration - 1500);
            });
            await(activity, "completion callback", () -> activity.completed == 1);
            assertEquals(1, activity.rendering);
        } finally { getInstrumentation().runOnMainSync(activity::finish); }
    }
    public void testMissingFileErrorCallback() throws Exception {
        CountDownLatch error = new CountDownLatch(1);
        try (MpvPlayer player = new MpvPlayer()) {
            player.setOnErrorListener((p, what, extra) -> { if (extra < 0) error.countDown(); return true; });
            player.setDataSource(getInstrumentation().getTargetContext().getFilesDir() + "/missing.mp4");
            player.prepareAsync();
            assertTrue("Missing file must deliver onError", error.await(15, TimeUnit.SECONDS));
        }
    }
}
