-dontwarn lib.kalu.mediaplayer.**
-dontwarn com.google.**
-dontwarn org.apache.commons.io.**

-keep class lib.kalu.mediaplayer.buried.** {*;}
-keep class lib.kalu.mediaplayer.config.** {*;}
-keep class lib.kalu.mediaplayer.listener.** {*;}
-keep class lib.kalu.mediaplayer.core.component.** {*;}
-keep class lib.kalu.mediaplayer.TestActivity {*;}
-keep class lib.kalu.mediaplayer.util.UdpMulticastUtil {*;}


# 自定义view
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