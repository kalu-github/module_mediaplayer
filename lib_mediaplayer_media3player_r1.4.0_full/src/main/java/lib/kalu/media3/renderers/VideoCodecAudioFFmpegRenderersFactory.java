package lib.kalu.media3.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.audio.AudioRendererEventListener;
import androidx.media3.exoplayer.audio.AudioSink;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.media3.util.MediaLogUtil;

@UnstableApi
public class VideoCodecAudioFFmpegRenderersFactory extends BaseRenderersFactory {

    public VideoCodecAudioFFmpegRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("BaseRenderersFactory => BaseVideoMediaCodecAudioFFmpegRenderersFactory =>");
    }

    protected int initRendererMode() {
        return DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON;
    }

    @Override
    protected void addVideoFFmpegRenderers(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addAudioCodecRenderer(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull AudioSink audioSink, @NonNull Handler eventHandler, @NonNull AudioRendererEventListener eventListener, @NonNull ArrayList<Renderer> out) {
    }
}
