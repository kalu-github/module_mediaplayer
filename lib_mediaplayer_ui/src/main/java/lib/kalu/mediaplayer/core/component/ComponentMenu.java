package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.MalformedParameterizedTypeException;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.popu.PopuView;

public class ComponentMenu extends RelativeLayout implements ComponentApiMenu {
    public ComponentMenu(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_menu;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_menu_root;
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {
        try {
            long timeMillis = getTimeMillis();
            if (timeMillis <= 0L)
                throw new Exception();
            long currentTimeMillis = System.currentTimeMillis();
            long cast = currentTimeMillis - timeMillis;
            if (cast <= 4000L)
                throw new Exception();
            hide();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // action_down -> keycode_dpad_down
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            return keycodeDown(KeyEvent.ACTION_DOWN);
        }
        // action_down -> keycode_dpad_up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            return keycodeUp(KeyEvent.ACTION_DOWN);
        }
        // action_up -> keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            return keycodeLeft(KeyEvent.ACTION_UP);
        }
        // action_down -> keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            return keycodeLeft(KeyEvent.ACTION_DOWN);
        }
        // action_up -> keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return keycodeRight(KeyEvent.ACTION_UP);
        }
        // action_down -> keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return keycodeRight(KeyEvent.ACTION_DOWN);
        }
        // keycode_back
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            boolean componentShowing = isComponentShowing();
            if (componentShowing) {
                hide();
                return true;
            }
        }
        // keycode_dpad_center
        else if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            return keycodeCenter(KeyEvent.ACTION_DOWN);
        }
        return false;
    }

    @Override
    public void scrollEpisode(int action) {
        try {
            View focus = findFocus();
            if (null == focus)
                throw new Exception("error: focus null");
            int focusId = focus.getId();
            if (focusId != R.id.module_mediaplayer_component_menu_item_episode)
                throw new Exception("error: focusId != R.id.module_mediaplayer_component_menu_item_data");
            Object tag = focus.getTag();
            if (null == tag)
                throw new Exception("error: tag null");
            int index = (int) tag;
            LogUtil.log("ComponentMenu => keycodeRight => index = " + index);
            if (action == KeyEvent.KEYCODE_DPAD_LEFT && index <= 0)
                throw new Exception("error: index <= 0");

            int episodeItemCount = getEpisodeItemCount();
            LogUtil.log("ComponentMenu => keycodeRight => episodeItemCount = " + episodeItemCount);
            if (action == KeyEvent.KEYCODE_DPAD_RIGHT && index + 1 >= episodeItemCount)
                throw new Exception("error: index+1 >= " + episodeItemCount + ", index = " + index);

            ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            int childCount = dataGroup.getChildCount();

            int start;
            if (action == KeyEvent.KEYCODE_DPAD_LEFT) {
                start = index - 1;
            } else if (action == KeyEvent.KEYCODE_DPAD_RIGHT) {
                start = index - childCount + 2;
            } else {
                start = -1;
            }
            LogUtil.log("ComponentMenu => keycodeRight => start = " + start);
            if (start == -1)
                throw new Exception("error: start = -1");

//            int episodePlayingIndex = getEpisodePlayingIndex();
            for (int i = 0; i < childCount; i++) {

                View childAt = dataGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                int playIndex = i + start;
                loadEpisodeText(i, playIndex, false);
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => scrollEpisode => " + e.getMessage());
        }
    }

    @Override
    public void hide() {
        clearTimeMillis();
        superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_HIDE);
        ComponentApiMenu.super.hide();
    }

    @Override
    public void show() {
        updateTimeMillis();
        superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_SHOW);
        ComponentApiMenu.super.show();
    }

    @Override
    public void clearEpisodeText(int index, boolean changeVisibility) {
        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(index);
            if (null == childAt)
                throw new Exception("error: childAt null");
            if (changeVisibility) {
                childAt.setVisibility(View.INVISIBLE);
            }
            childAt.setTag(null);
            childAt.setSelected(false);
            childAt.setActivated(false);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
        }

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(index);
            if (null == childAt)
                throw new Exception("error: childAt null");
            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_popu);
            textView.setText("");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
        }

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(index);
            if (null == childAt)
                throw new Exception("error: childAt null");
            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_text);
            textView.setText("");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
        }

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(index);
            if (null == childAt)
                throw new Exception("error: childAt null");
            ImageView imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_left);
            imageView.setImageDrawable(null);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
        }

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(index);
            if (null == childAt)
                throw new Exception("error: childAt null");
            ImageView imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_right);
            imageView.setImageDrawable(null);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
        }
    }

    @Override
    public void loadEpisodeText(int childIndex, int episodeIndex, boolean changeVisibility) {
        LogUtil.log("ComponentMenu => loadEpisodeText => childIndex = " + childIndex + ", episodeIndex = " + episodeIndex);

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(childIndex);
            if (null == childAt)
                throw new Exception("error: childAt null");
            int playingIndex = getEpisodePlayingIndex();
            if (changeVisibility) {
                childAt.setVisibility(View.VISIBLE);
            }
            childAt.setTag(episodeIndex);
            childAt.setSelected(playingIndex == episodeIndex);
            if (changeVisibility) {
                childAt.setActivated(playingIndex == episodeIndex);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showEpisodeAt => Exception " + e.getMessage());
        }

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(childIndex);
            if (null == childAt)
                throw new Exception("error: childAt null");
            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_popu);
            textView.setText(String.valueOf(episodeIndex + 1) + "=>sjsxljsjsljljslcjl");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showEpisodeAt => Exception " + e.getMessage());
        }

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(childIndex);
            if (null == childAt)
                throw new Exception("error: childAt null");
            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_text);
            textView.setText(String.valueOf(episodeIndex + 1));
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => loadEpisodeText => Exception " + e.getMessage());
        }

        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            if (null == episodeGroup)
                throw new Exception("error: episodeGroup null");
            View childAt = episodeGroup.getChildAt(childIndex);
            if (null == childAt)
                throw new Exception("error: childAt null");
            int flagLoaction = getEpisodeFlagLoaction();
            ImageView imageView;
            if (flagLoaction == PlayerType.EpisodeFlagLoactionType.LEFT_TOP) {
                imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_left);
            } else {
                imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_right);
            }
            if (null == imageView)
                throw new Exception("error: imageView null");
            int freeItemCount = getEpisodeFreeItemCount();

            // 收费
            String vipImgUrl = getEpisodeFlagVipImgUrl();
            String vipFilePath = getEpisodeFlagVipFilePath();
            int vipResourceId = getEpisodeFlagVipResourceId();
            if (null != vipImgUrl) {
                if (freeItemCount > 0 && episodeIndex >= freeItemCount) {
                    loadEpisodeUrl(imageView, vipImgUrl);
                }
            } else if (null != vipFilePath) {
                if (freeItemCount > 0 && episodeIndex >= freeItemCount) {
                    loadEpisodeFile(imageView, vipFilePath);
                }
            } else if (0 != vipResourceId) {
                if (freeItemCount > 0 && episodeIndex >= freeItemCount) {
                    loadEpisodeResource(imageView, vipResourceId);
                }
            }

            // 免费
            String freeImgUrl = getEpisodeFlagFreeImgUrl();
            String freeFilePath = getEpisodeFlagFreeFilePath();
            int freeResourceId = getEpisodeFlagFreeResourceId();
            if (null != freeImgUrl) {
                if (freeItemCount < 0) {
                    loadEpisodeUrl(imageView, freeImgUrl);
                } else if (episodeIndex < freeItemCount) {
                    loadEpisodeUrl(imageView, freeImgUrl);
                }
            } else if (null != freeFilePath) {
                if (freeItemCount < 0) {
                    loadEpisodeFile(imageView, freeFilePath);
                } else if (episodeIndex < freeItemCount) {
                    loadEpisodeFile(imageView, freeFilePath);
                }
            } else if (0 != freeResourceId) {
                if (freeItemCount < 0) {
                    loadEpisodeResource(imageView, freeResourceId);
                } else if (episodeIndex < freeItemCount) {
                    loadEpisodeResource(imageView, freeResourceId);
                }
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showEpisodeAt => Exception " + e.getMessage());
        }
    }

    /**************/

    @Override
    public void showTabAt(int index) {
        LogUtil.log("ComponentMenu => showTabAt => index = " + index);
        initContentView();
        // 选集
        if (index == 0) {
            initEpisodeView();
        }
        // 倍速
        else if (index == 1) {
            initSpeedView();
        }
        // 画面比例
        else if (index == 2) {
            initScaleView();
        }
    }

    @Override
    public void requestTabAt(int index) {
        try {
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            if (tabCount <= 0)
                throw new Exception("warning: tabCount <=0");
            View childAt = tabGroup.getChildAt(index);
            if (null == childAt)
                throw new Exception("warning: childAt null");
            childAt.requestFocus();
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => requestTabAt => Exception " + e.getMessage());
        }
    }

    @Override
    public void initContentView() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_episode_root).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_speed_root).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_scale_root).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initContentView => Exception " + e.getMessage());
        }
    }

    @Override
    public void initTabData(int index) {
        try {
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            if (tabCount <= 0)
                throw new Exception("warning: tabCount <=0");
            for (int i = 0; i < tabCount; i++) {
                View childAt = tabGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                ((RadioButton) childAt).setChecked(i == index);
                ((RadioButton) childAt).setSelected(i == index);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => setTabChecked => Exception " + e.getMessage());
        }
    }

    @Override
    public void initTabView() {
        try {
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            if (tabCount > 0)
                throw new Exception("warning: tabCount >0");
            boolean hasEpisode;
            try {
                int episodeItemCount = getEpisodeItemCount();
                if (episodeItemCount <= 0)
                    throw new Exception("warning: episodeItemCount " + episodeItemCount);
                int episodePlayingIndex = getEpisodePlayingIndex();
                if (episodePlayingIndex < 0)
                    throw new Exception("warning: episodePlayingIndex " + episodePlayingIndex);
                hasEpisode = true;
            } catch (Exception e) {
                hasEpisode = false;
            }
            String[] stringArray = getResources().getStringArray(R.array.module_mediaplayer_array_tabs);
            for (int i = 0; i < stringArray.length; i++) {
                if (i == 0 && !hasEpisode)
                    continue;
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_tab, tabGroup, true);
                int count = tabGroup.getChildCount();
                View childAt = tabGroup.getChildAt(--count);
                if (null == childAt)
                    continue;
                ((TextView) childAt).setText(stringArray[i]);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initTabView => Exception " + e.getMessage());
        }
    }

    @Override
    public void initEpisodeView() {
        try {
            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
            episodeGroup.setVisibility(View.VISIBLE);
            int episodeCount = episodeGroup.getChildCount();
            if (episodeCount > 0)
                throw new Exception("warning: childCount >0");
            for (int i = 0; i < 10; i++) {
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_episode, episodeGroup, true);
                int count = episodeGroup.getChildCount();
                View childAt = episodeGroup.getChildAt(--count);
                if (null == childAt)
                    continue;
                // 1
                childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            v.setActivated(true);
                        }
                        View viewById = v.findViewById(R.id.module_mediaplayer_component_menu_item_episode_popu);
                        if (null != viewById) {
                            ((PopuView) viewById).setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
                            ((PopuView) viewById).setEllipsize(hasFocus ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
                        }
                    }
                });
                // 2
                clearEpisodeText(i, true);
                // 3
                int episodeItemCount = getEpisodeItemCount();
                if (i >= episodeItemCount)
                    continue;
                int episodePlayingIndex = getEpisodePlayingIndex();
                int num = episodePlayingIndex / 10;
                int start = num * 10;
                if (episodeItemCount > 10) {
                    int length = start + 10;
                    if (length > episodeItemCount) {
                        start -= Math.abs(length - episodeItemCount);
                    }
                }
                int playIndex = i + start - 1;
//                if (playIndex == episodePlayingIndex) {
//                    childAt.setSelected(true);
//                    childAt.setActivated(true);
//                }
                loadEpisodeText(i, playIndex, true);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initEpisodeView => Exception " + e.getMessage());
        }
    }

    @Override
    public void initSpeedView() {
        try {
            ViewGroup speedGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
            speedGroup.setVisibility(View.VISIBLE);
            int speedCount = speedGroup.getChildCount();
            if (speedCount > 0)
                throw new Exception("warning: speedCount >0");
            int[] speedTypes = getSpeedTypes();
            if (null == speedTypes || speedTypes.length == 0)
                throw new Exception("error: speeds null");
            for (int i = 0; i < speedTypes.length; i++) {
                // 1
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_speed, speedGroup, true);
                // 2
                int speedType = speedTypes[i];
                int videoSpeed = getVideoSpeed();
                int count = speedGroup.getChildCount();
                View childAt = speedGroup.getChildAt(--count);
                // 3
                childAt.setTag(speedType);
                childAt.setSelected(videoSpeed == speedType);
                if (speedType == PlayerType.SpeedType._0_5) {
                    ((RadioButton) childAt).setText("0.5");
                } else if (speedType == PlayerType.SpeedType._1_5) {
                    ((RadioButton) childAt).setText("1.5");
                } else if (speedType == PlayerType.SpeedType._2_0) {
                    ((RadioButton) childAt).setText("2.0");
                } else if (speedType == PlayerType.SpeedType._2_5) {
                    ((RadioButton) childAt).setText("2.5");
                } else if (speedType == PlayerType.SpeedType._3_0) {
                    ((RadioButton) childAt).setText("3.0");
                } else if (speedType == PlayerType.SpeedType._3_5) {
                    ((RadioButton) childAt).setText("3.5");
                } else if (speedType == PlayerType.SpeedType._4_0) {
                    ((RadioButton) childAt).setText("4.0");
                } else if (speedType == PlayerType.SpeedType._4_5) {
                    ((RadioButton) childAt).setText("4.5");
                } else if (speedType == PlayerType.SpeedType._5_0) {
                    ((RadioButton) childAt).setText("5.0");
                } else {
                    ((RadioButton) childAt).setText("1.0");
                }
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initSpeedView => Exception " + e.getMessage());
        }
    }

    @Override
    public void initScaleView() {
        try {
            ViewGroup scaleGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
            scaleGroup.setVisibility(View.VISIBLE);
            int scaleCount = scaleGroup.getChildCount();
            if (scaleCount > 0)
                throw new Exception("warning: scaleCount >0");
            int[] scaleTypes = getScaleTypes();
            if (null == scaleTypes || scaleTypes.length == 0)
                throw new Exception("error: scaleTypes null");
            for (int i = 0; i < scaleTypes.length; i++) {
                // 1
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_scale, scaleGroup, true);
                // 2
                int scaleType = scaleTypes[i];
                int videoScaleType = getVideoScaleType();
                int count = scaleGroup.getChildCount();
                View childAt = scaleGroup.getChildAt(--count);
                // 3
                childAt.setTag(scaleTypes[i]);
                childAt.setSelected(videoScaleType == scaleType);
                if (scaleType == PlayerType.ScaleType.REAL) {
                    ((RadioButton) childAt).setText("原始");
                } else if (scaleType == PlayerType.ScaleType.FULL) {
                    ((RadioButton) childAt).setText("全屏");
                } else if (scaleType == PlayerType.ScaleType._1_1) {
                    ((RadioButton) childAt).setText("1:1");
                } else if (scaleType == PlayerType.ScaleType._4_3) {
                    ((RadioButton) childAt).setText("4:3");
                } else if (scaleType == PlayerType.ScaleType._5_4) {
                    ((RadioButton) childAt).setText("5:4");
                } else if (scaleType == PlayerType.ScaleType._16_9) {
                    ((RadioButton) childAt).setText("16:9");
                } else if (scaleType == PlayerType.ScaleType._16_10) {
                    ((RadioButton) childAt).setText("16:10");
                } else {
                    ((RadioButton) childAt).setText("自动");
                }
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initScaleView => Exception " + e.getMessage());
        }
    }

    @Override
    public boolean keycodeLeft(int action) {
        try {

            if (action == KeyEvent.ACTION_UP) {
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("error: focus null");
                int focusId = focus.getId();
                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                    updateTimeMillis();
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int indexOfChild = tabGroup.indexOfChild(focus);
                    initTabData(indexOfChild);
                    showTabAt(indexOfChild);
                    return true;
                }
            } else if (action == KeyEvent.ACTION_DOWN) {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: showing false");
                updateTimeMillis();
                View focus = findFocus();
                int focusId = focus.getId();

                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int indexOfChild = tabGroup.indexOfChild(focus);
                    if (indexOfChild <= 0) {
                        return true;
                    }
                }
                // 选集
                else if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {
                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
                    int indexOfChild = dataGroup.indexOfChild(focus);
                    if (indexOfChild <= 0) {
                        scrollEpisode(KeyEvent.KEYCODE_DPAD_LEFT);
                        return true;
                    } else {
                        focus.setActivated(false);
                    }
                }
                // 倍速
                else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
                    int indexOfChild = dataGroup.indexOfChild(focus);
                    if (indexOfChild <= 0) {
                        return true;
                    }
                }
                // 画面比例
                else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
                    int indexOfChild = dataGroup.indexOfChild(focus);
                    if (indexOfChild <= 0) {
                        return true;
                    }
                }
            }
            throw new Exception("error: not find");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => keycodeLeft => Exception " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean keycodeRight(int action) {
        try {
            if (action == KeyEvent.ACTION_UP) {
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("error: focus null");
                int focusId = focus.getId();
                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                    updateTimeMillis();
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int indexOfChild = tabGroup.indexOfChild(focus);
                    initTabData(indexOfChild);
                    showTabAt(indexOfChild);
                    return true;
                }
            } else if (action == KeyEvent.ACTION_DOWN) {
                boolean showing = isComponentShowing();
                if (!showing)
                    throw new Exception("warning: showing false");
                updateTimeMillis();
                View focus = findFocus();
                int focusId = focus.getId();

                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int tabCount = tabGroup.getChildCount();
                    int indexOfChild = tabGroup.indexOfChild(focus);
                    if (indexOfChild + 1 >= tabCount) {
                        return true;
                    }
                }
                // 选集
                else if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {
                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
                    int dataCount = dataGroup.getChildCount();
                    int indexOfChild = dataGroup.indexOfChild(focus);
                    if (indexOfChild + 1 >= dataCount) {
                        scrollEpisode(KeyEvent.KEYCODE_DPAD_RIGHT);
                        return true;
                    } else {
                        focus.setActivated(false);
                    }
                }
                // 倍速
                else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
                    int dataCount = dataGroup.getChildCount();
                    int indexOfChild = dataGroup.indexOfChild(focus);
                    if (indexOfChild + 1 >= dataCount) {
                        return true;
                    }
                }
                // 画面比例
                else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
                    int dataCount = dataGroup.getChildCount();
                    int indexOfChild = dataGroup.indexOfChild(focus);
                    if (indexOfChild + 1 >= dataCount) {
                        return true;
                    }
                }
            }
            throw new Exception("error: not find");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => keycodeRight => Exception " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean keycodeDown(int action) {
        try {
            if (action == KeyEvent.ACTION_DOWN) {
                boolean playing = isPlaying();
                if (!playing)
                    throw new Exception("error: playing false");
                boolean componentShowing = isComponentShowing();
                if (componentShowing) {
                    View focus = findFocus();
                    if (null == focus)
                        throw new Exception("warning: focus null");
                    int focusId = focus.getId();
                    // 选集
                    if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {
                        requestTabAt(0);
                        return true;
                    }
                    // 倍速
                    else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
                        requestTabAt(1);
                        return true;
                    }
                    // 画面比例
                    else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
                        requestTabAt(2);
                        return true;
                    }
                } else {
                    show();
                    initTabView();
                    initTabData(0);
                    showTabAt(0);
                    requestTabAt(0);
                    return true;
                }
            }
            throw new Exception("warning: not find");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => keycodeDown => Exception " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean keycodeUp(int action) {
        try {

            if (action == KeyEvent.ACTION_DOWN) {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                // 菜单
                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int indexOfChild = tabGroup.indexOfChild(focus);
                    // 选集
                    if (indexOfChild == 0) {
                        boolean pass = false;
                        ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
                        int episodeCount = episodeGroup.getChildCount();
                        for (int i = 0; i < episodeCount; i++) {
                            View childAt = episodeGroup.getChildAt(i);
                            if (null == childAt)
                                continue;
                            boolean activated = childAt.isActivated();
                            if (!activated)
                                continue;
                            pass = true;
                            childAt.requestFocus();
                            break;
                        }
                        if (pass) {
                            updateTimeMillis();
                            return true;
                        }
                    }
                    // 倍速
                    else if (indexOfChild == 1) {
                        boolean pass = false;
                        RadioGroup speedGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
                        int speedCount = speedGroup.getChildCount();
                        for (int i = 0; i < speedCount; i++) {
                            View childAt = speedGroup.getChildAt(i);
                            if (null == childAt)
                                continue;
                            boolean selected = childAt.isSelected();
                            if (!selected)
                                continue;
                            pass = true;
                            childAt.requestFocus();
                            break;
                        }
                        if (pass) {
                            updateTimeMillis();
                            return true;
                        }
                    }
                    // 画面比例
                    else if (indexOfChild == 2) {
                        boolean pass = false;
                        RadioGroup scaleGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
                        int scaleCount = scaleGroup.getChildCount();
                        for (int i = 0; i < scaleCount; i++) {
                            View childAt = scaleGroup.getChildAt(i);
                            if (null == childAt)
                                continue;
                            boolean selected = childAt.isSelected();
                            if (!selected)
                                continue;
                            pass = true;
                            childAt.requestFocus();
                            break;
                        }
                        if (pass) {
                            updateTimeMillis();
                            return true;
                        }
                    }
                }
            }
            throw new Exception("warning: not find");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => keycodeUp => Exception " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean keycodeCenter(int action) {
        try {
            if (action == KeyEvent.ACTION_DOWN) {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");

                int focusId = focus.getId();

                // 选集
                if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {

                    int playingIndex = getEpisodePlayingIndex();
                    int episodeIndex = (int) focus.getTag();
                    if (playingIndex != episodeIndex) {
                        ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
                        int episodeCount = episodeGroup.getChildCount();
                        for (int i = 0; i < episodeCount; i++) {
                            View childAt = episodeGroup.getChildAt(i);
                            if (null == childAt)
                                continue;
                            childAt.setSelected(childAt == focus);
                            childAt.setActivated(childAt == focus);
                        }
                        hide();
                        stop();
                        clickEpisode(episodeIndex);
                        return true;
                    }
                }
                // 倍速
                else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
                    RadioGroup speedGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
                    int speedCount = speedGroup.getChildCount();
                    for (int i = 0; i < speedCount; i++) {
                        View childAt = speedGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        childAt.setSelected(childAt == focus);
                    }
                    Object tag = focus.getTag();
                    setVideoSpeed((Integer) tag);
                    return true;
                }
                // 画面比例
                else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
                    RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
                    int dataCount = dataGroup.getChildCount();
                    for (int i = 0; i < dataCount; i++) {
                        View childAt = dataGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        childAt.setSelected(childAt == focus);
                    }
                    Object tag = focus.getTag();
                    setVideoScaleType((Integer) tag);
                    return true;
                }
            }
            throw new Exception("warning: not find");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => keycodeCenter => keycode_dpad_center => " + e.getMessage());
            return false;
        }
    }
}
