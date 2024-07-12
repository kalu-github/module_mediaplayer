package lib.kalu.mediaplayer.core.render.surface;

import android.content.Context;

import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.core.render.VideoRenderFactory;

public class VideoSurfaceFactory implements VideoRenderFactory {

    private VideoSurfaceFactory() {
    }

    public static VideoSurfaceFactory build() {
        return new VideoSurfaceFactory();
    }

    @Override
    public VideoRenderApi create(Context context) {
        return new VideoSurfaceView(context);
    }
}
