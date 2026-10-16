# JNI looks up this static callback by name and signature.
-keep class lib.kalu.mpvplayer.MpvPlayer {
    private static void postEventFromNative(java.lang.Object, int, int, int, int, byte[]);
    native <methods>;
}
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
