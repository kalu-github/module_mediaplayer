package lib.kalu.mpvplayer;

import android.view.Surface;
import java.io.FileDescriptor;
import java.io.IOException;

/** Internal adapter, also used by deterministic state-machine tests. */
interface PlayerEngine {
    interface Events {
        default void buffered(int percent) {}
        void prepared();
        void completed();
        void error(int code);
        void info(int what);
        void size(int width,int height);
        void seeked();
        void subtitle(String text);
    }
    void events(Events events);
    void reset();
    void source(MediaSource source);
    default void source(FileDescriptor fd,long offset,long length) throws IOException { throw new IOException("File descriptors unavailable"); }
    default void prepareSync() throws IOException { throw new IOException("Synchronous preparation unavailable"); }
    void prepare();
    void play();
    void pause();
    void stop();
    void release();
    void seek(long positionMs);
    void surface(Surface surface);
    void surfaceSize(int width,int height);
    void set(String name,String value);
    Object get(String name);
}
