package lib.kalu.mpvplayer;

import android.view.Surface;
import java.io.FileDescriptor;
import java.io.IOException;

final class NativePlayerEngine implements PlayerEngine {
    private final MpvPlayer.Core player;
    NativePlayerEngine(java.util.concurrent.Executor callbacks) { player = new MpvPlayer.Core(callbacks); }
    public void events(Events e) { player.setEvents(e); }
    public void reset() { player.reset(); }
    public void source(MediaSource value) { player.setDataSource(value.uri,value.headers); }
    public void source(FileDescriptor fd,long offset,long length) throws IOException { player.setDataSource(fd,offset,length); }
    public void prepareSync() throws IOException { player.prepare(); }
    public void prepare() { player.prepareAsync(); }
    public void play() { player.start(); }
    public void pause() { player.pause(); }
    public void stop() { player.stop(); }
    public void release() { player.release(); }
    public void seek(long value) { player.seekTo(value); }
    public void surface(Surface value) { player.setSurface(value); }
    public void surfaceSize(int w,int h) { player.setSurfaceSize(w,h); }
    public void set(String name,String value) { player.setProperty(name,value); }
    public Object get(String name) { return player.queryProperty(name); }
}
