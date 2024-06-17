package lib.kalu.mediaplayer.widget.player;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;


import androidx.annotation.RequiresApi;

import org.json.JSONArray;

import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.listener.OnPlayerItemsLiatener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApi;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.util.LogUtil;


public class PlayerLayout extends RelativeLayout {

    public PlayerLayout(Context context) {
        super(context);
        init();
    }

    public PlayerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        removeOnPlayerEventListener();
        removeOnPlayerWindowListener();
        removeOnPlayerProgressListener();
        try {
            int childCount = getChildCount();
            if (childCount > 0)
                throw new Exception("childCount warning: " + childCount);
            PlayerView playerView = new PlayerView(getContext());
            playerView.setTag(R.id.module_mediaplayer_root_parent_id, getId());
            playerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            addView(playerView);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => init => " + e.getMessage());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("warning: null == playerView");
            boolean dispatchKeyEvent2 = playerView.dispatchKeyEvent(event);
            if (!dispatchKeyEvent2)
                throw new Exception("warning: dispatchKeyEvent2 false");
            return true;
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => dispatchKeyEvent => " + e.getMessage());
//            return super.dispatchKeyEvent(event);
            return false;
        }
    }

    /**********/

    private ViewGroup findDecorView(View view) {
        try {
            View parent = (View) view.getParent();
            if (null == parent) {
                return (ViewGroup) view;
            } else {
                return findDecorView(parent);
            }
        } catch (Exception e) {
            return (ViewGroup) view;
        }
    }

    private PlayerView getPlayerView() {
        try {
            int childCount = getChildCount();
            LogUtil.log("PlayerLayout => getPlayerView => childCount = " + childCount + ", this = " + this);
            // sample
            if (childCount == 1) {
                return (PlayerView) getChildAt(0);
            }
            // not
            else {
                ViewGroup decorView = findDecorView(this);
                LogUtil.log("PlayerLayout => getPlayerView => decorView = " + decorView);
                if (null == decorView)
                    throw new Exception("decorView error: null");
                int decorChildCount = decorView.getChildCount();
                LogUtil.log("PlayerLayout => getPlayerView => decorChildCount = " + decorChildCount);
                for (int i = 0; i < decorChildCount; i++) {
                    View childAt = decorView.getChildAt(i);
                    LogUtil.log("PlayerLayout => getPlayerView => childAt = " + childAt);
                    if (null == childAt)
                        continue;
                    if (childAt.getId() == R.id.module_mediaplayer_root) {
                        return (PlayerView) childAt;
                    }
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getPlayerView => " + e.getMessage());
            return null;
        }
    }

    /**********/

    protected boolean enableReleaseTag() {
        return false;
    }

    protected boolean enableDetachedFromWindowTodo() {
        return false;
    }

    protected boolean enableWindowVisibilityChangedTodo(int visibility) {
        return false;
    }

    protected boolean enableAttachedToWindowTodo() {
        return false;
    }

    /**********/

    public final boolean isFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFull();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => isFull => " + e.getMessage());
            return false;
        }
    }

    public final boolean isFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isFloat();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => isFloat => " + e.getMessage());
            return false;
        }
    }

    public final void startFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("warning: tags null");
            String mediaUrl = tags.getMediaUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            int kernelType = tags.getKernelType();
            int renderType = tags.getRenderType();
            boolean startFull = playerView.startFull(kernelType, renderType);
            LogUtil.log("PlayerLayout => startFull => status = " + startFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => startFull => " + e.getMessage());
        }
    }

    public final void stopFull() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("warning: tags null");
            String mediaUrl = tags.getMediaUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            int kernelType = tags.getKernelType();
            int renderType = tags.getRenderType();
            boolean stopFull = playerView.stopFull(kernelType, renderType);
            LogUtil.log("PlayerLayout => stopFull => status = " + stopFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => stopFull => " + e.getMessage());
        }
    }

    public final void startFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("warning: tags null");
            String mediaUrl = tags.getMediaUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            int kernelType = tags.getKernelType();
            int renderType = tags.getRenderType();
            boolean startFull = playerView.startFloat(kernelType, renderType);
            LogUtil.log("PlayerLayout => startFloat => status = " + startFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => startFloat => " + e.getMessage());
        }
    }
//
    public final void stopFloat() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tags = playerView.getTags();
            if (null == tags)
                throw new Exception("warning: tags null");
            String mediaUrl = tags.getMediaUrl();
            if (null == mediaUrl)
                throw new Exception("warning: mediaUrl null");
            int kernelType = tags.getKernelType();
            int renderType = tags.getRenderType();
            boolean stopFull = playerView.stopFloat(kernelType, renderType);
            LogUtil.log("PlayerLayout => stopFloat => status = " + stopFull);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => stopFloat => " + e.getMessage());
        }
    }

    public long getPosition() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getPosition();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getPosition => " + e.getMessage());
            return 0;
        }
    }

    public long getDuration() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getDuration();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getDuration => " + e.getMessage());
            return 0;
        }
    }

    public final void setVideoScaleType(@PlayerType.ScaleType.Value int scaleType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVideoScaleType(scaleType);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setVideoScaleType => " + e.getMessage());
        }
    }

    public final void setVideoRotation(@PlayerType.RotationType.Value int rotationType) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVideoRotation(rotationType);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setVideoRotation => " + e.getMessage());
        }
    }

    public final void addComponent(ComponentApi componentApi) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.addComponent(componentApi);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => addComponent => " + e.getMessage());
        }
    }

    public final void addAllComponent(List<ComponentApi> componentApis) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.addAllComponent(componentApis);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => addAllComponent => " + e.getMessage());
        }
    }

    public final <T extends ComponentApi> T findComponent(java.lang.Class<?> cls) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.findComponent(cls);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => findComponent => " + e.getMessage());
            return null;
        }
    }

    public final boolean showComponent(java.lang.Class<?> cls) {
        try {
            ComponentApi component = findComponent(cls);
            if (null == component)
                throw new Exception("component error: null");
            component.show();
            return true;
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => showComponent => " + e.getMessage());
            return false;
        }
    }

    public final boolean hideComponent(java.lang.Class<?> cls) {
        try {
            ComponentApi component = findComponent(cls);
            if (null == component)
                throw new Exception("component error: null");
            component.hide();
            return true;
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => hideComponent => " + e.getMessage());
            return false;
        }
    }

    public final void toggle() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.toggle();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => toggle => " + e.getMessage());
        }
    }

    public final void resume() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => resume => " + e.getMessage());
        }
    }

    public final void resume(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.resume(callEvent);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => resume => " + e.getMessage());
        }
    }

    public final void pause() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => pause => " + e.getMessage());
        }
    }

    public final void pause(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pause(callEvent);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => pause => " + e.getMessage());
        }
    }

    public final void release() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            boolean releaseTag = enableReleaseTag();
            playerView.release(releaseTag, false, true);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => release => " + e.getMessage());
        }
    }

    public final void release(boolean claerTag) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.release(true, claerTag, false);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => release => " + e.getMessage());
        }
    }

    public final void pauseKernel(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.pauseKernel(callEvent);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => pauseKernel => " + e.getMessage());
        }
    }

    public final void stop() {
        stop(true);
    }

    public final void stop(boolean callEvent) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.stop(true, callEvent);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => stop => " + e.getMessage());
        }
    }

    public final boolean isPlaying() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPlaying();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => isPlaying => " + e.getMessage());
            return false;
        }
    }

    public final boolean isPrepared() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.isPrepared();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => isPrepared => " + e.getMessage());
            return false;
        }
    }

    public final String getSubtitleUrl() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tag = ((VideoPlayerApi) playerView).getTags();
            if (null == tag)
                throw new Exception("error: tag null");
            return tag.getSubtitleUrl();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getSubtitleUrl => " + e.getMessage());
            return null;
        }
    }

    public final String getMediaUrl() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tag = ((VideoPlayerApi) playerView).getTags();
            if (null == tag)
                throw new Exception("error: tag null");
            return tag.getMediaUrl();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getMediaUrl => " + e.getMessage());
            return null;
        }
    }

    public void restart() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.restart();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => restart => " + e.getMessage());
        }
    }

    public void start(String playerUrl) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.start(playerUrl);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => start => " + e.getMessage());
        }
    }

    public void start(StartArgs args) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.start(args);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => start => " + e.getMessage());
        }
    }

    public final void setVolume(float left, float right) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setVolume(left, right);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setVolume => " + e.getMessage());
        }
    }

    public final void setMute(boolean enable) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setMute(enable);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setMute => " + e.getMessage());
        }
    }

    public final long getSeek() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tag = playerView.getTags();
            if (null == tag)
                throw new Exception("error: tag null");
            return tag.getSeek();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getSeek => " + e.getMessage());
            return 0L;
        }
    }

    public final long getMax() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            StartArgs tag = playerView.getTags();
            if (null == tag)
                throw new Exception("error: tag null");
            return tag.getMax();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getMax => " + e.getMessage());
            return 0;
        }
    }

    public final void seekTo(long postion) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.seekTo(postion);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => seekTo => " + e.getMessage());
        }
    }

    public final void callEventListener(@PlayerType.StateType.Value int state) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.callEventListener(state);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => callEventListener => " + e.getMessage());
        }
    }

    public final void setPlayerBackgroundColor(@ColorInt int color) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setBackgroundColor(color);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setPlayerBackgroundColor => " + e.getMessage());
        }
    }

    public final void setSpeed(@FloatRange(from = 1F, to = 4F) float speed) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setSpeed(speed);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setPlayerBackgroundColor => " + e.getMessage());
        }
    }

    @FloatRange(from = 1F, to = 4F)
    public final float getSpeed() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getSpeed();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setPlayerBackgroundColor => " + e.getMessage());
            return 1F;
        }
    }

    public final JSONArray getTrackInfo() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            JSONArray trackInfo = playerView.getTrackInfo();
            if (null == trackInfo)
                throw new Exception("trackInfo error: null");
            return trackInfo;
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getTrackInfo => " + e.getMessage());
            return null;
        }
    }

    public final boolean switchTrack(int trackId) {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.switchTrack(trackId);
            return true;
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => switchTrack => " + e.getMessage());
            return false;
        }
    }

    public final String screenshot() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            String screenshot = playerView.screenshot();
            if (null == screenshot || screenshot.length() == 0)
                throw new Exception("error: null == screenshot || screenshot.length() == 0");
            return screenshot;
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => screenshot => " + e.getMessage());
            return null;
        }
    }

    public final OnPlayerEventListener getOnPlayerEventListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getOnPlayerEventListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getOnPlayerEventListener => " + e.getMessage());
            return null;
        }
    }

    public final void removeOnPlayerEventListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.removeOnPlayerEventListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => removeOnPlayerEventListener => " + e.getMessage());
        }
    }

    public final void setOnPlayerEventListener(OnPlayerEventListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerEventListener(listener);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setOnPlayerEventListener => " + e.getMessage());
        }
    }

    public final OnPlayerProgressListener getOnPlayerProgressListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getOnPlayerProgressListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getOnPlayerProgressListener => " + e.getMessage());
            return null;
        }
    }

    public final void removeOnPlayerProgressListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.removeOnPlayerProgressListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => removeOnPlayerProgressListener => " + e.getMessage());
        }
    }

    public final void setOnPlayerProgressListener(OnPlayerProgressListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerProgressListener(listener);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setOnPlayerProgressListener => " + e.getMessage());
        }
    }

    public final OnPlayerWindowListener getOnPlayerWindowListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            return playerView.getOnPlayerWindowListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => getOnPlayerWindowListener => " + e.getMessage());
            return null;
        }
    }

    public final void removeOnPlayerWindowListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.removeOnPlayerWindowListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => removeOnPlayerWindowListener => " + e.getMessage());
        }
    }

    public final void setOnPlayerWindowListener(OnPlayerWindowListener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerWindowListener(listener);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setOnPlayerWindowListener => " + e.getMessage());
        }
    }

    public final void removeOnPlayerItemsListener() {
        try {
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.removeOnPlayerItemsListener();
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => removeOnPlayerItemsListener => " + e.getMessage());
        }
    }

    public final void setOnPlayerItemsListener(OnPlayerItemsLiatener listener) {
        try {
            if (null == listener)
                throw new Exception("listener error: null");
            PlayerView playerView = getPlayerView();
            if (null == playerView)
                throw new Exception("playerView error: null");
            playerView.setOnPlayerItemsListener(listener);
        } catch (Exception e) {
            LogUtil.log("PlayerLayout => setOnPlayerWindowListener => " + e.getMessage());
        }
    }
}
