package lib.kalu.mediaplayer.download.m3u8.inter;


import androidx.annotation.Keep;

/**
 * desc  : 监听基类
 */
@Keep
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
