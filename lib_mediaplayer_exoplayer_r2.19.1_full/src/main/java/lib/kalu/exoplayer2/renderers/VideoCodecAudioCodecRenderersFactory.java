package lib.kalu.exoplayer2.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.exoplayer2.util.ExoLogUtil;

public class VideoCodecAudioCodecRenderersFactory extends BaseRenderersFactory {

    public VideoCodecAudioCodecRenderersFactory(Context context) {
        super(context);
        ExoLogUtil.log("BaseRenderersFactory => BaseVideoMediaCodecAudioFFmpegRenderersFactory =>");
    }

    protected int initRendererMode() {
        return DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON;
    }

    @Override
    protected void addAudioFFmpegRenderers(@NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addVideoFFmpegRenderers(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }
}
