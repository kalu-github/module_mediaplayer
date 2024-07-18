package lib.kalu.mediaplayer.widget.player;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.util.LogUtil;


public final class PlayerView extends RelativeLayout implements VideoPlayerApi {

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
        addView(layoutPlayer, 0);
        // control
        RelativeLayout controlPlayer = new RelativeLayout(getContext());
        controlPlayer.setId(R.id.module_mediaplayer_component);
        controlPlayer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(controlPlayer, 1);
    }

    @Override
    protected void onDetachedFromWindow() {
        detachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        attachedToWindow();
        super.onAttachedToWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        windowVisibilityChanged(visibility);
        super.onWindowVisibilityChanged(visibility);
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
//        LogUtil.log("PlayerView => dispatchKeyEvent => action = " + event.getAction() + ", code = " + event.getKeyCode());
        try {
            // step1
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                // stopFull
                if (isFull()) {
                    stopFull();
                    return true;
                }
                //  stopFloat();
                else if (isFloat()) {
                    stopFloat();
                    return true;
                }
            }

            // step2
            ViewGroup viewGroup = getBaseComponentViewGroup();
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                boolean enableDispatchKeyEvent = ((ComponentApi) childAt).enableDispatchKeyEvent();
                if (enableDispatchKeyEvent) {
                    boolean dispatchKeyEvent = childAt.dispatchKeyEvent(event);
                    if (dispatchKeyEvent) {
                        return true;
                    }
                }
            }

            // error
            throw new Exception("warning: not todo");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public VideoRenderApi getVideoRender() {
        return mVideoRenderApi;
    }

    @Override
    public void setVideoRender(VideoRenderApi render) {
        mVideoRenderApi = render;
    }

    @Override
    public VideoKernelApi getVideoKernel() {
        return mVideoKernelApi;
    }

    @Override
    public void setVideoKernel(VideoKernelApi kernel) {
        mVideoKernelApi = kernel;
    }

    @Override
    public void checkVideoView() {
        if (getVisibility() == View.VISIBLE)
            return;
        pause();
    }

    @Override
    public void setScreenKeep(boolean enable) {
        setKeepScreenOn(enable);
    }
}