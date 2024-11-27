package lib.kalu.mediaplayer.core.component;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface ComponentApiMenu extends ComponentApi {

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }

    default int getEpisodeItemCount() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            int itemCount = tags.getEpisodeItemCount();
            if (itemCount <= 0)
                throw new Exception("warning: itemCount " + itemCount);
            return itemCount;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeItemCount => " + e.getMessage());
            return -1;
        }
    }

    default int getEpisodePlayingIndex() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            int playingIndex = tags.getEpisodePlayingIndex();
            if (playingIndex < 0)
                throw new Exception("warning: playingIndex " + playingIndex);
            return playingIndex;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodePlayingIndex => " + e.getMessage());
            return -1;
        }
    }

    default int getEpisodeFreeItemCount() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            int freeItemCount = tags.getEpisodeFreeItemCount();
            if (freeItemCount < 0)
                throw new Exception("warning: freeItemCount " + freeItemCount);
            return freeItemCount;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFreeItemCount => " + e.getMessage());
            return -1;
        }
    }

    default String getEpisodeFlagFreeImgUrl() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            String url = tags.getEpisodeFlagFreeImgUrl();
            if (null == url || url.length() == 0)
                throw new Exception("warning: url null");
            return url;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFlagFreeImgUrl => " + e.getMessage());
            return null;
        }
    }

    default String getEpisodeFlagFreeFilePath() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            String path = tags.getEpisodeFlagFreeFilePath();
            if (null == path || path.length() == 0)
                throw new Exception("warning: path null");
            return path;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFlagFreeFilePath => " + e.getMessage());
            return null;
        }
    }

    @DrawableRes
    default int getEpisodeFlagFreeResourceId() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            int resourceId = tags.getEpisodeFlagFreeResourceId();
            if (resourceId == 0)
                throw new Exception("warning: resourceId = 0");
            return resourceId;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFlagFreeResourceId => " + e.getMessage());
            return 0;
        }
    }

    default String getEpisodeFlagVipImgUrl() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            String url = tags.getEpisodeFlagVipImgUrl();
            if (null == url || url.length() == 0)
                throw new Exception("warning: url null");
            return url;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFlagVipImgUrl => " + e.getMessage());
            return null;
        }
    }

    default String getEpisodeFlagVipFilePath() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            String path = tags.getEpisodeFlagVipFilePath();
            if (null == path || path.length() == 0)
                throw new Exception("warning: url null");
            return path;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFlagVipFilePath => " + e.getMessage());
            return null;
        }
    }

    @DrawableRes
    default int getEpisodeFlagVipResourceId() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            int resourceId = tags.getEpisodeFlagVipResourceId();
            if (resourceId == 0)
                throw new Exception("warning: resourceId = 0");
            return resourceId;
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFlagVipResourceId => " + e.getMessage());
            return 0;
        }
    }

    @PlayerType.EpisodeFlagLoactionType.Value
    default int getEpisodeFlagLoaction() {
        try {
            StartArgs tags = getStartArgs();
            if (null == tags)
                throw new Exception("error: tags null");
            return tags.getEpisodeFlagLoaction();
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => getEpisodeFlagLoaction => " + e.getMessage());
            return PlayerType.EpisodeFlagLoactionType.DEFAULT;
        }
    }

    default void callListener(int episodeIndex) {
        try {
            OnPlayerEpisodeListener listener = getOnPlayerEpisodeListener();
            if (null == listener)
                throw new Exception("error: listener null");
            listener.onEpisode(episodeIndex);
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => callListener => " + e.getMessage());
        }
    }

    default void updateTimeMillis() {
        try {
            long millis = System.currentTimeMillis();
            ((View) this).setTag(millis);
        } catch (Exception e) {
        }
    }

    default void clearTimeMillis() {
        try {
            ((View) this).setTag(null);
        } catch (Exception e) {
        }
    }

    default long getTimeMillis() {
        try {
            Object tag = ((View) this).getTag();
            if (null == tag)
                throw new Exception("warning: tag null");
            return (long) tag;
        } catch (Exception e) {
            return 0L;
        }
    }

    /****** 选集   ******/

    default void scrollEpisodeText(int childIndex, int action) {
    }

    default void updateEpisodeText(int childIndex) {
    }

    default void clickEpisodeText(int childIndex, int episodeIndex) {
    }

    default void clearEpisodeText(int childIndex, boolean changeVisibility) {
    }

    default void loadEpisodeText(int childIndex, int episodeIndex, int playIndex, boolean changeVisibility) {
    }

    default void loadEpisodeUrl(@Nullable ImageView imageView, @Nullable String url) {
        try {
            imageView.setImageDrawable(null);
            imageView.setImageURI(Uri.parse(url));
        } catch (Exception e) {
        }
    }

    default void loadEpisodeFile(@Nullable ImageView imageView, @Nullable String path) {
        try {
            imageView.setImageDrawable(null);
            imageView.setImageURI(Uri.parse(path));
        } catch (Exception e) {
        }
    }

    default void loadEpisodeResource(@Nullable ImageView imageView, @DrawableRes int resId) {
        try {
            imageView.setImageDrawable(null);
            imageView.setImageResource(resId);
        } catch (Exception e) {
        }
    }

    /******************/

    default void initHideContentView() {
    }

    @IdRes
    default int[] initHideContentData() {
        return null;
    }


    default void showTabAt(int index) {
    }

    default void requestTabAt(int index) {
    }

    default void initTabUnderLine(int index) {
    }

    default void initTabView() {
    }

    default String[] initTabData() {
        return null;
    }

    default void initEpisodeView() {
    }

    default void initSpeedView() {
    }

    @PlayerType.SpeedType.Value
    default int[] initSpeedData() {
        return null;
    }

    default void initScaleView() {
    }

    @PlayerType.ScaleType.Value
    default int[] initScaleData() {
        return null;
    }

    default boolean keycodeUp(int action) {
        return false;
    }

    default boolean keycodeDown(int action) {
        return false;
    }

    default boolean keycodeLeft(int action) {
        return false;
    }

    default boolean keycodeRight(int action) {
        return false;
    }

    default boolean keycodeCenter(int action) {
        return false;
    }
}