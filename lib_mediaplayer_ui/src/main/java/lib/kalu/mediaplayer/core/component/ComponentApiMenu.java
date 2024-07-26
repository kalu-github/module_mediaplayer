package lib.kalu.mediaplayer.core.component;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public interface ComponentApiMenu extends ComponentApi {

    /***************/

    @PlayerType.ScaleType.Value
    ArrayList<Integer> mScaleTypes = new ArrayList<Integer>();

    default void setScaleTypes(@PlayerType.ScaleType.Value int... ints) {
        try {
            if (null == ints)
                throw new Exception("warning: ints null");
            mScaleTypes.clear();
            for (int i : ints) {
                mScaleTypes.add(i);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => setScaleTypes => Exception " + e.getMessage());
        }
    }

    @PlayerType.ScaleType.Value
    default int[] getScaleTypes() {
        if (null == mScaleTypes || mScaleTypes.size() == 0) {
            return ((View) this).getResources().getIntArray(R.array.module_mediaplayer_array_scales);
        } else {
            int size = mScaleTypes.size();
            @PlayerType.ScaleType.Value
            int[] values = new int[size];
            for (int i = 0; i < size; i++) {
                values[i] = mScaleTypes.get(i);
            }
            return values;
        }
    }

    /***************/

    @PlayerType.SpeedType.Value
    ArrayList<Integer> mSpeedTypes = new ArrayList<Integer>();

    default void setSpeedTypes(@PlayerType.SpeedType.Value int... ints) {
        try {
            if (null == ints)
                throw new Exception("warning: ints null");
            mSpeedTypes.clear();
            for (int i : ints) {
                mSpeedTypes.add(i);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => setSpeedTypes => Exception " + e.getMessage());
        }
    }

    @PlayerType.SpeedType.Value
    default int[] getSpeedTypes() {
        if (null == mSpeedTypes || mSpeedTypes.size() == 0) {
            return ((View) this).getResources().getIntArray(R.array.module_mediaplayer_array_speeds);
        } else {
            int size = mSpeedTypes.size();
            @PlayerType.SpeedType.Value
            int[] values = new int[size];
            for (int i = 0; i < size; i++) {
                values[i] = mSpeedTypes.get(i);
            }
            return values;
        }
    }

    /***************/

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

    default void clickEpisode(int pos) {
        try {
            OnPlayerEpisodeListener listener = getOnPlayerEpisodeListener();
            if (null == listener)
                throw new Exception("error: listener null");
            listener.onEpisode(pos);
        } catch (Exception e) {
            LogUtil.log("ComponentApiMenu => clickEpisode => " + e.getMessage());
        }
    }

    default void scrollEpisode(int action) {
    }

    default void updateItemSelected(int viewId) {
    }

//    default void setTabSelectedIndex(int index) {
//    }

//    default void setTabCheckedIndex(int index) {
//    }

    default void updateData(int checkedIndex) {
    }

    default void toggleEpisode(int focusId) {

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

//    default void tabCheckedRequestFocus() {
//    }

    default void clearFlag(int index) {
    }

    default void updateFlag(int index, int position) {

    }

    default void loadFlagUrl(@Nullable ImageView imageView, @Nullable String url) {

    }

    default void loadFlagFile(@Nullable ImageView imageView, @Nullable String path) {

    }

    default void loadFlagResource(@Nullable ImageView imageView, @DrawableRes int resId) {

    }
}