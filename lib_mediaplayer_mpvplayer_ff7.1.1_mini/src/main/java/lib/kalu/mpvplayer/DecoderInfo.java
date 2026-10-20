package lib.kalu.mpvplayer;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Platform codec query; does not assert support by FFmpeg's software decoders. */
public final class DecoderInfo {
    public final String name, mimeType;
    /** null on Android 6-9, where the platform has no authoritative hardware/software flag. */
    public final Boolean hardwareAccelerated;
    private DecoderInfo(String name,String mime,Boolean hardware) {
        this.name=name; mimeType=mime; hardwareAccelerated=hardware;
    }
    public static List<DecoderInfo> findVideoDecoders(String mime,int width,int height,double frameRate) {
        if (mime==null || !mime.startsWith("video/") || width<=0 || height<=0
                || Double.isNaN(frameRate) || Double.isInfinite(frameRate) || frameRate<0)
            throw new IllegalArgumentException("Invalid video format");
        MediaFormat format=MediaFormat.createVideoFormat(mime,width,height);
        if (frameRate>0) format.setFloat(MediaFormat.KEY_FRAME_RATE,(float)frameRate);
        return find(mime,format);
    }
    public static List<DecoderInfo> findAudioDecoders(String mime,int sampleRate,int channelCount) {
        if(mime==null || !mime.startsWith("audio/") || sampleRate<=0 || channelCount<=0)
            throw new IllegalArgumentException("Invalid audio format");
        return find(mime,MediaFormat.createAudioFormat(mime,sampleRate,channelCount));
    }
    private static List<DecoderInfo> find(String mime,MediaFormat format) {
        List<DecoderInfo> result=new ArrayList<>();
        for (MediaCodecInfo codec:new MediaCodecList(MediaCodecList.ALL_CODECS).getCodecInfos()) {
            if (codec.isEncoder()) continue;
            for (String type:codec.getSupportedTypes()) if (type.equalsIgnoreCase(mime)) {
                try {
                    if (codec.getCapabilitiesForType(type).isFormatSupported(format))
                        result.add(new DecoderInfo(codec.getName(),type,Build.VERSION.SDK_INT>=29 ? codec.isHardwareAccelerated() : null));
                } catch (IllegalArgumentException ignored) { /* codec does not expose this format */ }
            }
        }
        return Collections.unmodifiableList(result);
    }
}
