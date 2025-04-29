# 保护泛型
-keepattributes Signature

# 保护主动抛出异常
-keepattributes Exceptions

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# Lambda
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

# 保护注解
-keepattributes *Annotation*,InnerClasses,EnclosingMethod
#-keep @interface * {
#    *;
#}

# kalu
-dontwarn lib.kalu.mediax.**
-keep class lib.kalu.mediax.renderers.*{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediax.util.MediaLogUtil{
    public <methods>;
}

# rtmp client
-keep class lib.kalu.mediax.rtmp.RtmpClient {
    *;
}

# exoplayer
-keep class androidx.media3.exoplayer.RenderersFactory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.source.LoadEventInfo {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.source.MediaLoadData {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.ExoPlayer {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.ExoPlayer$Builder {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.SeekParameters {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.DecoderReuseEvaluation {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.DefaultLoadControl {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.ExoPlaybackException {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.analytics.AnalyticsListener {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.analytics.AnalyticsListener$EventTime {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.analytics.AnalyticsListener$Events {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.analytics.DefaultAnalyticsCollector {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.dash.DashMediaSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.hls.HlsMediaSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.rtsp.RtspMediaSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.smoothstreaming.SsMediaSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.source.DefaultMediaSourceFactory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.source.MediaSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.source.ProgressiveMediaSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.source.ProgressiveMediaSource$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.trackselection.DefaultTrackSelector {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.exoplayer.upstream.DefaultBandwidthMeter {
    public <fields>;
    public <methods>;
}
-dontnote androidx.media3.exoplayer.dash.offline.DashDownloader
-keepclassmembers class androidx.media3.exoplayer.dash.offline.DashDownloader {
  <init>(androidx.media3.common.MediaItem, androidx.media3.datasource.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote androidx.media3.exoplayer.hls.offline.HlsDownloader
-keepclassmembers class androidx.media3.exoplayer.hls.offline.HlsDownloader {
  <init>(androidx.media3.common.MediaItem, androidx.media3.datasource.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote androidx.media3.exoplayer.smoothstreaming.offline.SsDownloader
-keepclassmembers class androidx.media3.exoplayer.smoothstreaming.offline.SsDownloader {
  <init>(androidx.media3.common.MediaItem, androidx.media3.datasource.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote androidx.media3.exoplayer.dash.DashMediaSource$Factory
-keepclasseswithmembers class androidx.media3.exoplayer.dash.DashMediaSource$Factory {
  <init>(androidx.media3.datasource.DataSource$Factory);
}
-dontnote androidx.media3.exoplayer.hls.HlsMediaSource$Factory
-keepclasseswithmembers class androidx.media3.exoplayer.hls.HlsMediaSource$Factory {
  <init>(androidx.media3.datasource.DataSource$Factory);
}
-dontnote androidx.media3.exoplayer.smoothstreaming.SsMediaSource$Factory
-keepclasseswithmembers class androidx.media3.exoplayer.smoothstreaming.SsMediaSource$Factory {
  <init>(androidx.media3.datasource.DataSource$Factory);
}
-dontnote androidx.media3.exoplayer.rtsp.RtspMediaSource$Factory
-keepclasseswithmembers class androidx.media3.exoplayer.rtsp.RtspMediaSource$Factory {
  <init>();
}
-dontnote androidx.media3.effect.PreviewingSingleInputVideoGraph$Factory
-keepclasseswithmembers class androidx.media3.effect.PreviewingSingleInputVideoGraph$Factory {
  <init>(androidx.media3.common.VideoFrameProcessor$Factory);
}
-dontnote androidx.media3.effect.DefaultVideoFrameProcessor$Factory$Builder
-keepclasseswithmembers class androidx.media3.effect.DefaultVideoFrameProcessor$Factory$Builder {
  androidx.media3.effect.DefaultVideoFrameProcessor$Factory build();
}


# common
-dontwarn org.checkerframework.**
-dontwarn kotlin.annotations.jvm.**
-dontwarn javax.annotation.**
-dontwarn java.lang.ClassValue
-dontwarn java.lang.SafeVarargs
-dontwarn javax.lang.model.element.Modifier
-dontwarn sun.misc.Unsafe
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.j2objc.annotations.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keepclassmembernames class com.google.common.base.Function { *; }
-keep class androidx.media3.common.MediaLibraryInfo {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.DataReader {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.PriorityTaskManager {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.PriorityTaskManager$PriorityTooLowException {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.C {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.Format {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.MediaItem {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.MediaItem$Builder {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.MediaItem$SubtitleConfiguration {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.MediaItem$SubtitleConfiguration$Builder {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.MimeTypes {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.PlaybackException {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.PlaybackParameters {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.Player {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.VideoSize {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.util.Assertions {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.util.Clock {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.common.util.UnstableApi {
    public <fields>;
    public <methods>;
}

# datasource
-keep class androidx.media3.datasource.DataSink {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DataSink$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.TransferListener {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.TeeDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.PriorityDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.PlaceholderDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.FileDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.FileDataSource$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DataSpec {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DataSpec$Builder {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DataSourceException {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DataSource$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DefaultDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DefaultDataSource$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DefaultHttpDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.DefaultHttpDataSource$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.CacheDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.CacheDataSource$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.SimpleCache {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.Cache {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.Cache$CacheException {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.CacheDataSink {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.CacheDataSink$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.CacheKeyFactory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.CacheSpan {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.ContentMetadata {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.cache.ContentMetadataMutations {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.rtmp.RtmpDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.rtmp.RtmpDataSource$Factory {
    public <fields>;
    public <methods>;
}

# datasource okhttp
-keep class androidx.media3.datasource.okhttp.OkHttpDataSource {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.datasource.okhttp.OkHttpDataSource$Factory {
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.decoder.VideoDecoderOutputBuffer{
    public <fields>;
    public <methods>;
}

# datasource rtmp
-keepclassmembers class androidx.media3.datasource.RawResourceDataSource {
  public static android.net.Uri buildRawResourceUri(int);
}
-dontnote androidx.media3.datasource.rtmp.RtmpDataSource
-keepclassmembers class androidx.media3.datasource.rtmp.RtmpDataSource {
  <init>();
}

# datasource okhttp
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# decoder av1
-dontnote androidx.media3.decoder.av1.Libgav1VideoRenderer
-keepclassmembers class androidx.media3.decoder.av1.Libgav1VideoRenderer {
  <init>(long, android.os.Handler, androidx.media3.exoplayer.video.VideoRendererEventListener, int);
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class androidx.media3.decoder.VideoDecoderOutputBuffer {
  *;
}

# decoder ffmpeg
-dontnote androidx.media3.decoder.ffmpeg.ExperimentalFfmpegVideoRenderer
-keepclassmembers class androidx.media3.decoder.ffmpeg.ExperimentalFfmpegVideoRenderer {
  <init>(long, android.os.Handler, androidx.media3.exoplayer.video.VideoRendererEventListener, int);
}

-dontnote androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer
-keepclassmembers class androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer {
  <init>(android.os.Handler, androidx.media3.exoplayer.audio.AudioRendererEventListener, androidx.media3.exoplayer.audio.AudioSink);
}
-keep class androidx.media3.decoder.ffmpeg.FfmpegLibrary{
    native <methods>;
    public <fields>;
    public <methods>;
}
-keep class androidx.media3.decoder.ffmpeg.FfmpegVideoDecoder{
    native <methods>;
}
-keep class androidx.media3.decoder.ffmpeg.FfmpegAudioDecoder{
    native <methods>;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep, includedescriptorclasses class androidx.media3.decoder.ffmpeg.FfmpegAudioDecoder {
  private java.nio.ByteBuffer growOutputBuffer(androidx.media3.decoder.SimpleDecoderOutputBuffer, int);
}

# decoder flac
-dontnote androidx.media3.decoder.flac.LibflacAudioRenderer
-keepclassmembers class androidx.media3.decoder.flac.LibflacAudioRenderer {
  <init>(android.os.Handler, androidx.media3.exoplayer.audio.AudioRendererEventListener, androidx.media3.exoplayer.audio.AudioSink);
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class androidx.media3.decoder.flac.FlacDecoderJni {
    *;
}

# decoder opus
-dontnote androidx.media3.decoder.opus.LibopusAudioRenderer
-keepclassmembers class androidx.media3.decoder.opus.LibopusAudioRenderer {
  <init>(android.os.Handler, androidx.media3.exoplayer.audio.AudioRendererEventListener, androidx.media3.exoplayer.audio.AudioSink);
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class androidx.media3.decoder.SimpleDecoderOutputBuffer {
    *;
}

# decoder vp9
-dontnote androidx.media3.decoder.vp9.LibvpxVideoRenderer
-keepclassmembers class androidx.media3.decoder.vp9.LibvpxVideoRenderer {
  <init>(long, android.os.Handler, androidx.media3.exoplayer.video.VideoRendererEventListener, int);
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class androidx.media3.decoder.VideoDecoderOutputBuffer {
    *;
}

# decoder midi
-dontnote androidx.media3.decoder.midi.MidiRenderer
-keepclassmembers class androidx.media3.decoder.midi.MidiRenderer {
  <init>(android.content.Context);
}

# extractor
-keep class androidx.media3.extractor.DefaultExtractorsFactory {
   public <fields>;
   public <methods>;
}
-keep class androidx.media3.extractor.FlacStreamMetadata {
    *;
}
-keep class androidx.media3.extractor.metadata.flac.PictureFrame {
    *;
}
-dontnote androidx.media3.decoder.flac.FlacExtractor
-keepclassmembers class androidx.media3.decoder.flac.FlacExtractor {
  <init>(int);
}
-dontnote androidx.media3.decoder.flac.FlacLibrary
-keepclassmembers class androidx.media3.decoder.flac.FlacLibrary {
  public static boolean isAvailable();
}
-dontnote androidx.media3.decoder.midi.MidiExtractor
-keepclassmembers class androidx.media3.decoder.midi.MidiExtractor {
  <init>();
}
-dontwarn org.checkerframework.**
-dontwarn kotlin.annotations.jvm.**
-dontwarn javax.annotation.**

# database
-keep class androidx.media3.database.StandaloneDatabaseProvider {
    public <fields>;
    public <methods>;
}

#-keep class xx.xx.xx.*   本包下的类名保持
#-keep class xx.xx.xx.**  把本包和所含子包下的类名都保持
#-keep class xx.xx.xx.**{*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class xx.xx.xx{*;} 保持类名，同时保持里面的内容不被混淆