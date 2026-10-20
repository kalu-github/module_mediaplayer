package lib.kalu.mpvplayer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Immutable demuxed track. IDs are scoped to one SDK load session, never array indices. */
public final class TrackInfo {
    public enum Type { VIDEO, AUDIO, SUBTITLE }
    public enum Kind { HLS_VARIANT, DEMUXED_TRACK }
    public enum Support { SUPPORTED, UNSUPPORTED, UNKNOWN }
    public final String id, name, language, codec, mimeType;
    public final Type type;
    public final Kind kind;
    public final Support support;
    public final long bitrate, hlsBitrate, programId;
    public final int width, height, channelCount, sampleRate, rotation;
    public final double frameRate, pixelAspectRatio;
    public final boolean selected, defaultTrack, forced, external;
    final long nativeId;
    private final Map<String,Object> values;

    TrackInfo(long session, Type type, Map<String,Object> data) {
        this(session,type,data,null);
    }
    TrackInfo(long session, Type type, Map<String,Object> data, Set<String> decoderCodecs) {
        this.type=type; nativeId=integer(data,"id",-1); id=session+":"+type+":"+nativeId;
        name=text(data,"title"); language=text(data,"lang"); codec=text(data,"codec"); mimeType=mime(codec);
        hlsBitrate=integer(data,"hls-bitrate",-1); bitrate=integer(data,"demux-bitrate",-1);
        programId=integer(data,"program-id",-1);
        kind=type==Type.VIDEO && hlsBitrate>0 ? Kind.HLS_VARIANT : Kind.DEMUXED_TRACK;
        width=(int)integer(data,"demux-w",-1); height=(int)integer(data,"demux-h",-1);
        channelCount=(int)integer(data,"demux-channel-count",-1); sampleRate=(int)integer(data,"demux-samplerate",-1);
        rotation=(int)integer(data,"demux-rotation",0); frameRate=decimal(data,"demux-fps",-1);
        pixelAspectRatio=decimal(data,"demux-par",-1);
        selected=Boolean.TRUE.equals(data.get("selected")); defaultTrack=Boolean.TRUE.equals(data.get("default"));
        forced=Boolean.TRUE.equals(data.get("forced")); external=Boolean.TRUE.equals(data.get("external"));
        support=selected && !text(data,"decoder").isEmpty() ? Support.SUPPORTED
            : type!=Type.SUBTITLE && !codec.isEmpty() && decoderCodecs!=null && !decoderCodecs.contains(codec)
                ? Support.UNSUPPORTED : Support.UNKNOWN;
        values=Collections.unmodifiableMap(new LinkedHashMap<>(data));
    }
    static long integer(Map<String,Object> map,String key,long fallback) {
        Object value=map.get(key); return value instanceof Number ? ((Number)value).longValue() : fallback;
    }
    static double decimal(Map<String,Object> map,String key,double fallback) {
        Object value=map.get(key); return value instanceof Number ? ((Number)value).doubleValue() : fallback;
    }
    static String text(Map<String,Object> map,String key) { Object value=map.get(key); return value instanceof String ? (String)value : ""; }
    static String mime(String codec) {
        switch(codec) {
            case "h264": return "video/avc";
            case "hevc": return "video/hevc";
            case "av1": return "video/av01";
            case "vp8": return "video/x-vnd.on2.vp8";
            case "vp9": return "video/x-vnd.on2.vp9";
            case "aac": return "audio/mp4a-latm";
            case "mp3": return "audio/mpeg";
            case "ac3": return "audio/ac3";
            case "eac3": return "audio/eac3";
            case "opus": return "audio/opus";
            case "vorbis": return "audio/vorbis";
            case "webvtt": return "text/vtt";
            case "ass": case "ssa": return "text/x-ssa";
            case "subrip": return "application/x-subrip";
            case "eia_608": return "application/cea-608";
            default: return "";
        }
    }
    @Override public boolean equals(Object other) {
        return other instanceof TrackInfo && id.equals(((TrackInfo)other).id) && values.equals(((TrackInfo)other).values);
    }
    @Override public int hashCode() { return Objects.hash(id,values); }
}
