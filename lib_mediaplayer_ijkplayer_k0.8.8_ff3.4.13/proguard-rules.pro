# 1
-dontwarn lib.kalu.ijkplayer.util.**
-keep class lib.kalu.ijkplayer.util.** { *; }
# 2
-keep class lib.kalu.ijkplayer.misc.IAndroidIO{*;}
-keep class lib.kalu.ijkplayer.misc.IMediaDataSource{*;}
-keep class lib.kalu.ijkplayer.misc.IMediaFormat{*;}
-keep class lib.kalu.ijkplayer.misc.IjkTrackInfo{*;}
# 3
-keep class lib.kalu.ijkplayer.IMediaPlayer{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnBufferingUpdateListener{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnCompletionListener{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnErrorListener{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnInfoListener{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnPreparedListener{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnSeekCompleteListener{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnTimedTextListener{*;}
-keep class lib.kalu.ijkplayer.IMediaPlayer$OnVideoSizeChangedListener{*;}
-keep class lib.kalu.ijkplayer.IjkMediaPlayer{*;}
-keep class lib.kalu.ijkplayer.IjkTimedText{*;}
-keep class lib.kalu.ijkplayer.IjkMediaMeta{*;}