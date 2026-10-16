# Android mpv 解码核心

基于模块内的 `src/main/cpp/mpv-0.41.0` 和 `src/main/cpp/mpv-android-2026-09-17`，入口为
`lib.kalu.mpvplayer.MpvPlayer`。每个播放器持有独立的 libmpv 实例和事件线程。
原生目标不编译、链接 `cpp/player`、`cpp/sdl2` 或旧 FFmpeg 3.4.13 静态库。

支持准备、播放、暂停、停止、重新播放、seek、循环、倍速、音量、时长/位置、
Surface/SurfaceHolder、网络请求头、文件路径、文件描述符和 `content://`。
回调包括准备完成、播放结束、错误、缓冲、seek 完成、视频尺寸和文本字幕。
回调在 Android 主线程执行。mpv 的字幕渲染由 libass 负责。

默认 `hwdec=mediacodec-copy`，硬件解码不可用时由 mpv 回退软件解码；
`setHardwareDecoding(false)` 可强制软件解码。视频使用 Android EGL/OpenGL ES，
音频使用 `audiotrack,opensles`。无 Surface 时使用 `vo=null`。

## 模块内源码

从项目 `repo` 完整复制到 `src/main/cpp/` 的实际构建输入：

- `ffmpeg-7.1.1/`
- `mpv-0.41.0/`
- `mpv-android-2026-09-17/`
- `libass-0.17.4/`
- `libplacebo-7.351.0-full/`（包含全部子模块源码，已剥离嵌套 Git 仓库信息）

`source-copy-manifest.json` 记录最初复制时逐文件内容及链接校验的汇总（当时包含 Git 元数据）。
嵌套 `.git` 已移除，源码内容保持不变；`vendor-origins.json` 保存各仓库的来源和提交版本。
未复制重复的压缩包及未使用的不完整 libplacebo 目录。
其它已下载的依赖源码仍位于 `build/native/deps`；现有离线构建需保留这些源码。
源码目录与 `ffmpeg/<ABI>/{include,lib}` 编译产物、`mpv/` JNI 桥接目录独立。

## 版本与构建

最低 Android API **23**。模块使用 **FFmpeg 7.1.1** 和 **mpv 0.41.0**。
合并构建完成后，每个已编译 ABI 的依赖统一放在：

```text
src/main/cpp/ffmpeg/<ABI>/
├── include/              # FFmpeg、mpv 和第三方依赖头文件
├── lib/                  # .a 静态库；不存放 .so
├── manifest.json         # 版本及文件校验信息
├── link.cmake            # 静态库和系统库链接列表
├── ffmpeg.exports        # FFmpeg 导出接口
└── ffmpeg-roots.rsp       # 保留公开接口的链接参数
```

安装时直接删除并替换该 ABI 的旧目录，不做备份。CMake 从上述静态库链接两个
动态库，不再从嵌套的 `merged` 目录导入预编译 `.so`。
四种 ABI（`arm64-v8a`、`armeabi-v7a`、`x86`、`x86_64`）均已完成替换，
每个 ABI 的 `lib/` 中有 22 个 `.a`。

默认 AAR 每个 ABI 只包含两个动态库：

- `libffmpeg.so`：合并 avcodec、avformat、avfilter、avutil、avdevice、postproc、
  swresample 和 swscale，保留原有公开接口。
- `libmpvplayer.so`：合并 mpv、JNI、字幕/渲染依赖和 C++ 运行时；不需要另带
  `libmpv.so` 或 `libc++_shared.so`。Java 仍调用 `System.loadLibrary("mpvplayer")`。

Gradle 成品名为 `lib_mediaplayer_mpv_0.41.0_merged_debug.aar` / `merged_release.aar`。

依赖构建沿用给定 mpv-android 的脚本，使用本地 mpv 0.41.0 源码。
FFmpeg 优先使用 `src/main/cpp/ffmpeg-7.1.1` 本地源码，可通过 `--ffmpeg-source` 指定其它路径；
会检查版本并记录源码 SHA-256。没有本地源码时才克隆固定的 `n7.1.1` 标签。
libass、libplacebo 优先使用 `src/main/cpp/libass-0.17.4`、
`src/main/cpp/libplacebo-7.351.0-full`，
也可通过 `--libass-source`、`--libplacebo-source` 指定路径；会校验版本并记录源码 SHA-256。
模块内已复制完整的 libplacebo 子模块。仅当模块内缺少对应源码时，脚本才回退到项目 `repo` 目录。
libplacebo 的 GitHub 自动生成 ZIP 不包含子模块：当前 Android 构建要求
`3rdparty/{glad,jinja,markupsafe,fast_float}` 中有实际源码。
可用 `git clone --depth 1 --branch v7.351.0 --recurse-submodules https://github.com/haasn/libplacebo.git libplacebo-7.351.0-full`
获取完整源码，再传入 `--libplacebo-source /完整路径/libplacebo-7.351.0-full`。
dav1d、libass、libplacebo 的在线下载使用脚本中的固定标签；其它依赖版本来自
参考仓库的 `buildscripts/include/depinfo.sh`。下载目录、源码构建目录均在
本模块 `build/native`。安装时生成 `manifest.json`，记录 NDK、源码版本及库的 SHA-256，
并用 NDK readelf 检查运行库依赖是否齐全。网络下载失败不会安装半成品。

需要 Python（支持 `tarfile.data_filter`，建议 3.12+）、JDK 17、Android SDK 32、
CMake 3.22.1、NDK 28.1.13356709，以及宿主工具：
`git curl make meson ninja pkg-config`。Meson 至少 1.11.0（fontconfig 源码要求）。
脚本自动使用 `build/host-tools/bin` 和 SDK CMake 3.22.1 自带的 Ninja。
x86 / x86_64 的汇编优化构建另需 NASM。缺少 NASM 时可向 `build_native.py` 传入
`--disable-x86-asm`，使用 FFmpeg、dav1d 和 libass 的 C 实现；软件解码性能较低。
合并脚本会继承该选择，并将其记录在构建清单中。libass 使用其 Meson 构建；脚本中的安装和文本处理
已改用 macOS 自带工具，不再要求 Autoconf、Automake 或 GNU coreutils/sed。

ARM64 的宿主工具可以在可联网终端安装到本模块内，无需 Homebrew 或 sudo：

```sh
bash tools/setup_host_tools.sh
```

固定安装 [Meson 1.11.0](https://pypi.org/project/meson/1.11.0/) 和
[pkgconf 3.0.7.post0](https://pypi.org/project/pkgconf/3.0.7.post0/)，完成后自动检查工具。

在本模块目录执行（首次运行需要联网下载源代码）：

```sh
python3 tools/build_native.py --check --ndk "$ANDROID_NDK_ROOT"
python3 tools/build_native.py --abi arm64-v8a --ndk "$ANDROID_NDK_ROOT"
# 四种 ABI：arm64-v8a / armeabi-v7a / x86 / x86_64
python3 tools/build_native.py --abi all --ndk "$ANDROID_NDK_ROOT"
```

基础依赖构建完成后，构建并安装合并版所需的静态库及头文件：

```sh
python3 tools/build_merged_native.py --abi arm64-v8a --ndk "$ANDROID_NDK_ROOT"
```

该步骤复用已验证的第三方静态库，在 `build/merged-native` 独立编译 FFmpeg 和 mpv。
其它 ABI 分别执行相同命令。合并过程检查 FFmpeg 导出符号没有缺失，并阻止遗漏依赖。

已有部分本地源码时，可先完成源码暂存，无需下载或安装宿主构建工具。
此模式允许暂存缺子模块的 libplacebo，并在 `build/native/remaining-dependencies.json`
记录缺失项；正式构建会拒绝不完整源码。源码或脚本变化时，需要清理旧的
`build/native` 后重新暂存，避免混用旧构建结果：

```sh
python3 tools/build_native.py --prepare-local-sources --offline --ndk "$ANDROID_NDK_ROOT"
```

在可联网的终端一次补齐剩余依赖源码（复用已暂存目录，不要求先安装 Meson 等编译工具）：

```sh
python3 tools/build_native.py --download-only --ndk "$ANDROID_NDK_ROOT"
```

macOS 也可以执行 `bash tools/download_native_sources.sh`，自动选择支持安全解压的
Python（包括已安装的 Codex Python 运行环境），并使用 SDK 下的 NDK 28.1.13356709。
可通过 `ANDROID_NDK_ROOT` 或 `--ndk` 覆盖 NDK 路径。

下载中断后可重跑同一命令；失败的下载不会被当成完整源码使用。
下载完成只代表源码准备完成，随后仍需安装上述宿主工具并执行正式构建。

也可以先用 NDK、make 离线编译 FFmpeg 基础库：

```sh
python3 tools/build_ffmpeg_core.py --abi arm64-v8a --ndk "$ANDROID_NDK_ROOT"
```

基础库放在 `build/ffmpeg-core/<ABI>/prefix`，包含 `avcodec/avformat/avfilter/avutil/
swresample/swscale` 六个动态库，启用内置解码器和 Android MediaCodec。
该步骤用于先验证 FFmpeg Android 编译；未启用 mbedTLS、dav1d、libxml2，因此没有
HTTPS、dav1d 软件 AV1 和 DASH 支持。它不会安装到正式 mpv 依赖目录，也不产生 AAR。
完整播放器仍使用 `build_native.py` 构建依赖。

已有完整下载源代码时可加 `--offline`。修改源代码、NDK 或构建配方后，脚本会拒绝
复用不兼容的旧对象。构建配方或模块路径变化时会直接清理目标 ABI 的缓存后重编译；
源码或 NDK 变化时需清理本模块 `build/native` 后重建。

随后在项目根目录执行：

```sh
# 只编译已准备好依赖的 ABI
./gradlew :lib_mediaplayer_mpvplayer_ff7.1.1_mini:assembleDebug -PmpvAbis=arm64-v8a
# 默认四种 ABI，必须先准备全部依赖
./gradlew :lib_mediaplayer_mpvplayer_ff7.1.1_mini:assembleRelease
```

当前环境的 Gradle daemon 无法绑定本地 socket。这个模块没有 Android 资源或外部
Java 依赖，也可使用实际 CMake/JNI 编译、javac 和标准 AAR 布局完成打包：

```sh
python3 tools/package_aar.py --abi arm64-v8a --layout merged \
  --sdk "$ANDROID_HOME" --ndk "$ANDROID_NDK_ROOT" --java-home "$JAVA_HOME"
```

此方式要求先成功执行原生库构建，输出
`build/outputs/aar/lib_mediaplayer_mpv_0.41.0_arm64-v8a_merged_release.aar`，
包含 `classes.jar`、Manifest、消费者混淆规则和全部运行库。
SHA-256 文件位于同目录；原生库验证记录位于 `build/aar-merged/arm64-v8a/verification.json`。
两个独立 `.so` 同时输出到 `build/outputs/merged-jni/arm64-v8a`。
需要重建拆分版时，须先重新运行 `build_native.py` 安装拆分版依赖，再使用
`--layout split`；Gradle 使用 `-PmpvLayout=split`。不要同时向应用添加两版 AAR。
此路径只适用于当前没有 Android 资源的模块；加入资源、AIDL 或 Kotlin 后应使用 Gradle。

四种 ABI 的依赖安装完成后，可分别打包并合并为一个 AAR：

```sh
for abi in arm64-v8a armeabi-v7a x86 x86_64; do
  python3 tools/package_aar.py --abi "$abi" --layout merged \
    --sdk "$ANDROID_HOME" --ndk "$ANDROID_NDK_ROOT" --java-home "$JAVA_HOME" || exit 1
done
python3 tools/combine_aars.py --ndk "$ANDROID_NDK_ROOT"
```

合包工具核对四个单 ABI AAR 的校验和、Java 字节码、Manifest、ELF 架构和运行库依赖。
输出为 `build/outputs/aar/lib_mediaplayer_mpv_0.41.0_all_merged_release.aar`；每个 ABI
包含 `libffmpeg.so` 和 `libmpvplayer.so`，共 8 个动态库。


使用其它 NDK 时，依赖构建和 Gradle 必须保持一致：
`-PmpvNdkVersion=<版本号>`。`local.properties` 配置实际 SDK 路径，勿保留旧 Windows
`ndk.dir`。原生库均使用 16 KB 最大页对齐参数；发布前仍应验证最终 APK 的对齐。
缺少新版依赖时 CMake 会明确报错，不会自动使用 3.4.13，也不会生成伪造的 libmpv。

构建沿用上游启用 GPL 的选项；分发产物时应遵循 mpv、FFmpeg 和其依赖的许可证，
保留相应版权声明及源码提供方式。上游版权文件位于给定参考仓库和下载的依赖源码中。

## 使用

将 AAR 放入应用的 `libs` 目录并添加：

```groovy
implementation files('libs/lib_mediaplayer_mpv_0.41.0_all_merged_release.aar')
```

四 ABI 合包支持 **arm64-v8a、armeabi-v7a、x86、x86_64**，最低
**Android 6.0 / API 23**；同目录也提供单 ABI AAR。视频播放需要在 Surface 创建后调用 `setDisplay` 或
`setSurface`。默认使用 MediaCodec copy 模式，可通过 `setHardwareDecoding(false)`
切换为软件解码。

```java
MpvPlayer player = new MpvPlayer();
player.setOnPreparedListener(MpvPlayer::start);
player.setOnErrorListener((mp, what, extra) -> {
    // extra 是 libmpv 的负数错误码
    return true;
});
player.setDataSource("https://example.com/video.mp4");
player.prepareAsync();

// SurfaceHolder.Callback.surfaceCreated
player.setDisplay(holder);
// SurfaceHolder.Callback.surfaceChanged
player.setSurfaceSize(width, height);
// SurfaceHolder.Callback.surfaceDestroyed（在回调返回前执行）
player.setSurface(null);
// 使用结束
player.release();
```

`setDataSource(context, uri)` 可读取 `content://` 和 `android.resource://`。
文件描述符接口独立复制 fd，使用 `pread` 读取限定的 offset/length，不改变调用方文件位置；
调用返回即可关闭原 fd。仅接受可 seek 的普通文件，不支持 pipe 类型 ContentProvider。

常规顺序为 `setDataSource → prepareAsync → onPrepared → start`；`stop` 后需要再次
`prepare/prepareAsync`。播放完毕后可直接 `start` 重新播放。更换数据源前调用 `reset`。
`prepare()` 最多等待 30 秒，建议使用异步准备，避免阻塞 UI。所有实例方法串行化，
`reset/release` 会终止并等待原生事件线程，丢弃旧实例/旧加载的排队回调。

位置和时长默认接口返回毫秒 `int`，长媒体可用 `getCurrentPositionLong/getDurationLong`。
音量是所有声道的主音量，`setVolume(left,right)` 使用两者平均值，不提供左右声道独立平衡。

原 `FFmpegPlayer` 类保留为已弃用的名称别名。监听器的播放器参数改为 `MpvPlayer`，
需修改旧的显式回调签名。旧实现中的 Android 私有 Parcel、音频会话、WakeLock、
截图和元数据过滤等接口不属于本次新核心 API；此别名不是旧类的完整二进制兼容实现。

## 验证

`tools/verify_local.py` 使用本机 SDK、NDK 编译 Java 和四种 ABI 的 JNI 对象，
并运行带 ASan/UBSan 的文件片段测试；不需要下载第三方依赖。
JNI 对象检查使用本地 mpv 和 FFmpeg 7.1.1 的头文件，不能替代完整链接。

```sh
python3 tools/verify_local.py --sdk "$ANDROID_HOME" --ndk "$ANDROID_NDK_ROOT" --java-home "$JAVA_HOME"
```

`src/androidTest` 提供真实 libmpv 的设备测试，覆盖双实例、FD 片段、播放/seek/完成、
重置和错误回调。构建依赖并连接设备后运行：

```sh
./gradlew :lib_mediaplayer_mpvplayer_ff7.1.1_mini:connectedDebugAndroidTest -PmpvAbis=arm64-v8a
```

已完成四种 ABI 的完整依赖和 mpv 0.41.0 编译、JNI 链接及合并版 AAR 打包。
单 ABI AAR 各包含 2 个 `.so`，四 ABI 合包共 8 个；各库的目标架构、SONAME、动态依赖闭包、16 KB LOAD
对齐检查通过；FFmpeg 原有 1072 个导出、mpv 的 54 个 `mpv_*` API 和全部 9 个
Java native 方法的 JNI 导出均保留。C++ 运行时已静态链接，不依赖 `libc++_shared.so`。
合包工具检查四个 AAR 的 Java 类字节码和 Manifest 逐字节一致；ARM64 的 Java
类字节码和 Manifest 亦已与之前通过 D8/AAPT2 检查的拆分版核对一致。
同目录的 `.verification.json` 记录 AAR 和各平台库的哈希、大小、导出接口数量及动态依赖。
x86/x86_64 当前通过 `--disable-x86-asm` 离线构建，未启用 FFmpeg、dav1d、libass 的
NASM 汇编优化，软件解码性能较低；ARM 两个平台保留 ARM 汇编优化。
FFmpeg 7.1.1 构建启用了
MediaCodec、mbedTLS/HTTPS、dav1d 软件 AV1 和 DASH；mpv 启用了 Android EGL、
AudioTrack、OpenSL ES，禁用了 SDL2 音视频和游戏手柄支持。

先前的 Java/四种 ABI JNI 对象编译与文件片段 ASan/UBSan 检查通过。
Gradle 和 ADB 在当前沙箱中无法绑定本地
socket，因此 **尚未完成设备上的实际视频播放验证**；编译和静态检查不能替代真机测试。
