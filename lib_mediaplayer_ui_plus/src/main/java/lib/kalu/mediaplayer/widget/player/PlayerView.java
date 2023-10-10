package lib.kalu.mediaplayer.widget.player;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerBuilder;
import lib.kalu.mediaplayer.config.start.StartBuilder;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;

@Keep
public final class PlayerView extends RelativeLayout implements VideoPlayerApi, AudioPlayerApi {

    // 音频解码
    private AudioKernelApi mAudioKernelApi;
    // 视频解码
    private VideoKernelApi mVideoKernelApi;
    // 视频渲染
    private VideoRenderApi mVideoRenderApi;

    public PlayerView(Context context) {
        super(context);
        setId(R.id.module_mediaplayer_root);
        // player
        RelativeLayout layoutPlayer = new RelativeLayout(getContext());
        layoutPlayer.setId(R.id.module_mediaplayer_video);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutPlayer.setLayoutParams(layoutParams);
        addView(layoutPlayer);
        // control
        RelativeLayout controlPlayer = new RelativeLayout(getContext());
        controlPlayer.setId(R.id.module_mediaplayer_control);
        controlPlayer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(controlPlayer);
    }

    @Override
    public void addComponent(ComponentApi componentApi) {
        VideoPlayerApi.super.addComponent(componentApi);
    }

    @Override
    public void addAllComponent(List<ComponentApi> componentApis) {
        VideoPlayerApi.super.addAllComponent(componentApis);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchKeyEventPlayer(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public VideoRenderApi getVideoRender() {
        return mVideoRenderApi;
    }

    @Override
    public void setVideoRender(@NonNull VideoRenderApi render) {
        this.mVideoRenderApi = render;
    }

    @Override
    public VideoKernelApi getVideoKernel() {
        return mVideoKernelApi;
    }

    @Override
    public void setVideoKernel(@NonNull VideoKernelApi kernel) {
        this.mVideoKernelApi = kernel;
    }

    @Override
    public AudioKernelApi getAudioKernel() {
        return mAudioKernelApi;
    }

    @Override
    public void setAudioKernel(@NonNull AudioKernelApi kernel) {
        this.mAudioKernelApi = kernel;
    }

    @Override
    public void checkVideoReal() {
        if (getVisibility() == View.VISIBLE)
            return;
        pause();
    }

    @Override
    public void setScreenKeep(boolean enable) {
        setKeepScreenOn(enable);
    }

    @Override
    public boolean switchExternalAudio(@NonNull boolean enable) {
        try {
            setMute(enable ? true : false);
            setMuteAudio(enable ? false : true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}