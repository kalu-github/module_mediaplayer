#include <stdio.h>
#include <stdarg.h>
#include <jni.h>
#include <android/log.h>
#include "ffmpeg_log.h"

int mLog = 0;

void _exo_enable_logger(jboolean enable) {
    if (enable) {
        mLog = 1;
    } else {
        mLog = 0;
    }
}

//void _exo_log_print(int prio, const char *tag, const char *fmt, va_list ap) {
//    if (mLog == 1) {
//        __android_log_vprint(prio, tag, fmt, ap);
//    }
//}

void _exo_log_print(int prio, const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        char buffer[1000];
        va_list argptr;
        va_start(argptr, fmt);
        vsprintf(buffer, fmt, argptr);
        va_end(argptr);
        __android_log_write(prio, tag, buffer);
    }
}