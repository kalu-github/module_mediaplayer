-dontwarn lib.kalu.mediaplayer.**

# 保护主动抛出异常
#-keepattributes Exceptions
#-keepattributes Exceptions,SourceFile,LineNumberTable

# 保护泛型
-keepattributes Signature

# 保护注解
-keepattributes *Annotation*,InnerClasses,EnclosingMethod
#-keep @interface * {
#    *;
#}

# sdk
-keep class lib.kalu.mediaplayer.PlayerSDK {
    public <fields>;
    public <methods>;
}

# args
-keep class lib.kalu.mediaplayer.args.StartArgs {
    public <methods>;
}
-keep class lib.kalu.mediaplayer.args.StartArgs$Builder {
    public <methods>;
}

# type
-keep @interface lib.kalu.mediaplayer.type.PlayerType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$KernelType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$RenderType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$WindowType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$ScaleType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$CacheType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$ExoSeekType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$ExoRenderersType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$StateType {
    *;
}
-keep @interface lib.kalu.mediaplayer.type.PlayerType$SpeedType {
    *;
}

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
-keep class lib.kalu.mediaplayer.util.UdpUtil {
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

#-keep class xx.xx.xx.*        本包下的类名保持
#-keep class xx.xx.xx.**       把本包和所含子包下的类名都保持
#-keep class xx.xx.xx.** {*;}  把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class xx.xx.xx{*;}      保持类名，同时保持里面的内容不被混淆