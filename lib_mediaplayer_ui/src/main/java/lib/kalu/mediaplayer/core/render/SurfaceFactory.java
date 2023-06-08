package lib.kalu.mediaplayer.core.render;

import android.content.Context;

public class SurfaceFactory implements RenderFactory {

    private SurfaceFactory() {
    }

    public static SurfaceFactory build() {
        return new SurfaceFactory();
    }

    @Override
    public RenderApi create(Context context) {
        return new RenderSurfaceView(context);
    }
}
