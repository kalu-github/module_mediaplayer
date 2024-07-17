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
-dontwarn lib.kalu.exoplayer2.**
-keep class lib.kalu.exoplayer2.renderers.*{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.exoplayer2.util.ExoLogUtil{
    public <methods>;
}

# explayer2
-keep class com.google.android.exoplayer2.C{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.DefaultLoadControl{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ExoPlaybackException{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ExoPlayer{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ExoPlayer$Builder{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ExoPlayerLibraryInfo{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.Format{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.MediaItem{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.MediaItem$Builder{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.PlaybackException{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.PlaybackParameters{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.Player{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.SeekParameters{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.Tracks{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.Tracks$Group{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.analytics.AnalyticsListener{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.analytics.AnalyticsListener$EventTime{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.analytics.DefaultAnalyticsCollector{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.extractor.DefaultExtractorsFactory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.DefaultMediaSourceFactory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.LoadEventInfo{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.MediaLoadData{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.MediaSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.ProgressiveMediaSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.ProgressiveMediaSource$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.TrackGroup{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.dash.DashMediaSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.hls.HlsMediaSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.rtsp.RtspMediaSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.trackselection.DefaultTrackSelector{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.trackselection.TrackSelectionOverride{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.trackselection.TrackSelectionParameters{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.trackselection.TrackSelectionParameters$Builder{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.trackselection.TrackSelector{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataSource$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DefaultBandwidthMeter{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DefaultDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DefaultDataSource$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DefaultHttpDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DefaultHttpDataSource$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.CacheDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.CacheDataSource$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.SimpleCache{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.util.Clock{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.video.VideoSize{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.RenderersFactory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.database.StandaloneDatabaseProvider{
    public <fields>;
    public <methods>;
}
-keep class com.google.common.collect.ImmutableList{
    public <fields>;
    public <methods>;
}

-keep class com.google.android.exoplayer2.util.Assertions{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.util.Util{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.util.Assertions{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.util.PriorityTaskManager{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataSink{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataSink$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataSourceException{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataSpec{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataSpec$Builder{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.FileDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.FileDataSource$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.PlaceholderDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.PriorityDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.TeeDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.TransferListener{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.Cache{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.Cache$CacheException{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.CacheDataSink{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.CacheDataSink$Factory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.CacheKeyFactory{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.CacheSpan{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.ContentMetadata{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.cache.ContentMetadataMutations{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.upstream.DataReader{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.source.TrackGroupArray{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.decoder.DecoderReuseEvaluation{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.decoder.VideoDecoderOutputBuffer{
    public <fields>;
    public <methods>;
}

# core
-dontnote com.google.android.exoplayer2.source.dash.offline.DashDownloader
-keepclassmembers class com.google.android.exoplayer2.source.dash.offline.DashDownloader {
  <init>(com.google.android.exoplayer2.MediaItem, com.google.android.exoplayer2.upstream.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote com.google.android.exoplayer2.source.hls.offline.HlsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.hls.offline.HlsDownloader {
  <init>(com.google.android.exoplayer2.MediaItem, com.google.android.exoplayer2.upstream.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader {
  <init>(com.google.android.exoplayer2.MediaItem, com.google.android.exoplayer2.upstream.cache.CacheDataSource$Factory, java.util.concurrent.Executor);
}
-dontnote com.google.android.exoplayer2.source.dash.DashMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.dash.DashMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.rtsp.RtspMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.rtsp.RtspMediaSource$Factory {
  <init>();
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


# rtmp
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class lib.kalu.exoplayer2.rtmp.RtmpClient{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ext.rtmp.RtmpDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ext.rtmp.RtmpDataSource$Factory{
    public <fields>;
    public <methods>;
}

# okhttp
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keep class com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource{
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource$Factory{
    public <fields>;
    public <methods>;
}

# ffmpeg
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary{
    native <methods>;
    public <fields>;
    public <methods>;
}
-keep class com.google.android.exoplayer2.ext.ffmpeg.FfmpegVideoDecoder{
    native <methods>;
}
-keep class com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioDecoder{
    native <methods>;
}
-dontnote com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioSink);
}

# extractor
-dontwarn org.checkerframework.**
-dontwarn kotlin.annotations.jvm.**
-dontwarn javax.annotation.**


# database


# datasource
-keepclassmembers class com.google.android.exoplayer2.upstream.RawResourceDataSource {
  public static android.net.Uri buildRawResourceUri(int);
}
-dontnote com.google.android.exoplayer2.ext.rtmp.RtmpDataSource
-keepclassmembers class com.google.android.exoplayer2.ext.rtmp.RtmpDataSource {
  <init>();
}

#-keep class xx.xx.xx.*   本包下的类名保持
#-keep class xx.xx.xx.**  把本包和所含子包下的类名都保持
#-keep class xx.xx.xx.**{*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class xx.xx.xx{*;} 保持类名，同时保持里面的内容不被混淆