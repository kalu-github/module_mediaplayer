package lib.kalu.ijkplayer.misc;

@SimpleCClassName
public interface IAndroidIO {
    int open(String url);

    int read(byte[] buffer, int size);

    long seek(long offset, int whence);

    int close();
}
