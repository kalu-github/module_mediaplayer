-dontwarn lib.kalu.mediaplayer.**

# @interface
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod

# 泛型方法 返回值
-keepattributes Signature

#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# sdk
-keep class lib.kalu.mediaplayer.PlayerSDK {
    public <fields>;
    public <methods>;
}

# args
-keep class lib.kalu.mediaplayer.args.StartArgs {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediaplayer.args.StartArgs$Builder {
    public <fields>;
    public <methods>;
}

# type
-keep class lib.kalu.mediaplayer.type.PlayerType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$KernelType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$RenderType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$WindowType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$ScaleType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$CacheType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$ExoSeekType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$ExoFFmpegType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.type.PlayerType$StateType {
    public <fields>;
}
#-keep class lib.kalu.mediaplayer.config.player.PlayerType$* {
#    public <fields>;
#}

# buried
-keep class lib.kalu.mediaplayer.buried.BuriedEvent {
    public <fields>;
    public <methods>;
}

# listener
-keep class lib.kalu.mediaplayer.listener.** {
    public <fields>;
    public <methods>;
}

# util
-keep class lib.kalu.mediaplayer.util.UdpMulticastUtil {
    public <fields>;
    public <methods>;
}

# test
-keep class lib.kalu.mediaplayer.test.TestActivity {
    public <fields>;
    public <methods>;
}

# component
-keep class lib.kalu.mediaplayer.core.component.** {
     public <fields>;
     public <methods>;
}

# view
-keep class lib.kalu.mediaplayer.widget.player.PlayerLayout {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediaplayer.widget.player.PlayerView{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediaplayer.widget.loading.MPLoadingView{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediaplayer.widget.loading.MPLoadingViewSpeed{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediaplayer.widget.speed.SpeedLinearLayout{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediaplayer.widget.subtitle.SimpleSubtitleView{
    public <fields>;
    public <methods>;
}

#-keep class com.yuanxuzhen.bean.*   本包下的类名保持
#-keep class com.yuanxuzhen.bean.**  把本包和所含子包下的类名都保持
#-keep class com.yuanxuzhen.bean.** {*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class lib.kalu.kv.KVContentProvider{*;} 保持类名，同时保持里面的内容不被混淆