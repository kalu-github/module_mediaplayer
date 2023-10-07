package lib.kalu.mediaplayer.core.render;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;

@Keep
public final class VideoRenderFactoryManager {

    public static VideoRenderApi createRender(@NonNull Context context, @PlayerType.VideoRenderType int type) {
        if (type == PlayerType.VideoRenderType.VIDEO_SURFACE_VIEW) {
            return VideoSurfaceFactory.build().create(context);
        } else {
            return VideoTextureFactory.build().create(context);
        }
    }
}
