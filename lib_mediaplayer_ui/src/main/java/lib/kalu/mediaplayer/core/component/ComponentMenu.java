package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.listener.OnPlayerItemsLiatener;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public class ComponentMenu extends RelativeLayout implements ComponentApiMenu {
    public ComponentMenu(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu, this, true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // down
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {

            boolean showing = isComponentShowing();
            if (showing) {
                startDelayedMsg();
                View focus = findFocus();
                ViewParent parent = focus.getParent();
                int id = ((View) parent).getId();
                if (id == R.id.module_mediaplayer_component_menu_items_group) {
                    showSpeeds();
                }
            } else {
                show();
            }
            return true;
        }
        // up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            boolean showing = isComponentShowing();
            if (showing) {
                startDelayedMsg();
                View focus = findFocus();
                ViewParent parent = focus.getParent();
                int id = ((View) parent).getId();
                if (id == R.id.module_mediaplayer_component_menu_speeds_group) {
                    showItems();
                }
                return true;
            }
        }
        // left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            boolean showing = isComponentShowing();
            if (showing) {
                startDelayedMsg();
                View focus = findFocus();
                int id = focus.getId();
                if (id == R.id.module_mediaplayer_component_menu_speed_0_5) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_items_no1) {
                    moveItemsLeft();
                    return true;
                }
            }
        }
        // right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean showing = isComponentShowing();
            if (showing) {
                startDelayedMsg();
                View focus = findFocus();
                int id = focus.getId();
                if (id == R.id.module_mediaplayer_component_menu_speed_3_0) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_items_no10) {
                    moveItemsRight();
                    return true;
                }
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
            boolean componentShowing = isComponentShowing();
            if (componentShowing) {
                startDelayedMsg();
                View focus = findFocus();
                int id = ((View) focus.getParent()).getId();
                if (id == R.id.module_mediaplayer_component_menu_speeds_group) {
                    int focusId = focus.getId();
                    ((RadioGroup) focus.getParent()).check(focusId);
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
                } else if (id == R.id.module_mediaplayer_component_menu_items_group) {
                    ((RadioGroup) focus.getParent()).check(focus.getId());
                    CharSequence text = ((RadioButton) focus).getText();
                    if (null != text && text.length() > 0) {
                        hide();
                        stop();
                        int num = Integer.parseInt(text.toString());
                        int pos = --num;
                        callItemsClickListener(pos);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void moveItemsLeft() {
        try {
            View focus = findFocus();
            if (null == focus)
                throw new Exception("error: null == focus");
            if (!(focus instanceof RadioButton))
                throw new Exception("error: focus not RadioButton");
            CharSequence text = ((RadioButton) focus).getText();
            if (null == text || text.length() == 0)
                throw new Exception("error: text null");
            int num = Integer.parseInt(String.valueOf(text));
            if (num <= 1)
                throw new Exception("error: num <= 1");
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
            int childCount = radioGroup.getChildCount();
            int pos = --num;
            int start = pos - 1;
            int length = start + childCount;
            for (int i = start; i < length; i++) {
                int index = i - start;
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
                radioButton.setText(String.valueOf(i + 1));
                radioButton.setChecked(i == start);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => moveItemsLeft => " + e.getMessage());
        }
    }

    private void moveItemsRight() {
        try {
            View focus = findFocus();
            if (null == focus)
                throw new Exception("error: null == focus");
            if (!(focus instanceof RadioButton))
                throw new Exception("error: focus not RadioButton");
            CharSequence text = ((RadioButton) focus).getText();
            if (null == text || text.length() == 0)
                throw new Exception("error: text null");
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
            Object tag = radioGroup.getTag();
            if (null == tag)
                throw new Exception("error: null == tag");
            int count = (int) tag;
            int num = Integer.parseInt(String.valueOf(text));
            if (num >= count)
                throw new Exception("error: num >= " + count);
            int childCount = radioGroup.getChildCount();
            int pos = --num;
            int length = (pos + 2);
            int start = length - childCount;
            for (int i = start; i < length; i++) {
                int index = i - start;
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
                radioButton.setText(String.valueOf(i + 1));
                radioButton.setChecked(i + 1 == length);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => moveItemsRight => " + e.getMessage());
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
        try {
            findViewById(R.id.module_mediaplayer_component_menu_root).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_items_group).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => hide => " + e.getMessage());
        }
        stopDelayedMsg();
    }

    @Override
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => show => " + e.getMessage());
        }

        showItems();
        startDelayedMsg();
    }

    @Override
    public void setItemsData(int pos, int count) {

        RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
        radioGroup.setTag(count);
        int childCount = radioGroup.getChildCount();

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
            radioButton.setText(String.valueOf(i + 1));
            radioButton.setChecked(i == pos);
        }
    }

    private void showItems() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_items_group).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showItems => " + e.getMessage());
        }
        try {
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_items_group);
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == -1) {
                radioGroup.check(R.id.module_mediaplayer_component_menu_items_no1);
                checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            }
            findViewById(checkedRadioButtonId).requestFocus();
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showItems => " + e.getMessage());
        }
    }

    private void showSpeeds() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_items_group).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showSpeeds => " + e.getMessage());
        }
        try {
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_speeds_group);
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == -1) {
                radioGroup.check(R.id.module_mediaplayer_component_menu_speed_1_0);
                checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            }
            findViewById(checkedRadioButtonId).requestFocus();
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showSpeeds => " + e.getMessage());
        }
    }
}
