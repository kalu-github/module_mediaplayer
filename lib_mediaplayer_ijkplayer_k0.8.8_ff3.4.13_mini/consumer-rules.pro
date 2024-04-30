-dontwarn lib.kalu.ijkplayer.**

-keep class lib.kalu.ijkplayer.util.IjkLogUtil {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.misc.IAndroidIO {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.misc.IMediaDataSource {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.misc.IMediaFormat {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.misc.IjkTrackInfo {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.IMediaPlayer {
    public *;
#    public <fields>;
#    public <methods>;
}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnBufferingUpdateListener {*;}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnCompletionListener {*;}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnErrorListener {*;}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnInfoListener {*;}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnPreparedListener {*;}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnSeekCompleteListener {*;}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnTimedTextListener {*;}
#-keep class lib.kalu.ijkplayer.IMediaPlayer$OnVideoSizeChangedListener {*;}
-keep class lib.kalu.ijkplayer.IjkMediaPlayer {
    public *;
#    public <fields>;
    native <methods>;
#    public <methods>;
}
#-keep public interface lib.kalu.ijkplayer.IjkMediaPlayer$OnNativeInvokeListener {*;}
-keep class lib.kalu.ijkplayer.IjkTimedText {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.IjkMediaMeta {
    public <fields>;
    public <methods>;
}

#-keep class com.yuanxuzhen.bean.*   本包下的类名保持
#-keep class com.yuanxuzhen.bean.**  把本包和所含子包下的类名都保持
#-keep class com.yuanxuzhen.bean.** {*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class com.yuanxuzhen.YUAN{*;} 保持类名，同时保持里面的内容不被混淆
