package lib.kalu.mediaplayer.type;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(CLASS)
@Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
public @interface PlayerType {

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface NetType {
        int HTTP = 2_001;
        int EXO_OKHTTP = 2_002;
        int DEFAULT = HTTP;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({HTTP, EXO_OKHTTP, DEFAULT})
        @interface Value {
        }
    }

    /**
     * 播放模式
     * 普通模式，小窗口模式，正常模式三种其中一种
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface WindowType {
        //普通模式
        int NORMAL = 2_001;
        //全屏模式
        int FULL = 2_002;
        //窗口模式
        int FLOAT = 2_003;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({NORMAL, FULL, FLOAT})
        @interface Value {
        }
    }

    /**
     * 播放状态，主要是指播放器的各种状态
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface EventType {
        int INIT = 3_000;            // 准备
        int RENDER_FIRST_FRAME = 3_001;    // 出画面
        int START = 3_002;           // 播放开始
        int PLAY_WHEN_READY_DELAYED_TIME = 3_003; // 延迟播放
        int PLAY_WHEN_READY_TRUE = 3_005; // 立即播放
        int PLAY_WHEN_READY_FALSE = 3_006;  // 不立即播放
        int PAUSE = 3_007; // 播放暂停
        int RESUME = 3_008; // 播放恢复
        int COMPLETE = 3_009; // 播放完成
        int STOP = 3_010; // 播放停止
        int RELEASE = 3_011; // 播放销毁

        int SEEK_START_FORWARD = 3_012; // 快进
        int SEEK_START_REWIND = 3_013; // 快退
        int SEEK_FINISH = 3_014; // 快退


        int LOADING_START = 3_015; // 起播加载
        int LOADING_STOP = 3_016; // 起播加载
        int BUFFERING_START = 3_017; // 缓冲
        int BUFFERING_STOP = 3_018; // 缓冲

        int WINDOW_FULL_START = 3_019;
        int WINDOW_FULL_SUCC = 3_020;
        int WINDOW_FULL_FAIL = 3_021;
        int WINDOW_FLOAT_START = 3_022;
        int WINDOW_FLOAT_SUCC = 3_023;
        int WINDOW_FLOAT_FAIL = 3_024;
        int TRY_SEE_START = 3_025;
        int TRY_SEE_FINISH = 3_026;
        int COMPONENT_MENU_SHOW = 3_027;
        int COMPONENT_MENU_HIDE = 3_028;

        int ERROR = 3_029; // 播放错误
        int ERROR_BUFFERING_TIMEOUT = 3_030;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({
                INIT,
                RENDER_FIRST_FRAME,
                START,
                PLAY_WHEN_READY_DELAYED_TIME,
                PLAY_WHEN_READY_TRUE,
                PLAY_WHEN_READY_FALSE,
                PAUSE,
                RESUME,
                COMPLETE,
                STOP,
                RELEASE,
                SEEK_START_FORWARD,
                SEEK_START_REWIND,
                SEEK_FINISH,
                LOADING_START,
                LOADING_STOP,
                BUFFERING_START,
                BUFFERING_STOP,
                WINDOW_FULL_START,
                WINDOW_FULL_SUCC,
                WINDOW_FULL_FAIL,
                WINDOW_FLOAT_START,
                WINDOW_FLOAT_SUCC,
                WINDOW_FLOAT_FAIL,
                TRY_SEE_START,
                TRY_SEE_FINISH,
                COMPONENT_MENU_SHOW,
                COMPONENT_MENU_HIDE,
                ERROR,
                ERROR_BUFFERING_TIMEOUT})
        @interface Value {
        }
    }

    /**
     * 播放视频缩放类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface ScaleType {
        int AUTO = 4_001;  // 自适应屏幕，可能存在黑边
        int FULL = 4_002;  // 画面拉甚至全屏, 可能变形
        int REAL = 4_003;  // 视频原始尺寸, 可能存在黑边
        int _16_9 = 4_004;  // 画面拉伸16：9, 可能变形
        int _16_10 = 4_005; // 画面拉伸16：10, 可能变形
        int _5_4 = 4_006;   // 画面拉伸5：4, 可能变形
        int _4_3 = 4_007;   // 画面拉伸4：3, 可能变形
        int _1_1 = 4_008;   // 画面拉伸1:1, 可能变形
        int DEFAULT = AUTO; // 默认

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({AUTO,
                FULL,
                REAL,
                _16_9,
                _16_10,
                _5_4,
                _4_3,
                _1_1,
                DEFAULT})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface RotationType {
        int _0 = 5_001;
        int _90 = 5_002;
        int _180 = 5_003;
        int _270 = 5_004;
        int DEFAULT = _0;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({_0,
                _90,
                _180,
                _270,
                DEFAULT})
        @interface Value {
        }
    }

    /**
     * 通过注解限定类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface KernelType {
        int ANDROID = 6_001; // MediaPlayer，基于原生自带的播放器控件
        int EXO_V2 = 6_002; // exoplayer2
        int MEDIA_V3 = 6_003; // androidx media
        int IJK = 6_004; // ijk
        int VLC = 6_005; // vlc
        int FFPLAYER = 6_006; // ffmpeg
        int DEFAULT = ANDROID;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({IJK,
                ANDROID,
                EXO_V2,
                MEDIA_V3,
                VLC,
                FFPLAYER,
                DEFAULT})
        @interface Value {
        }
    }

    /**
     * 通过注解限定类型
     */
    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface RenderType {
        int TEXTURE_VIEW = 8_001;
        int SURFACE_VIEW = 8_002;
        int GL_SURFACE_VIEW = 8_003;
        int DEFAULT = SURFACE_VIEW;

        @IntDef({TEXTURE_VIEW,
                SURFACE_VIEW,
                GL_SURFACE_VIEW,
                DEFAULT})
        @Retention(RetentionPolicy.SOURCE)
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface DecoderType {
        int ANDROID_ALL_CODEC = 10_000;
        int EXO_ALL_CODEC = 10_001;
        int EXO_ALL_FFMPEG = 10_004;
        int EXO_ONLY_AUDIO_CODEC = 10_002;
        int EXO_ONLY_VIDEO_CODEC = 10_003;
        int EXO_ONLY_AUDIO_FFMPEG = 10_005;
        int EXO_ONLY_VIDEO_FFMPEG = 10_006;
        int EXO_VIDEO_CODEC_AUDIO_FFMPEG = 10_007;
        int EXO_VIDEO_FFMPEG_AUDIO_CODEC = 10_008;
        int IJK_ALL_CODEC = 10_009;
        int IJK_ALL_FFMPEG = 10_010;
        int DEFAULT = ANDROID_ALL_CODEC;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                DecoderType.ANDROID_ALL_CODEC,
                DecoderType.EXO_ALL_CODEC,
                DecoderType.EXO_ALL_FFMPEG,
                DecoderType.EXO_ONLY_AUDIO_CODEC,
                DecoderType.EXO_ONLY_VIDEO_CODEC,
                DecoderType.EXO_ONLY_AUDIO_FFMPEG,
                DecoderType.EXO_ONLY_VIDEO_FFMPEG,
                DecoderType.EXO_VIDEO_CODEC_AUDIO_FFMPEG,
                DecoderType.EXO_VIDEO_FFMPEG_AUDIO_CODEC,
                DecoderType.IJK_ALL_CODEC,
                DecoderType.IJK_ALL_FFMPEG,
                DecoderType.DEFAULT})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface SeekType {

        int DEFAULT = 9_001;
        int EXO_CLOSEST_SYNC = 9_002;
        int EXO_PREVIOUS_SYNC = 9_003;
        int EXO_NEXT_SYNC = 9_004;
        int EXO_EXACT = 9_005;
        int ANDROID_SEEK_PREVIOUS_SYNC = 9_006;
        int ANDROID_SEEK_NEXT_SYNC = 9_007;
        int ANDROID_SEEK_CLOSEST_SYNC = 9_008;
        int ANDROID_SEEK_CLOSEST = 9_009;
        int IJK_SEEK_FASTSEEK = 9_010;
        int IJK_SEEK_NOBUFFER = 9_011;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                SeekType.DEFAULT,
                SeekType.EXO_CLOSEST_SYNC,
                SeekType.EXO_PREVIOUS_SYNC,
                SeekType.EXO_NEXT_SYNC,
                SeekType.EXO_EXACT,
                SeekType.ANDROID_SEEK_PREVIOUS_SYNC,
                SeekType.ANDROID_SEEK_NEXT_SYNC,
                SeekType.ANDROID_SEEK_CLOSEST_SYNC,
                SeekType.ANDROID_SEEK_CLOSEST,
                SeekType.IJK_SEEK_FASTSEEK,
                SeekType.IJK_SEEK_NOBUFFER,
        })
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface SchemeType {

        String RTMP = "rtmp";
        String RTSP = "rtsp";
        String _MPD = ".mpd";
        String _M3U = ".m3u";
        String _M3U8 = ".m3u8";
        String _MATCHES = ".*\\.ism(l)?(/manifest(\\(.+\\))?)?";

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @StringDef(value = {
                SchemeType.RTMP,
                SchemeType.RTSP,
                SchemeType._MPD,
                SchemeType._M3U,
                SchemeType._M3U8,
                SchemeType._MATCHES})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface SpeedType {
        int _0_5 = 8_001;
        int _1_0 = 8_002;
        int _1_5 = 8_003;
        int _2_0 = 8_004;
        int _2_5 = 8_005;
        int _3_0 = 8_006;
        int _3_5 = 8_007;
        int _4_0 = 8_008;
        int _4_5 = 8_009;
        int _5_0 = 8_010;
        int DEFAULT = _1_0;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef({_0_5,
                _1_0,
                _1_5,
                _2_0,
                _2_5,
                _3_0,
                _3_5,
                _4_0,
                _4_5,
                _5_0,
                DEFAULT})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface CacheType {

        int DEFAULT = 9_001;
        int EXO_OPEN = 9_002;
        int EXO_CLOSE = 9_003;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {CacheType.DEFAULT, CacheType.EXO_OPEN, CacheType.EXO_CLOSE})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface CacheLocalType {

        int INNER = 10_001;
        int EXTERNAL = 10_002;
        int DEFAULT = INNER;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                CacheLocalType.EXTERNAL,
                CacheLocalType.INNER,
                CacheLocalType.DEFAULT})
        @interface Value {
        }
    }

    @Documented
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    @interface CacheSizeType {

        int AUTO = 11_001;
        int _100 = 11_002;
        int _200 = 11_003;
        int _400 = 11_004;
        int _800 = 11_005;
        int DEFAULT = AUTO;

        @Documented
        @Retention(CLASS)
        @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
        @IntDef(value = {
                CacheSizeType.AUTO,
                CacheSizeType.DEFAULT,
                CacheSizeType._100,
                CacheSizeType._200,
                CacheSizeType._400,
                CacheSizeType._800,
        })
        @interface Value {
        }
    }
}