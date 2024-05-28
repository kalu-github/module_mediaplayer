package lib.kalu.mediaplayer.core.render;

import android.content.Context;




import lib.kalu.mediaplayer.type.PlayerType;


public final class VideoRenderFactoryManager {

    public static VideoRenderApi createRender( Context context, @PlayerType.RenderType int type) {
        if (type == PlayerType.RenderType.SURFACE_VIEW) {
            return VideoSurfaceFactory.build().create(context);
        } else {
            return VideoTextureFactory.build().create(context);
        }
    }
}
