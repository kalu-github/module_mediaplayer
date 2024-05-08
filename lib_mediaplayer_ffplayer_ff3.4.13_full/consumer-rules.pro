-dontwarn lib.kalu.ffplayer.**
-keep class lib.kalu.ffplayer.FFmpegPlayer {
    private long mNativeContext;
    private int mNativeSurfaceTexture;
    private int mListenerContext;
    private static void postEventFromNative(java.lang.Object, int, int, int, java.lang.Object);
    public <fields>;
    public <methods>;
    native <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnBufferingUpdateListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnCompletionListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnErrorListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnInfoListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnPreparedListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnSeekCompleteListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnTimedTextListener {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.ffplayer.inter.OnVideoSizeChangedListener {
    public <fields>;
    public <methods>;
}

#-keep class com.yuanxuzhen.bean.*   本包下的类名保持
#-keep class com.yuanxuzhen.bean.**  把本包和所含子包下的类名都保持
#-keep class com.yuanxuzhen.bean.** {*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class com.yuanxuzhen.YUAN{*;} 保持类名，同时保持里面的内容不被混淆
