package lib.kalu.mediaplayer.util.download.m3u8.inter;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.util.download.m3u8.bean.M3u8;

/**
 * desc  : 获取M3U8信息
 */
@Keep
public interface OnM3u8InfoListener extends BaseListener {

    /**
     * 获取成功的时候回调
     */
    void onSuccess(M3u8 m3U8);
}
