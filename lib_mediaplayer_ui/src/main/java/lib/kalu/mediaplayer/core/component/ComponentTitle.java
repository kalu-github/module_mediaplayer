package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;

public final class ComponentTitle extends RelativeLayout implements ComponentApi {

    public ComponentTitle(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.module_mediaplayer_component_title, this, true);
    }

    @Override
    public void show() {
        try {
            findViewById(R.id.module_mediaplayer_controller_title).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }

        try {
            TextView textView = findViewById(R.id.module_mediaplayer_controller_title_time);
            textView.setText("time");
        } catch (Exception e) {
        }
    }

    @Override
    public void gone() {
        try {
            findViewById(R.id.module_mediaplayer_controller_title).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(int value) {
        try {
            setText(this, R.id.module_mediaplayer_controller_title_name, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentText(@NonNull String value) {
        try {
            setText(this, R.id.module_mediaplayer_controller_title_name, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextSize(int value) {
        try {
            setTextSize(this, R.id.module_mediaplayer_controller_title_name, value);
        } catch (Exception e) {
        }
    }

    @Override
    public void setComponentTextColor(int color) {
        try {
            setTextColor(this, R.id.module_mediaplayer_controller_title_name, color);
        } catch (Exception e) {
        }
    }
}
