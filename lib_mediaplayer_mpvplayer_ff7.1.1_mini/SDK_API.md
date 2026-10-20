# HLS 播放器 SDK API

对应需求：[Android M3U8（HLS）多轨道播放器需求文档](<../repo/Android M3U8（HLS）多轨道播放器需求文档.md>)。

统一入口为 `lib.kalu.mpvplayer.MpvPlayer`，底层仍使用本模块的 mpv / FFmpeg。
原有播放方法和 `OnPreparedListener` 等监听器保留，与新 API 共用同一套状态、底层实例和主线程回调；不再提供单独的 HlsPlayer 类。
最低 Android API 23，无新增 Maven 运行时依赖。

## 接入示例

```java
PlayerConfig config = new PlayerConfig.Builder()
        .autoPlay(true)
        .networkTimeoutMs(30_000)
        .buffer(30_000, 2_000, 64L * 1024 * 1024)
        .retry(3, 1_000)
        .stallTimeoutMs(15_000) // 0 表示不启用持续缓冲超时检测
        .build();

MpvPlayer player = new MpvPlayer(config);
player.addPlayerListener(new PlayerListener() {
    @Override public void onPrepared(MpvPlayer p) {
        // 只有底层识别出多个 HLS 视频 Variant，才能启用持续自动码率策略。
        if (p.supports(MpvPlayer.Capability.ADAPTIVE_HLS_VARIANTS)) {
            OperationResult result = p.setVideoTrackMode(MpvPlayer.VideoTrackMode.AUTO);
        }
    }
    @Override public void onError(MpvPlayer p, PlayerError error) {
        // error.code 保留底层错误码；error.getCause() 保留可获取的 Java 原始异常。
        // retryable / recoverable 不等同于一定恢复成功，观察 onRecoveryResult。
    }
});
player.addTrackListener(new TrackListener() {
    @Override public void onTrackSelectionResult(MpvPlayer p, TrackInfo.Type type,
                                                 String requestedId, OperationResult result) {
        // ACCEPTED 只代表提交请求；APPLIED 代表已完成对应确认，FAILED 代表失败或超时。
    }
});
player.addAnalyticsListener((p, stats) -> {
    // 时间单位为毫秒，码率/估算带宽为 bit/s；未知数值为 -1。
});

Map<String, String> headers = new HashMap<>();
headers.put("User-Agent", "MyAndroidPlayer/1.0");
headers.put("Referer", "https://example.com/");
// Cookie 同样通过 headers.put("Cookie", ...) 设置。
player.setMediaSource(new MediaSource("https://example.com/master.m3u8",
        headers, MediaSource.StreamType.LIVE));
player.prepareAsync(); // 异步准备，完成后回调 onPrepared。

// SurfaceHolder.Callback.surfaceCreated，或 TextureView 对应的 Surface 可用时：
player.setSurface(surface);
player.setSurfaceSize(width, height);

// Surface 销毁前：
player.clearSurface();
// Activity.onDestroy / Fragment.onDestroyView，按实际持有范围释放：
player.release();
```

代码片段省略 import。`MediaSource` 接受网络 URL 或普通文件路径；现有 `content://`、文件描述符片段接口仍由底层 `MpvPlayer` 提供。
Surface 归调用方所有：SDK 不调用调用方 Surface 的 `release()`。TextureView 包装出来的 Surface 应由调用方在分离后释放。
前后台策略由宿主决定：只暂停画面可调用 `pauseVideoRendering()`，恢复调用 `resumeVideoRendering()`；
需要暂停声音时使用 `pause()` / `resume()`。无需持有 Activity 或 Fragment 引用。

## 播放和状态

| API | 说明 |
| --- | --- |
| `setMediaSource(String / MediaSource)` | 替换媒体源，取消旧加载的重试、轮询和未交付事件；Header 复制为只读数据，拒绝 CR/LF 注入。 |
| `prepareAsync()` / `prepare()` | 前者异步准备；后者保留同步语义并可能抛出 IOException，避免在 UI 线程调用。两者均交付一次 `onPrepared`。 |
| `play()` / `resume()` | 未准备时启动准备；准备中记录播放意图；已准备时开始/恢复。 |
| `pause()` / `stop()` | 暂停保留当前媒体；停止回到 IDLE，保留源供再次播放，取消重试。 |
| `replay()` / `reload()` | 重播从头开始；重载会尝试恢复可 Seek 的非直播媒体位置。两者建立新的轨道 ID 会话。 |
| `setAutoPlay()` / `setLooping()` | 自动播放偏好和底层循环播放选项。 |
| `trySeekTo()` / `fastForward()` / `rewind()` | 返回明确操作结果；成功提交 Seek 后由 `onSeekComplete` 通知。只有部分缓存可 Seek 时检查目标是否落在缓存区间内。 |
| `getCurrentPositionLong()` / `getDurationLong()` | long 毫秒，未知为 `-1`；结束后保留已知时长及最后状态信息。原有不带 Long 后缀的方法返回 int，未知为 0，超出 int 范围时饱和。 |
| `getBufferedPosition()` / `getBufferedDuration()` / `getBufferedPercentage()` | 分别为缓存末端媒体时间、当前缓存时长和相对于总时长的百分比；不可获取为 `-1`。 |
| `getPlaybackState()` / `isPlaying()` / `isBuffering()` | IDLE、PREPARING、READY、PLAYING、PAUSED、BUFFERING、ENDED、ERROR、RELEASED；播放意图有效且处于 BUFFERING 时 `isPlaying()` 为 true。 |
| `setPlaybackSpeed()` / `getPlaybackSpeed()` | 范围 0.5～2.0，包含需求中的各档位；已声明为直播且无可 Seek 缓冲时，非 1.0 倍速返回 UNSUPPORTED。 |
| `setVolume()` / `getVolume()` / `setMuted()` / `isMuted()` | 音量范围 0～1；静音独立于音量，取消静音不会覆盖之前的音量设置。 |
| `release()` / `close()` | 幂等释放，取消后台任务并释放底层实例；可通过 `onReleased` 接收最终事件。 |

兼容方法 `setDataSource(String/Context+Uri/FileDescriptor)`、`start()`、`reset()`、`seekTo(int/long)`、`setDisplay()` 和 `setOn*Listener()` 均由同一个 `MpvPlayer` 提供。`seekTo` 保留 void 签名，失败抛出异常；需要结果对象时使用 `trySeekTo`。文件描述符由 SDK 复制持有，调用方可立即关闭自己的描述符；重播和重载仍可使用，切源、reset 或 release 后释放。`setSpeed` 保留正数倍速兼容入口，新接口 `setPlaybackSpeed` 按需求限制在 0.5～2.0。

`MediaSource.StreamType` 为 UNKNOWN、VOD、LIVE。SDK 不会仅凭 URL 后缀、缺失时长或不能 Seek 就判定直播。
`isLive()` 只有在源明确标记 LIVE 时才为 true；需要区分未知状态时使用 `getStreamType()`。

所有 SDK 监听器在主线程交付，且不在 SDK 锁内调用用户代码。`add/remove` 支持多个监听器。
切源、停止及释放会使旧会话的排队事件失效；已经执行中的用户回调不会被强行中断。

## 三类轨道

视频、音频、字幕分别提供 `get*Tracks()`、`getSelected*Track()`、`select*Track(id)`。
音频和字幕另有 `disableAudioTrack()` / `restoreAudioTrack()`、`disableSubtitleTrack()` / `restoreSubtitleTrack()`，
以及 `setPreferredAudioLanguage()` / `setPreferredSubtitleLanguage()`。语言参数可为 `zh,en` 等逗号分隔列表。
设置语言偏好后，已启用且准备完成的对应轨道会重新应用自动选择策略；不会悄悄恢复用户已禁用的音频或字幕。

`TrackInfo` 包含：

- 轨道 ID、类型、名称、语言、编码、MIME、选中/默认/强制/外部轨道标记。
- 分辨率、容器标记帧率、旋转、像素宽高比、音频声道数和采样率。
- `bitrate`（轨道自身的容器码率提示）、`hlsBitrate`（Variant 声明码率）、`programId`。
- `kind` 区分带 HLS 码率信息的视频 Variant 和普通解复用轨道，不将数组下标当作轨道 ID。
- `support` 为 SUPPORTED / UNSUPPORTED / UNKNOWN：实际已打开解码器的轨道可报告支持；
  底层解码器清单明确缺少的音视频编码报告不支持；仅有元数据而未确认的保持未知。

轨道 ID 包含加载会话和类型。换源、重播、重载后应重新取列表，禁止缓存旧 ID 跨会话使用。
源未提供的数值返回 `-1`，未提供的字符串为空，`getSelected*Track()` 无选中轨道时为 null。
轨道列表和选中状态按配置的 `progressIntervalMs` 刷新，默认 500ms，并在准备完成时立即刷新。

手动选择返回带 `requestId` 的 ACCEPTED 后，需要等待 `TrackListener.onTrackSelectionResult`：
观察到指定轨道的选中状态才返回 APPLIED，超时返回 FAILED，新请求替代旧请求会返回 CANCELLED。
禁用轨道以“当前无选中轨道”作为确认条件。恢复自动选择的结果表示策略已应用，字幕自动策略允许最终不选择任何字幕。
这里的选中确认不等同于无缝切换或首帧已经显示，后续解码失败仍通过错误事件报告。

## 视频码率策略

| 模式 | 实现行为 |
| --- | --- |
| AUTO | SDK 按 `demuxer-cache-state/raw-input-rate` 的估算输入速率和当前缓冲选择已识别的 HLS Variant。使用估算带宽的 75% 作为预算；不足 1.5 秒缓存或缓冲中优先降档，至少 5 秒缓存才允许升档；两次策略切换至少间隔 10 秒。缺少带宽数据不做基于带宽的升档。 |
| FIXED | 手动选轨后进入此模式，不执行 SDK 自动升降档。重载时依据码率、program、编码、尺寸及名称/语言重新匹配，匹配缺失或有歧义则报错，不复用旧会话数字 ID 猜测新轨道。 |
| AUTO_THEN_FIXED | 默认模式。开播使用 mpv 的自动选轨，HLS 初始码率偏好设为 `min`，在首次观察到实际选中且有解码器的轨道后明确固定。不在后续网络波动时改轨；新媒体源重新选择。 |

AUTO 在准备完成且存在至少两个已识别、未明确不支持的 HLS 视频 Variant 时才能启用，否则返回 UNSUPPORTED。
它是基于底层估算值的保守策略，不承诺无缝切换或精确带宽测量；支持状态未知的轨道仍可能在实际解码时失败。
AUTO_THEN_FIXED 的初选是最低声明码率偏好，不包含开播前主动网络测速。
视频、音频和字幕选择分别管理；SDK 当前不解析 Master Playlist 中完整的 AUDIO/SUBTITLES Rendition Group 关联关系。

`setVideoTrackMode(FIXED)` 固定当前视频；`setVideoTrackMode(AUTO)` 解除固定并启用持续策略。
`isVideoTrackLocked()` 仅在观察到固定结果后为 true。

## 画面、字幕和解码能力

- `setSurface` / `setSurfaceSize`：接入 SurfaceView 或 TextureView 对应的 Surface，可动态替换。
- `setResizeMode`：FIT 等比例完整显示；FILL 拉伸铺满；ZOOM 等比例放大裁剪；ORIGINAL 使用不缩放的原始大小。
- `getVideoWidth/Height/Rotation/PixelAspectRatio`：读取画面/轨道信息。
- `setSubtitleStyle(SubtitleStyle)`：字体大小、ARGB 文字/背景/描边颜色、位置百分比、透明度和横纵边距。
  字号及边距使用 mpv 按 720 高度缩放的字幕坐标单位，不是 Android sp/dp；需要覆盖 ASS 自带样式时显式设置 `overrideAss=true`。
  边距不等于任意矩形裁剪区域；位图字幕不保证应用文字字体样式。
- `setHardwareDecoding` / `getActiveHardwareDecoder`：配置 MediaCodec copy 模式或软件解码，查询实际硬解路径。
- `getAvailableDecoderCodecs()`：查询底层编译出的音视频解码器编码列表。
- `DecoderInfo.findVideoDecoders(...)` / `findAudioDecoders(...)`：按格式查询 Android MediaCodec 支持情况。
  Android 6～9 无权威硬件/软件标志，`hardwareAccelerated` 返回 null；平台查询也不能代替实际解码验证。

## 缓冲、恢复和统计

`PlayerConfig` 是不可变配置，通过构造播放器传入。Header、User-Agent、Referer、Cookie 使用 `MediaSource.headers`。
网络超时为底层统一的 `network-timeout`；缓冲最大时长、最大字节数和起播/恢复共享阈值映射到 mpv 缓存选项。
最大缓存时长是预读目标，实际缓存仍受字节上限、源可用数据、流结束等因素约束。

重试默认关闭。启用后，对底层可重试的加载失败及配置的持续缓冲超时，按固定间隔重载；
同一源的重试总次数有上限，准备成功不会重置该上限。用户显式切源、重播、重载可开始新一轮预算。
点播恢复仅在已知可 Seek 时尝试恢复位置；直播重载重新交给底层打开播放列表。
`onRecoveryResult(true)` 表示重载后完成准备并应用恢复流程，不是网络或分片完全无误的保证。
这是源级重载恢复，不提供每个分片请求的独立重试调度，也不代替系统 ConnectivityManager。

`getPlaybackStats()` 和 `AnalyticsListener` 提供进度、缓冲、音视频码率、分辨率、帧率、编码、
估算输入带宽、首帧耗时、累计缓冲次数/时长、输出丢帧及观察到的轨道切换次数。
统计以一次加载会话为单位，重播/重载会重置；尚未准备时快照可能为 null。
HTTP 状态码、分片加载耗时和分片失败计数底层未暴露，明确返回 `-1`。

## 能力边界和需求对应

`supports(Capability)` 用于能力查询；不支持的显式操作返回 `OperationResult.Status.UNSUPPORTED`。
下表区分已实现的 SDK 接口、依赖底层/媒体的能力和当前仍缺少的功能，不能把本次接口实现视为需求文档全部验收通过。

| 需求范围 | 当前状态 |
| --- | --- |
| 基础控制、Header、统一超时、进度、音量/静音、倍速、状态和多监听器 | 已实现接口和状态逻辑；有本地状态测试。 |
| 多视频/音频/字幕轨道查询、选择、变化和结果事件 | 已接通真实 libmpv `track-list` / `vid` / `aid` / `sid`，受底层实际识别出的轨道限制。 |
| AUTO / FIXED / AUTO_THEN_FIXED | 已实现上述策略；尚需真实多码率 HLS、不同带宽及设备场景验证。 |
| HLS Master/Media、VOD/Live/Event、TS/fMP4、AES-128、Discontinuity、Byte Range、EXT-X-MAP、重定向 | 由已构建的 FFmpeg/libmpv 协议栈处理，本次未新增独立协议实现，也未完成逐项媒体验收；AES-128 不代表支持 DRM。 |
| H.264/H.265/AAC/MP3/AC-3/E-AC-3、WebVTT、CEA 字幕 | 受当前解码器、媒体及设备能力限制；提供查询及错误通道，未声明所有编码/profile、CEA-708 等组合均已通过播放测试。 |
| 完整 Variant 与独立 Rendition Group 元数据 | 只提供解复用轨道及 HLS bitrate/program 提示，完整 Master 关联信息当前不支持。 |
| 直播 Playlist 窗口、源端最新位置、真实直播延迟和目标延迟控制 | 当前不支持。`getLiveWindow().available=false`；`seekToLiveEdge`、`setLiveTargetDelay` 返回 UNSUPPORTED。 |
| 本地缓存可 Seek 区间 | `getCachedSeekRanges()`；与服务器直播窗口明确区分。 |
| 网络断开/恢复系统通知、独立连接/读取超时、HTTP 状态码 | 当前未接入，能力查询返回 false。 |
| 独立最小/最大/起播/恢复四阈值 | 当前仅有最大预读和起播/恢复共享阈值；不能独立配置完整四阈值策略。 |
| 分片级请求监控和重试 | 当前不提供；有源级重载重试。 |
| 细分网络、M3U8、分片、解码错误类型 | 错误类型模型已定义，实际映射仅使用可确定的 libmpv 错误码。不能从聚合加载错误推断具体 HTTP 状态或具体分片原因。 |
| 任意字幕显示矩形、清除 Surface 中已显示像素 | 当前不支持。`clearSurface()` 只分离渲染目标，不保证清掉 View 留存的最后一帧；`clearVideoFrame()` 返回 UNSUPPORTED。 |
| 释放、切源、重复实例、Surface 重建 | 已实现资源/事件管理；真机解码器、线程及 Surface 资源验收仍需设备测试。 |

## 验证方式

`tools/verify_local.py` 现在额外执行 `tests/java/.../SdkStateTest.java` 的确定性主机测试，
74 项断言覆盖会话隔离、轨道 ID、切换确认/取消/超时、三种码率模式、有限重试、停止取消重试、释放及未知数据语义，
以及旧方法签名对应的同步准备、监听器转发、重复回调抑制、reset 隔离和 int/long 时间兼容。
这些测试使用测试后端验证 SDK 策略，不是解码或真实网络播放证据。

`src/androidTest/.../MpvPlayerTest.java` 增加真实 JNI 结构化属性读取（含 Unicode）和统一 SDK 音频播放用例。
Java 和四种 ABI 的 JNI 编译、本地测试、AAR 链接/打包检查通过，也不能代替 Android 上的 HLS 实播验收。
