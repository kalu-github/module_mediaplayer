package lib.kalu.ijkplayer.misc;

@SimpleCClassName
public interface IMediaDataSource {
    int readAt(long position, byte[] buffer, int offset, int size);

    long getSize();

    void close();
}
