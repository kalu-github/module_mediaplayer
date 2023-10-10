package lib.kalu.mediaplayer.core.render;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public final class VideoRenderFactoryManager {

    public static VideoRenderApi createRender(@NonNull Context context, @PlayerType.RenderType int type) {
        if (type == PlayerType.RenderType.SURFACE_VIEW) {
            return VideoSurfaceFactory.build().create(context);
        } else {
            return VideoTextureFactory.build().create(context);
        }
    }
}
