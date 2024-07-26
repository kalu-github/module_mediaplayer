package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

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
        // keycode_dpad_down
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            try {
                boolean componentShowing = isComponentShowing();
                if (componentShowing) {
                    View focus = findFocus();
                    if (null == focus)
                        throw new Exception("warning: focus null");
                    int focusId = focus.getId();
                    if (focusId == R.id.module_mediaplayer_component_menu_item_data) {
                        RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                        int childCount = tabGroup.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View childAt = tabGroup.getChildAt(i);
                            boolean selected = childAt.isSelected();
                            if (!selected)
                                continue;
                            focus.setActivated(true);
                            childAt.requestFocus();
                            break;
                        }
                        return true;
                    }
                } else {
                    show();
                    updateData(0);
                    RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int childCount = tabGroup.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = tabGroup.getChildAt(i);
                        boolean selected = childAt.isSelected();
                        if (!selected)
                            continue;
                        childAt.requestFocus();
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
        // keycode_dpad_up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                updateTimeMillis();
                View focus = findFocus();
                int focusId = focus.getId();

                if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                    RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                    int dataCount = dataGroup.getChildCount();
                    for (int i = 0; i < dataCount; i++) {
                        View childAt = dataGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        boolean activated = childAt.isActivated();
                        if (!activated)
                            continue;
                        childAt.requestFocus();
                        break;
                    }
                    return true;
                }
            } catch (Exception e) {
            }
        }
        // keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            View focus = findFocus();
            int focusId = focus.getId();
            if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                int indexOfChild = tabGroup.indexOfChild(focus);
                updateData(indexOfChild);
            }
        }
        // keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
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
                } else if (focusId == R.id.module_mediaplayer_component_menu_item_data) {

                    int checkedTag = -1;
                    RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int childCount = tabGroup.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        RadioButton radioButton = (RadioButton) tabGroup.getChildAt(i);
                        boolean selected = radioButton.isSelected();
                        if (!selected)
                            continue;
                        checkedTag = (int) radioButton.getTag();
                        break;
                    }
                    if (checkedTag == -1)
                        throw new Exception("warning: checkedTag =-1");

                    // 选集
                    if (checkedTag == 0) {
                        ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int indexOfChild = dataGroup.indexOfChild(focus);
                        if (indexOfChild <= 0) {
                            scrollEpisode(KeyEvent.KEYCODE_DPAD_LEFT);
                            return true;
                        }
                    }
                    // 倍速
                    else if (checkedTag == 1) {
                        ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int indexOfChild = dataGroup.indexOfChild(focus);
                        if (indexOfChild <= 0) {
                            return true;
                        }
                    }
                    // 画面
                    else if (checkedTag == 2) {
                        ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int indexOfChild = dataGroup.indexOfChild(focus);
                        if (indexOfChild <= 0) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        // keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            View focus = findFocus();
            int focusId = focus.getId();
            if (focusId == R.id.module_mediaplayer_component_menu_item_tab) {
                ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                int indexOfChild = tabGroup.indexOfChild(focus);
                updateData(indexOfChild);
            }
        }
        // keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
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
                } else if (focusId == R.id.module_mediaplayer_component_menu_item_data) {

                    int checkedTag = -1;
                    RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int tabCount = tabGroup.getChildCount();
                    for (int i = 0; i < tabCount; i++) {
                        View childAt = tabGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        boolean selected = childAt.isSelected();
                        if (!selected)
                            continue;
                        checkedTag = (int) childAt.getTag();
                        break;
                    }
                    if (checkedTag == -1)
                        throw new Exception("warning: checkedTag =-1");

                    // 选集
                    if (checkedTag == 0) {
                        ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int dataCount = dataGroup.getChildCount();
                        int indexOfChild = dataGroup.indexOfChild(focus);
                        if (indexOfChild + 1 >= dataCount) {
                            scrollEpisode(KeyEvent.KEYCODE_DPAD_RIGHT);
                            return true;
                        }
                    }
                    // 倍速
                    else if (checkedTag == 1) {
                        ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int dataCount = dataGroup.getChildCount();
                        int indexOfChild = dataGroup.indexOfChild(focus);
                        if (indexOfChild + 1 >= dataCount) {
                            return true;
                        }
                    }
                    // 画面
                    else if (checkedTag == 2) {
                        ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int dataCount = dataGroup.getChildCount();
                        int indexOfChild = dataGroup.indexOfChild(focus);
                        if (indexOfChild + 1 >= dataCount) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
            }
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
            LogUtil.log("ComponentMenu => dispatchKeyEvent => keycode_dpad_center =>");
            updateTimeMillis();
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");

                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_item_data)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_data");

                int checkedTag = -1;
                RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                int tabCount = tabGroup.getChildCount();
                for (int i = 0; i < tabCount; i++) {
                    View childAt = tabGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    boolean selected = childAt.isSelected();
                    if (!selected)
                        continue;
                    checkedTag = (int) childAt.getTag();
                    break;
                }
                LogUtil.log("ComponentMenu => dispatchKeyEvent => keycode_dpad_center => checkedTag = " + checkedTag);
                if (checkedTag == -1)
                    throw new Exception("warning: checkedTag =-1");

                // 选集
                if (checkedTag == 0) {
                    toggleEpisode(focusId);
                }
                // 倍速
                else if (checkedTag == 1) {
                    // 1
                    RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                    int dataCount = dataGroup.getChildCount();
                    for (int i = 0; i < dataCount; i++) {
                        View childAt = dataGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        childAt.setSelected(childAt == focus);
                    }
                    // 2
                    @PlayerType.SpeedType.Value
                    int id = Integer.parseInt(String.valueOf(((RadioButton) focus).getHint()));
                    setVideoSpeed(id);
                }
                // 画面
                else if (checkedTag == 2) {
                    // 1
                    RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                    int dataCount = dataGroup.getChildCount();
                    for (int i = 0; i < dataCount; i++) {
                        View childAt = dataGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        childAt.setSelected(childAt == focus);
                    }
                    // 2
                    @PlayerType.ScaleType.Value
                    int id = Integer.parseInt(String.valueOf(((RadioButton) focus).getHint()));
                    setVideoScaleType(id);
                }

                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentMenu => dispatchKeyEvent => keycode_dpad_center => " + e.getMessage());
            }
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
            if (focusId != R.id.module_mediaplayer_component_menu_item_data)
                throw new Exception("error: focusId != R.id.module_mediaplayer_component_menu_item_data");
            String text = (String) ((RadioButton) focus).getText();
            if (null == text || text.length() <= 0)
                throw new Exception("error: text null");
            int index = Integer.parseInt(text) - 1;
            LogUtil.log("ComponentMenu => scrollEpisode => index = " + index);
            if (action == KeyEvent.KEYCODE_DPAD_LEFT && index <= 0)
                throw new Exception("error: index <= 0");

            int episodeItemCount = getEpisodeItemCount();
            if (action == KeyEvent.KEYCODE_DPAD_RIGHT && index + 1 >= episodeItemCount)
                throw new Exception("error: index+1 >= " + episodeItemCount + ", index = " + index);

            RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
            int childCount = dataGroup.getChildCount();

            int start;
            if (action == KeyEvent.KEYCODE_DPAD_LEFT) {
                start = index - 1;
            } else if (action == KeyEvent.KEYCODE_DPAD_RIGHT) {
                start = index - childCount + 2;
            } else {
                start = -1;
            }
            if (start == -1)
                throw new Exception("error: start = -1");

            int episodePlayingIndex = getEpisodePlayingIndex();
            for (int i = 0; i < childCount; i++) {

                RadioButton radioButton = (RadioButton) dataGroup.getChildAt(i);
                if (null == radioButton)
                    continue;
                clearFlag(i);
                int position = i + start;
                updateFlag(i, position);

                radioButton.setText(String.valueOf(position + 1));
                radioButton.setSelected(position == episodePlayingIndex);
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
        LogUtil.log("ComponentMenu => show =>");
        updateTimeMillis();
        superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_SHOW);
        ComponentApiMenu.super.show();
    }

//    @Override
//    public void tabCheckedRequestFocus() {
//        LogUtil.log("ComponentMenu => tabCheckedRequestFocus =>");
//        try {
//            int checkedTag = -1;
//            RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
//            int tabCount = tabGroup.getChildCount();
//            for (int i = 0; i < tabCount; i++) {
//                RadioButton radioButton = (RadioButton) tabGroup.getChildAt(i);
//                boolean checked = radioButton.isChecked();
//                if (!checked)
//                    continue;
//                checkedTag = (int) radioButton.getTag();
//                break;
//            }
//            if (checkedTag == -1)
//                throw new Exception("warning: checkedTag =-1");
//
//            // 选集
//            if (checkedTag == 0) {
//                tabGroup.getChildAt(0).requestFocus();
//            }
//            // 倍速
//            else if (checkedTag == 1) {
//                tabGroup.getChildAt(tabCount >= 3 ? 1 : 0).requestFocus();
//            }
//            // 画面
//            else if (checkedTag == 2) {
//                tabGroup.getChildAt(tabCount >= 3 ? 2 : 1).requestFocus();
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => tabCheckedRequestFocus => Exception " + e.getMessage());
//        }
//    }

    @Override
    public void updateData(int checkedIndex) {
        Toast.makeText(getContext(), "updateData => checkedIndex = " + checkedIndex, Toast.LENGTH_SHORT).show();
        LogUtil.log("ComponentMenu => updateData => checkedIndex = " + checkedIndex);

        // 选项栏 inflate view
        try {

            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            if (tabCount > 0)
                throw new Exception("warning: tabCount >0");

            String[] data;
            try {
                int episodeItemCount = getEpisodeItemCount();
                if (episodeItemCount <= 0)
                    throw new Exception("warning: episodeItemCount " + episodeItemCount);
                int episodePlayingIndex = getEpisodePlayingIndex();
                if (episodePlayingIndex < 0)
                    throw new Exception("warning: episodePlayingIndex " + episodePlayingIndex);
                data = new String[]{"选集", "倍速", "画面"};
            } catch (Exception e) {
                data = new String[]{"倍速", "画面"};
            }

            for (String s : data) {
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_tab, tabGroup, true);
                int count = tabGroup.getChildCount();
                int index = --count;
                View childAt = tabGroup.getChildAt(index);
                if (null == childAt)
                    continue;
                ((RadioButton) childAt).setText(s);
                ((RadioButton) childAt).setChecked(index == 0);
                if ("选集".equals(s)) {
                    LogUtil.log("ComponentMenu => updateData => setTag 0");
                    ((RadioButton) childAt).setTag(0);
                } else if ("倍速".equals(s)) {
                    LogUtil.log("ComponentMenu => updateData => setTag 1");
                    ((RadioButton) childAt).setTag(1);
                } else if ("画面".equals(s)) {
                    LogUtil.log("ComponentMenu => updateData => setTag 2");
                    ((RadioButton) childAt).setTag(2);
                } else {
                    LogUtil.log("ComponentMenu => updateData => setTag null");
                    ((RadioButton) childAt).setTag(null);
                }
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception1 " + e.getMessage());
        }

        // 选项栏 选中
        try {
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            if (tabCount <= 0)
                throw new Exception("warning: tabCount >=0");
            for (int i = 0; i < tabCount; i++) {
                View childAt = tabGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                childAt.setSelected(i == checkedIndex);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception2 " + e.getMessage());
        }

        // 信息 inflate view
        try {
            ViewGroup flagGroup = findViewById(R.id.module_mediaplayer_component_menu_flag_root);
            int childCount = flagGroup.getChildCount();
            if (childCount > 0)
                throw new Exception("warning: childCount >0");
            for (int i = 0; i < 10; i++) {
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_flag, flagGroup, true);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception3 " + e.getMessage());
        }

        // 数据 inflate view
        try {
            ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
            int dataCount = dataGroup.getChildCount();
            if (dataCount > 0)
                throw new Exception("warning: dataCount >0");
            for (int i = 0; i < 10; i++) {
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_data, dataGroup, true);
                int count = dataGroup.getChildCount();
                int index = --count;
                View childAt = dataGroup.getChildAt(index);
                if (null == childAt)
                    continue;
                childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            v.setActivated(false);
                        }
                    }
                });
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception4 " + e.getMessage());
        }

        // 数据
        try {

            RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            View child = tabGroup.getChildAt(checkedIndex);
            int tag = (int) child.getTag();

            // 选集
            if (tag == 0) {

                RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                int dataCount = dataGroup.getChildCount();
                for (int i = 0; i < dataCount; i++) {

                    clearFlag(i);
                    View childAt = dataGroup.getChildAt(i);
                    if (null == childAt)
                        continue;

                    childAt.setTag(null);
                    childAt.setEnabled(false);
                    childAt.setSelected(false);
                    childAt.setActivated(false);
                    childAt.setVisibility(View.INVISIBLE);

                    ((RadioButton) childAt).setText("");
                    ((RadioButton) childAt).setHint(null);

                    int episodeItemCount = getEpisodeItemCount();
                    if (i >= episodeItemCount)
                        continue;

                    int episodePlayingIndex = getEpisodePlayingIndex();
                    int num = episodePlayingIndex / dataCount;
                    int start = num * dataCount;
                    if (episodeItemCount > dataCount) {
                        int length = start + dataCount;
                        if (length > episodeItemCount) {
                            start -= Math.abs(length - episodeItemCount);
                        }
                    }

                    int position = i + start;
                    childAt.setEnabled(true);
                    childAt.setTag(position);
                    if (position == episodePlayingIndex) {
                        childAt.setSelected(true);
                        childAt.setActivated(true);
                    }
                    ((RadioButton) childAt).setText(String.valueOf(position + 1));

                    childAt.setVisibility(View.VISIBLE);
                    updateFlag(i, position);
                }
            }
            // 倍速
            else if (tag == 1) {
                int[] speeds = getSpeedTypes();

                RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                int dataCount = dataGroup.getChildCount();
                for (int i = 0; i < dataCount; i++) {

                    clearFlag(i);
                    View childAt = dataGroup.getChildAt(i);
                    if (null == childAt)
                        continue;

                    childAt.setTag(null);
                    childAt.setEnabled(false);
                    childAt.setSelected(false);
                    childAt.setActivated(false);
                    childAt.setVisibility(View.INVISIBLE);

                    ((RadioButton) childAt).setText("");
                    ((RadioButton) childAt).setHint(null);

                    if (i >= speeds.length)
                        continue;

                    childAt.setTag(1);
                    childAt.setEnabled(true);
                    ((RadioButton) childAt).setHint(String.valueOf(speeds[i]));

                    if (speeds[i] == PlayerType.SpeedType._0_5) {
                        ((RadioButton) childAt).setText("0.5");
                    } else if (speeds[i] == PlayerType.SpeedType._1_5) {
                        ((RadioButton) childAt).setText("1.5");
                    } else if (speeds[i] == PlayerType.SpeedType._2_0) {
                        ((RadioButton) childAt).setText("2.0");
                    } else if (speeds[i] == PlayerType.SpeedType._2_5) {
                        ((RadioButton) childAt).setText("2.5");
                    } else if (speeds[i] == PlayerType.SpeedType._3_0) {
                        ((RadioButton) childAt).setText("3.0");
                    } else if (speeds[i] == PlayerType.SpeedType._3_5) {
                        ((RadioButton) childAt).setText("3.5");
                    } else if (speeds[i] == PlayerType.SpeedType._4_0) {
                        ((RadioButton) childAt).setText("4.0");
                    } else if (speeds[i] == PlayerType.SpeedType._4_5) {
                        ((RadioButton) childAt).setText("4.5");
                    } else if (speeds[i] == PlayerType.SpeedType._5_0) {
                        ((RadioButton) childAt).setText("5.0");
                    } else {
                        ((RadioButton) childAt).setText("1.0");
                    }

                    int videoSpeed = getVideoSpeed();
                    if (videoSpeed == speeds[i]) {
                        childAt.setSelected(true);
                        childAt.setActivated(true);
                    }
                    childAt.setVisibility(View.VISIBLE);
                }
            }
            // 画面
            else if (tag == 2) {
                int[] scales = getScaleTypes();
                // radioButton.clearFlag();

                RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                int dataCount = dataGroup.getChildCount();
                for (int i = 0; i < dataCount; i++) {

                    clearFlag(i);
                    View childAt = dataGroup.getChildAt(i);
                    if (null == childAt)
                        continue;

                    childAt.setTag(null);
                    childAt.setEnabled(false);
                    childAt.setSelected(false);
                    childAt.setActivated(false);
                    childAt.setVisibility(View.INVISIBLE);

                    ((RadioButton) childAt).setText("");

                    if (i >= scales.length)
                        continue;

                    childAt.setTag(2);
                    childAt.setEnabled(true);
                    ((RadioButton) childAt).setHint(String.valueOf(scales[i]));

                    int videoScaleType = getVideoScaleType();
                    LogUtil.log("ComponentMenu => updateData3 => i =  " + i + ", scales[i] = " + scales[i] + ", videoScaleType = " + videoScaleType);

                    if (scales[i] == PlayerType.ScaleType.REAL) {
                        ((RadioButton) childAt).setText("原始");
                    } else if (scales[i] == PlayerType.ScaleType.FULL) {
                        ((RadioButton) childAt).setText("全屏");
                    } else if (scales[i] == PlayerType.ScaleType._1_1) {
                        ((RadioButton) childAt).setText("1:1");
                    } else if (scales[i] == PlayerType.ScaleType._4_3) {
                        ((RadioButton) childAt).setText("4:3");
                    } else if (scales[i] == PlayerType.ScaleType._5_4) {
                        ((RadioButton) childAt).setText("5:4");
                    } else if (scales[i] == PlayerType.ScaleType._16_9) {
                        ((RadioButton) childAt).setText("16:9");
                    } else if (scales[i] == PlayerType.ScaleType._16_10) {
                        ((RadioButton) childAt).setText("16:10");
                    } else {
                        ((RadioButton) childAt).setText("自动");
                    }

                    if (videoScaleType == scales[i]) {
                        childAt.setSelected(true);
                        childAt.setActivated(true);
                    }
                    childAt.setVisibility(View.VISIBLE);
                }
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception5 " + e.getMessage());
        }
    }

    @Override
    public void toggleEpisode(int focusId) {

//        try {
//            RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//            RadioButton radioButton = itemGroup.findViewById(focusId);
//            Object tag = radioButton.getTag();
//            if (null == tag)
//                throw new Exception("error: tag null");
//            int index = (int) tag;
//            int episodePlayingIndex = getEpisodePlayingIndex();
//            if (index == episodePlayingIndex)
//                throw new Exception("warning: index == episodePlayingIndex");
//
//            int itemCount = itemGroup.getChildCount();
//            for (int i = 0; i < itemCount; i++) {
//                RadioButton childAt = (RadioButton) itemGroup.getChildAt(i);
//                if (null == childAt)
//                    continue;
//
//                Object tag1 = childAt.getTag();
//                if (null == tag1)
//                    continue;
//                int position = (int) tag1;
//                childAt.setSelected(position == index);
//                childAt.setChecked(position == index);
//            }
//
//            // 2
//            hide();
//            stop();
//            clickEpisode(index);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clickEpisode => Exception " + e.getMessage());
//        }
    }

//    @Override
//    public void setTabSelectedIndex(int index) {
//        updateTimeMillis();
//        try {
//            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
//            int childCount = tabGroup.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View childAt = tabGroup.getChildAt(i);
//                if (null == childAt)
//                    continue;
//                childAt.setSelected(i == index);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => setTabSelectedIndex => Exception " + e.getMessage());
//        }
//    }

//    @Override
//    public void setTabCheckedIndex(int index) {
//        updateTimeMillis();
//        try {
//            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
//            int childCount = tabGroup.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                View childAt = tabGroup.getChildAt(i);
//                if (null == childAt)
//                    continue;
//                ((RadioButton) childAt).setChecked(i == index);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => setTabCheckedIndex => Exception " + e.getMessage());
//        }
//    }

    @Override
    public void updateItemSelected(int viewId) {
//        try {
//            RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//            int checkedRadioButtonId = tabGroup.getCheckedRadioButtonId();
//            if (checkedRadioButtonId != R.id.module_mediaplayer_component_menu_tab_episode)
//                throw new Exception();
//            RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//            int itemCount = itemGroup.getChildCount();
//            for (int i = 0; i < itemCount; i++) {
//                View childAt = itemGroup.getChildAt(i);
//                if (null == childAt)
//                    continue;
//                int focusId = childAt.getId();
//                childAt.setSelected(focusId == viewId);
//            }
//        } catch (Exception e) {
//        }
    }

    @Override
    public void clearFlag(int index) {
        try {
            ViewGroup flagGroup = findViewById(R.id.module_mediaplayer_component_menu_flag_root);
            RelativeLayout itemGroup = (RelativeLayout) flagGroup.getChildAt(index);
            ImageView imageView0 = itemGroup.findViewById(R.id.module_mediaplayer_component_menu_item_flag_img0);
            imageView0.setImageDrawable(null);
            ImageView imageView1 = itemGroup.findViewById(R.id.module_mediaplayer_component_menu_item_flag_img1);
            imageView1.setImageDrawable(null);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => clearFlag => Exception " + e.getMessage());
        }
    }

    @Override
    public void updateFlag(int index, int position) {
        LogUtil.log("ComponentMenu => updateFlag => index = " + index + ", position = " + position);

        try {
            ImageView imageView;
            int flagLoaction = getEpisodeFlagLoaction();
            if (flagLoaction == PlayerType.EpisodeFlagLoactionType.LEFT_TOP) {
                ViewGroup flagGroup = findViewById(R.id.module_mediaplayer_component_menu_flag_root);
                RelativeLayout itemGroup = (RelativeLayout) flagGroup.getChildAt(index);
                imageView = itemGroup.findViewById(R.id.module_mediaplayer_component_menu_item_flag_img0);
            } else {
                ViewGroup flagGroup = findViewById(R.id.module_mediaplayer_component_menu_flag_root);
                RelativeLayout itemGroup = (RelativeLayout) flagGroup.getChildAt(index);
                imageView = itemGroup.findViewById(R.id.module_mediaplayer_component_menu_item_flag_img1);
            }
            if (null == imageView)
                throw new Exception("error: imageView null");
            int freeItemCount = getEpisodeFreeItemCount();

            // 收费
            String vipImgUrl = getEpisodeFlagVipImgUrl();
            String vipFilePath = getEpisodeFlagVipFilePath();
            int vipResourceId = getEpisodeFlagVipResourceId();
            if (null != vipImgUrl) {
                if (freeItemCount > 0 && position >= freeItemCount) {
                    loadFlagUrl(imageView, vipImgUrl);
                }
            } else if (null != vipFilePath) {
                if (freeItemCount > 0 && position >= freeItemCount) {
                    loadFlagFile(imageView, vipFilePath);
                }
            } else if (0 != vipResourceId) {
                if (freeItemCount > 0 && position >= freeItemCount) {
                    loadFlagResource(imageView, vipResourceId);
                }
            }

            // 免费
            String freeImgUrl = getEpisodeFlagFreeImgUrl();
            String freeFilePath = getEpisodeFlagFreeFilePath();
            int freeResourceId = getEpisodeFlagFreeResourceId();
            if (null != freeImgUrl) {
                if (freeItemCount < 0) {
                    loadFlagUrl(imageView, freeImgUrl);
                } else if (position < freeItemCount) {
                    loadFlagUrl(imageView, freeImgUrl);
                }
            } else if (null != freeFilePath) {
                if (freeItemCount < 0) {
                    loadFlagFile(imageView, freeFilePath);
                } else if (position < freeItemCount) {
                    loadFlagFile(imageView, freeFilePath);
                }
            } else if (0 != freeResourceId) {
                if (freeItemCount < 0) {
                    loadFlagResource(imageView, freeResourceId);
                } else if (position < freeItemCount) {
                    loadFlagResource(imageView, freeResourceId);
                }
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateFlag => Exception " + e.getMessage());
        }
    }

    @Override
    public void loadFlagUrl(ImageView imageView, String url) {
        try {
            imageView.setImageDrawable(null);
            imageView.setImageURI(Uri.parse(url));
        } catch (Exception e) {
        }
    }

    @Override
    public void loadFlagFile(@Nullable ImageView imageView, @Nullable String path) {
        try {
            imageView.setImageDrawable(null);
            imageView.setImageURI(Uri.parse(path));
        } catch (Exception e) {
        }
    }

    @Override
    public void loadFlagResource(@Nullable ImageView imageView, int resId) {
        try {
            imageView.setImageDrawable(null);
            imageView.setImageResource(resId);
        } catch (Exception e) {
        }
    }
}
