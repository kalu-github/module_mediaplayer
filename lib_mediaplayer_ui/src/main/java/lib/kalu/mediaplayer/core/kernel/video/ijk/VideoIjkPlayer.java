package lib.kalu.mediaplayer.core.kernel.video.ijk;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import lib.kalu.ijkplayer.IMediaPlayer;
import lib.kalu.ijkplayer.IjkMediaPlayer;
import lib.kalu.ijkplayer.IjkTimedText;
import lib.kalu.ijkplayer.inter.OnBufferingUpdateListener;
import lib.kalu.ijkplayer.inter.OnCompletionListener;
import lib.kalu.ijkplayer.inter.OnErrorListener;
import lib.kalu.ijkplayer.inter.OnInfoListener;
import lib.kalu.ijkplayer.inter.OnNativeInvokeListener;
import lib.kalu.ijkplayer.inter.OnPreparedListener;
import lib.kalu.ijkplayer.inter.OnSeekCompleteListener;
import lib.kalu.ijkplayer.inter.OnTimedTextListener;
import lib.kalu.ijkplayer.inter.OnVideoSizeChangedListener;
import lib.kalu.ijkplayer.misc.IMediaFormat;
import lib.kalu.ijkplayer.misc.IjkTrackInfo;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public final class VideoIjkPlayer extends VideoBasePlayer {

    private IjkMediaPlayer mIjkPlayer = null;

    public VideoIjkPlayer() {
    }

    @Override
    public VideoIjkPlayer getPlayer() {
        return this;
    }

    @Override
    public void releaseDecoder(boolean isFromUser) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer warning: null");
            if (isFromUser) {
                setEvent(null);
            }
            clear();
            unRegistListener();
            release();
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, StartArgs args) {
        try {
            if (null != mIjkPlayer)
                throw new Exception("warning: null != mIjkPlayer");
            mIjkPlayer = new IjkMediaPlayer();
            registListener();
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, StartArgs args) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("error: mIjkPlayer null");
            if (null == args)
                throw new Exception("error: args null");
            String url = args.getUrl();
            if (url == null || url.length() == 0)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.INIT_READY);
            mIjkPlayer.setDataSource(context, Uri.parse(url), null);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.PREPARE_START);
            boolean prepareAsync = args.isPrepareAsync();
            if (prepareAsync) {
                mIjkPlayer.prepareAsync();
            } else {
                mIjkPlayer.prepareAsync();
            }
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => startDecoder => " + e.getMessage());
        }
    }

    // player => ff_ffplay_options.h
    @Override
    public void initOptions(Context context, StartArgs args) {

        try {
            if (null == mIjkPlayer)
                throw new Exception("error: mIjkPlayer null");
            boolean mute = isMute();
            setVolume(mute ? 0L : 1L, mute ? 0L : 1L);
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => initOptions => Exception1 " + e.getMessage());
        }

        // log
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (null == args)
                throw new Exception("error: args null");
            Class<?> clazz = Class.forName("lib.kalu.ijkplayer.util.IjkLogUtil");
            if (null == clazz)
                throw new Exception("warning: lib.kalu.ijkplayer.util.IjkLogUtil not find");
            boolean log = args.isLog();
            lib.kalu.ijkplayer.util.IjkLogUtil.setLogger(log);
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => initOptions => Exception2 " + e.getMessage());
        }

        // ijk options
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (null == args)
                throw new Exception("error: args null");

            int decoderType = args.getDecoderType();
            boolean useMediaCodec = (decoderType == PlayerType.DecoderType.IJK_ALL_CODEC);
            LogUtil.log("VideoMedia3Player => createDecoder => decoderType = " + decoderType);

            int player = IjkMediaPlayer.OPT_CATEGORY_PLAYER;
            // 禁用音频
            mIjkPlayer.setOption(player, "an", 0);
            // 禁用视频, 不解码不渲染
            mIjkPlayer.setOption(player, "vn", 0);
            // 禁用图像, 解码不渲染
            mIjkPlayer.setOption(player, "nodisp", 0);
            // 音量 => [0,100]
            mIjkPlayer.setOption(player, "volume", 100);
            // ??
            mIjkPlayer.setOption(player, "fast", 1);
            // 循环播放次数
            mIjkPlayer.setOption(player, "loop", 1);
            // 以音频帧为时间基准，当视频帧和音频帧不同步时，允许丢弃的视频帧数
            mIjkPlayer.setOption(player, "framedrop", 100);
            // 是否解码字幕数据
            mIjkPlayer.setOption(player, "subtitle", 0);
            // ??
            mIjkPlayer.setOption(player, "rdftspeed", 0);
            // 读取和解码流以使用启发式方法填充丢失的信息
            mIjkPlayer.setOption(player, "find_stream_info", 1);
            // 图像颜色空间格式
            // SDL_FCC_YV12 ---- bpp=12, Planar mode: Y + V + U (3 planes)
            // SDL_FCC_I420 ---- bpp=12, Planar mode: Y + U + V (3 planes)
            // SDL_FCC_RV16 ---- bpp=16, RGB565
            // SDL_FCC_RV24 ---- bpp=24, RGB888
            // SDL_FCC_RV32 ---- bpp=32, RGBX8888
            mIjkPlayer.setOption(player, "overlay-format", useMediaCodec ? IjkMediaPlayer.SDL_FCC_RV32 : IjkMediaPlayer.SDL_FCC_RV16);
            // 允许的最大播放帧率，当视频的实际帧率大于这个数值时，将丢弃部分视频帧 => [-1,121]
            mIjkPlayer.setOption(player, "max-fps", useMediaCodec ? 60 : 30);
            // 是否播放准备工作完成后自动开始播放
            mIjkPlayer.setOption(player, "start-on-prepared", (isPlayWhenReady() ? 1 : 0));
            // 视频帧队列大小 => [3,16]
            mIjkPlayer.setOption(player, "video-pictq-size", 3);
            // 不限制输入缓冲区大小, 对实时流很有用
            // 0: url
            // 1: connentTimeout
            // 2: log
            // 3: seekParams
            // 4: bufferingTimeoutRetry
            // 5: kernelAlwaysRelease
            // 6: live
            boolean isLive = args.isLive();
            mIjkPlayer.setOption(player, "infbuf", isLive ? 1 : 0);
            // 预读数据的缓冲区大小 => [0, 15 * 1024 * 1024]
            mIjkPlayer.setOption(player, "max-buffer-size", 400 * 1024); // 400KB
            // 停止预读的最小帧数, 即预读帧数大于等于该值时, 将停止预读 => [2,50000]
            mIjkPlayer.setOption(player, "min-frames", 50000);
            // 缓冲读取线程的第一次唤醒时间, 单位毫秒 => [100,1000]
            mIjkPlayer.setOption(player, "first-high-water-mark-ms", 100);
            // 缓冲读取线程的第二次唤醒时间, 单位毫秒 => [100,1000]
            mIjkPlayer.setOption(player, "next-high-water-mark-ms", 100);
            // 缓冲读取线程的第三次唤醒时间, 单位毫秒 => [100,1000]
            mIjkPlayer.setOption(player, "last-high-water-mark-ms", 100);
            // 暂停输出, 直到停止后读取足够的数据包
            mIjkPlayer.setOption(player, "packet-buffering", 1);
            // 播放开始时对音视频进行同步操作
            mIjkPlayer.setOption(player, "sync-av-start", 1);
            // 强制使用指定格式，如RTSP, H264,FLV, MKV, MP4, AVI等
            mIjkPlayer.setOption(player, "iformat", null);
            // 如果使用实时模式而不是调整模式，则返回流中的位置
            // 这个用例主要是在使用自定义的不可搜索数据源时，该数据源以不是流开头的缓冲区开始。
            // 我们希望 get_current_position 返回流中的时间，而不是播放器的内部时间。
            mIjkPlayer.setOption(player, "no-time-adjust", 0);
            // 为5.1声道预设中央混合电平
            mIjkPlayer.setOption(player, "preset-5-1-center-mix-level", (long) (1 / Math.sqrt(2)));
            // 起始播放位置的偏移量，单位毫秒, 例如可以设置从第20秒的位置播放
            mIjkPlayer.setOption(player, "seek-at-start", 0);
            // 某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
            mIjkPlayer.setOption(player, "enable-accurate-seek", 1);
            // 设置精确寻帧的超时时间, 单位毫秒 => [0,5000]
            mIjkPlayer.setOption(player, "accurate-seek-timeout", 100); // 100ms
            // 不计算真实的帧率
            mIjkPlayer.setOption(player, "skip-calc-frame-rate", 0);
            // ??
            mIjkPlayer.setOption(player, "get-frame-mode", 0);
            // 异步创建解码器
            mIjkPlayer.setOption(player, "async-init-decoder", 0);
            // ??
            mIjkPlayer.setOption(player, "video-mime-type", null);
            // Android自动旋转角度
            mIjkPlayer.setOption(player, "mediacodec-auto-rotate", (useMediaCodec ? 1 : 0));
            // Android硬解, 是否分辨率变化时解码
            mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", (useMediaCodec ? 1 : 0));
            // Android硬解 ??
            mIjkPlayer.setOption(player, "mediacodec-sync", (useMediaCodec ? 1 : 0));
            // Android硬解 ??
            mIjkPlayer.setOption(player, "mediacodec-default-name", null);
            // Android硬解
            mIjkPlayer.setOption(player, "mediacodec-all-videos", (useMediaCodec ? 1 : 0));
            // Android硬解avc
            mIjkPlayer.setOption(player, "mediacodec-avc", (useMediaCodec ? 1 : 0));
            // Android硬解hevc
            mIjkPlayer.setOption(player, "mediacodec-hevc", (useMediaCodec ? 1 : 0));
            // Android硬解mpeg2
            mIjkPlayer.setOption(player, "mediacodec-mpeg2", (useMediaCodec ? 1 : 0));
            // Android硬解mpeg4
            mIjkPlayer.setOption(player, "mediacodec-mpeg4", (useMediaCodec ? 1 : 0));
            // Android硬解vp8
            mIjkPlayer.setOption(player, "mediacodec-vp8", (useMediaCodec ? 1 : 0));
            // Android硬解vp9
            mIjkPlayer.setOption(player, "mediacodec-vp9", (useMediaCodec ? 1 : 0));
            // Android音频解码opensl
            mIjkPlayer.setOption(player, "opensles", 0);
            // Android音频倍速
            mIjkPlayer.setOption(player, "soundtouch", 0);
            // Android初始化延迟时间
            mIjkPlayer.setOption(player, "ijkmeta-delay-init", 0);
            // Android等待开始绘制
            mIjkPlayer.setOption(player, "render-wait-start", 0);
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => initOptions => OPT_CATEGORY_PLAYER => " + e.getMessage());
        }

        // format
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (null == args)
                throw new Exception("error: args null");
            int format = IjkMediaPlayer.OPT_CATEGORY_FORMAT;
            // 设置播放前的探测时间 1,达到首屏秒开效果， bug有画面没声音
            mIjkPlayer.setOption(format, "analyzeduration", 100); // 100ms
            // 设置最长分析时长
            mIjkPlayer.setOption(format, "analyzemaxduration", 400); // 400ms
            // 探测带第一帧后就会数据返回，如果这个值设置过小，会导致流的信息分析不完整，从而导致丢失流，用于秒开
            mIjkPlayer.setOption(format, "probesize", 200 * 1024);// 200Kb
            // 通过立即清理数据包来减少等待时长, 每处理一个packet以后刷新io上下文
            mIjkPlayer.setOption(format, "flush_packets", 1);
            // 若是是rtsp协议，能够优先用tcp(默认是用udp)
            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
            // 超时时间,timeout参数只对http设置有效,单位ms => 10s
            // mIjkPlayer.setOption(format, "timeout", timout);
            // 重连次数
            mIjkPlayer.setOption(format, "reconnect", 0);

            // 快进
            @PlayerType.SeekType.Value
            int seekType = args.getSeekType();
            if (seekType == PlayerType.SeekType.IJK_SEEK_NOBUFFER) {
                // 设置seekTo能够快速seek到指定位置并播放, 解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
                mIjkPlayer.setOption(format, "fflags", "fastseek");
            } else {
                // 起播seek会失效
                mIjkPlayer.setOption(format, "fflags", "nobuffer");
            }

            // 根据媒体类型来配置 => bug => resp aac音频无声音
            mIjkPlayer.setOption(format, "allowed_media_types", "video");
            // rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
            mIjkPlayer.setOption(format, "rtsp_flags", "prefer_tcp");
            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
            // 如果项目中同时使用了HTTP和HTTPS的视频源的话，要注意如果视频源刚好是相同域名，
            mIjkPlayer.setOption(format, "http-detect-range-support", 1);
            // 清空DNS,有时因为在APP里面要播放多种类型的视频(如:MP4,直播,直播平台保存的视频,和其他http视频), 有时会造成因为DNS的问题而报10000问题的
            mIjkPlayer.setOption(format, "dns_cache_clear", 1);
            mIjkPlayer.setOption(format, "dns_cache_timeout", -1);
            // user_agent
            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", "ijkplayer");
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => initOptions => OPT_CATEGORY_FORMAT => " + e.getMessage());
        }

        // codec
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (null == args)
                throw new Exception("error: args null");
            int decoderType = args.getDecoderType();
            boolean useMediaCodec = (decoderType == PlayerType.DecoderType.IJK_ALL_CODEC);
            int codec = IjkMediaPlayer.OPT_CATEGORY_CODEC;
            // IJK_AVDISCARD_NONE    =-16, ///< discard nothing
            // IJK_AVDISCARD_DEFAULT =  0, ///< 如果包大小为0，则抛弃无效的包
            // IJK_AVDISCARD_NONREF  =  8, ///< 抛弃非参考帧（I帧）
            // IJK_AVDISCARD_BIDIR   = 16, ///< 抛弃B帧
            // IJK_AVDISCARD_NONKEY  = 32, ///< 抛弃除关键帧以外的，比如B，P帧
            // IJK_AVDISCARD_ALL     = 48, ///< 抛弃所有的帧
            // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            mIjkPlayer.setOption(codec, "skip_loop_filter", (useMediaCodec ? 0 : 48));
//            mIjkPlayer.setOption(codec, "skip_frame", 100); // // 跳过帧
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => initOptionsIjk => OPT_CATEGORY_CODEC => " + e.getMessage());
        }
    }

//    @Override
//    public void setDisplay(SurfaceHolder surfaceHolder) {
//        try {
//            if (null == mIjkPlayer)
//                throw new Exception("mIjkPlayer error: null");
//            mIjkPlayer.setDisplay(surfaceHolder);
//        } catch (Exception e) {
//            LogUtil.log("VideoIjkPlayer => setDisplay => " + e.getMessage());
//        }
//    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.setSurface(surface);
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => setSurface => " + e.getMessage());
        }
    }

    @Override
    public void registListener() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("error: mIjkPlayer null");
            mIjkPlayer.setOnErrorListener(onErrorListener);
            mIjkPlayer.setOnCompletionListener(onCompletionListener);
            mIjkPlayer.setOnInfoListener(onInfoListener);
            mIjkPlayer.setOnPreparedListener(onPreparedListener);
            mIjkPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mIjkPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
            mIjkPlayer.setOnTimedTextListener(onTimedTextListener);
            // 设置视频缓冲更新监听事件
            mIjkPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mIjkPlayer.setOnNativeInvokeListener(onNativeInvokeListener);
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => registListener => Exception " + e.getMessage());
        }
    }

    @Override
    public void unRegistListener() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("error: mIjkPlayer null");
            // 设置视频错误监听器
            mIjkPlayer.setOnErrorListener(null);
            // 设置视频播放完成监听事件
            mIjkPlayer.setOnCompletionListener(null);
            // 设置视频信息监听器
            mIjkPlayer.setOnInfoListener(null);
            // 设置视频缓冲更新监听事件
            mIjkPlayer.setOnBufferingUpdateListener(null);
            // 设置准备视频播放监听事件
            mIjkPlayer.setOnPreparedListener(null);
            // 设置视频大小更改监听器
            mIjkPlayer.setOnVideoSizeChangedListener(null);
            // 设置视频seek完成监听事件
            mIjkPlayer.setOnSeekCompleteListener(null);
            // 设置时间文本监听器
            mIjkPlayer.setOnTimedTextListener(null);
            // 缓冲
            mIjkPlayer.setOnBufferingUpdateListener(null);
            mIjkPlayer.setOnNativeInvokeListener(null);
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => unRegistListener => Exception " + e.getMessage());
        }
    }

    //    /**
    //     * 用于播放raw和asset里面的视频文件
    //     */
    //    @Override
    //    public void setDataSource(AssetFileDescriptor fd) {
    //        try {
    //            mIjkPlayer.setDataSource(new IMediaDataSourceForRaw(fd));
    //        } catch (Exception e) {
    //        }
    //    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.pause();
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => pause => " + e.getMessage());
        }
    }

    @Override
    public void release() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.setSurface(null);
            mIjkPlayer.release();
            mIjkPlayer = null;
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => release => " + e.getMessage());
        }
    }

    @Override
    public void start() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.start();
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => start => " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        clear();
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.stop();
            mIjkPlayer.reset();
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => stop => " + e.getMessage());
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            return mIjkPlayer.isPlaying();
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void seekTo(long seek) {
        try {
            if (seek < 0)
                throw new Exception("error: seek<0");
            if (null == mIjkPlayer)
                throw new Exception("error: mIjkPlayer null");
            StartArgs args = getStartArgs();
            if (null == args)
                throw new Exception("error: args null");

            long duration = getDuration();
            if (duration > 0 && seek > duration) {
                seek = duration;
            }

            long position = getPosition();
            onEvent(PlayerType.KernelType.IJK, seek < position ? PlayerType.EventType.SEEK_START_REWIND : PlayerType.EventType.SEEK_START_FORWARD);
            mIjkPlayer.seekTo(seek);
            LogUtil.log("VideoIjkPlayer => seekTo =>");
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => seekTo => " + e.getMessage());
        }
    }

    @Override
    public long getPosition() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            long currentPosition = mIjkPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
//            MPLogUtil.log("VideoIjkPlayer => getPosition => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public long getDuration() {
        try {
            if (!isPrepared())
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            long duration = mIjkPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
//            MPLogUtil.log("VideoIjkPlayer => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public boolean setSpeed(float speed) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.setSpeed(speed);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    /****************/

    @Override
    public void setVolume(float v1, float v2) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            boolean videoMute = isMute();
            if (videoMute) {
                mIjkPlayer.setVolume(0F, 0F);
            } else {
                float value = Math.max(v1, v2);
                if (value > 1f) {
                    value = 1f;
                }
                mIjkPlayer.setVolume(value, value);
            }
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => setVolume => " + e.getMessage());
        }
    }

    @Override
    public JSONArray getTrackInfo() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            IjkTrackInfo[] ijkTrackInfos = mIjkPlayer.getTrackInfo();
            if (null == ijkTrackInfos)
                throw new Exception("ijkTrackInfos error: null");
            JSONArray data = new JSONArray();
            for (IjkTrackInfo t : ijkTrackInfos) {
                if (null == t)
                    continue;
                JSONObject o = new JSONObject();
                o.put("type", t.getTrackType());
                o.put("launcher", t.getLanguage());
                o.put("info", t.getInfoInline());
                IMediaFormat format = t.getFormat();
                if (null != format) {
                    o.put("mime", format.getString("mime"));
                    o.put("width", format.getInteger("width"));
                    o.put("height", format.getInteger("height"));
                }
                data.put(o);
            }
            return data;
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => getTrackInfo => " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean switchTrack(int trackId) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.selectTrack(trackId);
            return true;
        } catch (Exception e) {
            LogUtil.log("VideoIjkPlayer => switchTrack => " + e.getMessage());
            return false;
        }
    }

    /**
     * 设置视频信息监听器
     */
    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            LogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", extra = " + extra);
            switch (what) {
                // 拉流开始
                case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:
                    break;
                // 拉流成功
                case IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN:
//                    onEvent(kernelType, PlayerType.EventType.LOADING_STOP);
                    break;
                // 缓冲开始
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    if (isPrepared()) {
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.BUFFERING_START);
                    } else {
                        LogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
                // 缓冲结束
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (isPrepared()) {
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.BUFFERING_STOP);
                    } else {
                        LogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
                // 出画面 => 起播
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    try {
                        if (isPrepared())
                            throw new Exception("warning: mPrepared true");
                        setPrepared(true);
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.PREPARE_COMPLETE);
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.VIDEO_RENDERING_START);
                        long seek = getPlayWhenReadySeekToPosition();
                        if (seek <= 0) {
                            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.START);
                            // 立即播放
                            boolean playWhenReady = isPlayWhenReady();
                            onEvent(PlayerType.KernelType.IJK, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                            if (!playWhenReady) {
                                pause();
                                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.PAUSE);
                            }
                        } else {
                            // 起播快进
                            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.SEEK_START_FORWARD);
                            setPlayWhenReadySeekFinish(true);
                            seekTo(seek);
                        }
                    } catch (Exception e) {
                        LogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", msg = " + e.getMessage());
                    }
                    break;
//                // 出画面 => 快进起播
//                case IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START:
//                    try {
//                        if (!mPrepared)
//                            throw new Exception("error: mPrepared false");
//                        onEvent(kernelType, PlayerType.EventType.VIDEO_START_SEEK);
//                    } catch (Exception e) {
//                        MPLogUtil.log("VideoIjkPlayer => onInfo => " + e.getMessage());
//                    }
//                    break;
                // 通知
                default:
//                    onEvent(kernelType, what);
                    break;
            }
            return true;
        }
    };

    private OnSeekCompleteListener onSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            LogUtil.log("VideoIjkPlayer => onSeekComplete =>");

            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.SEEK_FINISH);

            try {
                long seek = getPlayWhenReadySeekToPosition();
                if (seek <= 0L)
                    throw new Exception("warning: seek<=0");
                boolean playWhenReadySeekFinish = isPlayWhenReadySeekFinish();
                if (playWhenReadySeekFinish)
                    throw new Exception("warning: playWhenReadySeekFinish true");
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.START);
                // 立即播放
                boolean playWhenReady = isPlayWhenReady();
                onEvent(PlayerType.KernelType.IJK, playWhenReady ? PlayerType.EventType.START_PLAY_WHEN_READY_TRUE : PlayerType.EventType.START_PLAY_WHEN_READY_FALSE);
                if (!playWhenReady) {
                    pause();
                    onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.PAUSE);
                }
            } catch (Exception e) {
                LogUtil.log("VideoIjkPlayer => onSeekComplete => Exception1 " + e.getMessage());
            }

            try {
//                boolean prepared = isPrepared();
//                if (prepared)
//                    throw new Exception("warning: prepared true");
                boolean playing = isPlaying();
                if (playing)
                    throw new Exception("warning: playing true");
                start();
            } catch (Exception e) {
                LogUtil.log("VideoIjkPlayer => onSeekComplete => Exception2 " + e.getMessage());
            }
        }
    };

    private OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            LogUtil.log("VideoIjkPlayer => onPrepared =>");
            start();
        }
    };

    private OnVideoSizeChangedListener onVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sar_num, int sar_den) {
            try {
                if (null == iMediaPlayer)
                    throw new Exception("error: iMediaPlayer null");
                int videoWidth = iMediaPlayer.getVideoWidth();
                if (videoWidth <= 0)
                    throw new Exception("error: videoWidth <=0");
                int videoHeight = iMediaPlayer.getVideoHeight();
                if (videoHeight <= 0)
                    throw new Exception("error: videoHeight <=0");
                boolean videoSizeChanged = isVideoSizeChanged();
                if (videoSizeChanged)
                    throw new Exception("warning: videoSizeChanged = true");
                StartArgs args = getStartArgs();
                if (null == args)
                    throw new Exception("error: args null");
                setVideoSizeChanged(true);
                @PlayerType.ScaleType.Value
                int scaleType = args.getscaleType();
                int rotation = args.getRotation();
                onUpdateSizeChanged(PlayerType.KernelType.IJK, videoWidth, videoHeight, rotation, scaleType);
            } catch (Exception e) {
                LogUtil.log("VideoIjkPlayer => onVideoSizeChanged => " + e.getMessage());
            }
        }
    };

    /**
     * 设置视频错误监听器
     * int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染
     * int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲
     * int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
     * int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频选择信息
     * int MEDIA_ERROR_SERVER_DIED = 100;//视频中断，一般是视频源异常或者不支持的视频类型。
     * int MEDIA_ERROR_IJK_PLAYER = -10000,//一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
     * int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
     */
    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
            LogUtil.log("VideoIjkPlayer => onError => framework_err = " + framework_err + ", impl_err = " + impl_err);
            stop();
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.STOP);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.ERROR);
            return true;
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            LogUtil.log("VideoIjkPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.COMPLETE);
        }
    };

    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        }
    };

    /**
     * 设置时间文本监听器
     */
    private OnTimedTextListener onTimedTextListener = new OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
            LogUtil.log("VideoIjkPlayer => onTimedText => text = " + ijkTimedText.getText());
        }
    };
    private OnNativeInvokeListener onNativeInvokeListener = new OnNativeInvokeListener() {
        @Override
        public boolean onNativeInvoke(int i, Bundle bundle) {
            LogUtil.log("VideoIjkPlayer => onNativeInvoke => i => " + i + ", bundle = " + bundle);
            return true;
        }
    };
}
