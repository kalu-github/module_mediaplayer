package lib.kalu.mediaplayer.core.render.texture;

import android.content.Context;

import lib.kalu.mediaplayer.core.render.VideoRenderApi;
import lib.kalu.mediaplayer.core.render.VideoRenderFactory;

public class VideoTextureFactory implements VideoRenderFactory {

    private VideoTextureFactory() {
    }

    public static VideoTextureFactory build() {
        return new VideoTextureFactory();
    }

    @Override
    public VideoRenderApi create(Context context) {
        return new VideoTextureView(context);
    }
}
