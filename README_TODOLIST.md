# Android mpv 开源依赖清单与作用说明

整理日期：2026-10-19。范围为当前 `dev_mpvplayer` 分支实际启用的
[lib_mediaplayer_mpvplayer_ff7.1.1_mini](lib_mediaplayer_mpvplayer_ff7.1.1_mini/README.md)
模块，默认采用 `merged` 合并构建。

[settings.gradle](settings.gradle) 当前只启用这个模块，其 Java 层没有声明额外的第三方运行时依赖。
下列版本和用途依据本地源码、构建配方以及四种 ABI 的实际链接清单整理。
同一项目可能生成多个静态库；作为链接输入，也不表示它的全部功能在每次播放时都启用。

## 1. 播放核心、解码和网络

| 开源库 | 项目采用版本 | 在本项目中的作用 | 链接输入 / 所属产物 |
| --- | --- | --- | --- |
| mpv / libmpv | 0.41.0 | 播放调度核心：管理打开媒体、音视频同步、播放/暂停、seek、倍速、轨道选择及播放事件。Java 通过本项目 JNI 桥接调用 libmpv。 | `libmpv.a` → `libmpvplayer.so` |
| FFmpeg | 7.1.1（`n7.1.1`） | 读取本地/网络媒体，解析 MP4、MKV 等封装，解码音视频，提供滤镜、图像转换和音频重采样；通过 MediaCodec 接入 Android 硬件解码。 | 8 个组件静态库 → `libmpvffmpeg.so`，见下一节 |
| dav1d | 1.5.1 | AV1 软件视频解码器，供 FFmpeg 的 `libdav1d` 解码器使用。 | `libdav1d.a` → `libmpvffmpeg.so` |
| Mbed TLS | 3.6.7 | 为 FFmpeg 的 HTTPS/TLS 访问提供加密协议、证书处理和底层密码算法支持。 | `libmbedtls.a`、`libmbedx509.a`、`libmbedcrypto.a` → `libmpvffmpeg.so` |
| libxml2 | 2.15.4 | XML 解析：FFmpeg 用于解析 DASH 的 MPD 清单，Fontconfig 用于读取字体配置。 | `libxml2.a` 出现在 FFmpeg 和 MPV 两组链接输入中 |
| Lua | 5.2.4 | 提供 mpv 的 Lua 脚本运行能力。构建启用 Lua，但当前 JNI 默认关闭 OSC 屏幕控制器，不会因此自动出现上游播放器界面。 | `liblua.a` → `libmpvplayer.so` |

### FFmpeg 的 8 个组件

这些是同一个 FFmpeg 项目的组成部分，版本均为 7.1.1。

| 组件 | 静态库 | 作用与当前配置 |
| --- | --- | --- |
| libavformat | `libavformat.a` | 处理媒体输入、网络协议和解封装，把 MP4/MKV 等容器拆成音视频数据包。当前还保留 MOV、Matroska、MPEG-TS 封装器，供缓存导出等能力使用。 |
| libavcodec | `libavcodec.a` | 音视频及字幕编解码；播放主要使用解码功能，并接入 MediaCodec、dav1d。当前裁剪配置仅显式保留 MJPEG、PNG 编码器，供截图等能力使用。 |
| libavutil | `libavutil.a` | 各组件共用的数据结构、内存管理、时间基、像素/采样格式及数学工具。 |
| libavfilter | `libavfilter.a` | 组织音视频滤镜处理链；具体效果取决于播放时的滤镜配置。 |
| libswscale | `libswscale.a` | 图像缩放、像素格式转换和色彩空间转换，例如 YUV 与 RGB 之间的转换。 |
| libswresample | `libswresample.a` | 音频采样率、采样格式和声道布局转换，使解码后的 PCM 适配输出设备。 |
| libpostproc | `libpostproc.a` | 提供去块等视频后处理算法接口；保留链接及公开接口不表示默认播放会启用这些效果。 |
| libavdevice | `libavdevice.a` | FFmpeg 的设备输入输出框架。当前配方使用 `--disable-devices`，保留组件库不代表启用了摄像头、麦克风采集等设备功能。 |

## 2. 字幕和字体

这一组依赖用于原生字幕绘制。文本字幕回调由本项目 JNI/Java 桥接传递，字幕画面的布局和绘制由下列库协作完成。

| 开源库 | 项目采用版本 | 在本项目中的作用 | 静态库 |
| --- | --- | --- | --- |
| libass | 0.17.4 | 解析和渲染 ASS/SSA 字幕，处理样式、定位、描边、阴影等效果，是 mpv 字幕渲染的重要组成部分。 | `libass.a` |
| FreeType | 2.14.3 | 读取字体文件并将字形栅格化，为字幕提供实际可绘制的文字形状。 | `libfreetype.a` |
| HarfBuzz | 14.4.0 | 文字塑形：把字符序列转换成正确的字形及位置，处理连字、组合字符和复杂文字排版。 | `libharfbuzz.a` |
| FriBidi | 1.0.16 | Unicode 双向文字处理，使阿拉伯语、希伯来语与从左到右文字混排时顺序正确。 | `libfribidi.a` |
| libunibreak | 8.0 | Unicode 断行支持，帮助字幕在合适的位置自动换行。 | `libunibreak.a` |
| Fontconfig | 2.18.3 | 字体查找和匹配，按字体名、样式等选择字体，并支持字体回退；配置解析依赖 libxml2。 | `libfontconfig.a` |

以上静态库作为 `libmpvplayer.so` 的链接输入。简单理解：Fontconfig 找字体，HarfBuzz/FriBidi/libunibreak
处理文字布局，FreeType 生成字形，libass 将它们组织为字幕画面。

## 3. 视频渲染及随源码引入的依赖

| 开源库 | 版本 / 固定提交 | 在本项目中的作用 | 使用方式 |
| --- | --- | --- | --- |
| libplacebo | 7.351.0 | mpv 的图像处理与 GPU 渲染依赖，提供缩放、色彩处理、着色器等能力。具体使用哪些处理路径，由 mpv 的视频输出和选项决定。 | `libplacebo.a` 参与 `libmpvplayer.so` 链接 |
| fast_float | `1bf70101536d37fa9954cc4f03fd0903d045a9f3` | 快速、与区域设置无关的浮点数字符串解析，用于 libplacebo 的数值转换代码。 | C++ 头文件依赖，不单独生成 `.so` |
| glad | `73db193f853e2ee079bf3ca8a64aa2eaf6459043` | 生成 OpenGL/EGL 等图形 API 的函数加载代码，帮助 libplacebo 使用运行设备提供的图形接口。 | 生成器在构建机运行；生成的加载代码参与原生编译 |
| Jinja | `15206881c006c79667fe5154fe80c01c65410679` | Python 模板引擎，供图形接口代码生成过程使用。 | 构建时依赖，不作为 Python 包放入 AAR |
| MarkupSafe | `28ace20b140d15c083e1cbc163ee6b7778ba098c` | Jinja 使用的字符串标记和转义支持。 | 构建时依赖，不参与 Android 播放 |

子模块的来源和提交见
[vendor-origins.json](lib_mediaplayer_mpvplayer_ff7.1.1_mini/src/main/cpp/vendor-origins.json)。
当前 libplacebo 构建禁用 Vulkan 和 demos；JNI 有 Surface 时使用 `vo=gpu`、Android EGL/OpenGL ES，
没有 Surface 时使用 `vo=null`。不能仅凭 libplacebo 被链接，就认定所有高级画质功能都默认开启。

## 4. 系统库和运行时

下面这些依赖由 Android 系统或 NDK 提供，与项目自行编译并打包的第三方库分开记录。

| 依赖 / 接口 | 作用 | 提供方式 |
| --- | --- | --- |
| zlib（`libz`） | 压缩与解压基础能力，供 FFmpeg、字体等相关代码使用。 | Android 系统库，版本随设备系统 |
| LLVM libc++ | 支持 JNI、mpv 及依赖中的 C++ 标准库功能。 | 默认 `c++_static`，随 NDK 28.1.13356709 静态链接，不另带 `libc++_shared.so` |
| Bionic libc、libm、libdl / pthread | C 运行时、数学运算、动态加载和线程同步；Android 的 pthread 功能由 libc 提供。 | Android 系统库 |
| atomic 支持库 | 为目标架构提供必要的原子操作支持。 | NDK 工具链链接支持 |
| `libandroid` / ANativeWindow | 将 Java Surface 交给原生视频输出使用。 | Android 系统接口 |
| `libmediandk` / MediaCodec | 访问 Android 媒体编解码能力；当前默认 `hwdec=mediacodec-copy`。 | Android 系统接口 |
| EGL / OpenGL ES | 创建图形上下文，并将视频渲染到 Surface；部分图形函数由加载器动态解析。 | Android 系统及设备图形驱动 |
| AudioTrack / OpenSL ES | 向 Android 音频系统输出 PCM；当前音频输出配置为 `audiotrack,opensles`。 | Android 系统接口 |
| `liblog` | 将原生初始化、播放错误等信息写入 Logcat。 | Android 系统库 |

EGL/OpenGL ES、MediaCodec、AudioTrack 等在这里记录的是所用平台接口，不表示仓库包含设备驱动的源码。

## 5. 参考工程、已下载但未进入当前产物的项目

| 项目 | 版本 / 来源 | 当前用途或状态 |
| --- | --- | --- |
| mpv-android | 本地 `mpv-android-2026-09-17` 源码目录 | 参考其 Android 播放实现和依赖构建脚本。本项目使用自己的 Java/JNI 桥接，不将该目录中的完整 Kotlin App 编译进 AAR。其 App 的 Gradle 依赖也不是当前模块的运行时依赖。 |
| curl / libcurl | 下载配方为 8.21.0 | 参考依赖树包含下载/构建此库；当前四种 ABI 的最终链接清单均没有 `libcurl.a`。当前网络播放由 FFmpeg 协议层与 Mbed TLS 支持。宿主机 `curl` 命令仍用于下载源码。 |
| Vulkan-Headers | `cacef3039d277c448c89336290ec3937270b0996` | 随 libplacebo 完整源码保存的 Vulkan API 头文件。当前配方禁用 Vulkan，不作为当前 Vulkan 后端的启用证据。 |
| Nuklear | `b0fbe40b943e735150465875d81e4ed0562359a8` | libplacebo 示例程序的界面库；当前 `demos=false`，不编入播放器。 |
| SDL2 | 当前未使用 | MPV 构建明确禁用 SDL2 视频、音频及游戏手柄支持，当前 CMake 不链接旧 SDL2 播放路径。 |
| FFmpeg 3.4.13 / 旧 player | 当前未使用 | 当前启用的模块使用 FFmpeg 7.1.1；`settings.gradle` 中旧版本模块处于注释状态。 |

## 6. 构建和维护工具

这些工具用于生成、验证或管理产物，本身不是播放器的媒体解码依赖。

| 工具 | 项目配置 / 版本 | 作用 |
| --- | --- | --- |
| Gradle | Wrapper 8.0 | 执行 Android 模块构建任务。 |
| Android Gradle Plugin | 8.0.2 | 组织 Java 编译、Manifest 处理、原生构建与 AAR 打包。 |
| CMake | Gradle 配置 3.22.1 | 编译 JNI，将静态库链接为两个最终 `.so`。 |
| Ninja | 随构建环境 / SDK CMake 提供 | 执行 CMake、Meson 生成的原生编译任务。 |
| Meson | 安装脚本固定 1.11.0 | 配置 mpv、libass、libplacebo 及多项原生依赖的交叉编译。 |
| pkgconf / pkg-config | 安装包固定 `pkgconf==3.0.7.post0` | 读取 `.pc` 文件，确定依赖库的头文件位置和链接参数。 |
| GNU Make | 使用宿主环境版本 | 执行 FFmpeg、Lua 等依赖的 Makefile。 |
| LLVM / Clang / LLD | 随 NDK 28.1.13356709 | 编译 C/C++、汇编并链接 Android ELF；相关工具还用于检查架构、导出符号和动态依赖。 |
| NASM | x86 / x86_64 汇编构建时需要 | 编译 FFmpeg、dav1d、libass 的部分 x86 优化实现；支持通过 `--disable-x86-asm` 关闭相应优化。 |
| Python | 构建文档建议 3.12+ | 执行源码下载、依赖构建、校验、合包和本地验证脚本。 |
| JDK / Java 工具链 | 构建文档要求 JDK 17；Java 源码目标为 8 | 编译 Java 接口，运行 Gradle 及 Android 打包工具。 |
| Git / Git LFS | 使用安装环境版本 | 管理源码及 `.a`、`.so`、AAR、ZIP 等大文件。 |
| curl 命令行工具 | 使用宿主环境版本 | 下载依赖源码归档；与是否将 `libcurl` 链接进播放器是两件事。 |

Android SDK platform 32、Build Tools、ADB 用于编译、打包和设备测试。
测试代码使用 Android 自带的 `android.test.runner` / `android.test.base`；模块未额外声明第三方 Android 测试框架依赖。

## 7. 最终 AAR 的对应关系

四种 ABI：`arm64-v8a`、`armeabi-v7a`、`x86`、`x86_64`。当前各 ABI 的清单均包含 **22 个不同的 `.a`**，
默认合并为每种 ABI 两个 `.so`：

| 最终产物 | 主要链接输入 | 负责的功能 |
| --- | --- | --- |
| `libmpvffmpeg.so` | FFmpeg 的 8 个组件 + dav1d + Mbed TLS 的 3 个组件 + libxml2 | 媒体读取、解封装、解码、音视频数据处理、HTTPS 和 DASH 相关支持 |
| `libmpvplayer.so` | mpv + 本项目 JNI + libass/字体库 + libplacebo + Lua + C++ 运行时，并链接 `libmpvffmpeg.so` | Java 接口与回调桥接、播放控制、同步、字幕和音视频输出 |

libxml2 同时出现在两组链接输入中，因此两组输入的数量不能直接相加作为不同库的数量。
fast_float、glad 生成代码等并不对应独立的 `.a` 文件。

Java 入口调用 `System.loadLibrary("mpvplayer")`。
`libmpvplayer.so` 的 ELF 动态段记录 `DT_NEEDED: libmpvffmpeg.so`，由 Android 动态链接器加载该依赖。

## 8. 清单依据与后续维护

- [模块 Gradle 配置](lib_mediaplayer_mpvplayer_ff7.1.1_mini/build.gradle)：Android 版本、NDK、CMake、Java 及测试依赖。
- [CMake 链接配置](lib_mediaplayer_mpvplayer_ff7.1.1_mini/src/main/cpp/CMakeLists.txt)：两个动态库的生成及系统库链接方式。
- [ARM64 静态库链接清单](lib_mediaplayer_mpvplayer_ff7.1.1_mini/src/main/cpp/ffmpeg/arm64-v8a/link.cmake)：运行时库输入；本次已核对其余三种 ABI 的清单一致。
- [ARM64 构建清单](lib_mediaplayer_mpvplayer_ff7.1.1_mini/src/main/cpp/ffmpeg/arm64-v8a/manifest.json)：源码来源、构建版本及静态库校验值；同级 ABI 目录各自保留对应记录。
- [依赖版本配方](lib_mediaplayer_mpvplayer_ff7.1.1_mini/src/main/cpp/mpv-android-2026-09-17/buildscripts/include/depinfo.sh) 与 [项目构建脚本](lib_mediaplayer_mpvplayer_ff7.1.1_mini/tools/build_native.py)：第三方版本及本项目对参考配方的调整。FFmpeg、dav1d、NDK 等应以项目覆盖值和实际构建清单为准，不能直接照搬参考工程默认值。
- [宿主工具安装脚本](lib_mediaplayer_mpvplayer_ff7.1.1_mini/tools/setup_host_tools.sh)：固定的 Meson、pkgconf 安装版本。

后续升级库、修改裁剪选项、增加 Android 模块或重新生成静态库时，应同步更新对应版本、用途和“是否进入最终产物”的状态。
