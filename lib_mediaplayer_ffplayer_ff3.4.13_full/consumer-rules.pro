-dontwarn lib.kalu.ffplayer.**
-keep class lib.kalu.ffplayer.FFmpegPlayer {
    public <fields>;
    public <methods>;
    native <methods>;
}

#-keep class com.yuanxuzhen.bean.*   本包下的类名保持
#-keep class com.yuanxuzhen.bean.**  把本包和所含子包下的类名都保持
#-keep class com.yuanxuzhen.bean.** {*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class com.yuanxuzhen.YUAN{*;} 保持类名，同时保持里面的内容不被混淆
