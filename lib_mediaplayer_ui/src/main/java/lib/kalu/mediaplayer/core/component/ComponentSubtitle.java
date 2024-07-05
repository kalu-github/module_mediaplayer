package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;



import lib.kalu.mediaplayer.R;

public class ComponentSubtitle extends RelativeLayout implements ComponentApi {

    public ComponentSubtitle( Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_subtitle;
    }

    @Override
    public int initLayoutIdComponentRoot() {
        return R.id.module_mediaplayer_component_subtitle_root;
    }
}
