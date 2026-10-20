package lib.kalu.mpvplayer;

interface PlayerScheduler {
    long now();
    void post(Runnable action,long delayMs);
    void cancelAll();
}
