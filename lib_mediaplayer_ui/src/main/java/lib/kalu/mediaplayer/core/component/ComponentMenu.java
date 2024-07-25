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

//        RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
//        int dataCount = dataGroup.getChildCount();
//        for (int i = 0; i < dataCount; i++) {
//            View childAt = dataGroup.getChildAt(i);
//            if (null == childAt)
//                continue;
//            childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    int focusId = v.getId();
//                    updateItemSelected(focusId);
//                }
//            });
//        }
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
                        tabCheckedRequestFocus();
                        return true;
                    }
                } else {
                    show();
                    updateData();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            tabCheckedRequestFocus();
                        }
                    });
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

                    int checkedTag = -1;
                    RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int childCount = tabGroup.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        RadioButton radioButton = (RadioButton) tabGroup.getChildAt(i);
                        boolean checked = radioButton.isChecked();
                        if (!checked)
                            continue;
                        checkedTag = (int) radioButton.getTag();
                        break;
                    }
                    if (checkedTag == -1)
                        throw new Exception("warning: checkedTag =-1");

                    // 选集
                    if (checkedTag == 0) {
                        RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int dataCount = dataGroup.getChildCount();
                        for (int i = 0; i < dataCount; i++) {
                            View childAt = dataGroup.getChildAt(i);
                            if (null == childAt)
                                continue;
                            boolean selected = childAt.isSelected();
                            if (!selected)
                                continue;
                            childAt.requestFocus();
                            break;
                        }
                    }
                    // 倍速
                    else if (checkedTag == 1) {
                        RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int dataCount = dataGroup.getChildCount();
                        for (int i = 0; i < dataCount; i++) {
                            RadioButton radioButton = (RadioButton) dataGroup.getChildAt(i);
                            boolean checked = radioButton.isChecked();
                            if (!checked)
                                continue;
                            radioButton.requestFocus();
                            break;
                        }
                    }
                    // 画面
                    else if (checkedTag == 2) {
                        RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
                        int dataCount = dataGroup.getChildCount();
                        for (int i = 0; i < dataCount; i++) {
                            RadioButton radioButton = (RadioButton) dataGroup.getChildAt(i);
                            boolean checked = radioButton.isChecked();
                            if (!checked)
                                continue;
                            radioButton.requestFocus();
                            break;
                        }
                    }
                    return true;
                }
            } catch (Exception e) {
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
                        boolean checked = radioButton.isChecked();
                        if (!checked)
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
                    int childCount = tabGroup.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        RadioButton radioButton = (RadioButton) tabGroup.getChildAt(i);
                        boolean checked = radioButton.isChecked();
                        if (!checked)
                            continue;
                        checkedTag = (int) radioButton.getTag();
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
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                updateTimeMillis();
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");

                int focusId = focus.getId();

                int checkedTag = -1;
                RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                int childCount = tabGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    RadioButton radioButton = (RadioButton) tabGroup.getChildAt(i);
                    boolean checked = radioButton.isChecked();
                    if (!checked)
                        continue;
                    checkedTag = (int) radioButton.getTag();
                    break;
                }
                if (checkedTag == -1)
                    throw new Exception("warning: checkedTag =-1");

                // 选集
                if (checkedTag == 0) {
                    toggleEpisode(focusId);
                }
                // 倍速
                else if (checkedTag == 1) {
                    toggleSpeed(focusId);
                }
                // 画面
                else if (checkedTag == 2) {
                    toggleScale(focusId);
                }

                return true;
            } catch (Exception e) {
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
                radioButton.setChecked(position == episodePlayingIndex);
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

    @Override
    public void tabCheckedRequestFocus() {
        LogUtil.log("ComponentMenu => tabCheckedRequestFocus =>");
        try {
            int checkedTag = -1;
            RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            for (int i = 0; i < tabCount; i++) {
                RadioButton radioButton = (RadioButton) tabGroup.getChildAt(i);
                boolean checked = radioButton.isChecked();
                if (!checked)
                    continue;
                checkedTag = (int) radioButton.getTag();
                break;
            }
            if (checkedTag == -1)
                throw new Exception("warning: checkedTag =-1");

            // 选集
            if (checkedTag == 0) {
                tabGroup.getChildAt(0).requestFocus();
            }
            // 倍速
            else if (checkedTag == 1) {
                tabGroup.getChildAt(tabCount >= 3 ? 1 : 0).requestFocus();
            }
            // 画面
            else if (checkedTag == 2) {
                tabGroup.getChildAt(tabCount >= 3 ? 2 : 1).requestFocus();
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => tabCheckedRequestFocus => Exception " + e.getMessage());
        }
    }

    @Override
    public void updateData() {
        LogUtil.log("ComponentMenu => updateData =>");

        // 选项栏 inflate view
        try {

            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int childCount = tabGroup.getChildCount();
            if (childCount > 0)
                throw new Exception("warning: childCount >0");

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
                childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        try {
                            if (!hasFocus)
                                throw new Exception("warning: hasFocus false");
                            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                            int indexOfChild = viewGroup.indexOfChild(v);
                            Toast.makeText(getContext(), "indexOfChild = " + indexOfChild + ", checkedTag = " + v.getTag(), Toast.LENGTH_SHORT).show();
                            setTabSelectedIndex(indexOfChild);
                            updateData();
                        } catch (Exception e) {
                            LogUtil.log("ComponentMenu => updateData => onFocusChange => Exception5 " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception1 " + e.getMessage());
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
            LogUtil.log("ComponentMenu => updateData => Exception2 " + e.getMessage());
        }

        // 数据 inflate view
        try {
            ViewGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
            int childCount = dataGroup.getChildCount();
            if (childCount > 0)
                throw new Exception("warning: childCount >0");
            for (int i = 0; i < 10; i++) {
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_data, dataGroup, true);
                int count = dataGroup.getChildCount();
                int index = --count;
                View childAt = dataGroup.getChildAt(index);
                if (null == childAt)
                    continue;
//                childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if(hasFocus){
//
//                        }
//                        else
//                        v.setSelected(hasFocus);
//                    }
//                });
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception3 " + e.getMessage());
        }

        // 数据
        try {

            int checkedTag = -1;
            RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            for (int i = 0; i < tabCount; i++) {
                View childAt = tabGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                boolean checked = ((RadioButton) childAt).isChecked();
                if (!checked)
                    continue;
                checkedTag = (int) childAt.getTag();
                break;
            }

            if (checkedTag == -1) {
                checkedTag = 0;
            }

            RadioGroup dataGroup = findViewById(R.id.module_mediaplayer_component_menu_data_root);
            int dataCount = dataGroup.getChildCount();

            // 选集
            if (checkedTag == 0) {

                for (int i = 0; i < dataCount; i++) {

                    clearFlag(i);
                    RadioButton radioButton = (RadioButton) dataGroup.getChildAt(i);

                    int episodePlayingIndex = getEpisodePlayingIndex();
                    int episodeItemCount = getEpisodeItemCount();
                    int num = episodePlayingIndex / dataCount;
                    int start = num * dataCount;
                    if (episodeItemCount > dataCount) {
                        int length = start + dataCount;
                        if (length > episodeItemCount) {
                            start -= Math.abs(length - episodeItemCount);
                        }
                    }
                    if (i < episodeItemCount) {
                        radioButton.setEnabled(true);
                        radioButton.setVisibility(View.VISIBLE);

                        int position = i + start;
                        updateFlag(i, position);

                        radioButton.setText(String.valueOf(position + 1));
                        radioButton.setChecked(position == episodePlayingIndex);
                        radioButton.setSelected(position == episodePlayingIndex);
                    } else {
                        radioButton.setEnabled(false);
                        radioButton.setVisibility(View.GONE);

                        radioButton.setText("");
                        radioButton.setChecked(false);
                        radioButton.setSelected(false);
                    }
                }
            }
            // 倍速
            else if (checkedTag == 1) {
                int[] speeds = getSpeedTypes();

                for (int i = 0; i < dataCount; i++) {

                    clearFlag(i);
                    RadioButton radioButton = (RadioButton) dataGroup.getChildAt(i);

                    radioButton.setSelected(false);
                    radioButton.setEnabled(i < speeds.length);
                    radioButton.setVisibility(i < speeds.length ? View.VISIBLE : View.INVISIBLE);

                    if (i < speeds.length) {
                        float speed = getSpeed();
                        radioButton.setChecked(speed == speeds[i]);
                    } else {
                        radioButton.setChecked(false);
                    }
                    if (i < speeds.length) {
                        radioButton.setTag(speeds[i]);
                        if (speeds[i] == PlayerType.SpeedType._0_5) {
                            radioButton.setText("0.5");
                        } else if (speeds[i] == PlayerType.SpeedType._1_5) {
                            radioButton.setText("1.5");
                        } else if (speeds[i] == PlayerType.SpeedType._2_0) {
                            radioButton.setText("2.0");
                        } else if (speeds[i] == PlayerType.SpeedType._2_5) {
                            radioButton.setText("2.5");
                        } else if (speeds[i] == PlayerType.SpeedType._3_0) {
                            radioButton.setText("3.0");
                        } else if (speeds[i] == PlayerType.SpeedType._3_5) {
                            radioButton.setText("3.5");
                        } else if (speeds[i] == PlayerType.SpeedType._4_0) {
                            radioButton.setText("4.0");
                        } else if (speeds[i] == PlayerType.SpeedType._4_5) {
                            radioButton.setText("4.5");
                        } else if (speeds[i] == PlayerType.SpeedType._5_0) {
                            radioButton.setText("5.0");
                        } else {
                            radioButton.setText("1.0");
                        }
                    } else {
                        radioButton.setText("");
                    }
                }
            }
            // 画面
            else if (checkedTag == 2) {
                int[] scales = getScaleTypes();
                // radioButton.clearFlag();

                for (int i = 0; i < dataCount; i++) {

                    clearFlag(i);

                    RadioButton radioButton = (RadioButton) dataGroup.getChildAt(i);

                    radioButton.setVisibility(i < scales.length ? View.VISIBLE : View.INVISIBLE);

                    if (i < scales.length) {
                        radioButton.setTag(scales[i]);
                        if (scales[i] == PlayerType.ScaleType.REAL) {
                            radioButton.setText("原始");
                        } else if (scales[i] == PlayerType.ScaleType.FULL) {
                            radioButton.setText("全屏");
                        } else if (scales[i] == PlayerType.ScaleType._1_1) {
                            radioButton.setText("1:1");
                        } else if (scales[i] == PlayerType.ScaleType._4_3) {
                            radioButton.setText("4:3");
                        } else if (scales[i] == PlayerType.ScaleType._5_4) {
                            radioButton.setText("5:4");
                        } else if (scales[i] == PlayerType.ScaleType._16_9) {
                            radioButton.setText("16:9");
                        } else if (scales[i] == PlayerType.ScaleType._16_10) {
                            radioButton.setText("16:10");
                        } else {
                            radioButton.setText("自动");
                        }
                        int videoScaleType = getVideoScaleType();
                        boolean flag = videoScaleType == scales[i];
                        LogUtil.log("ComponentMenu => updateData => i =  " + i + ", checked = " + flag);
                        radioButton.setChecked(flag);
                        radioButton.setSelected(flag);
                        radioButton.setEnabled(true);
                    } else {
                        LogUtil.log("ComponentMenu => updateData => i =  " + i + ", checked = false");
                        radioButton.setTag(null);
                        radioButton.setChecked(false);
                        radioButton.setSelected(false);
                        radioButton.setEnabled(false);
                        radioButton.setText("");
                    }
                }
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateData => Exception5 " + e.getMessage());
        }
    }

    @Override
    public void toggleScale(int focusId) {

//        // 选中状态
//        RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//        int itemCount = itemGroup.getChildCount();
//        for (int i = 0; i < itemCount; i++) {
//            RadioButton radioButton = (RadioButton) itemGroup.getChildAt(i);
//            radioButton.setChecked(false);
//        }
//        RadioButton radioButton = (RadioButton) itemGroup.findViewById(focusId);
//        radioButton.setChecked(true);
//
//        try {
//            int tag = (int) radioButton.getTag();
//            setVideoScaleType(tag);
//            setSpeed(tag);
//        } catch (Exception e) {
//        }
    }

    @Override
    public void toggleSpeed(int focusId) {

//        // 选中状态
//        RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//        int itemCount = itemGroup.getChildCount();
//        for (int i = 0; i < itemCount; i++) {
//            RadioButton radioButton = (RadioButton) itemGroup.getChildAt(i);
//            radioButton.setChecked(false);
//        }
//        RadioButton radioButton = (RadioButton) itemGroup.findViewById(focusId);
//        radioButton.setChecked(true);
//
//        try {
//            int tag = (int) radioButton.getTag();
//            setSpeed(tag);
//        } catch (Exception e) {
//        }
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

    @Override
    public void setTabSelectedIndex(int index) {
        updateTimeMillis();
        try {
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int tabCount = tabGroup.getChildCount();
            for (int i = 0; i < tabCount; i++) {
                View childAt = tabGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                ((RadioButton) childAt).setChecked(i == index);
                ((RadioButton) childAt).setSelected(i == index);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => setTabSelectedIndex => Exception " + e.getMessage());
        }
    }

    @Override
    public void setTabCheckedIndex(int index) {
        updateTimeMillis();
        try {
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int childCount = tabGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = tabGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                ((RadioButton) childAt).setChecked(i == index);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => setTabCheckedIndex => Exception " + e.getMessage());
        }
    }

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
