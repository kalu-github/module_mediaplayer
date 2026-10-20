# Public MP4 playback verification

This standalone diagnostic APK consumes the supplied AAR's `classes.jar` and native
libraries. It does not substitute a mock or Android MediaPlayer for MpvPlayer.
The default URL is the public Big Buck Bunny MP4:
https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4

Build and run with an existing Android SDK (build-tools 36.0.0, platform 32) and JDK:

```sh
python3 lib_mediaplayer_mpvplayer_ff7.1.1_mini/tools/build_playback_test.py \
  --aar repo/lib_mediaplayer_mpv_0.41.0_merged_release.aar \
  --sdk "$ANDROID_SDK_ROOT" --java-home "$JAVA_HOME" --run
```

Use `--serial DEVICE_SERIAL` when multiple devices are connected. Without `--run`,
the command only builds/signs the APK; it never reports playback as passed.
Results are written under `build/playback-test/`: `result.json`,
`instrumentation.txt`, `logcat.txt`, and the actual decoded `video-frame.png`.

The device must be unlocked and support TextureView/GLES, AudioTrack and Visualizer.
It needs internet access to the URL, and media volume must be audible. The installer
grants RECORD_AUDIO to this diagnostic APK because Visualizer requires it; it does
not use microphone recording. A test-only reflection call sets a dedicated AudioTrack
session ID, so waveform checks are scoped to this player, not other apps' audio.

Checks: main-thread prepared/size/first-frame/seek/completion callbacks, three changing
non-flat TextureView pixel frames, advancing playback clock, nonconstant/non-silent
PCM in the player's AudioTrack session, no duplicate first-frame event after seeking,
and an error callback for a missing file. Buffering events are logged, not required:
a fast connection may not buffer. The sample has no subtitle requirement.

PCM evidence establishes audio signal at AudioTrack, not physical speaker audibility.
The report deliberately keeps `speaker_audibility_verified` false. Listening or an
external audio loopback recording is still needed to establish that last condition.

起播响应性检查：测试记录创建到 `prepareAsync()` 返回的主线程调用耗时，以及首帧前 16 ms Handler 心跳的最大延迟（包含状态查询）。公开 MP4 用例要求两者低于 1000 ms，并在 `MpvPlaybackTest` 日志中输出实测值。构建 APK 不表示该检查已经在设备上通过。
