package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    public void callEvent(int playState) {
        switch (playState) {
            case PlayerType.EventType.COMPLETE:
                hide();
                break;
        }
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {
        try {
            Object tag = getTag();
            if (null == tag)
                throw new Exception("warning: tag null");
            long start = (long) tag;
            long cast = (position - start);
            if (cast < 4000)
                throw new Exception("warning: cast < 4000");
            hide();
        } catch (Exception e) {
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        // keycode_back
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            boolean componentShowing = isComponentShowing();
//            if (componentShowing) {
//                hide();
//                return true;
//            }
//        }

        // keycode_dpad_down
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            try {
                boolean componentShowing = isComponentShowing();
                if (componentShowing) {
                    View focus = findFocus();
                    if (null == focus)
                        throw new Exception("warning: focus null");
                    int focusId = focus.getId();
                    if (focusId != R.id.module_mediaplayer_component_menu_item_child)
                        throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_child");
                    Object tag = focus.getTag();
                    if (null == tag)
                        throw new Exception("warning: tag null");
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    View childAt = tabGroup.getChildAt((Integer) tag);
                    childAt.requestFocus();
                } else {
                    show();
                    updateTabData(0, true);
                }
                return true;
            } catch (Exception e) {
            }
        }
        // keycode_dpad_up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                View focus = findFocus();
                int focusId = focus.getId();
                if (focusId == R.id.module_mediaplayer_component_menu_item_child)
                    throw new Exception("warning: focusId == R.id.module_mediaplayer_component_menu_item_child");
                TextView textView = focus.findViewById(R.id.module_mediaplayer_component_menu_tab_child_text);
                CharSequence text = textView.getText();
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                ViewGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_root);
                itemGroup.getChildAt(0).requestFocus();

                // 选集
                if ("选集".equals(text)) {
//                    try {
//                        RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//                        int checkedRadioButtonId = itemGroup.getCheckedRadioButtonId();
//                        itemGroup.findViewById(checkedRadioButtonId).requestFocus();
//                    } catch (Exception e) {
//                    }
                }
                // 倍速
                else if ("倍速".equals(text)) {
//                    try {
//                        RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//                        int checkedRadioButtonId = itemGroup.getCheckedRadioButtonId();
//                        itemGroup.findViewById(checkedRadioButtonId).requestFocus();
//                    } catch (Exception e) {
//                    }
                }
                // 画面
                else if ("画面".equals(text)) {
//                    try {
//                        RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//                        int itemCount = itemGroup.getChildCount();
//                        for (int i = 0; i < itemCount; i++) {
//                            View childAt = itemGroup.getChildAt(i);
//                            if (null == childAt)
//                                continue;
//                            boolean selected = childAt.isSelected();
//                            if (!selected)
//                                continue;
//                            childAt.requestFocus();
//                            break;
//                        }
//                    } catch (Exception e) {
//                    }
                }
                return true;
            } catch (Exception e) {
            }
        }
        // keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                boolean showing = isComponentShowing();
                if (!showing)
                    throw new Exception("warning: showing false");
                View focus = findFocus();
                int focusId = focus.getId();
                // 菜单
                if (focusId == R.id.module_mediaplayer_component_menu_tab_child) {
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int childCount = tabGroup.getChildCount();
                    int index = tabGroup.indexOfChild(focus);
                    if (index + 1 >= childCount) {
                        return true;
                    }
                }
                // 数据
                else if (focusId == R.id.module_mediaplayer_component_menu_item_child) {
                }

//                RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//                int checkedRadioButtonId = tabGroup.getCheckedRadioButtonId();
//                // 画面
//                if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_scale) {
//                    try {
//                        int[] ints = getResources().getIntArray(R.array.module_mediaplayer_array_scales);
//                        RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//                        RadioButton radioButton = (RadioButton) itemGroup.getChildAt(ints.length - 1);
//                        int buttonId = radioButton.getId();
//                        if (focusId != buttonId)
//                            throw new Exception();
//                        return true;
//                    } catch (Exception e) {
//                    }
//                }
//                // 倍速
//                else if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_speed) {
//                    try {
//                        int[] ints = getResources().getIntArray(R.array.module_mediaplayer_array_speeds);
//                        RadioGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_group);
//                        RadioButton radioButton = (RadioButton) itemGroup.getChildAt(ints.length - 1);
//                        int buttonId = radioButton.getId();
//                        if (focusId != buttonId)
//                            throw new Exception();
//                        return true;
//                    } catch (Exception e) {
//                    }
//                }
//                // 选集
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_n9) {
//                    scrollEpisode(KeyEvent.KEYCODE_DPAD_RIGHT);
//                    return true;
//                }
            } catch (Exception e) {
            }
        }
        // keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: showing false");
                View focus = findFocus();
                int focusId = focus.getId();

                // 菜单
                if (focusId == R.id.module_mediaplayer_component_menu_tab_child) {
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int index = tabGroup.indexOfChild(focus);
                    if (index == 0) {
                        return true;
                    }
                }
                // 数据
                else if (focusId == R.id.module_mediaplayer_component_menu_item_child) {

                    int index = -1;
                    ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
                    int childCount = tabGroup.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = tabGroup.getChildAt(i);
                        if (null == childAt)
                            continue;
                        Object tag = childAt.getTag();
                        if (null == tag)
                            continue;
                        if ((boolean) tag) {
                            index = i;
                        }
                    }

                    // 选集
                    if (index == 0) {

                    }
                    // 倍速
                    else if (index == 1) {

                    }
                    // 画面
                    else if (index == 2) {

                    }

                }

//                // 选集
//                if (focusId == R.id.module_mediaplayer_component_menu_tab_episode) {
//                    RadioButton radioButton = findViewById(R.id.module_mediaplayer_component_menu_tab_episode);
//                    return radioButton.isEnabled();
//                }
//                // 倍速
//                else if (focusId == R.id.module_mediaplayer_component_menu_tab_speed) {
//                    RadioButton radioButton = findViewById(R.id.module_mediaplayer_component_menu_tab_episode);
//                    return !radioButton.isEnabled();
//                }
//                //
//                else if (focusId == R.id.module_mediaplayer_component_menu_item_n0) {
//                    RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//                    int checkedRadioButtonId = tabGroup.getCheckedRadioButtonId();
//                    if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_episode) {
//                        scrollEpisode(KeyEvent.KEYCODE_DPAD_LEFT);
//                    }
//                    return true;
//                }
            } catch (Exception e) {
            }
        }
        // keycode_dpad_center
        else if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_item_child)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_child");
                Object tag = focus.getTag();
                if (null == tag)
                    throw new Exception("warning: tag null");

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

                // 选集
                if (0 == (int) tag && hasEpisode) {
                    toggleEpisode(focusId);
                }
                // 倍速
                else if (0 == (int) tag) {
                    toggleSpeed();
                }
                // 倍速
                else if (1 == (int) tag && hasEpisode) {
                    toggleSpeed();
                }
                // 画面
                else if (1 == (int) tag) {
                    toggleScale();
                }
                // 画面
                else if (2 == (int) tag && hasEpisode) {
                    toggleScale();
                } else {
                    throw new Exception("not find");
                }

                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentMenu => dispatchKeyEvent => Exception4 " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public void scrollEpisode(int action) {

        try {
            View focus = findFocus();
            if (null == focus)
                throw new Exception("error: null == focus");
            int focusId = focus.getId();
            if (focusId != R.id.module_mediaplayer_component_menu_item_child)
                throw new Exception("error: focusId != R.id.module_mediaplayer_component_menu_item");
            Object tag = focus.getTag();
            if (null == tag)
                throw new Exception("error: null == tag");

            int index = (int) tag;
            if (action == KeyEvent.KEYCODE_DPAD_LEFT && index <= 0)
                throw new Exception("error: index <= 0");

            int episodeItemCount = getEpisodeItemCount();
            if (action == KeyEvent.KEYCODE_DPAD_RIGHT && index + 1 >= episodeItemCount)
                throw new Exception("error: index+1 >= " + episodeItemCount + ", index = " + index);

            ViewGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_root);
            int itemCount = itemGroup.getChildCount();

            int start;
            if (action == KeyEvent.KEYCODE_DPAD_LEFT) {
                start = index - 1;
            } else if (action == KeyEvent.KEYCODE_DPAD_RIGHT) {
                start = index - itemCount + 2;
            } else {
                start = -1;
            }
            if (start == -1)
                throw new Exception("error: start = -1");

            // 选集角标
            int loaction = getEpisodeFlagLoaction();
            int vipResourceId = getEpisodeFlagVipResourceId();
            int freeResourceId = getEpisodeFlagFreeResourceId();
            int freeItemCount = getEpisodeFreeItemCount();

            int episodePlayingIndex = getEpisodePlayingIndex();
            for (int i = 0; i < itemCount; i++) {
                ItemRelativeLayout radioButton = (ItemRelativeLayout) itemGroup.getChildAt(i);
                if (null == radioButton)
                    continue;
                int position = i + start;

//                if (freeItemCount < 0) {
//                    radioButton.setFlag(loaction, 0, freeResourceId);
//                } else if (position < freeItemCount) {
//                    radioButton.setFlag(loaction, 0, freeResourceId);
//                } else {
//                    radioButton.setFlag(loaction, vipResourceId, 0);
//                }

//                radioButton.setTag(position);
//                radioButton.setText(String.valueOf(position + 1));
//                radioButton.setChecked(position == episodePlayingIndex);
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => scrollEpisode => " + e.getMessage());
        }
    }

    @Override
    public void hide() {
        superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_HIDE);
        try {
            setTag(null);
        } catch (Exception e) {
        }
        ComponentApiMenu.super.hide();
    }

    @Override
    public void show() {
        superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_SHOW);
        try {
            setTag(getPosition());
        } catch (Exception e) {
        }
        ComponentApiMenu.super.show();
    }

    @Override
    public void updateTabData(int index, boolean requestFocus) {

        // 菜单
        try {

            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int childCount = tabGroup.getChildCount();
            if (childCount > 0)
                throw new Exception("warning: childCount > 0");

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

            int length = data.length;
            for (int i = 0; i < length; i++) {
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_tab, tabGroup, true);
                View childAt = tabGroup.getChildAt(i);
                childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        setTag(getPosition());
                        int indexOfChild = tabGroup.indexOfChild(v);
                        updateTabData(indexOfChild, false);
                    }
                });

                TextView textView = childAt.findViewById(R.id.module_mediaplayer_component_menu_tab_child_text);
                textView.setText(data[i]);
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateTabData => Exception1 " + e.getMessage());
        }

        // 默认焦点
        try {
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            int childCount = tabGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = tabGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                childAt.setTag(i == index);
            }

            if (!requestFocus)
                throw new Exception("warning: requestFocus false");
            View childAt = tabGroup.getChildAt(index);
            childAt.requestFocus();
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateTabData => Exception2 " + e.getMessage());
        }

        // 数据
        try {

            ViewGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_root);
            int childCount = itemGroup.getChildCount();
            if (childCount > 0)
                throw new Exception("warning: childCount > 0");

            for (int i = 0; i < 10; i++) {
                LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item, itemGroup, true);
                View childAt = itemGroup.getChildAt(i);
                childAt.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
//                    int focusId = v.getId();
//                    updateItemSelected(focusId);
                        setTag(getPosition());
                    }
                });
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateTabData => Exception3 " + e.getMessage());
        }

//        // 数据
//        try {
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
//
//            RadioGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//            int checkedRadioButtonId = tabGroup.getCheckedRadioButtonId();
//            if (checkedRadioButtonId == -1) {
//                checkedRadioButtonId = hasEpisode ? R.id.module_mediaplayer_component_menu_tab_episode : R.id.module_mediaplayer_component_menu_tab_speed;
//            }
//
//            int tabCount = tabGroup.getChildCount();
//            for (int i = 0; i < tabCount; i++) {
//                RadioButton radioButton = (RadioButton) tabGroup.getChildAt(i);
//                radioButton.setVisibility(i == 0 && !hasEpisode ? View.GONE : View.VISIBLE);
//            }
//
//            tabGroup.check(checkedRadioButtonId);
//
//            // 焦点
//            if (requestFocus) {
//                findViewById(checkedRadioButtonId).requestFocus();
//            }
//
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => updateTabCheckedChange => Exception1 " + e.getMessage());
//        }

        // 数据
        try {

            // 菜单
            ViewGroup tabGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_root);
            View childAt = tabGroup.getChildAt(index);
            TextView tabText = childAt.findViewById(R.id.module_mediaplayer_component_menu_tab_child_text);
            CharSequence text = tabText.getText();

            // 数据
            ViewGroup itemGroup = findViewById(R.id.module_mediaplayer_component_menu_item_root);
            int itemCount = itemGroup.getChildCount();

            // 选集
            if ("选集".equals(text)) {
                int episodePlayingIndex = getEpisodePlayingIndex();
                int episodeItemCount = getEpisodeItemCount();
                int num = episodePlayingIndex / itemCount;
                int start = num * itemCount;
                if (episodeItemCount > itemCount) {
                    int length = start + itemCount;
                    if (length > episodeItemCount) {
                        start -= Math.abs(length - episodeItemCount);
                    }
                }

                // 选集角标
                int loaction = getEpisodeFlagLoaction();
                int vipResourceId = getEpisodeFlagVipResourceId();
                int freeResourceId = getEpisodeFlagFreeResourceId();
                int freeItemCount = getEpisodeFreeItemCount();

                for (int i = 0; i < itemCount; i++) {
                    ItemRelativeLayout itemView = (ItemRelativeLayout) itemGroup.getChildAt(i);
                    itemView.setVisibility(i < episodeItemCount ? View.VISIBLE : View.INVISIBLE);
                    itemView.setEnabled(i < episodeItemCount);
                    itemView.setTag(index);

                    TextView textView = itemView.findViewById(R.id.module_mediaplayer_component_menu_item_child_text);

                    if (i < episodeItemCount) {

                        int position = i + start;
//                        if (freeItemCount < 0) {
//                            textView.setFlag(loaction, 0, freeResourceId);
//                        } else if (position < freeItemCount) {
//                            radioButton.setFlag(loaction, 0, freeResourceId);
//                        } else {
//                            radioButton.setFlag(loaction, vipResourceId, 0);
//                        }

//                        textView.setHint(position);
                        textView.setText(String.valueOf(position + 1));
//                        radioButton.setChecked(position == episodePlayingIndex);
//                        radioButton.setSelected(position == episodePlayingIndex);
                    } else {
//                        radioButton.setEnabled(false);

//                        radioButton.clearFlag();

//                        textView.setHint(-1);
                        textView.setText("");
//                        radioButton.setChecked(false);
//                        radioButton.setSelected(false);
                    }

                }
            }
            // 倍速
            else if ("倍速".equals(text)) {
                int[] speeds = getSpeedTypes();

                for (int i = 0; i < itemCount; i++) {
                    ItemRelativeLayout itemView = (ItemRelativeLayout) itemGroup.getChildAt(i);
                    itemView.setVisibility(i < speeds.length ? View.VISIBLE : View.INVISIBLE);
                    itemView.setEnabled(i < speeds.length);
                    itemView.setTag(index);

                    TextView textView = itemView.findViewById(R.id.module_mediaplayer_component_menu_item_child_text);

//                    radioButton.clearFlag();
//                    radioButton.setSelected(false);
//                    radioButton.setEnabled(i < speeds.length);

//                    if (i < speeds.length) {
//                        float speed = getSpeed();
//                        radioButton.setChecked(speed == speeds[i]);
//                    } else {
//                        radioButton.setChecked(false);
//                    }

                    if (i < speeds.length) {
                        textView.setTag(speeds[i]);
                        if (speeds[i] == PlayerType.SpeedType._0_5) {
                            textView.setText("0.5");
                        } else if (speeds[i] == PlayerType.SpeedType._1_5) {
                            textView.setText("1.5");
                        } else if (speeds[i] == PlayerType.SpeedType._2_0) {
                            textView.setText("2.0");
                        } else if (speeds[i] == PlayerType.SpeedType._2_5) {
                            textView.setText("2.5");
                        } else if (speeds[i] == PlayerType.SpeedType._3_0) {
                            textView.setText("3.0");
                        } else if (speeds[i] == PlayerType.SpeedType._3_5) {
                            textView.setText("3.5");
                        } else if (speeds[i] == PlayerType.SpeedType._4_0) {
                            textView.setText("4.0");
                        } else if (speeds[i] == PlayerType.SpeedType._4_5) {
                            textView.setText("4.5");
                        } else if (speeds[i] == PlayerType.SpeedType._5_0) {
                            textView.setText("5.0");
                        } else {
                            textView.setText("1.0");
                        }
                    } else {
                        textView.setText("");
                        textView.setTag(null);
                    }
                }
            }
            // 画面
            else if ("画面".equals(text)) {
                int[] scales = getScaleTypes();
                for (int i = 0; i < itemCount; i++) {
                    ItemRelativeLayout itemView = (ItemRelativeLayout) itemGroup.getChildAt(i);
                    itemView.setVisibility(i < scales.length ? View.VISIBLE : View.INVISIBLE);
                    itemView.setEnabled(i < scales.length);
                    itemView.setTag(index);

                    TextView textView = itemView.findViewById(R.id.module_mediaplayer_component_menu_item_child_text);

//                    radioButton.clearFlag();
//                    radioButton.setSelected(false);
//                    radioButton.setEnabled(i < scales.length);

//                    if (i < scales.length) {
//                        int videoScaleType = getVideoScaleType();
//                        radioButton.setChecked(videoScaleType == scales[i]);
//                    } else {
//                        radioButton.setChecked(false);
//                    }

                    if (i < scales.length) {
                        textView.setTag(scales[i]);
                        if (scales[i] == PlayerType.ScaleType.REAL) {
                            textView.setText("原始");
                        } else if (scales[i] == PlayerType.ScaleType.FULL) {
                            textView.setText("全屏");
                        } else if (scales[i] == PlayerType.ScaleType._1_1) {
                            textView.setText("1:1");
                        } else if (scales[i] == PlayerType.ScaleType._4_3) {
                            textView.setText("4:3");
                        } else if (scales[i] == PlayerType.ScaleType._5_4) {
                            textView.setText("5:4");
                        } else if (scales[i] == PlayerType.ScaleType._16_9) {
                            textView.setText("16:9");
                        } else if (scales[i] == PlayerType.ScaleType._16_10) {
                            textView.setText("16:10");
                        } else {
                            textView.setText("自动");
                        }
                    } else {
                        textView.setText("");
                        textView.setTag(null);
                    }
                }
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => updateTabData => Exception4 " + e.getMessage());
        }
    }

    @Override
    public void toggleScale() {

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
        try {
            View focus = findFocus();
            if (null == focus)
                throw new Exception("warning: focus null");
            int focusId = focus.getId();
            if (focusId != R.id.module_mediaplayer_component_menu_item_child)
                throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_child");
            TextView textView = focus.findViewById(R.id.module_mediaplayer_component_menu_item_child_text);
            Object tag = textView.getTag();
            if (null == tag)
                throw new Exception("warning: tag null");
            setVideoScaleType((Integer) tag);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => toggleScale => Exception " + e.getMessage());
        }
    }

    @Override
    public void toggleSpeed() {

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
        try {
            View focus = findFocus();
            if (null == focus)
                throw new Exception("warning: focus null");
            int focusId = focus.getId();
            if (focusId != R.id.module_mediaplayer_component_menu_item_child)
                throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_child");
            TextView textView = focus.findViewById(R.id.module_mediaplayer_component_menu_item_child_text);
            Object tag = textView.getTag();
            if (null == tag)
                throw new Exception("warning: tag null");
            setSpeed((Integer) tag);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => toggleSpeed => Exception " + e.getMessage());
        }
    }

    @Override
    public void toggleEpisode(int focusId) {
//        try {
//
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
    public void updateTabSelected(int viewId) {
//        startDelayedMsg();
//        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//        radioGroup.check(viewId);
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

    public static final class ItemRelativeLayout extends RelativeLayout {
        public ItemRelativeLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    public static final class TabRelativeLayout extends RelativeLayout {
        public TabRelativeLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }
}
