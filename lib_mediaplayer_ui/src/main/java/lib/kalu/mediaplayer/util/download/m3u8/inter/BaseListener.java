package lib.kalu.mediaplayer.util.download.m3u8.inter;




/**
 * desc  : 监听基类
 */

public interface BaseListener {
    /**
     * 开始的时候回调
     */
    void onStart();

    /**
     * 错误的时候回调
     *
     * @param errorMsg 错误异常
     */
    void onError(Throwable errorMsg);
}
