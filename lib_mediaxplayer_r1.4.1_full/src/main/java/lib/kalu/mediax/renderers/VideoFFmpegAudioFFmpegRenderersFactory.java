package lib.kalu.mediax.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.audio.AudioRendererEventListener;
import androidx.media3.exoplayer.audio.AudioSink;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.mediax.util.MediaLogUtil;

@UnstableApi
public final class VideoFFmpegAudioFFmpegRenderersFactory extends DefaultRenderersFactory {

    public VideoFFmpegAudioFFmpegRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("DefaultRenderersFactory => VideoFFmpegAudioFFmpegRenderersFactory =>");
    }

    @Override
    protected void addVideoCodec(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull Handler eventHandler, @NonNull VideoRendererEventListener eventListener, @NonNull long allowedVideoJoiningTimeMs, @NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addAudioCodec(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull AudioSink audioSink, @NonNull Handler eventHandler, @NonNull AudioRendererEventListener eventListener, @NonNull ArrayList<Renderer> out) {
    }
}
