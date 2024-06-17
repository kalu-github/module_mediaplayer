package lib.kalu.mediaplayer.buried;


import lib.kalu.mediaplayer.type.PlayerType;

public interface BuriedEvent {

    void onPlaying(String url, long position, long duration);

    void onExit(String url, long position, long duration);

    void onCompletion(String url, long position, long duration);

    void onError(String url, long position, long duration);

    void onPause(String url, long position, long duration);

    void onResume(String url, long position, long duration);

    void onSeek(String url, long position, long duration);

    void onWindow(@PlayerType.WindowType.Value int value, String url, long position, long duration);
}