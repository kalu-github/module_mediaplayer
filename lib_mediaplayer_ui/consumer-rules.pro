-dontwarn lib.kalu.mediaplayer.**

# @interface
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

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
-keep class lib.kalu.mediaplayer.TestActivity {
    public <fields>;
    public <methods>;
}

# config
-keep class lib.kalu.mediaplayer.config.player.PlayerType {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.config.player.PlayerType$** {
    public <fields>;
}
-keep class lib.kalu.mediaplayer.config.player.PlayerSDK {
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediaplayer.config.start.StartBuilder {
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