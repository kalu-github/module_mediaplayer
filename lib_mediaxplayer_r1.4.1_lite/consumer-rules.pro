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

# mediax
-dontwarn androidx.media3.**
-keep class androidx.media3.** {
    *;
}

# kalu
-dontwarn lib.kalu.mediax.**
-keep class lib.kalu.mediax.renderers.*{
    public <fields>;
    public <methods>;
}
-keep class lib.kalu.mediax.util.MediaLogUtil{
    public <methods>;
}

#-keep class xx.xx.xx.*   本包下的类名保持
#-keep class xx.xx.xx.**  把本包和所含子包下的类名都保持
#-keep class xx.xx.xx.**{*;} 把本包和所含子包下的类名都保持，同时保持里面的内容不被混淆
#-keep class xx.xx.xx{*;} 保持类名，同时保持里面的内容不被混淆