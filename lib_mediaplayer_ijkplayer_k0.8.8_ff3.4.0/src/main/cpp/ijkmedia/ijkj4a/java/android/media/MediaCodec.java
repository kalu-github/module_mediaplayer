package ijkmedia.ijkj4a.java.android.media;

import java.nio.ByteBuffer;

import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.view.Surface;

@SimpleCClassName
@MinApi(16)
public class MediaCodec {
    public static class BufferInfo {
        public int  flags;
        public int  offset;
        public long presentationTimeUs;
        public int  size;

        public BufferInfo();
    }

    public static MediaCodec createByCodecName(String name);

    public void configure(MediaFormat format, Surface surface, MediaCrypto crypto, int flags);

    public final MediaFormat getOutputFormat();

    public ByteBuffer[] getInputBuffers();

    public final int  dequeueInputBuffer(long timeoutUs);
    public final void queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags);

    public final int  dequeueOutputBuffer(BufferInfo info, long timeoutUs);
    public final void releaseOutputBuffer(int index, boolean render);

    public final void start();
    public final void stop();
    public final void flush();
    public final void release();
}
