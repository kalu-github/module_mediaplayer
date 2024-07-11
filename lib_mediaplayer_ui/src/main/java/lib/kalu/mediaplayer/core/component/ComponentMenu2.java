//package lib.kalu.mediaplayer.core.component;
//
//import android.content.Context;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.RelativeLayout;
//
//import lib.kalu.mediaplayer.R;
//import lib.kalu.mediaplayer.type.PlayerType;
//import lib.kalu.mediaplayer.util.LogUtil;
//
//public class ComponentMenu2 extends RelativeLayout implements ComponentApiMenu {
//    public ComponentMenu2(Context context) {
//        super(context);
//        inflate();
//
//        findViewById(R.id.module_mediaplayer_component_menu_tab_episode).setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                updateTabChecked(R.id.module_mediaplayer_component_menu_tab_episode);
//            }
//        });
//        findViewById(R.id.module_mediaplayer_component_menu_tab_speed).setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                updateTabChecked(R.id.module_mediaplayer_component_menu_tab_speed);
//            }
//        });
//        findViewById(R.id.module_mediaplayer_component_menu_tab_scale).setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                updateTabChecked(R.id.module_mediaplayer_component_menu_tab_scale);
//            }
//        });
//        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                updateTabCheckedChange(false);
//            }
//        });
//    }
//
//    @Override
//    public int initLayoutId() {
//        return R.layout.module_mediaplayer_component_menu2;
//    }
//
//    @Override
//    public void callEventListener(int playState) {
//        if (playState == PlayerType.StateType.STATE_END) {
////            try {
////                RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
////                Object tag = radioGroup.getTag();
////                if (null == tag) throw new Exception("error: null == tag");
////                int count = (int) tag;
////                if (count <= 1) throw new Exception("warning: count <= 1");
////                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
////                RadioButton radioButton = radioGroup.findViewById(checkedRadioButtonId);
////                int num = Integer.parseInt(radioButton.getText().toString());
////                if (num >= count) throw new Exception("warning: num >= count");
////                setItemsChecked(num);
////            } catch (Exception e) {
////                LogUtil.log("ComponentMenu => callEventListener => Exception " + e.getMessage());
////            }
//        }
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        // down
//        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
//            try {
//                boolean componentShowing = isComponentShowing();
//                if (componentShowing) {
//                    View focus = findFocus();
//                    if (null == focus) throw new Exception("warning: focus null");
//                    ViewParent parent = focus.getParent();
//                    if (null == parent) throw new Exception("warning: parent null");
//                    int id = ((View) parent.getParent()).getId();
//                    if (id != R.id.module_mediaplayer_component_menu_top_group)
//                        throw new Exception("warning: id not R.id.module_mediaplayer_component_menu_top_group");
//                    RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//                    int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//                    if (checkedRadioButtonId == -1) {
//                        checkedRadioButtonId = R.id.module_mediaplayer_component_menu_tab_episode;
//                    }
//                    findViewById(checkedRadioButtonId).requestFocus();
//                } else {
//                    show();
//                    updateTabCheckedChange(true);
//                    startDelayedMsg();
//                }
//                return true;
//            } catch (Exception e) {
//            }
//        }
//        // up
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
//            try {
//                boolean componentShowing = isComponentShowing();
//                if (!componentShowing)
//                    throw new Exception("warning: componentShowing false");
//                startDelayedMsg();
//                View focus = findFocus();
//                int id = ((View) focus.getParent()).getId();
//                if (id != R.id.module_mediaplayer_component_menu_episode_group && id != R.id.module_mediaplayer_component_menu_speeds_group && id != R.id.module_mediaplayer_component_menu_scale_group)
//                    throw new Exception("warning: can not up");
//                return true;
//            } catch (Exception e) {
//            }
//        }
//        // left
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//            try {
//                boolean componentShowing = isComponentShowing();
//                if (!componentShowing)
//                    throw new Exception("warning: showing false");
//                startDelayedMsg();
//                View focus = findFocus();
//                int id = focus.getId();
//                if (id == R.id.module_mediaplayer_component_menu_speed_0_5) {
//                    return true;
//                } else if (id == R.id.module_mediaplayer_component_menu_scale_type0) {
//                    return true;
//                } else if (id == R.id.module_mediaplayer_component_menu_tab_episode) {
//                    return true;
//                } else if (id == R.id.module_mediaplayer_component_menu_episode_no1) {
//                    scrollNextItem(KeyEvent.KEYCODE_DPAD_LEFT);
//                    return true;
//                }
//            } catch (Exception e) {
//            }
//        }
//        // right
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            try {
//                boolean showing = isComponentShowing();
//                if (!showing)
//                    throw new Exception("warning: showing false");
//                startDelayedMsg();
//                View focus = findFocus();
//                int id = focus.getId();
//                if (id == R.id.module_mediaplayer_component_menu_speed_3_0) {
//                    return true;
//                } else if (id == R.id.module_mediaplayer_component_menu_scale_type3) {
//                    return true;
//                } else if (id == R.id.module_mediaplayer_component_menu_tab_scale) {
//                    return true;
//                } else if (id == R.id.module_mediaplayer_component_menu_episode_no10) {
//                    scrollNextItem(KeyEvent.KEYCODE_DPAD_RIGHT);
//                    return true;
//                }
//            } catch (Exception e) {
//            }
//        }
//        // back
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            boolean componentShowing = isComponentShowing();
//            if (componentShowing) {
//                hide();
//                return true;
//            }
//        }
//        // center
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//            try {
//                boolean componentShowing = isComponentShowing();
//                if (!componentShowing)
//                    throw new Exception("warning: componentShowing false");
//                startDelayedMsg();
//                View focus = findFocus();
//                if (null == focus)
//                    throw new Exception("warning: focus null");
//                int id = ((ViewGroup) focus.getParent()).getId();
//                // 画面比例
//                if (id == R.id.module_mediaplayer_component_menu_scale_group) {
//                    toggleScale(focus.getId());
//                }
//                // 倍速
//                else if (id == R.id.module_mediaplayer_component_menu_speeds_group) {
//                    toggleSpeed(focus.getId());
//                }
//                // 选集
//                else if (id == R.id.module_mediaplayer_component_menu_episode_group) {
//                    toggleEpisode(focus.getId());
//                }
//                return true;
//            } catch (Exception e) {
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void scrollNextItem(int action) {
//        if (action == KeyEvent.KEYCODE_DPAD_LEFT) {
//            try {
//                View focus = findFocus();
//                if (null == focus) throw new Exception("error: null == focus");
//                if (!(focus instanceof RadioButton))
//                    throw new Exception("error: focus not RadioButton");
//                CharSequence text = ((RadioButton) focus).getText();
//                if (null == text || text.length() == 0) throw new Exception("error: text null");
//                int num = Integer.parseInt(String.valueOf(text));
//                if (num <= 1) throw new Exception("error: num <= 1");
//                RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
//                int childCount = radioGroup.getChildCount();
//                int pos = --num;
//                int start = pos - 1;
//                int length = start + childCount;
//                for (int i = start; i < length; i++) {
//                    int index = i - start;
//                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
//                    radioButton.setText(String.valueOf(i + 1));
//                    radioButton.setChecked(i == start);
//                    CharSequence tempPlay = radioButton.getHint();
//                    int tempPlayPos = Integer.parseInt(tempPlay.toString());
//                    CharSequence tempText = radioButton.getText();
//                    int tempPos = Integer.parseInt(tempText.toString());
//                    radioButton.setSelected(tempPlayPos + 1 == tempPos);
//                }
//            } catch (Exception e) {
//                LogUtil.log("ComponentMenu => scrollNextItem => " + e.getMessage());
//            }
//        } else if (action == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            try {
//                View focus = findFocus();
//                if (null == focus) throw new Exception("error: null == focus");
//                if (!(focus instanceof RadioButton))
//                    throw new Exception("error: focus not RadioButton");
//                CharSequence text = ((RadioButton) focus).getText();
//                if (null == text || text.length() == 0)
//                    throw new Exception("error: text null");
//                int count = getEpisodeCount();
//                RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
//                int num = Integer.parseInt(String.valueOf(text));
//                if (num >= count)
//                    throw new Exception("error: num >= " + count);
//                int childCount = radioGroup.getChildCount();
//                int pos = --num;
//                int length = (pos + 2);
//                int start = length - childCount;
//                for (int i = start; i < length; i++) {
//                    int index = i - start;
//                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
//                    radioButton.setText(String.valueOf(i + 1));
//                    radioButton.setChecked(i + 1 == length);
//                    CharSequence tempPlay = radioButton.getHint();
//                    int tempPlayPos = Integer.parseInt(tempPlay.toString());
//                    CharSequence tempText = radioButton.getText();
//                    int tempPos = Integer.parseInt(tempText.toString());
//                    radioButton.setSelected(tempPlayPos + 1 == tempPos);
//                }
//            } catch (Exception e) {
//                LogUtil.log("ComponentMenu => scrollNextItem => " + e.getMessage());
//            }
//        }
//    }
//
//    public void setItemsChecked1(int position) {
//
//        try {
//            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
//            int childCount = radioGroup.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
//                radioButton.setHint(position);
//                CharSequence tempText = radioButton.getText();
//                int tempPos = Integer.parseInt(tempText.toString());
//                radioButton.setSelected(position + 1 == tempPos);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => setItemsChecked => Exception " + e.getMessage());
//        }
//
//        try {
//            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
//            int childCount = radioGroup.getChildCount();
//            RadioButton radioButton1 = (RadioButton) radioGroup.getChildAt(0);
//            RadioButton radioButton2 = (RadioButton) radioGroup.getChildAt(childCount - 1);
//            int start = Integer.parseInt(radioButton1.getText().toString()) - 1;
//            int end = Integer.parseInt(radioButton2.getText().toString()) - 1;
//            if (position < start) {
//                scrollNextItem(KeyEvent.KEYCODE_DPAD_LEFT);
//            } else if (position > end) {
//                scrollNextItem(KeyEvent.KEYCODE_DPAD_RIGHT);
//            } else {
//                int index = position % childCount;
//                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
//                int id = radioButton.getId();
//                radioGroup.check(id);
//            }
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => setItemsChecked => Exception " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void hide() {
//        ComponentApiMenu.super.hide();
//
//        superCallEventListener(false, true, PlayerType.StateType.STATE_COMPONENT_MENU_HIDE);
//        stopDelayedMsg();
//    }
//
//    @Override
//    public void show() {
//        ComponentApiMenu.super.show();
//        superCallEventListener(false, true, PlayerType.StateType.STATE_COMPONENT_MENU_SHOW);
//
//        // 剧集数据
//        try {
//            int episodeCount = getEpisodeCount();
//            if (episodeCount <= 0)
//                throw new Exception("warning: episodeCount " + episodeCount);
//            int episodePlaying = getEpisodePlaying();
//            if (episodePlaying < 0)
//                throw new Exception("warning: episodePlaying " + episodePlaying);
//            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
////            radioGroup.setTag(count);
//            int childCount = radioGroup.getChildCount();
//            if (episodeCount >= childCount) {
//                int num = episodePlaying / childCount;
//                int start = num * childCount;
//                int length = start + childCount;
//                if (length > episodeCount) {
//                    start -= Math.abs(length - episodeCount);
//                    length = episodeCount;
//                }
//                for (int i = start; i < length; i++) {
//                    int index = i - start;
//                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
//                    radioButton.setEnabled(true);
//                    radioButton.setVisibility(View.VISIBLE);
//                    radioButton.setChecked(i == episodePlaying);
//                    radioButton.setText(String.valueOf(i + 1));
//                    // 在播pos
//                    radioButton.setHint(String.valueOf(episodePlaying));
//                    radioButton.setSelected(i == episodePlaying);
//                }
//            } else {
//                for (int i = 0; i < childCount; i++) {
//                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
//                    radioButton.setEnabled(i < episodeCount);
//                    radioButton.setVisibility(i < episodeCount ? View.VISIBLE : View.INVISIBLE);
//                    radioButton.setChecked(i == episodePlaying);
//                    radioButton.setText(String.valueOf(i + 1));
//                    // 在播pos
//                    radioButton.setHint(String.valueOf(episodePlaying));
//                    radioButton.setSelected(i == episodePlaying);
//                }
//            }
//            findViewById(R.id.module_mediaplayer_component_menu_tab_episode).setVisibility(View.VISIBLE);
//        } catch (Exception e) {
//            findViewById(R.id.module_mediaplayer_component_menu_tab_episode).setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public int initLayoutIdComponentRoot() {
//        return R.id.module_mediaplayer_component_menu_root;
//    }
//
//    @Override
//    public void updateTabCheckedChange(boolean requestFocus) {
//        boolean hasEpisode;
//        try {
//            int episodeCount = getEpisodeCount();
//            if (episodeCount <= 0)
//                throw new Exception("warning: episodeCount " + episodeCount);
//            int episodePlaying = getEpisodePlaying();
//            if (episodePlaying < 0)
//                throw new Exception("warning: episodePlaying " + episodePlaying);
//            hasEpisode = true;
//        } catch (Exception e) {
//            hasEpisode = false;
//        }
//
//        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//        if (checkedRadioButtonId == -1) {
//            checkedRadioButtonId = hasEpisode ? R.id.module_mediaplayer_component_menu_tab_episode : R.id.module_mediaplayer_component_menu_tab_speed;
//        }
//
//        // 选集
//        if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_episode) {
//            findViewById(R.id.module_mediaplayer_component_menu_episode_group).setVisibility(View.VISIBLE);
//            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
//            findViewById(R.id.module_mediaplayer_component_menu_scale_group).setVisibility(View.GONE);
//        }
//        // 倍速
//        else if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_speed) {
//            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.VISIBLE);
//            findViewById(R.id.module_mediaplayer_component_menu_episode_group).setVisibility(View.GONE);
//            findViewById(R.id.module_mediaplayer_component_menu_scale_group).setVisibility(View.GONE);
//        }
//        // 画面比例
//        else if (checkedRadioButtonId == R.id.module_mediaplayer_component_menu_tab_scale) {
//            findViewById(R.id.module_mediaplayer_component_menu_scale_group).setVisibility(View.VISIBLE);
//            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
//            findViewById(R.id.module_mediaplayer_component_menu_episode_group).setVisibility(View.GONE);
//        }
//
//        if (requestFocus) {
//            findViewById(checkedRadioButtonId).requestFocus();
//        }
//    }
//
//    @Override
//    public void updateTabChecked(int id) {
//        startDelayedMsg();
//        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
//        radioGroup.check(id);
////        int childCount = radioGroup.getChildCount();
////        for (int i = 0; i < childCount; i++) {
////            View childAt = radioGroup.getChildAt(i);
////            if (null == childAt)
////                continue;
////            int childAtId = childAt.getId();
////            ((RadioButton) childAt).setChecked(childAtId == id);
////        }
//    }
//
//    @Override
//    public void toggleScale(int focusId) {
//        // 4:3
//        if (focusId == R.id.module_mediaplayer_component_menu_scale_type0) {
//            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_4_3);
//        }
//        // 16:9
//        else if (focusId == R.id.module_mediaplayer_component_menu_scale_type1) {
//            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_16_9);
//        }
//        // 全屏
//        else if (focusId == R.id.module_mediaplayer_component_menu_scale_type2) {
//            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_SCREEN_MATCH);
//        }
//        // 原始大小
//        else if (focusId == R.id.module_mediaplayer_component_menu_scale_type3) {
//            setVideoScaleType(PlayerType.ScaleType.SCREEN_SCALE_VIDEO_ORIGINAL);
//        }
//    }
//
//    @Override
//    public void toggleSpeed(int focusId) {
//        if (focusId == R.id.module_mediaplayer_component_menu_speeds_group) {
//            setSpeed(0.5F);
//        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_1_0) {
//            setSpeed(1.0F);
//        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_1_5) {
//            setSpeed(1.5F);
//        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_2_0) {
//            setSpeed(2.0F);
//        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_2_5) {
//            setSpeed(2.5F);
//        } else if (focusId == R.id.module_mediaplayer_component_menu_speed_3_0) {
//            setSpeed(3.0F);
//        }
//    }
//
//    @Override
//    public void toggleEpisode(int focusId) {
//        try {
//            RadioButton radioButton = findViewById(focusId);
//
//            CharSequence hint = radioButton.getHint();
//            int playPos = Integer.parseInt(hint.toString());
//
//            CharSequence text = radioButton.getText();
//            int numPos = Integer.parseInt(text.toString());
//
//            // 判断在播pos
//            if (playPos + 1 == numPos)
//                throw new Exception("warning: playPos+1== numPos");
//
//            int newPos = numPos - 1;
//
//            // 1
//            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_episode_group);
//            int childCount = radioGroup.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                RadioButton button = (RadioButton) radioGroup.getChildAt(i);
//                button.setHint(String.valueOf(newPos));
//                CharSequence tempText = button.getText();
//                int tempPos = Integer.parseInt(tempText.toString());
//                button.setSelected(newPos + 1 == tempPos);
//            }
//
//            // 2
//            hide();
//            stop();
//            radioGroup.check(focusId);
//            callEpisodeClickListener(newPos);
//        } catch (Exception e) {
//            LogUtil.log("ComponentMenu => clickEpisode => Exception " + e.getMessage());
//        }
//    }
//}
