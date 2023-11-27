package lib.kalu.exoplayer2.ffmpeg;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

/**
 * EXTENSION_RENDERER_MODE_OFF: 关闭扩展
 * EXTENSION_RENDERER_MODE_ON: 打开扩展，优先使用硬解
 * EXTENSION_RENDERER_MODE_PREFER: 打开扩展，优先使用软解
 */
public final class FFmpegLowAudioRenderersFactory extends BaseRenderersFactory {

    @Override
    protected int initRendererMode() {
        return EXTENSION_RENDERER_MODE_PREFER;
    }

    public FFmpegLowAudioRenderersFactory(Context context) {
        super(context);
    }

    @Override
    protected void buildAudioRenderers(@NonNull Context context, @ExtensionRendererMode int extensionRendererMode, @NonNull MediaCodecSelector mediaCodecSelector, @NonNull boolean enableDecoderFallback, @NonNull AudioSink audioSink, @NonNull Handler eventHandler, @NonNull AudioRendererEventListener eventListener, @NonNull ArrayList<Renderer> out) {
        super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);
        out.add(0, new FfmpegAudioRenderer());
        for (Renderer renderer : out) {
            ExoLogUtil.log("FFmpegLowAudioRenderersFactory => buildAudioRenderers => renderer = " + renderer);
        }
    }

    @Override
    protected void buildVideoRenderers(Context context, @ExtensionRendererMode int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, Handler eventHandler, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        for (Renderer renderer : out) {
            ExoLogUtil.log("FFmpegLowAudioRenderersFactory => buildAudioRenderers => renderer = " + renderer);
        }
        super.buildVideoRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
    }
}
