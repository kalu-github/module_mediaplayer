package lib.kalu.mediaplayer.core.render;

import android.content.Context;

public class VideoTextureFactory implements VideoRenderFactory {

    private VideoTextureFactory() {
    }

    public static VideoTextureFactory build() {
        return new VideoTextureFactory();
    }

    @Override
    public VideoRenderApi create(Context context) {
        return new VideoRenderTextureView(context);
    }
}
