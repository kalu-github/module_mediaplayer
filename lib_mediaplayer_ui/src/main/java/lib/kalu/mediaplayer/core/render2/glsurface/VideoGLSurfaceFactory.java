package lib.kalu.mediaplayer.core.render2.glsurface;

import android.content.Context;

import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.core.render.VideoRenderFactory;

public class VideoGLSurfaceFactory implements VideoRenderFactory {

    private VideoGLSurfaceFactory() {
    }

    public static VideoGLSurfaceFactory build() {
        return new VideoGLSurfaceFactory();
    }

    @Override
    public VideoRenderApi create(Context context) {
        return new VideoGLSurfaceView(context);
    }
}
