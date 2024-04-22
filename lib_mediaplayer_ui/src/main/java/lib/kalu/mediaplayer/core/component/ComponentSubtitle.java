package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;



import lib.kalu.mediaplayer.R;

public class ComponentSubtitle extends RelativeLayout implements ComponentApi {

    public ComponentSubtitle( Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_subtitle, this, true);
    }

    @Override
    public final void show() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_subtitle).setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
    }

    @Override
    public final void hide() {
        try {
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_subtitle).setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }
}
