package lib.kalu.mediaplayer.core.render;

import android.content.Context;

public class VideoSurfaceFactory implements VideoRenderFactory {

    private VideoSurfaceFactory() {
    }

    public static VideoSurfaceFactory build() {
        return new VideoSurfaceFactory();
    }

    @Override
    public VideoRenderApi create(Context context) {
        return new VideoRenderSurfaceView(context);
    }
}
