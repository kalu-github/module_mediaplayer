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
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.IjkMediaPlayer {
    private long mNativeMediaPlayer;
    private long mNativeMediaDataSource;
    private long mNativeAndroidIO;
    private int mNativeSurfaceTexture;
    private static void postEventFromNative(java.lang.Object, int , int , int , java.lang.Object);
    private static java.lang.String onSelectCodec(java.lang.Object, java.lang.String, int , int);
    private static boolean onNativeInvoke(java.lang.Object, int, ...);
    public <fields>;
    native <methods>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.IjkTimedText {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.IjkMediaMeta {
    public <fields>;
    public <methods>;
}

-keep class lib.kalu.ijkplayer.inter.OnBufferingUpdateListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnCompletionListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnControlMessageListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnErrorListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnInfoListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnNativeInvokeListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnPreparedListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnSeekCompleteListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnTimedTextListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ijkplayer.inter.OnVideoSizeChangedListener {
    public <fields>;
    public <methods>;
}

#-keep class com.yuanxuzhen.bean.*   本包下的类名保持
#-keep class com.yuanxuzhen.bean.**  把本包和所含子包下的类名都保持
#-keep class com.yuanxuzhen.bean.** {*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class com.yuanxuzhen.YUAN{*;} 保持类名，同时保持里面的内容不被混淆
