#
#### libijkplayer.so => lib_mediaplayer_ijk_k0.8.8_ff3.4_release_xxx.aar
```
https://github.com/kalu-github/ijkplayer-k0.8.8-ff3.4
```

#
#### libijkplayer.so => lib_mediaplayer_ijk_k0.8.8_ff3.4_openssl_release_xxx.aar
```
https://github.com/kalu-github/ijkplayer-k0.8.8-ff3.4-openssl
```

#
#### libijkplayer.so => lib_mediaplayer_ijk_k0.8.8_ff4.0_release_xxx.aar
```
https://github.com/kalu-github/ijkplayer-k0.8.8-ff4.0
```

#
#### libijkplayer.so => lib_mediaplayer_ijk_k0.8.8_ff4.0_openssl_release_xxx.aar
```
https://github.com/kalu-github/ijkplayer-k0.8.8-ff4.0-openssl
```

#
#### libexoplayer-ffmpeg.so => 
```
https://github.com/kalu-github/exoplayer2-ff4.4.3
```
#
#### libffplayer-ffmpeg.so => 
```
https://github.com/kalu-github/ffplayer-ff4.0
```

#
####  exoplayer-extensions
```
- [x] av1 => 视频格式
- [x] cast => DRM支持
- [x] cronet => 网络库
- [✓] ffmpeg
- [x] flac => 音频解码库
- [x] ima => ad
- [x] leanback
- [x] media2
- [x] mediasession => 音频播放器
- [x] okhttp => 网络库
- [x] opus => 音频解码库
- [✓] rtmp
- [x] vp9 => 视频格式
- [x] workmanager
```

#
#### 更新
```
2023-03-22
1. 更新：exoplayer-2.18.5
```
```
2023-03-14
1. 新增：ffplayer_1.0
```
```
2023-03-10
1. 更新：vlcplayer-r3.5.1
```
```
2023-02-22
1. 更新：exoplayer-r2.18.3
```
```
2023-02-03
1. 更新：vlc-3.5.1
```
```
2023-01-31
1. 新增：音频播放器，可选exoplayer、mediaplayer
```
```
2023-01-12
1. 优化：libijkplayer-ffmpeg.so编译脚本
```
```
2023-01-11
1. 新增：exoplayer-ffmpeg视频软解
2. 优化：进度条更新【TextureView基于回调方法，SurfaceView基于Handler消息】
```
```
2022-12-07
1. 修改：exoplayer-udp-超时时长[com/google/android/exoplayer2/upstream/UdpDataSource.java-55]
```
```
2022-12-02
1. 新增：视频网络缓冲, 显示提示loading和网速
```
```
2022-11-25
1. 新增：exoplayer-ffmpeg音频软解
```
```
2022-11-24
1. exoplayer-2.18.2
```
```
2022-11-09
1. ijk增加log开关
```
```
2022-11-03
1. ijk增加armv7、arm64、x86、x86_64
```
```
2022-10-27
1. 新增cmake编译ijkplayer
```
```
2022-09-19
1. fix bug => exoplayer 子线程获取时长exception【com.google.android.exoplayer2.ExoPlayerImpl -> verifyApplicationThread】
```
```
2022-09-02
1.新增：全屏播放
2.新增：小窗播放
```
```
2022-08-05
1.新增：针对RecyclerView自动回收销毁机制, 增加autoRelease方法
```
```
2022-08-02
1.新增：支持试播，指定开始时间、试播时长
2.新增：支持视频指定配音音频，配音音频和原音音频可以切换
```
```
2022-07-21
1.新增vlc-r3.4.9
```
```
2022-06-29
1.更新exoplayer-r2.18.0
2.编译ijk-so
```
```
2022-06-02
1.解决快进bug
```
```
2022-02-19
1.更新ffmpeg4.0版本, ijk软解
```
```
2022-02-11
1. 新增ijkplayer硬解
```
```
2021-12-19
1. 解决android6.0 crash [指定版本=>'com.google.guava:guava:30.1-android']
```
```
2021-12-15
1. 更新exoplayer2.16.1
```
```
2021-11-23
1. 优化ui
2. 删除冗余模块
3. 添加rtsp支持
```
```
2021-09-29
1. 更新exoplayer-2.15.0
2. 重构lib_mediaplayer_ui模块
```

#
####  计划
```
- [✓] 支持TV
- [✓] 支持exoplayer硬解
- [✓] 支持ijkplayer软解
- [✓] 编译更新ffmpeg4.0
```

#
#### 资料
```
https://exoplayer.dev/
https://github.com/google/ExoPlayer
https://mvnrepository.com/search?q=exoplayer
https://github.com/bilibili/ijkplayer
https://github.com/bilibili/FFmpeg/tags
https://code.videolan.org/videolan/vlc-android
https://mvnrepository.com/artifact/org.videolan.android/libvlc-all
```

#
####  exoplayer-jar
```
files('libs/androidx-annotation-1.2.0.jar')
files('libs/commons-io-2.5.jar')
files('libs/guava-30.1-android.jar')
files('libs/failureaccess-1.0.1.jar')
files('libs/checker-qual-2.5.0.jar')
files('libs/checker-compat-qual-2.5.0.jar')
files('libs/j2objc-annotations-1.3.jar')
files('libs/error_prone_annotations-2.10.0.jar')
files('libs/annotations-3.0.1.jar')
```

#
####  优化
```
https://hejunlin.blog.csdn.net/article/details/57075026?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-57075026-blog-80149176.235%5Ev31%5Epc_relevant_increate_t0_download_v2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1-57075026-blog-80149176.235%5Ev31%5Epc_relevant_increate_t0_download_v2&utm_relevant_index=2
```
