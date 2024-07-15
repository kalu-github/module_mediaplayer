package lib.kalu.media3.renderers;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.video.VideoRendererEventListener;

import java.util.ArrayList;

import lib.kalu.media3.util.MediaLogUtil;

;

@UnstableApi
public class VideoCodecAudioCodecRenderersFactory extends BaseRenderersFactory {

    public VideoCodecAudioCodecRenderersFactory(Context context) {
        super(context);
        MediaLogUtil.log("BaseRenderersFactory => BaseOnlyMediaCodecRenderersFactory =>");
    }
}
