package lib.kalu.mediaplayer.core.kernel.video.ijk;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.FloatRange;


import org.json.JSONArray;
import org.json.JSONObject;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.VideoKernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.VideoBasePlayer;
import lib.kalu.mediaplayer.core.player.video.VideoPlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.ijkplayer.IMediaPlayer;
import lib.kalu.ijkplayer.IjkMediaPlayer;
import lib.kalu.ijkplayer.IjkTimedText;
import lib.kalu.ijkplayer.misc.IMediaFormat;
import lib.kalu.ijkplayer.misc.IjkTrackInfo;


public final class VideoIjkPlayer extends VideoBasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mPlayWhenReady = true;
    private boolean mPrepared = false;

    private boolean mUseMediaCodec;
    private IjkMediaPlayer mIjkPlayer = null;

    public VideoIjkPlayer(boolean useMediaCodec, VideoPlayerApi musicApi, VideoKernelApiEvent eventApi) {
        super(musicApi, eventApi);
        mUseMediaCodec = useMediaCodec;
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
            release();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(Context context, boolean logger, int seekParameters) {
        try {
            releaseDecoder(false);
            mIjkPlayer = new IjkMediaPlayer();
            mIjkPlayer.reset();
            mIjkPlayer.setLooping(false);
            //处理UA问题
            //            if (headers != null) {
            //                String userAgent = headers.get("User-Agent");
            //                if (!TextUtils.isEmpty(userAgent)) {
            //                    mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent);
            //                }
            //            }
            initOptionsIjk();
            initListener();
            setVolume(1F, 1F);
            lib.kalu.ijkplayer.util.IjkLogUtil.setLogger(logger);
            IjkMediaPlayer.native_setLogger(logger);
            IjkMediaPlayer.native_setLogLevel(logger ? IjkMediaPlayer.IJK_LOG_INFO : IjkMediaPlayer.IJK_LOG_DEFAULT);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(Context context, String url, boolean prepareAsync) {
        MPLogUtil.log("VideoIjkPlayer => startDecoder => mIjkPlayer = " + mIjkPlayer + ", url = " + url + ", prepareAsync = " + prepareAsync);
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (url == null || url.length() == 0)
                throw new Exception("url error: " + url);
            mIjkPlayer.setDataSource(context, Uri.parse(url), null);
            if (prepareAsync) {
                mIjkPlayer.prepareAsync();
            } else {
                mIjkPlayer.prepareAsync();
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => startDecoder => " + e.getMessage());
        }
    }

    @Override
    public void initOptionsIjk() {
        // player => ff_ffplay_options.h
        try {
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
            mIjkPlayer.setOption(player, "overlay-format", mUseMediaCodec ? IjkMediaPlayer.SDL_FCC_RV32 : IjkMediaPlayer.SDL_FCC_RV16);
            // 允许的最大播放帧率，当视频的实际帧率大于这个数值时，将丢弃部分视频帧 => [-1,121]
            mIjkPlayer.setOption(player, "max-fps", mUseMediaCodec ? 121 : 30);
            // 是否播放准备工作完成后自动开始播放
            mIjkPlayer.setOption(player, "start-on-prepared", (mPlayWhenReady ? 1 : 0));
            // 视频帧队列大小 => [3,16]
            mIjkPlayer.setOption(player, "video-pictq-size", 3);
            // 不限制输入缓冲区大小, 对实时流很有用
            mIjkPlayer.setOption(player, "infbuf", 0);
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
            // 使用精确寻帧, 例如，拖动播放后，会寻找最近的关键帧进行播放，很有可能关键帧的位置不是拖动后的位置，而是较前的位置。可以设置这个参数来解决问题
            mIjkPlayer.setOption(player, "enable-accurate-seek", 0);
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
            mIjkPlayer.setOption(player, "mediacodec-auto-rotate", 0);
            // Android硬解, 是否分辨率变化时解码
            mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", 0);
            // Android硬解 ??
            mIjkPlayer.setOption(player, "mediacodec-sync", 1);
            // Android硬解 ??
            mIjkPlayer.setOption(player, "mediacodec-default-name", null);
            // Android硬解
            mIjkPlayer.setOption(player, "mediacodec-all-videos", (mUseMediaCodec ? 1 : 0));
            // Android硬解avc
            mIjkPlayer.setOption(player, "mediacodec-avc", (mUseMediaCodec ? 1 : 0));
            // Android硬解hevc
            mIjkPlayer.setOption(player, "mediacodec-hevc", (mUseMediaCodec ? 1 : 0));
            // Android硬解mpeg2
            mIjkPlayer.setOption(player, "mediacodec-mpeg2", (mUseMediaCodec ? 1 : 0));
            // Android硬解mpeg4
            mIjkPlayer.setOption(player, "mediacodec-mpeg4", (mUseMediaCodec ? 1 : 0));
            // Android硬解vp8
            mIjkPlayer.setOption(player, "mediacodec-vp8", (mUseMediaCodec ? 1 : 0));
            // Android硬解vp9
            mIjkPlayer.setOption(player, "mediacodec-vp9", (mUseMediaCodec ? 1 : 0));
            // Android音频解码opensl
            mIjkPlayer.setOption(player, "opensles", 0);
            // Android音频倍速
            mIjkPlayer.setOption(player, "soundtouch", 0);
            // Android初始化延迟时间
            mIjkPlayer.setOption(player, "ijkmeta-delay-init", 0);
            // Android等待开始绘制
            mIjkPlayer.setOption(player, "render-wait-start", 0);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => initOptionsIjk => OPT_CATEGORY_PLAYER => " + e.getMessage());
        }

        // format
        try {
            int format = IjkMediaPlayer.OPT_CATEGORY_FORMAT;
            // 设置播放前的探测时间 1,达到首屏秒开效果， bug有画面没声音
            mIjkPlayer.setOption(format, "analyzeduration", 100); // 100ms
            // 设置最长分析时长
            mIjkPlayer.setOption(format, "analyzemaxduration", 400); // 400ms
            // 探测带第一帧后就会数据返回，如果这个值设置过小，会导致流的信息分析不完整，从而导致丢失流，用于秒开
            mIjkPlayer.setOption(format, "probesize", 200 * 1024);// 200Kb
            // 通过立即清理数据包来减少等待时长, 每处理一个packet以后刷新io上下文
            mIjkPlayer.setOption(format, "flush_packets", 1);
            // 清空DNS,有时因为在APP里面要播放多种类型的视频(如:MP4,直播,直播平台保存的视频,和其他http视频), 有时会造成因为DNS的问题而报10000问题的
            mIjkPlayer.setOption(format, "dns_cache_clear", 1);
            // 若是是rtsp协议，能够优先用tcp(默认是用udp)
            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
            // 超时时间,单位ms => 10s
            mIjkPlayer.setOption(format, "timeout", 10 * 1000); // 10s
            // 设置seekTo能够快速seek到指定位置并播放, 解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
            mIjkPlayer.setOption(format, "fflags", "fastseek");
//            mIjkPlayer.setOption(format, "fflags", "nobuffer");  // 起播seek会失效
            // 根据媒体类型来配置 => bug => resp aac音频无声音
            mIjkPlayer.setOption(format, "allowed_media_types", "video");
            // rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
            mIjkPlayer.setOption(format, "rtsp_flags", "prefer_tcp");
            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
            // ??
            mIjkPlayer.setOption(format, "http-detect-range-support", 0);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => initOptionsIjk => OPT_CATEGORY_FORMAT => " + e.getMessage());
        }

        // codec
        try {
            int codec = IjkMediaPlayer.OPT_CATEGORY_CODEC;
            // IJK_AVDISCARD_NONE    =-16, ///< discard nothing
            // IJK_AVDISCARD_DEFAULT =  0, ///< 如果包大小为0，则抛弃无效的包
            // IJK_AVDISCARD_NONREF  =  8, ///< 抛弃非参考帧（I帧）
            // IJK_AVDISCARD_BIDIR   = 16, ///< 抛弃B帧
            // IJK_AVDISCARD_NONKEY  = 32, ///< 抛弃除关键帧以外的，比如B，P帧
            // IJK_AVDISCARD_ALL     = 48, ///< 抛弃所有的帧
            // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            mIjkPlayer.setOption(codec, "skip_loop_filter", (mUseMediaCodec ? 0 : 48));
//            mIjkPlayer.setOption(codec, "skip_frame", 100); // // 跳过帧
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => initOptionsIjk => OPT_CATEGORY_CODEC => " + e.getMessage());
        }
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.setDisplay(surfaceHolder);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setDisplay => " + e.getMessage());
        }
    }

    @Override
    public void setSurface(Surface surface, int w, int h) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.setSurface(surface);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setSurface => " + e.getMessage());
        }
    }

    /**
     * ijk视频播放器监听listener
     */
    private void initListener() {
        mIjkPlayer.setOnErrorListener(onErrorListener);
        mIjkPlayer.setOnCompletionListener(onCompletionListener);
        mIjkPlayer.setOnInfoListener(onInfoListener);
        mIjkPlayer.setOnPreparedListener(onPreparedListener);
        mIjkPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mIjkPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        mIjkPlayer.setOnTimedTextListener(onTimedTextListener);
        // 设置视频缓冲更新监听事件
        mIjkPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mIjkPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
            @Override
            public boolean onNativeInvoke(int i, Bundle bundle) {
                MPLogUtil.log("VideoIjkPlayer => onNativeInvoke => i => " + i + ", bundle = " + bundle);
                return true;
            }
        });
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
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.pause();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => pause => " + e.getMessage());
        }
    }

    @Override
    public void release() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
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
            mIjkPlayer.setSurface(null);
            mIjkPlayer.reset();
            mIjkPlayer.release();
            mIjkPlayer = null;
            mPrepared = false;
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => release => " + e.getMessage());
        }
    }

    @Override
    public void start() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.start();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => start => " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.stop();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => stop => " + e.getMessage());
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            return mIjkPlayer.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }

    @Override
    public void seekTo(long seek) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (seek < 0)
                throw new Exception("seek error: " + seek);
            long duration = getDuration();
            if (seek > duration) {
                MPLogUtil.log("VideoIjkPlayer => seekTo => seek = " + seek + ", duration = " + duration);
                pause();
                stop();
                release();
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_SEEK_TIME);
            } else {
                MPLogUtil.log("VideoIjkPlayer => seekTo => succ");
                mIjkPlayer.seekTo(seek);
            }
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => seekTo => " + e.getMessage());
        }
    }

    @Override
    public long getPosition() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            long currentPosition = mIjkPlayer.getCurrentPosition();
            if (currentPosition < 0)
                throw new Exception("currentPosition warning: " + currentPosition);
            return currentPosition;
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => getPosition => " + e.getMessage());
            return -1L;
        }
    }

    @Override
    public long getDuration() {
        try {
            if (!mPrepared)
                throw new Exception("mPrepared warning: false");
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            long duration = mIjkPlayer.getDuration();
            if (duration <= 0)
                throw new Exception("duration warning: " + duration);
            return duration;
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => getDuration => " + e.getMessage());
            return -1L;
        }
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        this.mPlayWhenReady = playWhenReady;
    }

    @Override
    public boolean isPlayWhenReady() {
        return mPlayWhenReady;
    }

    @Override
    public boolean setSpeed(@FloatRange(from = 1F, to = 4F) float speed) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.setSpeed(speed);
            return true;
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setSpeed => " + e.getMessage());
            return false;
        }
    }

    @Override
    @FloatRange(from = 1F, to = 4F)
    public float getSpeed() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            float speed = mIjkPlayer.getSpeed();
            if (speed < 1f)
                throw new Exception("speed error: " + speed);
            return speed;
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => getSpeed => " + e.getMessage());
            return 1F;
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
            MPLogUtil.log("VideoIjkPlayer => setVolume => " + e.getMessage());
        }
    }

    @Override
    public boolean isMute() {
        return mMute;
    }

    @Override
    public void setMute(boolean v) {
        mMute = v;
        setVolume(v ? 0f : 1f, v ? 0f : 1f);
    }

    @Override
    public long getSeek() {
        return mSeek;
    }

    @Override
    public void setSeek(long seek) {
        if (seek < 0)
            return;
        mSeek = seek;
    }

    @Override
    public long getMax() {
        return mMax;
    }

    @Override
    public void setMax(long max) {
        if (max < 0)
            return;
        mMax = max;
    }

    @Override
    public boolean isLive() {
        return mLive;
    }

    @Override
    public void setLive(boolean live) {
        this.mLive = live;
    }

    @Override
    public void setLooping(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public boolean isLooping() {
        return mLoop;
    }

    @Override
    public boolean isPrepared() {
        return mPrepared;
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
            MPLogUtil.log("VideoIjkPlayer => getTrackInfo => " + e.getMessage());
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
            MPLogUtil.log("VideoIjkPlayer => switchTrack => " + e.getMessage());
            return false;
        }
    }

    /**
     * 设置视频信息监听器
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            MPLogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", extra = " + extra);

            switch (what) {
                // 拉流开始
                case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:
                    onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_START);
                    break;
                // 拉流成功
                case IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN:
//                    onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
                    break;
                // 缓冲开始
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    if (mPrepared) {
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_START);
                    } else {
                        MPLogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
                // 缓冲结束
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (mPrepared) {
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_STOP);
                    } else {
                        MPLogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", mPrepared = false");
                    }
                    break;
                // 出画面 => 起播
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    try {
                        if (mPrepared)
                            throw new Exception("warning: mPrepared true");
                        mPrepared = true;
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
                        long seek = getSeek();
                        if (seek <= 0) {
                            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_START);
                        } else {
                            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_RENDERING_START);
                            // 起播快进
                            seekTo(seek);
                        }
                    } catch (Exception e) {
                        MPLogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", msg = " + e.getMessage());
                    }
                    break;
//                // 出画面 => 快进起播
//                case IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START:
//                    try {
//                        if (!mPrepared)
//                            throw new Exception("error: mPrepared false");
//                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_START_SEEK);
//                    } catch (Exception e) {
//                        MPLogUtil.log("VideoIjkPlayer => onInfo => " + e.getMessage());
//                    }
//                    break;
                // 通知
                default:
//                    onEvent(PlayerType.KernelType.IJK, what);
                    break;
            }
            return true;
        }
    };

    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("VideoIjkPlayer => onSeekComplete =>");
            try {
                long seek = getSeek();
                if (seek <= 0)
                    throw new Exception();
                setSeek(0);
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_START);
            } catch (Exception e) {
            }
        }
    };

    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("VideoIjkPlayer => onPrepared =>");
            start();
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sar_num, int sar_den) {
            try {
                if (null == iMediaPlayer)
                    throw new Exception("iMediaPlayer error: null");
                int videoWidth = iMediaPlayer.getVideoWidth();
                int videoHeight = iMediaPlayer.getVideoHeight();
                MPLogUtil.log("VideoIjkPlayer => onVideoSizeChanged => videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);
                if (videoWidth <= 0 && videoHeight <= 0)
                    throw new Exception("videoWidth error: " + videoWidth + ", videoHeight error: " + videoHeight);
                onUpdateSizeChanged(PlayerType.KernelType.IJK, videoWidth, videoHeight, PlayerType.RotationType.Rotation_0);
            } catch (Exception e) {
                MPLogUtil.log("VideoIjkPlayer => onVideoSizeChanged => " + e.getMessage());
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
    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
            MPLogUtil.log("VideoIjkPlayer => onError => framework_err = " + framework_err + ", impl_err = " + impl_err);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_STOP);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_PARSE);
            return true;
        }
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("VideoIjkPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        }
    };

    /**
     * 设置时间文本监听器
     */
    private IMediaPlayer.OnTimedTextListener onTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
            MPLogUtil.log("VideoIjkPlayer => onTimedText => text = " + ijkTimedText.getText());
        }
    };
}
