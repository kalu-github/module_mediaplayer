package lib.kalu.mediaplayer.core.kernel.audio.ijk;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.core.kernel.audio.MusicKernelApi;
import lib.kalu.mediaplayer.core.kernel.audio.OnMusicPlayerChangeListener;

@Keep
public final class MusicIjkPlayer implements MusicKernelApi {

    private tv.danmaku.ijk.media.player.IjkMediaPlayer mIjkPlayer;

    public MusicIjkPlayer() {
        if (null == mIjkPlayer) {
            mIjkPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
            mIjkPlayer.setLooping(false);
            // 字幕; 1显示。0禁止
            mIjkPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "subtitle", 0);
            //是否有视频, 1无视频、0有视频
            mIjkPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "vn", 1);
            //是否有声音, 1无声音、0有声音
            mIjkPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "an", 0);
            mIjkPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "volume", 100);
        }
    }

    @Override
    public void setMusicListener(@NonNull OnMusicPlayerChangeListener listener) {

    }

    @Override
    public void createDecoder(@NonNull Context context) {

    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull String musicUrl) {

    }

    @Override
    public void start(long position, OnMusicPlayerChangeListener l) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void release() {

    }

    @Override
    public void addListener(long position) {

    }

    @Override
    public void removeListener(boolean clear) {

    }

    @Override
    public void setLooping(boolean v) {

    }

    @Override
    public void setVolume(float v) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

//    @Override
//    public boolean isEnable() {
//        return false;
//    }
//
//    @Override
//    public void setEnable(boolean v) {
//
//    }

    @Override
    public void seekTo(long v) {

    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public long getPosition() {
        return 0;
    }
}
