# Android mpv 播放核心

模块：[lib_mediaplayer_mpvplayer_ff7.1.1_mini](lib_mediaplayer_mpvplayer_ff7.1.1_mini/README.md)。

基于本地 mpv 0.41.0 与 mpv-android 参考源码，使用独立 JNI 桥接，不使用旧 player/SDL2。
新版依赖构建配置采用 FFmpeg 7.1.1；构建步骤、接口示例和验证范围见模块说明。

## Git 大文件

静态库（`.a`）、动态库（`.so`）、AAR 和 ZIP 通过 Git LFS 提交，完整文件保留在本地，
推送时文件内容上传到远程 LFS 存储。源码与 Gradle Wrapper JAR 仍使用普通 Git。

先安装 [Git LFS](https://git-lfs.com/)，然后在项目根目录执行：

```sh
git lfs install --local
git add .gitattributes README.md
# 将已经暂存的原生库、AAR 和源码压缩包转换为 LFS 指针；不删除本地文件。
git add --renormalize .
git lfs status
git lfs fsck
```

确认后正常执行 `git commit` 和 `git push`。当前新增文件尚未提交，不需要改写历史。
首次克隆的机器也需要安装 Git LFS；已有克隆可执行 `git lfs pull` 下载完整文件后再构建。
