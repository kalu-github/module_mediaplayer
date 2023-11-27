package lib.kalu.exoplayer2.ffmpeg;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegVideoRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

class BaseRenderersFactory extends com.google.android.exoplayer2.DefaultRenderersFactory {

    public BaseRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(initRendererMode());
        ExoLogUtil.log("BaseRenderersFactory => BaseRenderersFactory =>");
    }


    @Override
    protected void buildAudioRenderers(@NonNull Context context,
                                       @ExtensionRendererMode int extensionRendererMode,
                                       @NonNull MediaCodecSelector mediaCodecSelector,
                                       @NonNull boolean enableDecoderFallback,
                                       @NonNull AudioSink audioSink,
                                       @NonNull Handler eventHandler,
                                       @NonNull AudioRendererEventListener eventListener,
                                       @NonNull ArrayList<Renderer> out) {
        logAudioRenderer(out);
        addFFmpegAudioRenderers(out);
        addMediaCodecAudioRenderer(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);
        logAudioRenderer(out);
    }


    @Override
    protected void buildVideoRenderers(@NonNull Context context,
                                       @ExtensionRendererMode int extensionRendererMode,
                                       @NonNull MediaCodecSelector mediaCodecSelector,
                                       @NonNull boolean enableDecoderFallback,
                                       @NonNull Handler eventHandler,
                                       @NonNull VideoRendererEventListener eventListener,
                                       @NonNull long allowedVideoJoiningTimeMs,
                                       @NonNull ArrayList<Renderer> out) {
        logVideoRenderer(out);
        addFFmpegVideoRenderers(allowedVideoJoiningTimeMs, eventHandler, eventListener, 50, out);
        addMediaCodecVideoRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, allowedVideoJoiningTimeMs, out);
        logVideoRenderer(out);
    }

    protected int initRendererMode() {
        return com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
    }

    protected void addMediaCodecAudioRenderer(@NonNull Context context,
                                              @ExtensionRendererMode int extensionRendererMode,
                                              @NonNull MediaCodecSelector mediaCodecSelector,
                                              @NonNull boolean enableDecoderFallback,
                                              @NonNull AudioSink audioSink,
                                              @NonNull Handler eventHandler,
                                              @NonNull AudioRendererEventListener eventListener,
                                              @NonNull ArrayList<Renderer> out) {
        try {
            MediaCodecAudioRenderer audioRenderer = new MediaCodecAudioRenderer(
                    context,
                    getCodecAdapterFactory(),
                    mediaCodecSelector,
                    enableDecoderFallback,
                    eventHandler,
                    eventListener,
                    audioSink);
            out.add(audioRenderer);
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => addMediaCodecAudioRenderer => " + e.getMessage());
        }
    }

    protected void addFFmpegAudioRenderers(@NonNull ArrayList<Renderer> out) {
        try {
            FfmpegAudioRenderer ffmpegAudioRenderer = new FfmpegAudioRenderer();
            out.add(ffmpegAudioRenderer);
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => addFFmpegAudioRenderers => " + e.getMessage());
        }
    }

    protected void addMediaCodecVideoRenderers(@NonNull Context context,
                                               @ExtensionRendererMode int extensionRendererMode,
                                               @NonNull MediaCodecSelector mediaCodecSelector,
                                               @NonNull boolean enableDecoderFallback,
                                               @NonNull Handler eventHandler,
                                               @NonNull VideoRendererEventListener eventListener,
                                               @NonNull long allowedVideoJoiningTimeMs,
                                               @NonNull ArrayList<Renderer> out) {
        try {
            MediaCodecVideoRenderer videoRenderer = new MediaCodecVideoRenderer(
                    context,
                    getCodecAdapterFactory(),
                    mediaCodecSelector,
                    allowedVideoJoiningTimeMs,
                    enableDecoderFallback,
                    eventHandler,
                    eventListener,
                    MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY);
            out.add(videoRenderer);
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => addMediaCodecVideoRenderers => " + e.getMessage());
        }
    }

    protected void addFFmpegVideoRenderers(long allowedJoiningTimeMs,
                                           @Nullable Handler eventHandler,
                                           @Nullable VideoRendererEventListener eventListener,
                                           int maxDroppedFramesToNotify,
                                           @NonNull ArrayList<Renderer> out) {
        try {
            FfmpegVideoRenderer ffmpegVideoRenderer = new FfmpegVideoRenderer(allowedJoiningTimeMs, eventHandler, eventListener, maxDroppedFramesToNotify);
            out.add(ffmpegVideoRenderer);
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => addFFmpegVideoRenderers => " + e.getMessage());
        }
    }

    private void logAudioRenderer(@NonNull ArrayList<Renderer> out) {
        try {
            if (null == out || out.size() == 0)
                throw new Exception("out warning: " + out);
            ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => -------start---------");
            for (Renderer r : out) {
                if (null == r)
                    continue;
                ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => r = " + r);
            }
            ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => --------end---------");
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => logAudioRenderer => " + e.getMessage());
        }
    }

    private void logVideoRenderer(@NonNull ArrayList<Renderer> out) {
        try {
            if (null == out || out.size() == 0)
                throw new Exception("out warning: " + out);
            ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => -------start---------");
            for (Renderer r : out) {
                if (null == r)
                    continue;
                ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => r = " + r);
            }
            ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => --------end---------");
        } catch (Exception e) {
            ExoLogUtil.log("BaseRenderersFactory => logVideoRenderer => " + e.getMessage());
        }
    }
}
