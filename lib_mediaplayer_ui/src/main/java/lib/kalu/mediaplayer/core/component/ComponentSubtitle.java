package lib.kalu.mediaplayer.core.component;

import android.content.Context;
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
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_subtitle_root;
    }
}
