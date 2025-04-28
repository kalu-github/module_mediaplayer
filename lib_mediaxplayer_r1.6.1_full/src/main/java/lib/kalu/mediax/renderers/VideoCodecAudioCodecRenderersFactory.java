package lib.kalu.mediax.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;;

import lib.kalu.mediax.util.MediaLogUtil;

@UnstableApi
public class VideoCodecAudioCodecRenderersFactory extends BaseRenderersFactory {

    public VideoCodecAudioCodecRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("BaseRenderersFactory => BaseOnlyMediaCodecRenderersFactory =>");
    }

    @Override
    protected void addAudioFFmpegRenderers(@NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addVideoFFmpegRenderers(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }
}
