package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import lib.kalu.mediaplayer.R;

public class ComponentMenu extends RelativeLayout implements ComponentApiMenu {
    public ComponentMenu(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu, this, true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // down
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            show();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final void hide() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void show() {
        try {
            findViewById(R.id.module_mediaplayer_component_menu_speed).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }

        try {
            RadioGroup radioGroup = findViewById(R.id.module_mediaplayer_component_menu_speed_group);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.module_mediaplayer_component_menu_speed_0_5) {
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
        }
    }
}
