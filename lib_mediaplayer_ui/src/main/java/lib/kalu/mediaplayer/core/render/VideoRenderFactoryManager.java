package lib.kalu.mediaplayer.core.render;

import android.content.Context;

import lib.kalu.mediaplayer.core.render.glsurface.VideoGLSurfaceFactory;
import lib.kalu.mediaplayer.core.render.surface.VideoSurfaceFactory;
import lib.kalu.mediaplayer.core.render.texture.VideoTextureFactory;
import lib.kalu.mediaplayer.bean.type.PlayerType;


public final class VideoRenderFactoryManager {

    public static VideoRenderApi createRender(Context context, @PlayerType.RenderType int type) {
        // TextureView
        if (type == PlayerType.RenderType.TEXTURE_VIEW) {
            return VideoTextureFactory.build().create(context);
        }
        // GLSurfaceView
        else if (type == PlayerType.RenderType.GL_SURFACE_VIEW) {
            return VideoGLSurfaceFactory.build().create(context);
        }
        // SurfaceView
        else {
            return VideoSurfaceFactory.build().create(context);
        }
    }
}
