package ijkmedia.ijkj4a.java.java.nio;

import android.os.Build;

import java.nio.Buffer;

@SimpleCClassName
@IncludeUtil
public class ByteBuffer {
    public static ByteBuffer allocate(int capacity);
    public static ByteBuffer allocateDirect(int capacity);
    public final Buffer limit(int newLimit);
}
