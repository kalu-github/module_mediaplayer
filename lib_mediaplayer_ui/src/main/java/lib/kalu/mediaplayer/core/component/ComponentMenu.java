package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentMenu extends RelativeLayout implements ComponentApi {
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
//        try {
//            long timeMillis = getTimeMillis();
//            if (timeMillis <= 0L)
//                throw new Exception();
//            long currentTimeMillis = System.currentTimeMillis();
//            long cast = currentTimeMillis - timeMillis;
//            if (cast <= 4000L)
//                throw new Exception();
//            hide();
//        } catch (Exception e) {
//        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtil.log("ComponentMenu => dispatchKeyEvent => action =  " + event.getAction() + ", keyCode = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount());

        // action_up keycode_dpad_down
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            try {
                boolean componentShowing = isComponentShowing();
                if (componentShowing)
                    throw new Exception("warning: componentShowing true");
                show();
                superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_SHOW);
                showTabGroupView();
                showTabGroupData(0);
                requestFocusedTabGroup();
            } catch (Exception e) {
            }
            return true;
        }
        // action_down keycode_dpad_down
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            try {
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_item_content)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_content");
                requestFocusedTabGroup();
                return true;
            } catch (Exception e) {
            }
        }
        // action_down -> keycode_dpad_up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_tab_group_item)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_tab_group_item");
//                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//                int indexOfChild = viewGroup.indexOfChild(focus);
//                // 选集
//                if (indexOfChild == 0) {
//                    boolean pass = false;
//                    ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//                    int episodeCount = episodeGroup.getChildCount();
//                    for (int i = 0; i < episodeCount; i++) {
//                        View childAt = episodeGroup.getChildAt(i);
//                        if (null == childAt)
//                            continue;
//                        boolean activated = childAt.isActivated();
//                        if (!activated)
//                            continue;
//                        pass = true;
//                        childAt.requestFocus();
//                        break;
//                    }
//                    if (pass) {
//                        updateTimeMillis();
//                        return true;
//                    }
//                }
//
//                else

                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                int speedCount = viewGroup.getChildCount();
                for (int i = 0; i < speedCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    boolean selected = childAt.isSelected();
                    if (!selected)
                        continue;
                    childAt.requestFocus();
                    //  updateTimeMillis();
                    return true;
                }
                throw new Exception("warning: not find");
            } catch (Exception e) {
                LogUtil.log("ComponentMenu => keycodeUp => Exception " + e.getMessage());
                return false;
            }
        }
        // action_down -> keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {

//                if (action == KeyEvent.ACTION_UP) {
//                    View focus = findFocus();
//                    if (null == focus)
//                        throw new Exception("error: focus null");
//                    int focusId = focus.getId();
//                    if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
//                        updateTimeMillis();
//                        ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
//                        int indexOfChild = tabGroup.indexOfChild(focus);
//                        showTabAt(indexOfChild);
//                        return true;
//                    }
//                } else

                // updateTimeMillis();
                View focus = findFocus();
                int focusId = focus.getId();

                // 菜单
                if (focusId == R.id.module_mediaplayer_component_menu_tab_group_item) {
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                    int indexOfChild = viewGroup.indexOfChild(focus);
                    if (indexOfChild <= 0) {

                    } else {
//                        selectedTabGroup(--indexOfChild);
//                        requestFocusedTabGroup();
                        showTabGroupData(--indexOfChild);
                        return viewGroup.dispatchKeyEvent(event);
                    }
                    return true;
                }
                // 内容
                else {
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                    int indexOfChild = viewGroup.indexOfChild(focus);
                    if (indexOfChild <= 0) {

                    } else {
                        // selectedTabGroup(--indexOfChild);
                        //  requestFocusedTabGroup();
                        return viewGroup.dispatchKeyEvent(event);
                    }
                }
//                // 选集
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild <= 0) {
//                        scrollEpisodeText(0, KeyEvent.KEYCODE_DPAD_LEFT);
//                        return true;
//                    } else {
//                        int nextIndexOfChild = indexOfChild - 1;
//                        updateEpisodeText(nextIndexOfChild);
//                    }
//                }
//                // 倍速
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild <= 0) {
//                        return true;
//                    }
//                }
//                // 画面比例
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild <= 0) {
//                        return true;
//                    }
//                }
//                throw new Exception("error: not find");
            } catch (Exception e) {
            }
//            return true;
        }
        // action_up -> keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//            return keycodeLeft(KeyEvent.ACTION_UP);
//            return true;
        }
        // action_down -> keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            // return keycodeRight(KeyEvent.ACTION_DOWN);

            try {

//                if (action == KeyEvent.ACTION_UP) {
//                    View focus = findFocus();
//                    if (null == focus)
//                        throw new Exception("error: focus null");
//                    int focusId = focus.getId();
//                    if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
//                        updateTimeMillis();
//                        ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
//                        int indexOfChild = tabGroup.indexOfChild(focus);
//                        showTabAt(indexOfChild);
//                        return true;
//                    }
//                } else

                // updateTimeMillis();
                View focus = findFocus();
                int focusId = focus.getId();

                // 菜单
                if (focusId == R.id.module_mediaplayer_component_menu_tab_group_item) {
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                    int childCount = viewGroup.getChildCount();
                    int indexOfChild = viewGroup.indexOfChild(focus);
                    if (indexOfChild + 1 >= childCount) {

                    } else {
//                        selectedTabGroup(--indexOfChild);
//                        requestFocusedTabGroup();
                        showTabGroupData(++indexOfChild);
                        return viewGroup.dispatchKeyEvent(event);
                    }
                }
                // 内容
                else {
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                    int childCount = viewGroup.getChildCount();
                    int indexOfChild = viewGroup.indexOfChild(focus);
                    if (indexOfChild + 1 >= childCount) {

                    } else {
                        // selectedTabGroup(--indexOfChild);
                        //  requestFocusedTabGroup();
                        return viewGroup.dispatchKeyEvent(event);
                    }
                }
//                // 选集
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild <= 0) {
//                        scrollEpisodeText(0, KeyEvent.KEYCODE_DPAD_LEFT);
//                        return true;
//                    } else {
//                        int nextIndexOfChild = indexOfChild - 1;
//                        updateEpisodeText(nextIndexOfChild);
//                    }
//                }
//                // 倍速
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild <= 0) {
//                        return true;
//                    }
//                }
//                // 画面比例
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild <= 0) {
//                        return true;
//                    }
//                }
//                throw new Exception("error: not find");
            } catch (Exception e) {
            }

            return true;
        }
//        // action_up -> keycode_dpad_right
//        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            return keycodeRight(KeyEvent.ACTION_UP);
//        }
//        // keycode_back
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            boolean componentShowing = isComponentShowing();
//            if (componentShowing) {
//                hide();
//                superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_HIDE);
//                return true;
//            }
//        }
//        // keycode_dpad_center
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//            return keycodeCenter(KeyEvent.ACTION_DOWN);
//        }


        boolean componentShowing = isComponentShowing();
        if (componentShowing)
            return true;

        return false;
    }

    @Override
    public void hide() {
//        clearEpisodeText();
//        clearTimeMillis();
        ComponentApi.super.hide();
    }

    @Override
    public void show() {
//        updateTimeMillis();
        ComponentApi.super.show();
    }

//    public void scrollEpisodeText(int childIndex, int action) {
//        try {
//            View focus = findFocus();
//            if (null == focus)
//                throw new Exception("error: focus null");
//            int focusId = focus.getId();
//            if (focusId != R.id.module_mediaplayer_component_menu_item_episode)
//                throw new Exception("error: focusId != R.id.module_mediaplayer_component_menu_item_data");
//            Object tag = focus.getTag();
//            if (null == tag)
//                throw new Exception("error: tag null");
//            int index = (int) tag;
//            LogUtil.log("ComponentMenu => keycodeRight => index = " + index);
//            if (action == KeyEvent.KEYCODE_DPAD_LEFT && index <= 0)
//                throw new Exception("error: index <= 0");
//
//            int episodeItemCount = getEpisodeItemCount();
//            LogUtil.log("ComponentMenu => keycodeRight => episodeItemCount = " + episodeItemCount);
//            if (action == KeyEvent.KEYCODE_DPAD_RIGHT && index + 1 >= episodeItemCount)
//                throw new Exception("error: index+1 >= " + episodeItemCount + ", index = " + index);
//
//            ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            int childCount = dataGroup.getChildCount();
//
//            int start;
//            if (action == KeyEvent.KEYCODE_DPAD_LEFT) {
//                start = index - 1;
//            } else if (action == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                start = index - childCount + 2;
//            } else {
//                start = -1;
//            }
//            LogUtil.log("ComponentMenu => keycodeRight => start = " + start);
//            if (start == -1)
//                throw new Exception("error: start = -1");
//
//            int playIndex = getEpisodePlayingIndex();
//            for (int i = 0; i < childCount; i++) {
//
//                View childAt = dataGroup.getChildAt(i);
//                if (null == childAt)
//                    continue;
//                int episodeIndex = i + start;
//                loadEpisodeText(i, episodeIndex, playIndex, false);
//            }
//
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => scrollEpisodeText => " + e.getMessage());
//        }
//    }

//    public void clearEpisodeText() {
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            episodeGroup.removeAllViews();
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
//        }
//    }

//    public void updateEpisodeText(int childIndex) {
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            int childCount = episodeGroup.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View childAt = episodeGroup.getChildAt(i);
//                childAt.setActivated(i == childIndex);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => updateEpisodeText => Exception " + e.getMessage());
//        }
//    }

//    public void clickEpisodeText(int childIndex, int episodeIndex) {
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            int childCount = episodeGroup.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View childAt = episodeGroup.getChildAt(i);
//                childAt.setSelected(i == childIndex);
//                childAt.setActivated(i == childIndex);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clickEpisodeText => Exception " + e.getMessage());
//        }
//    }

//    public void clearEpisodeText(int childIndex, boolean changeVisibility) {
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            if (changeVisibility) {
//                childAt.setVisibility(View.INVISIBLE);
//            }
//            childAt.setTag(null);
//            childAt.setHovered(false);
//            childAt.setSelected(false);
//            childAt.setActivated(false);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
//        }
//
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_popu);
//            textView.setText("");
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
//        }
//
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_text);
//            textView.setText("");
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
//        }
//
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            ImageView imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_left);
//            imageView.setImageDrawable(null);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
//        }
//
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            ImageView imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_right);
//            imageView.setImageDrawable(null);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clearEpisodeText => Exception " + e.getMessage());
//        }
//    }

//    public void loadEpisodeText(int childIndex, int episodeIndex, int playIndex, boolean changeVisibility) {
////        LogUtil.log("ComponentMenu => loadEpisodeText => childIndex = " + childIndex + ", episodeIndex = " + episodeIndex + ", playIndex = " + playIndex);
//
//        // popu
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_popu);
//            String popuText = initEpisodePopuText(episodeIndex);
//            textView.setText(popuText);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showEpisodeAt => Exception " + e.getMessage());
//        }
//
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            if (changeVisibility) {
//                childAt.setVisibility(View.VISIBLE);
//            }
//            childAt.setTag(episodeIndex);
//            childAt.setSelected(playIndex == episodeIndex);
//            if (changeVisibility) {
//                childAt.setActivated(playIndex == episodeIndex);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showEpisodeAt => Exception " + e.getMessage());
//        }
//
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
//            TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_text);
//            textView.setText(String.valueOf(episodeIndex + 1));
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => loadEpisodeText => Exception " + e.getMessage());
//        }
//
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            if (null == episodeGroup)
//                throw new Exception("error: episodeGroup null");
//            View childAt = episodeGroup.getChildAt(childIndex);
//            if (null == childAt)
//                throw new Exception("error: childAt null");
////            int flagLoaction = getEpisodeFlagLoaction();
//            ImageView imageView;
////            if (flagLoaction == PlayerType.EpisodeFlagLoactionType.LEFT_TOP) {
////                imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_left);
////            } else {
//            imageView = childAt.findViewById(R.id.module_mediaplayer_component_menu_item_episode_img_top_right);
////            }
//            if (null == imageView)
//                throw new Exception("error: imageView null");
//            imageView.setImageDrawable(null);
//
//            int freeItemCount = getEpisodeFreeItemCount();
//
//            // 收费
//            String vipImgUrl = getEpisodeFlagVipImgUrl();
//            String vipFilePath = getEpisodeFlagVipFilePath();
//            int vipResourceId = getEpisodeFlagVipResourceId();
//            if (null != vipImgUrl) {
//                if (freeItemCount > 0 && episodeIndex >= freeItemCount) {
//                    loadEpisodeUrl(imageView, vipImgUrl);
//                }
//            } else if (null != vipFilePath) {
//                if (freeItemCount > 0 && episodeIndex >= freeItemCount) {
//                    loadEpisodeFile(imageView, vipFilePath);
//                }
//            } else if (0 != vipResourceId) {
//                if (freeItemCount > 0 && episodeIndex >= freeItemCount) {
//                    loadEpisodeResource(imageView, vipResourceId);
//                }
//            }
//
//            // 免费
//            String freeImgUrl = getEpisodeFlagFreeImgUrl();
//            String freeFilePath = getEpisodeFlagFreeFilePath();
//            int freeResourceId = getEpisodeFlagFreeResourceId();
//            if (null != freeImgUrl) {
//                if (freeItemCount < 0) {
//                    loadEpisodeUrl(imageView, freeImgUrl);
//                } else if (episodeIndex < freeItemCount) {
//                    loadEpisodeUrl(imageView, freeImgUrl);
//                }
//            } else if (null != freeFilePath) {
//                if (freeItemCount < 0) {
//                    loadEpisodeFile(imageView, freeFilePath);
//                } else if (episodeIndex < freeItemCount) {
//                    loadEpisodeFile(imageView, freeFilePath);
//                }
//            } else if (0 != freeResourceId) {
//                if (freeItemCount < 0) {
//                    loadEpisodeResource(imageView, freeResourceId);
//                } else if (episodeIndex < freeItemCount) {
//                    loadEpisodeResource(imageView, freeResourceId);
//                }
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showEpisodeAt => Exception " + e.getMessage());
//        }
//    }

    /**************/

//    public int[] initHideContentData() {
//        return new int[]{R.id.module_mediaplayer_component_menu_episode_root, R.id.module_mediaplayer_component_menu_speed_root, R.id.module_mediaplayer_component_menu_scale_root};
//    }

//    public void initHideContentView() {
//        try {
//            int[] ints = initHideContentData();
//            if (null == ints || ints.length == 0)
//                throw new Exception("error: ints null");
//            for (int id : ints) {
//                View viewById = findViewById(id);
//                if (null == viewById)
//                    continue;
//                viewById.setVisibility(View.GONE);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => initHideContentView => Exception " + e.getMessage());
//        }
//    }
    public String[] initTabData() {
        return getResources().getStringArray(R.array.module_mediaplayer_array_tabs);
    }

    public void showTabContent(int index) {
        try {
            if (index < 0)
                throw new Exception("warning: index < 0");
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int tabCount = tabGroup.getChildCount();
            if (tabCount <= 0)
                throw new Exception("warning: tabCount <= 0");
            if (index >= tabCount)
                throw new Exception("warning: index >= tabCount");
//            if (index == 0) {
//
//            }
            // 倍速
            if (index == 0) {
                showTabSpeedView();
            } else if (index == 1) {
                showTabScaleView();
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showTabContent => Exception " + e.getMessage());
        }
    }

    public int[] initScaleData() {
        return getResources().getIntArray(R.array.module_mediaplayer_array_scales);
    }

    public void showTabScaleView() {
        try {
            int[] scaleData = initScaleData();
            if (null == scaleData)
                throw new Exception("error: scaleData null");
            if (scaleData.length == 0)
                throw new Exception("error: scaleData.length == 0");
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
            viewGroup.removeAllViews();
            for (int i = 0; i < scaleData.length; i++) {
                // 1
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_content, viewGroup, true);
                // 2
                int scaleType = scaleData[i];
                int videoScaleType = getVideoScaleType();
                View childAt = viewGroup.getChildAt(i);
                // 3
                childAt.setTag(scaleType);
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
            LogUtil.log("ComponentMenu => showTabScaleView => Exception " + e.getMessage());
        }
    }

    public int[] initSpeedData() {
        return getResources().getIntArray(R.array.module_mediaplayer_array_speeds);
    }

    private void showTabSpeedView() {
        try {
            int[] speedData = initSpeedData();
            if (null == speedData)
                throw new Exception("error: speedData null");
            if (speedData.length == 0)
                throw new Exception("error: speedData.length == 0");
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
            viewGroup.removeAllViews();
            for (int i = 0; i < speedData.length; i++) {
                // 1
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_content, viewGroup, true);
                // 2
                int speedType = speedData[i];
                int videoSpeed = getVideoSpeed();
                View childAt = viewGroup.getChildAt(i);
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
            LogUtil.log("ComponentMenu => showTabSpeedView => Exception " + e.getMessage());
        }
    }

    private void showTabGroupView() {
        try {
            String[] tabData = initTabData();
            if (null == tabData)
                throw new Exception("error: tabData null");
            if (tabData.length == 0)
                throw new Exception("error: tabData.length == 0");
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int tabCount = tabGroup.getChildCount();
            if (tabCount > 0)
                throw new Exception("warning: tabCount >0");
//            boolean hasEpisode;
//            try {
//                int episodeItemCount = getEpisodeItemCount();
//                if (episodeItemCount <= 0)
//                    throw new Exception("warning: episodeItemCount " + episodeItemCount);
//                int episodePlayingIndex = getEpisodePlayingIndex();
//                if (episodePlayingIndex < 0)
//                    throw new Exception("warning: episodePlayingIndex " + episodePlayingIndex);
//                hasEpisode = true;
//            } catch (Exception e) {
//                hasEpisode = false;
//            }
            for (int i = 0; i < tabData.length; i++) {
//                if (i == 0 && !hasEpisode)
//                    continue;
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_tab, tabGroup, true);
                View childAt = tabGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                ((TextView) childAt).setText(tabData[i]);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showTabGroupView => Exception " + e.getMessage());
        }
    }

    public void showTabGroupData(int index) {
        try {
            if (index < 0)
                throw new Exception("warning: index < 0");
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int tabCount = tabGroup.getChildCount();
            if (tabCount <= 0)
                throw new Exception("warning: tabCount <= 0");
            if (index >= tabCount)
                throw new Exception("warning: index >= tabCount");
            //
            //            initHideContentView();
            // 选中
            selectedTabGroup(index);
            // 内容
            showTabContent(index);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showTabGroupData => Exception " + e.getMessage());
        }
    }

    private void requestFocusedTabGroup() {
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("warning: childCount <= 0");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                boolean selected = childAt.isSelected();
                if (!selected)
                    continue;
                childAt.requestFocus();
                return;
            }
            throw new Exception("warning: not find");
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => requestFocusedTabGroup => Exception " + e.getMessage());
        }
    }

    private void selectedTabGroup(int index) {
        try {
            if (index < 0)
                throw new Exception("warning: index < 0");
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("warning: childCount <= 0");
            if (index >= childCount)
                throw new Exception("warning: index >= childCount");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                childAt.setSelected(i == index);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => selectedTabGroup => Exception " + e.getMessage());
        }
    }

//    public void initEpisodeView() {
//        try {
//            ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//            episodeGroup.setVisibility(View.VISIBLE);
//            int episodeCount = episodeGroup.getChildCount();
//            if (episodeCount > 0)
//                throw new Exception("warning: childCount >0");
//            for (int i = 0; i < 10; i++) {
//                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_episode, episodeGroup, true);
//                View childAt = episodeGroup.getChildAt(i);
//                if (null == childAt)
//                    continue;
//                // 1
//                childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        // 1
//                        TextView episodeView = v.findViewById(R.id.module_mediaplayer_component_menu_item_episode_text);
//                        episodeView.setHovered(hasFocus);
//                        // 2
//                        View popuView = v.findViewById(R.id.module_mediaplayer_component_menu_item_episode_popu);
//                        popuView.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
//                        ((PopuView) popuView).setEllipsize(hasFocus ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
//                    }
//                });
//                // 2
//                clearEpisodeText(i, true);
//                int episodeItemCount = getEpisodeItemCount();
//                if (i >= episodeItemCount)
//                    continue;
//                // 3
//                int playIndex = getEpisodePlayingIndex();
//                int mult = playIndex / 10;
//                int start = mult * 10;
//                if (episodeItemCount > 10) {
//                    int end = start + 10;
//                    if (end > episodeItemCount) {
//                        int value = end - episodeItemCount; // 6
//                        start -= value;
//                    }
//                }
//                int episodeIndex = start + i;
//                loadEpisodeText(i, episodeIndex, playIndex, true);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => initEpisodeView => Exception " + e.getMessage());
//        }
//    }

//    public boolean keycodeRight(int action) {
//        try {
//            if (action == KeyEvent.ACTION_UP) {
//                View focus = findFocus();
//                if (null == focus)
//                    throw new Exception("error: focus null");
//                int focusId = focus.getId();
//                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
//                    updateTimeMillis();
//                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
//                    int indexOfChild = tabGroup.indexOfChild(focus);
//                    showTabAt(indexOfChild);
//                    return true;
//                }
//            } else if (action == KeyEvent.ACTION_DOWN) {
//                boolean showing = isComponentShowing();
//                if (!showing)
//                    throw new Exception("warning: showing false");
//                updateTimeMillis();
//                View focus = findFocus();
//                int focusId = focus.getId();
//
//                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
//                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
//                    int tabCount = tabGroup.getChildCount();
//                    int indexOfChild = tabGroup.indexOfChild(focus);
//                    if (indexOfChild + 1 >= tabCount) {
//                        return true;
//                    } else {
//                        initTabUnderLine(-1);
//                    }
//                }
//                // 选集
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//                    int childCount = dataGroup.getChildCount();
//                    int episodeItemCount = getEpisodeItemCount();
//                    int realCount = Math.min(episodeItemCount, childCount);
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    int nextIndexOfChild = indexOfChild + 1;
//                    if (nextIndexOfChild >= realCount) {
//                        scrollEpisodeText(indexOfChild, KeyEvent.KEYCODE_DPAD_RIGHT);
//                        return true;
//                    } else {
//                        updateEpisodeText(nextIndexOfChild);
//                    }
//                }
//                // 倍速
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
//                    int dataCount = dataGroup.getChildCount();
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild + 1 >= dataCount) {
//                        return true;
//                    }
//                }
//                // 画面比例
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
//                    ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
//                    int dataCount = dataGroup.getChildCount();
//                    int indexOfChild = dataGroup.indexOfChild(focus);
//                    if (indexOfChild + 1 >= dataCount) {
//                        return true;
//                    }
//                }
//            }
//            throw new Exception("error: not find");
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => keycodeRight => Exception " + e.getMessage());
//            return false;
//        }
//    }

//    public boolean keycodeCenter(int action) {
//        try {
//            if (action == KeyEvent.ACTION_DOWN) {
//                boolean componentShowing = isComponentShowing();
//                if (!componentShowing)
//                    throw new Exception("warning: componentShowing false");
//                View focus = findFocus();
//                if (null == focus)
//                    throw new Exception("warning: focus null");
//
//                int focusId = focus.getId();
//
//                // 选集
//                if (focusId == R.id.module_mediaplayer_component_menu_item_episode) {
//
//                    int playingIndex = getEpisodePlayingIndex();
//                    int episodeIndex = (int) focus.getTag();
//                    if (playingIndex != episodeIndex) {
//                        hide();
//                        stop();
//                        ViewGroup episodeGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_root);
//                        int index = episodeGroup.indexOfChild(focus);
//                        clickEpisodeText(index, episodeIndex);
//                        callListener(episodeIndex);
//                        return true;
//                    }
//                }
//                // 倍速
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_speed) {
//                    RadioGroup speedGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_root);
//                    int speedCount = speedGroup.getChildCount();
//                    for (int i = 0; i < speedCount; i++) {
//                        View childAt = speedGroup.getChildAt(i);
//                        if (null == childAt)
//                            continue;
//                        childAt.setSelected(childAt == focus);
//                    }
//                    Object tag = focus.getTag();
//                    setVideoSpeed((Integer) tag);
//                    return true;
//                }
//                // 画面比例
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_scale) {
//                    RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_scale_root);
//                    int dataCount = dataGroup.getChildCount();
//                    for (int i = 0; i < dataCount; i++) {
//                        View childAt = dataGroup.getChildAt(i);
//                        if (null == childAt)
//                            continue;
//                        childAt.setSelected(childAt == focus);
//                    }
//                    Object tag = focus.getTag();
//                    setVideoScaleType((Integer) tag);
//                    return true;
//                }
//            }
//            throw new Exception("warning: not find");
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => keycodeCenter => keycode_dpad_center => " + e.getMessage());
//            return false;
//        }
//    }

    /****************/

//    private int getEpisodeItemCount() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            int episodeItemCount = jsonObject.optInt("EpisodeItemCount", 0);
//            if (episodeItemCount <= 0)
//                throw new Exception("warning: episodeItemCount " + episodeItemCount);
//            return episodeItemCount;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeItemCount => " + e.getMessage());
//            return -1;
//        }
//    }

//    private int getEpisodePlayingIndex() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            int episodePlayingIndex = jsonObject.optInt("EpisodePlayingIndex", -1);
//            if (episodePlayingIndex < 0)
//                throw new Exception("warning: episodePlayingIndex " + episodePlayingIndex);
//            return episodePlayingIndex;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodePlayingIndex => " + e.getMessage());
//            return -1;
//        }
//    }

//    private int getEpisodeFreeItemCount() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            int episodeFreeItemCount = jsonObject.optInt("EpisodeFreeItemCount", -1);
//            if (episodeFreeItemCount < 0)
//                throw new Exception("warning: episodeFreeItemCount " + episodeFreeItemCount);
//            return episodeFreeItemCount;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeFreeItemCount => " + e.getMessage());
//            return -1;
//        }
//    }

//    private String getEpisodeFlagFreeImgUrl() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            String url = jsonObject.optString("EpisodeFlagFreeImgUrl", null);
//            if (null == url || url.length() == 0)
//                throw new Exception("warning: url null");
//            return url;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeFlagFreeImgUrl => " + e.getMessage());
//            return null;
//        }
//    }

//    private String getEpisodeFlagFreeFilePath() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            String path = jsonObject.optString("EpisodeFlagFreeFilePath", null);
//            if (null == path || path.length() == 0)
//                throw new Exception("warning: path null");
//            return path;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeFlagFreeFilePath => " + e.getMessage());
//            return null;
//        }
//    }

//    @DrawableRes
//    private int getEpisodeFlagFreeResourceId() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            int resourceId = jsonObject.optInt("EpisodeFlagFreeResourceId", 0);
//            if (resourceId == 0)
//                throw new Exception("warning: resourceId = 0");
//            return resourceId;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeFlagFreeResourceId => " + e.getMessage());
//            return 0;
//        }
//    }

//    private String getEpisodeFlagVipImgUrl() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            String url = jsonObject.optString("EpisodeFlagVipImgUrl", null);
//            if (null == url || url.length() == 0)
//                throw new Exception("warning: url null");
//            return url;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeFlagVipImgUrl => " + e.getMessage());
//            return null;
//        }
//    }

//    private String getEpisodeFlagVipFilePath() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            String path = jsonObject.optString("EpisodeFlagVipFilePath", null);
//            if (null == path || path.length() == 0)
//                throw new Exception("warning: url null");
//            return path;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeFlagVipFilePath => " + e.getMessage());
//            return null;
//        }
//    }

//    @DrawableRes
//    private int getEpisodeFlagVipResourceId() {
//        try {
//            StartArgs tags = getStartArgs();
//            if (null == tags)
//                throw new Exception("error: tags null");
//            JSONObject jsonObject = tags.getExtraData();
//            if (null == jsonObject)
//                throw new Exception("error: jsonObject null");
//            int resourceId = jsonObject.optInt("EpisodeFlagVipResourceId", 0);
//            if (resourceId == 0)
//                throw new Exception("warning: resourceId = 0");
//            return resourceId;
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => getEpisodeFlagVipResourceId => " + e.getMessage());
//            return 0;
//        }
//    }

//    private void callListener(int curIndex) {
//        try {
//            OnPlayerEpisodeListener listener = getPlayerView().getOnPlayerEpisodeListener();
//            if (null == listener)
//                throw new Exception("error: listener null");
//            listener.onEpisode(curIndex);
//        } catch (Exception e) {
//            LogUtil.log("ComponentApiMenu => callListener => " + e.getMessage());
//        }
//    }

//    private void updateTimeMillis() {
//        try {
//            long millis = System.currentTimeMillis();
//            ((View) this).setTag(millis);
//        } catch (Exception e) {
//        }
//    }

//    private void clearTimeMillis() {
//        try {
//            ((View) this).setTag(null);
//        } catch (Exception e) {
//        }
//    }

//    private long getTimeMillis() {
//        try {
//            Object tag = ((View) this).getTag();
//            if (null == tag)
//                throw new Exception("warning: tag null");
//            return (long) tag;
//        } catch (Exception e) {
//            return 0L;
//        }
//    }

    /****** 选集   ******/


//    private void loadEpisodeUrl(@Nullable ImageView imageView, @Nullable String url) {
//        try {
//            imageView.setImageURI(Uri.parse(url));
//        } catch (Exception e) {
//        }
//    }

//    private void loadEpisodeFile(@Nullable ImageView imageView, @Nullable String path) {
//        try {
//            imageView.setImageURI(Uri.parse(path));
//        } catch (Exception e) {
//        }
//    }

//    private void loadEpisodeResource(@Nullable ImageView imageView, @DrawableRes int resId) {
//        try {
//            imageView.setImageResource(resId);
//        } catch (Exception e) {
//        }
//    }

//    public String initEpisodePopuText(int index) {
//        try {
//            return getResources().getString(R.string.module_mediaplayer_string_episode_popu, index + 1);
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
