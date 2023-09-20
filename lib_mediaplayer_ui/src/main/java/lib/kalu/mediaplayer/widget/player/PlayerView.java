package lib.kalu.mediaplayer.widget.player;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public final class PlayerView extends RelativeLayout implements VideoPlayerApi {

    // 解码
    protected VideoKernelApi mKernel;
    // 渲染
    protected VideoRenderApi mRender;

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
        return mRender;
    }

    @Override
    public void setVideoRender(@NonNull VideoRenderApi render) {
        mRender = render;
    }

    @Override
    public VideoKernelApi getVideoKernel() {
        return mKernel;
    }

    @Override
    public void setVideoKernel(@NonNull VideoKernelApi kernel) {
        mKernel = kernel;
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
}