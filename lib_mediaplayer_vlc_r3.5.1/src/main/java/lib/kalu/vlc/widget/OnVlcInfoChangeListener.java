package lib.kalu.vlc.widget;

public interface OnVlcInfoChangeListener {

    void onStart();

    void onPlay();

    void onPause();

    void onResume();

    void onEnd();

    void onError();

//    void onNewVideoLayout();
}
