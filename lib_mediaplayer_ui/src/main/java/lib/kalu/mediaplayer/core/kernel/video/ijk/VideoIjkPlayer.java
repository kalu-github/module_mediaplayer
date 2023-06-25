package lib.kalu.mediaplayer.core.kernel.video.ijk;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.kernel.video.KernelApiEvent;
import lib.kalu.mediaplayer.core.kernel.video.base.BasePlayer;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

@Keep
public final class VideoIjkPlayer extends BasePlayer {

    private long mSeek = 0L; // 快进
    private long mMax = 0L; // 试播时常
    private boolean mLoop = false; // 循环播放
    private boolean mLive = false;
    private boolean mMute = false;
    private boolean mPlayWhenReady = true;
    private boolean isFromUserSeekComplete = false;
    private boolean isFromNetBufferStart = false;


    private boolean mUseMediaCodec;
    private tv.danmaku.ijk.media.player.IjkMediaPlayer mIjkPlayer = null;

    public VideoIjkPlayer(@NonNull boolean useMediaCodec, @NonNull PlayerApi musicApi, @NonNull KernelApiEvent eventApi) {
        super(musicApi, eventApi);
        mUseMediaCodec = useMediaCodec;
    }

    @NonNull
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
            stopExternalMusic(true);

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
            mIjkPlayer.pause();
            mIjkPlayer.stop();
            mIjkPlayer.reset();
            mIjkPlayer.release();
            mIjkPlayer = null;
            MPLogUtil.log("VideoIjkPlayer => releaseDecoder => succ");
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => releaseDecoder => " + e.getMessage());
        }
    }

    @Override
    public void createDecoder(@NonNull Context context, @NonNull boolean logger, @NonNull int seekParameters) {
        try {
            releaseDecoder(false);
            mIjkPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
            mIjkPlayer.setLooping(false);
            //处理UA问题
            //            if (headers != null) {
            //                String userAgent = headers.get("User-Agent");
            //                if (!TextUtils.isEmpty(userAgent)) {
            //                    mIjkPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent);
            //                }
            //            }
            initListener();
            setVolume(1F, 1F);
            setOptions();
            tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogger(logger);
            tv.danmaku.ijk.media.player.IjkMediaPlayer.native_setLogLevel(tv.danmaku.ijk.media.player.IjkMediaPlayer.IJK_LOG_INFO);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => createDecoder => " + e.getMessage());
        }
    }

    @Override
    public void startDecoder(@NonNull Context context, @NonNull String url) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (url == null || url.length() == 0)
                throw new Exception("url error: " + url);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_START);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_STOP);
            mIjkPlayer.setDataSource(context, Uri.parse(url), null);
            mIjkPlayer.prepareAsync();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => startDecoder => " + e.getMessage());
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_URL);
        }
    }

    @Override
    public void setOptions() {

        // player
        try {
            int player = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER;
            // 循环次数1次， 不能是0
            mIjkPlayer.setOption(player, "loop", 1);
            // 硬解码相关 0关闭
            mIjkPlayer.setOption(player, "mediacodec-auto-rotate", 0);
            mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", 0);
            mIjkPlayer.setOption(player, "mediacodec-all-videos", mUseMediaCodec ? 1 : 0);
            mIjkPlayer.setOption(player, "mediacodec-avc", mUseMediaCodec ? 1 : 0);
            mIjkPlayer.setOption(player, "mediacodec-hevc", mUseMediaCodec ? 1 : 0);
            mIjkPlayer.setOption(player, "mediacodec-mpeg2", mUseMediaCodec ? 1 : 0);
            mIjkPlayer.setOption(player, "mediacodec-mpeg4", mUseMediaCodec ? 1 : 0);
            mIjkPlayer.setOption(player, "mediacodec-vp8", mUseMediaCodec ? 1 : 0);
            mIjkPlayer.setOption(player, "mediacodec-vp9", mUseMediaCodec ? 1 : 0);
            // 使用opensles 进行音频的解码播放 1、允许 0、不允许[1音频有稍许延迟]
            mIjkPlayer.setOption(player, "opensles", 0);
            mIjkPlayer.setOption(player, "overlay-format", tv.danmaku.ijk.media.player.IjkMediaPlayer.SDL_FCC_RV32);
            mIjkPlayer.setOption(player, "framedrop", 2);
            if (mPlayWhenReady) {
                mIjkPlayer.setOption(player, "start-on-prepared", 1);
            } else {
                mIjkPlayer.setOption(player, "start-on-prepared", 0);
            }
            // 某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，通俗一点就是FFMPEG不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
            mIjkPlayer.setOption(player, "enable-accurate-seek", 1);
            // soundtouch倍速 1：开启 O:关闭
            mIjkPlayer.setOption(player, "soundtouch", 0);
            // 播放错误, 重试次数
            mIjkPlayer.setOption(player, "reconnect", 4);
            // 字幕; 1显示。0禁止
            mIjkPlayer.setOption(player, "subtitle", 0);
            // 视频, 1黑屏 0原画面
            mIjkPlayer.setOption(player, "vn", 0);
            // 音频, 1静音 0原音
            mIjkPlayer.setOption(player, "an", 0);
            // 减小预读取的阀值，这样能更快的打开视频
            // mIjkPlayer.setOption(player, "max-buffer-size", 20 * 1024 * 1024);// 20M
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setOptions => OPT_CATEGORY_PLAYER => " + e.getMessage());
        }

        // format
        try {
            int format = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT;
            mIjkPlayer.setOption(format, "http-detect-range-support", 0);
            // 清空DNS,有时因为在APP里面要播放多种类型的视频(如:MP4,直播,直播平台保存的视频,和其他http视频), 有时会造成因为DNS的问题而报10000问题的
            mIjkPlayer.setOption(format, "dns_cache_clear", 1);
            // 若是是rtsp协议，能够优先用tcp(默认是用udp)
            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
            // 每处理一个packet以后刷新io上下文
            mIjkPlayer.setOption(format, "flush_packets", 1);
            // 超时时间,单位ms => 20s
            mIjkPlayer.setOption(format, "timeout", 10 * 1000 * 1000);
            // 设置seekTo能够快速seek到指定位置并播放, 解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
            mIjkPlayer.setOption(format, "fflags", "fastseek");
            // 根据媒体类型来配置 => bug => resp aac音频无声音
            mIjkPlayer.setOption(format, "allowed_media_types", "video");
            // rtsp设置 https://ffmpeg.org/ffmpeg-protocols.html#rtsp
            mIjkPlayer.setOption(format, "rtsp_flags", "prefer_tcp");
            mIjkPlayer.setOption(format, "rtsp_transport", "tcp");
            // 探测带第一帧后就会数据返回，如果这个值设置过小，会导致流的信息分析不完整，从而导致丢失流，用于秒开
            // mIjkPlayer.setOption(format, "probesize", 1 * 1024 * 1024); // 1M
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setOptions => OPT_CATEGORY_FORMAT => " + e.getMessage());
        }
        try {
            int codec = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_CODEC;
            mIjkPlayer.setOption(codec, "skip_loop_filter", 48);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setOptions => OPT_CATEGORY_CODEC => " + e.getMessage());
        }

//        // player
//        try {
//            int player = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER;
//            // 硬解 1：开启 O:关闭
//            // 丢帧是在视频帧处理不过来的时候丢弃一些帧达到同步的效果
//            mIjkPlayer.setOption(player, "framedrop", 4); // 4
//            // sdl渲染
//            mIjkPlayer.setOption(player, "overlay-format", tv.danmaku.ijk.media.player.IjkMediaPlayer.SDL_FCC_RV32);
//            // 直播场景时实时推流，可以开启无限制buffer，这样可以尽可能快的读取数据，避免出现网络拥塞恢复后延迟累积的情况。
//            // 是否无限读(如果设置了该属性infbuf为1，则设置max-buffer-size无效)
//            mIjkPlayer.setOption(player, "infbuf", 0);
//            // 视频帧处理不过来的时候丢弃一些帧达到同步的效果
//            mIjkPlayer.setOption(player, "framedrop", 5);
//            // 默认最小帧数
//            mIjkPlayer.setOption(player, "min-frames", 2);
//            // 最大缓存时长
//            mIjkPlayer.setOption(player, "max_cached_duration", 3);
//            // 自动旋屏 1显示。0禁止
//            mIjkPlayer.setOption(player, "mediacodec-auto-rotate", 0);
//            // 处理分辨率变化 1显示。0禁止
//            mIjkPlayer.setOption(player, "mediacodec-handle-resolution-change", 0);
//            // 不额外优化（使能非规范兼容优化，默认值0 ）
//            mIjkPlayer.setOption(player, "fast", 1);
//            // 是否开启预缓冲，通常直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
//            mIjkPlayer.setOption(player, "packet-buffering", 0);
//            // 须要准备好后自动播放
//            mIjkPlayer.setOption(player, "start-on-prepared", 1);
//        } catch (Exception e) {
//        }
//
//        // format
//        try {
        // 设置播放前的探测时间 1,达到首屏秒开效果
//        mIjkPlayer.setOption(format, "analyzeduration", 100);
        // 设置播放前的最大探测时间 （100未测试是否是最佳值）
//        mIjkPlayer.setOption(format, "analyzemaxduration", 100);
//            int format = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT;
//            // 不清楚 1、允许 0、不允许
//            mIjkPlayer.setOption(format, "http-detect-range-support", 0);
//            // 最大帧率 20
//            mIjkPlayer.setOption(format, "max-fps", 0);
//            // 缩短播放的rtmp视频延迟在1s内
//            mIjkPlayer.setOption(format, "fflags", "nobuffer");
//        } catch (Exception e) {
//        }
//
//        // codec
//        try {
//            // IJK_AVDISCARD_NONE    =-16, ///< discard nothing
//            // IJK_AVDISCARD_DEFAULT =  0, ///< 如果包大小为0，责抛弃无效的包
//            // IJK_AVDISCARD_NONREF  =  8, ///< 抛弃非参考帧（I帧）
//            // IJK_AVDISCARD_BIDIR   = 16, ///< 抛弃B帧
//            // IJK_AVDISCARD_NONKEY  = 32, ///< 抛弃除关键帧以外的，比如B，P帧
//            // IJK_AVDISCARD_ALL     = 48, ///< 抛弃所有的帧
//            int codec = tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_CODEC;
//            // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
//            mIjkPlayer.setOption(codec, "skip_loop_filter", 48L);
//            // 跳过帧
//            mIjkPlayer.setOption(codec, "skip_frame", 0);
//        } catch (Exception e) {
//        }
//
////            // 3、缓冲相关
////            // 解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
////            mIjkPlayer.setOption(format, "fflags", "fastseek");
////    //        mIjkPlayer.setOption(format, "fflags", "nobuffer");
////
////            // 2、 网络相关
////            // 23、设置播放前的最大探测时间
////            mIjkPlayer.setOption(format, "rtbufsize", 60);
    }

    /**
     * ijk视频播放器监听listener
     */
    private void initListener() {
        // 设置监听，可以查看ijk中的IMediaPlayer源码监听事件
        // 设置视频错误监听器
        mIjkPlayer.setOnErrorListener(onErrorListener);
        // 设置视频播放完成监听事件
        mIjkPlayer.setOnCompletionListener(onCompletionListener);
        // 设置视频信息监听器
        mIjkPlayer.setOnInfoListener(onInfoListener);
        // 设置准备视频播放监听事件
        mIjkPlayer.setOnPreparedListener(onPreparedListener);
        // 设置视频大小更改监听器
        mIjkPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        // 设置视频seek完成监听事件
        mIjkPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        // 设置时间文本监听器
        mIjkPlayer.setOnTimedTextListener(onTimedTextListener);
        // 设置视频缓冲更新监听事件
        mIjkPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mIjkPlayer.setOnNativeInvokeListener(new tv.danmaku.ijk.media.player.IjkMediaPlayer.OnNativeInvokeListener() {
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
    //            MPLogUtil.log(e.getMessage(), e);
    //        }
    //    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.pause();
            MPLogUtil.log("VideoIjkPlayer => pause => succ");
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => pause => " + e.getMessage());
        }
    }

    @Override
    public void start() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.start();
            MPLogUtil.log("VideoIjkPlayer => start => succ");
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
            MPLogUtil.log("VideoIjkPlayer => stop => succ");
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => stop => " + e.getMessage());
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            return mIjkPlayer.isPlaying();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => isPlaying => " + e.getMessage());
            return false;
        }
    }


    @Override
    public void seekTo(long seek, @NonNull boolean isPrepared) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (!isPrepared) {
                isFromUserSeekComplete = true;
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_START);
            }
            mIjkPlayer.seekTo(seek);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => seekTo => " + e.getMessage());
        }
    }

    @Override
    public long getPosition() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            return mIjkPlayer.getCurrentPosition();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => getPosition => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public long getDuration() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            return (int) mIjkPlayer.getDuration();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => getDuration => " + e.getMessage());
            return 0L;
        }
    }

    @Override
    public void setSurface(@NonNull Surface surface, int w, int h) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            if (null == surface)
                throw new Exception("surface error: null");
            mIjkPlayer.setSurface(surface);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setSurface => " + e.getMessage());
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
    public void setSpeed(float speed) {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            mIjkPlayer.setSpeed(speed);
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => setSpeed => " + e.getMessage());
        }
    }

    @Override
    public float getSpeed() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            return mIjkPlayer.getSpeed(0);
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
    public void setLive(@NonNull boolean live) {
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

    public IjkTrackInfo[] getTrackInfo() {
        try {
            if (null == mIjkPlayer)
                throw new Exception("mIjkPlayer error: null");
            return mIjkPlayer.getTrackInfo();
        } catch (Exception e) {
            MPLogUtil.log("VideoIjkPlayer => getTrackInfo => " + e.getMessage());
            return null;
        }
    }

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
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_ERROR_PARSE);
            return true;
        }
    };

    /**
     * 设置视频播放完成监听事件
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("VideoIjkPlayer => onCompletion =>");
            onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_VIDEO_END);
        }
    };


    /**
     * 设置视频信息监听器
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            MPLogUtil.log("VideoIjkPlayer => onInfo => what = " + what + ", extra = " + extra);

            switch (what) {
                // 拉流
                case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:
                    break;
                // 首帧画面 => 开播
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
                    onEvent(PlayerType.KernelType.IJK, what);
                    break;
                // 缓冲开始
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    isFromNetBufferStart = true;
                    break;
                // 缓冲结束
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (!isFromUserSeekComplete && isFromNetBufferStart) {
                        isFromNetBufferStart = false;
                        onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_STOP);
                    }
                    break;
                // 首帧画面 => 快进
                case IMediaPlayer.MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE:
                    isFromUserSeekComplete = false;
                    onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_STOP);
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START:
                case IMediaPlayer.MEDIA_INFO_AUDIO_SEEK_RENDERING_START:
                    break;
                // 通知
                default:
                    onEvent(PlayerType.KernelType.IJK, what);
                    break;
            }
            return true;
        }
    };

    /**
     * 设置视频缓冲更新监听事件
     */
    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            MPLogUtil.log("VideoIjkPlayer => onBufferingUpdate => percent = " + percent + ", isFromNetBufferStart = " + isFromNetBufferStart);
            if (percent > 0 && isFromNetBufferStart) {
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_LOADING_STOP);
                onEvent(PlayerType.KernelType.IJK, PlayerType.EventType.EVENT_BUFFERING_START);
            }
        }
    };


    /**
     * 设置准备视频播放监听事件
     */
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            MPLogUtil.log("VideoIjkPlayer => onPrepared => seek = " + mSeek);
            long seek = getSeek();
            if (seek > 0) {
                seekTo(seek, true);
            }
        }
    };

    /**
     * 设置视频大小更改监听器
     */
    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sar_num, int sar_den) {
            MPLogUtil.log("VideoIjkPlayer => onVideoSizeChanged => width = " + width + ", height = " + height);
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                onChanged(PlayerType.KernelType.IJK, videoWidth, videoHeight, -1);
            }
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

    /**
     * 设置视频seek完成监听事件
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            isFromNetBufferStart = false;
            MPLogUtil.log("VideoIjkPlayer => onSeekComplete => ");
        }
    };
}
