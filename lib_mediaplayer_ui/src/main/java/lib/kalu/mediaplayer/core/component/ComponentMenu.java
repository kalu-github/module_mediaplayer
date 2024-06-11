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
        }
    }

    @Override
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_root).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }

        showItems();
        initListener();
    }


    private void showItems() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_items_group).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_menu_items_no1).requestFocus();
        } catch (Exception e) {
        }
    }

    private void showSpeeds() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_items_group).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_menu_speeds_group).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_menu_speed_1_0).requestFocus();
        } catch (Exception e) {
        }
    }

    private void initListener() {
        LogUtil.log("ComponentMenu => initListener => ");
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed_0_5).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "0.5", Toast.LENGTH_SHORT).show();
                    setSpeed(0.5F);
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initListener => " + e.getMessage());
        }
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed_1_0).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSpeed(1.0F);
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initListener => " + e.getMessage());
        }
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed_1_5).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSpeed(1.5F);
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initListener => " + e.getMessage());
        }
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed_2_0).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSpeed(2.0F);
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initListener => " + e.getMessage());
        }
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed_2_5).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSpeed(2.5F);
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initListener => " + e.getMessage());
        }
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed_3_0).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSpeed(3.0F);
                }
            });
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => initListener => " + e.getMessage());
        }
    }
}
