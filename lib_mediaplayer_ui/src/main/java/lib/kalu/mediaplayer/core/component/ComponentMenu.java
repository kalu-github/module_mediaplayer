package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.listener.OnPlayerItemsLiatener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public class ComponentMenu extends RelativeLayout implements ComponentApiMenu {
    public ComponentMenu(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu, this, true);

        findViewById(R.id.module_mediaplayer_component_menu_tab_no0).setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateTabChecked(R.id.module_mediaplayer_component_menu_tab_no0);
            }
        });
        findViewById(R.id.module_mediaplayer_component_menu_tab_no1).setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateTabChecked(R.id.module_mediaplayer_component_menu_tab_no1);
            }
        });
        findViewById(R.id.module_mediaplayer_component_menu_tab_no2).setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateTabChecked(R.id.module_mediaplayer_component_menu_tab_no2);
            }
        });
        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateTabUI(true);
            }
        });
    }

    @Override
    public void callEventListener(int playState) {
        if (playState == PlayerType.StateType.STATE_END) {
//            try {
//                RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
//                Object tag = radioGroup.getTag();
//                if (null == tag) throw new Exception("error: null == tag");
//                int count = (int) tag;
//                if (count <= 1) throw new Exception("warning: count <= 1");
//                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//                RadioButton radioButton = radioGroup.findViewById(checkedRadioButtonId);
//                int num = Integer.parseInt(radioButton.getText().toString());
//                if (num >= count) throw new Exception("warning: num >= count");
//                setItemsChecked(num);
//            } catch (Exception e) {
//                LogUtil.log("ComponentMenu => callEventListener => Exception " + e.getMessage());
//            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // down
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            try {
                boolean componentShowing = isComponentShowing();
                if (componentShowing) {
                    View focus = findFocus();
                    if (null == focus) throw new Exception("warning: focus null");
                    ViewParent parent = focus.getParent();
                    if (null == parent) throw new Exception("warning: parent null");
                    int id = ((View) parent.getParent()).getId();
                    if (id != R.id.module_mediaplayer_component_menu_top_group)
                        throw new Exception("warning: id not R.id.module_mediaplayer_component_menu_top_group");
                    RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                    int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == -1) {
                        checkedRadioButtonId = R.id.module_mediaplayer_component_menu_tab_no0;
                    }
                    findViewById(checkedRadioButtonId).requestFocus();
                } else {
                    show();
                    updateTabUI(false);
                    startDelayedMsg();
                }
                return true;
            } catch (Exception e) {
            }
        }
        // up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                startDelayedMsg();
                View focus = findFocus();
                int id = ((View) focus.getParent()).getId();
                if (id != R.id.module_mediaplayer_component_menu_episode_group)
                    throw new Exception("warning: id not R.id.module_mediaplayer_component_menu_episode_group");
                return true;
            } catch (Exception e) {
            }
        }
        // left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: showing false");
                startDelayedMsg();
                View focus = findFocus();
                int id = focus.getId();
                if (id == R.id.module_mediaplayer_component_menu_speed_0_5) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_scale_type0) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_tab_no0) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_episode_no1) {
                    scrollNextItem(KeyEvent.KEYCODE_DPAD_LEFT);
                    return true;
                }
            } catch (Exception e) {
            }
        }
        // right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                boolean showing = isComponentShowing();
                if (!showing)
                    throw new Exception("warning: showing false");
                startDelayedMsg();
                View focus = findFocus();
                int id = focus.getId();
                if (id == R.id.module_mediaplayer_component_menu_speed_3_0) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_scale_type3) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_tab_no2) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_episode_no10) {
                    scrollNextItem(KeyEvent.KEYCODE_DPAD_RIGHT);
                    return true;
                }
            } catch (Exception e) {
            }
        }
        // back
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            boolean componentShowing = isComponentShowing();
            if (componentShowing) {
                hide();
                return true;
            }
        }
        // center
        else if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing)
                    throw new Exception("warning: componentShowing false");
                startDelayedMsg();
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int id = ((ViewGroup) focus.getParent()).getId();
                // 画面比例
                if (id == R.id.module_mediaplayer_component_menu_scale_group) {
                    toggleScale(focus.getId());
                }
                // 倍速
                else if (id == R.id.module_mediaplayer_component_menu_speeds_group) {
                    toggleSpeed(focus.getId());
                }
                // 选集
                else if (id == R.id.module_mediaplayer_component_menu_episode_group) {
                    toggleEpisode(focus.getId());
                }
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public void scrollNextItem(int action) {
        if (action == KeyEvent.KEYCODE_DPAD_LEFT) {
            try {
                View focus = findFocus();
                if (null == focus) throw new Exception("error: null == focus");
                if (!(focus instanceof RadioButton))
                    throw new Exception("error: focus not RadioButton");
                CharSequence text = ((RadioButton) focus).getText();
                if (null == text || text.length() == 0) throw new Exception("error: text null");
                int num = Integer.parseInt(String.valueOf(text));
                if (num <= 1) throw new Exception("error: num <= 1");
                RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
                int childCount = radioGroup.getChildCount();
                int pos = --num;
                int start = pos - 1;
                int length = start + childCount;
                for (int i = start; i < length; i++) {
                    int index = i - start;
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
                    radioButton.setText(String.valueOf(i + 1));
                    radioButton.setChecked(i == start);
                    CharSequence tempPlay = radioButton.getHint();
                    int tempPlayPos = Integer.parseInt(tempPlay.toString());
                    CharSequence tempText = radioButton.getText();
                    int tempPos = Integer.parseInt(tempText.toString());
                    radioButton.setSelected(tempPlayPos + 1 == tempPos);
                }
            } catch (Exception e) {
                LogUtil.log("ComponentMenu => scrollNextItem => " + e.getMessage());
            }
        } else if (action == KeyEvent.KEYCODE_DPAD_RIGHT) {
            try {
                View focus = findFocus();
                if (null == focus) throw new Exception("error: null == focus");
                if (!(focus instanceof RadioButton))
                    throw new Exception("error: focus not RadioButton");
                CharSequence text = ((RadioButton) focus).getText();
                if (null == text || text.length() == 0) throw new Exception("error: text null");
                RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
                Object tag = radioGroup.getTag();
                if (null == tag) throw new Exception("error: null == tag");
                int count = (int) tag;
                int num = Integer.parseInt(String.valueOf(text));
                if (num >= count) throw new Exception("error: num >= " + count);
                int childCount = radioGroup.getChildCount();
                int pos = --num;
                int length = (pos + 2);
                int start = length - childCount;
                for (int i = start; i < length; i++) {
                    int index = i - start;
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
                    radioButton.setText(String.valueOf(i + 1));
                    radioButton.setChecked(i + 1 == length);
                    CharSequence tempPlay = radioButton.getHint();
                    int tempPlayPos = Integer.parseInt(tempPlay.toString());
                    CharSequence tempText = radioButton.getText();
                    int tempPos = Integer.parseInt(tempText.toString());
                    radioButton.setSelected(tempPlayPos + 1 == tempPos);
                }
            } catch (Exception e) {
                LogUtil.log("ComponentMenu => scrollNextItem => " + e.getMessage());
            }
        }
    }

    @Override
    public void setItemsChecked(int position) {

        try {
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                radioButton.setHint(position);
                CharSequence tempText = radioButton.getText();
                int tempPos = Integer.parseInt(tempText.toString());
                radioButton.setSelected(position + 1 == tempPos);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => setItemsChecked => Exception " + e.getMessage());
        }

        try {
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
            int childCount = radioGroup.getChildCount();
            RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(0);
            RadioButton radioButton2 = (RadioButton) radioGroup.getChildAt(childCount - 1);
            int start = Integer.parseInt(radioButton1.getText().toString()) - 1;
            int end = Integer.parseInt(radioButton2.getText().toString()) - 1;
            if (position < start) {
                scrollNextItem(KeyEvent.KEYCODE_DPAD_LEFT);
            } else if (position > end) {
                scrollNextItem(KeyEvent.KEYCODE_DPAD_RIGHT);
            } else {
                int index = position % childCount;
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
                int id = radioButton.getId();
                radioGroup.check(id);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => setItemsChecked => Exception " + e.getMessage());
        }
    }

    @Override
    public boolean isComponentShowing() {
        try {
            int visibility = findViewById(R.id.module_mediaplayer_component_menu_root).getVisibility();
            return visibility == View.VISIBLE;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public final void hide() {
        LogUtil.log("ComponentMenu => hide => ");
        checkComponentPause2();
        try {
            findViewById(R.id.module_mediaplayer_component_menu_root).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_episode_group).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => hide => " + e.getMessage());
        }
        stopDelayedMsg();
    }

    @Override
    public final void show() {
        LogUtil.log("ComponentMenu => show => ");
        checkComponentPause1();
        try {
            findViewById(R.id.module_mediaplayer_component_menu_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => show => " + e.getMessage());
        }
    }

    @Override
    public void setItemsData(int pos, int count) {

        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
        radioGroup.setTag(count);
        int childCount = radioGroup.getChildCount();


        if (count >= childCount) {
            int num = pos / childCount;
            int start = num * childCount;
            int length = start + childCount;
            if (length > count) {
                start -= Math.abs(length - count);
                length = count;
            }
            for (int i = start; i < length; i++) {
                int index = i - start;
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
                radioButton.setEnabled(true);
                radioButton.setVisibility(View.VISIBLE);
                radioButton.setChecked(i == pos);
                radioButton.setText(String.valueOf(i + 1));
                // 在播pos
                radioButton.setHint(String.valueOf(pos));
                radioButton.setSelected(i == pos);
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                radioButton.setEnabled(i < count);
                radioButton.setVisibility(i < count ? View.VISIBLE : View.INVISIBLE);
                radioButton.setChecked(i == pos);
                radioButton.setText(String.valueOf(i + 1));
                // 在播pos
                radioButton.setHint(String.valueOf(pos));
                radioButton.setSelected(i == pos);
            }
        }
    }

    @Override
    public void updateTabUI(boolean isFromUser) {
        try {
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == -1) {
                checkedRadioButtonId = R.id.module_mediaplayer_component_menu_tab_no0;
            }

            if (!isFromUser) {
                findViewById(checkedRadioButtonId).requestFocus();
            }

            // 选集
            if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_no0) {
                findViewById(R.id.module_mediaplayer_component_menu_episode_group).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_menu_scale_group).setVisibility(View.GONE);
            }
            // 倍速
            else if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_no1) {
                findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_menu_episode_group).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_menu_scale_group).setVisibility(View.GONE);
            }
            // 画面比例
            else if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_no2) {
                findViewById(R.id.module_mediaplayer_component_menu_scale_group).setVisibility(View.VISIBLE);
                findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
                findViewById(R.id.module_mediaplayer_component_menu_episode_group).setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void updateTabChecked(int id) {
        startDelayedMsg();
        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
        radioGroup.check(id);
//        int childCount = radioGroup.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View childAt = radioGroup.getChildAt(i);
//            if (null == childAt)
//                continue;
//            int childAtId = childAt.getId();
//            ((RadioButton) childAt).setChecked(childAtId == id);
//        }
    }

    @Override
    public void toggleScale(int focusId) {
        // 4:3
        if (focusId == R.id.module_mediaplayer_component_menu_scale_type0) {
            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_4_3);
        }
        // 16:9
        else if (focusId == R.id.module_mediaplayer_component_menu_scale_type1) {
            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_16_9);
        }
        // 全屏
        else if (focusId == R.id.module_mediaplayer_component_menu_scale_type2) {
            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_SCREEN_MATCH);
        }
        // 原始大小
        else if (focusId == R.id.module_mediaplayer_component_menu_scale_type3) {
            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_VIDEO_ORIGINAL);
        }
    }

    @Override
    public void toggleSpeed(int focusId) {
        if (focusId == R.id.module_mediaplayer_component_menu_speeds_group) {
            setSpeed(0.5F);
        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_1_0) {
            setSpeed(1.0F);
        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_1_5) {
            setSpeed(1.5F);
        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_2_0) {
            setSpeed(2.0F);
        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_2_5) {
            setSpeed(2.5F);
        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_3_0) {
            setSpeed(3.0F);
        }
    }

    @Override
    public void toggleEpisode(int focusId) {
        try {
            RadioButton radioButton = findViewById(focusId);

            CharSequence hint = radioButton.getHint();
            int playPos = Integer.parseInt(hint.toString());

            CharSequence text = radioButton.getText();
            int numPos = Integer.parseInt(text.toString());

            // 判断在播pos
            if (playPos + 1 == numPos)
                throw new Exception("warning: playPos+1== numPos");

            int newPos = numPos - 1;

            // 1
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton button = (RadioButton) radioGroup.getChildAt(i);
                button.setHint(String.valueOf(newPos));
                CharSequence tempText = button.getText();
                int tempPos = Integer.parseInt(tempText.toString());
                button.setSelected(newPos + 1 == tempPos);
            }

            // 2
            hide();
            stop();
            radioGroup.check(focusId);
            callItemsClickListener(newPos);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => clickEpisode => Exception " + e.getMessage());
        }
    }

    //    @Override
//    public void showSpeeds() {
//        try {
//            findViewById(R.id.module_mediaplayer_component_menu_items_group).setVisibility(View.GONE);
//            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.VISIBLE);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showSpeeds => " + e.getMessage());
//        }
//        try {
//            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_speeds_group);
//            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//            if (checkedRadioButtonId == -1) {
//                radioGroup.check(R.id.module_mediaplayer_component_menu_speed_1_0);
//                checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//            }
//            findViewById(checkedRadioButtonId).requestFocus();
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showSpeeds => " + e.getMessage());
//        }
//    }

//    @Override
//    public void showItems() {
//        try {
//            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
//            findViewById(R.id.module_mediaplayer_component_menu_items_group).setVisibility(View.VISIBLE);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showItems => " + e.getMessage());
//        }

//        try {
//            int playIndex = -1;
//            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
//            int childCount = radioGroup.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
//                CharSequence hint = radioButton.getHint();
//                int playPos = Integer.parseInt(String.valueOf(hint));
//                if (playPos >= 0) {
//                    playIndex = playPos;
//                    break;
//                }
//            }
//            if (playIndex == -1)
//                throw new Exception("error: playIndex == -1");
//
//            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//            CharSequence text = ((RadioButton) radioGroup.findViewById(checkedRadioButtonId)).getText();
//            int numPos = Integer.parseInt(String.valueOf(text));
//
//            if (Math.abs(numPos - playIndex) >= childCount) {
//                radioGroup.findViewById(checkedRadioButtonId).requestFocus();
//            } else {
//                radioGroup.getChildAt(playIndex % childCount).requestFocus();
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showItems => " + e.getMessage());
//        }
//        try {
//            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
//            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//            if (checkedRadioButtonId == -1) {
//                radioGroup.check(R.id.module_mediaplayer_component_menu_items_no1);
//                checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//            }
//            findViewById(checkedRadioButtonId).requestFocus();
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => showItems => " + e.getMessage());
//        }
//    }
}
