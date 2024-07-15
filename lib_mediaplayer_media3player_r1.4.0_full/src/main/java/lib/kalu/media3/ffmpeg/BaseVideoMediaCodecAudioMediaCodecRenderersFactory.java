package lib.kalu.media3.ffmpeg;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;;

import lib.kalu.media3.util.MediaLogUtil;

public class BaseVideoMediaCodecAudioMediaCodecRenderersFactory extends BaseRenderersFactory {

    public BaseVideoMediaCodecAudioMediaCodecRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("BaseRenderersFactory => BaseOnlyMediaCodecRenderersFactory =>");
    }

    @Override
    protected void addFFmpegAudioRenderers(@NonNull ArrayList<Renderer> out) {
    }

    @Override
    protected void addFFmpegVideoRenderers(long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify, @NonNull ArrayList<Renderer> out) {
    }
}
