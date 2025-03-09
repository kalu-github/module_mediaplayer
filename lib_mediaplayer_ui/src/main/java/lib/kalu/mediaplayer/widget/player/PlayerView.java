package lib.kalu.mediaplayer.widget.player;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.component.ComponentBackPressed;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.util.LogUtil;


public final class PlayerView extends RelativeLayout implements VideoPlayerApi {

    // 视频解码
    private VideoKernelApi mVideoKernelApi;
    // 视频渲染
    private VideoRenderApi mVideoRenderApi;

    public PlayerView(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setId(R.id.module_mediaplayer_id_player);
        // player
        RelativeLayout playerLayout = new RelativeLayout(getContext());
        playerLayout.setId(R.id.module_mediaplayer_video);
        LayoutParams playerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        playerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        playerLayout.setLayoutParams(playerLayoutParams);
        addView(playerLayout, 0);
        // control
        RelativeLayout controlLayout = new RelativeLayout(getContext());
        controlLayout.setId(R.id.module_mediaplayer_component);
        LayoutParams controlLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        controlLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        controlLayout.setLayoutParams(controlLayoutParams);
        addView(controlLayout, 1);
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
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        LogUtil.log("PlayerView => dispatchKeyEvent => action = " + event.getAction() + ", code = " + event.getKeyCode() + ", isFull = " + isFull());
        try {

            // Component step1
            ViewGroup componentGroup = getBaseComponentViewGroup();
            int childCount = componentGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = componentGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                boolean assignableFrom = ComponentApi.class.isAssignableFrom(childAt.getClass());
                if (!assignableFrom)
                    continue;
                boolean componentShowing = ((ComponentApi) childAt).isComponentShowing();
                if (componentShowing) {
                    boolean dispatchKeyEvent = childAt.dispatchKeyEvent(event);
                    if (dispatchKeyEvent) {
                        return true;
                    }
                }
            }
            // Component step2
            for (int i = 0; i < childCount; i++) {
                View childAt = componentGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                boolean assignableFrom = ComponentApi.class.isAssignableFrom(childAt.getClass());
                if (!assignableFrom)
                    continue;
                boolean dispatchKeyEvent = childAt.dispatchKeyEvent(event);
                LogUtil.log("PlayerView => dispatchKeyEvent => i = " + i + ", dispatchKeyEvent = " + dispatchKeyEvent + ", childAt = " + childAt);
                if (dispatchKeyEvent) {
                    return true;
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
    public void checkVideoVisibility() {
        try {
            int visibility = getVisibility();
            if (visibility == View.VISIBLE)
                throw new Exception("warning: visibility == View.VISIBLE");
            pause();
        } catch (Exception e) {
            LogUtil.log("PlayerView => checkVideoVisibility => Exception " + e.getMessage());
        }
    }

    @Override
    public void setScreenKeep(boolean enable) {
        setKeepScreenOn(enable);
    }
}