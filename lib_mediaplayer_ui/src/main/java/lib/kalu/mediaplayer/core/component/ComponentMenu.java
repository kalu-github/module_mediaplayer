package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.util.LogUtil;

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
                View focus = findFocus();
                int id = focus.getId();
                if (id == R.id.module_mediaplayer_component_menu_speed_0_5) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_items_no1) {
                    return true;
                }
            }
        }
        // right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            boolean showing = isComponentShowing();
            if (showing) {
                View focus = findFocus();
                int id = focus.getId();
                if (id == R.id.module_mediaplayer_component_menu_speed_3_0) {
                    return true;
                } else if (id == R.id.module_mediaplayer_component_menu_items_no10) {
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
                View focus = findFocus();
                int id = ((View) focus.getParent()).getId();
                if (id == R.id.module_mediaplayer_component_menu_speeds_group) {
                    ((RadioGroup) focus.getParent()).check(focus.getId());
                } else if (id == R.id.module_mediaplayer_component_menu_items_group) {
                    ((RadioGroup) focus.getParent()).check(focus.getId());
                }
                return true;
            }
        }
        return false;
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
    }

    @Override
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => show => " + e.getMessage());
        }

        showItems();
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
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Toast.makeText(getContext(), "切换选集", Toast.LENGTH_SHORT).show();
                }
            });
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
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.module_mediaplayer_component_menu_speeds_group) {
                        setSpeed(0.5F);
                    } else if (checkedId == R.id.module_mediaplayer_component_menu_speed_1_0) {
                        setSpeed(1.0F);
                    } else if (checkedId == R.id.module_mediaplayer_component_menu_speed_1_5) {
                        setSpeed(1.5F);
                    } else if (checkedId == R.id.module_mediaplayer_component_menu_speed_2_0) {
                        setSpeed(2.0F);
                    } else if (checkedId == R.id.module_mediaplayer_component_menu_speed_2_5) {
                        setSpeed(2.5F);
                    } else if (checkedId == R.id.module_mediaplayer_component_menu_speed_3_0) {
                        setSpeed(3.0F);
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showSpeeds => " + e.getMessage());
        }
    }
}
