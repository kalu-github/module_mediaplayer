package lib.kalu.mpvplayer;

import android.test.InstrumentationTestCase;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Runs against the actual libmpv/FFmpeg libraries on an Android device. */
@SuppressWarnings("deprecation")
public class MpvPlayerTest extends InstrumentationTestCase {
    private File wave;
    private static final int PREFIX = 13;
    private static final int SAMPLES = 16000;
    private static final int BYTES = 44 + SAMPLES * 2;

    @Override protected void setUp() throws Exception {
        super.setUp();
        wave = File.createTempFile("mpv-slice-", ".bin", getInstrumentation().getTargetContext().getCacheDir());
        ByteBuffer data = ByteBuffer.allocate(BYTES).order(ByteOrder.LITTLE_ENDIAN);
        data.put(new byte[]{'R','I','F','F'}).putInt(BYTES - 8).put(new byte[]{'W','A','V','E'});
        data.put(new byte[]{'f','m','t',' '}).putInt(16).putShort((short)1).putShort((short)1);
        data.putInt(8000).putInt(16000).putShort((short)2).putShort((short)16);
        data.put(new byte[]{'d','a','t','a'}).putInt(SAMPLES * 2);
        while (data.hasRemaining()) data.putShort((short)0);
        try (FileOutputStream out = new FileOutputStream(wave)) {
            out.write(new byte[PREFIX]); out.write(data.array()); out.write(new byte[19]);
        }
    }
    @Override protected void tearDown() throws Exception {
        if (wave != null) wave.delete();
        super.tearDown();
    }
    private void setSlice(MpvPlayer player) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(wave, "r")) {
            player.setDataSource(file.getFD(), PREFIX, BYTES);
        }
    }
    public void testSlicePlaybackSeekAndCompletion() throws Exception {
        try (MpvPlayer player = new MpvPlayer()) {
            CountDownLatch prepared = new CountDownLatch(1), seeked = new CountDownLatch(1), done = new CountDownLatch(1);
            AtomicInteger errors = new AtomicInteger();
            player.setOnPreparedListener(p -> prepared.countDown());
            player.setOnSeekCompleteListener(p -> seeked.countDown());
            player.setOnCompletionListener(p -> done.countDown());
            player.setOnErrorListener((p, what, extra) -> { errors.incrementAndGet(); return true; });
            setSlice(player);
            player.prepareAsync();
            assertTrue(prepared.await(10, TimeUnit.SECONDS));
            assertEquals(2000, player.getDuration());
            assertFalse(player.isPlaying());
            player.start();
            player.pause();
            assertFalse(player.isPlaying());
            player.seekTo(1000);
            player.start();
            assertTrue(seeked.await(10, TimeUnit.SECONDS));
            assertTrue(done.await(10, TimeUnit.SECONDS));
            assertEquals(0, errors.get());
            assertFalse(player.isPlaying());
        }
    }
    public void testIndependentInstancesAndReset() throws Exception {
        try (MpvPlayer first = new MpvPlayer(); MpvPlayer second = new MpvPlayer()) {
            setSlice(first); setSlice(second);
            first.prepare(); second.prepare();
            first.release(); first.release();
            second.start();
            assertTrue(second.isPlaying());
            second.reset();
            assertFalse(second.isPlaying());
            setSlice(second);
            second.prepare();
            assertEquals(2000, second.getDuration());
        }
    }
    public void testResetDiscardsPendingPrepareCallback() throws Exception {
        try (MpvPlayer player = new MpvPlayer()) {
            AtomicInteger callbacks = new AtomicInteger();
            player.setOnPreparedListener(p -> callbacks.incrementAndGet());
            setSlice(player);
            getInstrumentation().runOnMainSync(() -> { player.prepareAsync(); player.reset(); });
            getInstrumentation().waitForIdleSync();
            assertEquals(0, callbacks.get());
        }
    }
    public void testLoadErrorIsDelivered() throws Exception {
        try (MpvPlayer player = new MpvPlayer()) {
            CountDownLatch error = new CountDownLatch(1);
            AtomicInteger code = new AtomicInteger();
            player.setOnErrorListener((p, what, extra) -> { code.set(extra); error.countDown(); return true; });
            player.setDataSource(new File(wave.getParentFile(), "does-not-exist.mp4").getPath());
            player.prepareAsync();
            assertTrue(error.await(10, TimeUnit.SECONDS));
            assertTrue(code.get() < 0);
        }
    }
}
