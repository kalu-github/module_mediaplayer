#include <stdio.h>
#include <stdarg.h>
#include <jni.h>
#include <android/log.h>
#include "log_base.h"

int mLog = 0;

void _ijk_enable_logger(jboolean enable) {
    if (enable) {
        mLog = 1;
    } else {
        mLog = 0;
    }
}

int _ijk_vprint_verbose(int prio, const char *tag, const char *fmt, va_list ap) {
    if (mLog == 1) {
        __android_log_vprint(prio, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_debug(int prio, const char *tag, const char *fmt, va_list ap) {
    if (mLog == 1) {
        __android_log_print(prio, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_info(int prio, const char *tag, const char *fmt, va_list ap) {
    if (mLog == 1) {
        __android_log_vprint(prio, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_warning(int prio, const char *tag, const char *fmt, va_list ap) {
    if (mLog == 1) {
        __android_log_vprint(prio, tag, fmt, ap);
    }
    return 0;
}

int _ijk_vprint_error(int prio, const char *tag, const char *fmt, va_list ap) {
    if (mLog == 1) {
        __android_log_vprint(prio, tag, fmt, ap);
    }
    return 0;
}

int _ijk_print_verbose(int prio, const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        char buffer[1000];
        va_list argptr;
        va_start(argptr, fmt);
        vsprintf(buffer, fmt, argptr);
        va_end(argptr);
        __android_log_write(prio, tag, buffer);
    }
    return 0;
}

int _ijk_print_debug(int prio, const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        char buffer[1000];
        va_list argptr;
        va_start(argptr, fmt);
        vsprintf(buffer, fmt, argptr);
        va_end(argptr);
        __android_log_write(prio, tag, buffer);
    }
    return 0;
}

int _ijk_print_info(int prio, const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        char buffer[1000];
        va_list argptr;
        va_start(argptr, fmt);
        vsprintf(buffer, fmt, argptr);
        va_end(argptr);
        __android_log_write(prio, tag, buffer);
    }
    return 0;
}

int _ijk_print_warning(int prio, const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        char buffer[1000];
        va_list argptr;
        va_start(argptr, fmt);
        vsprintf(buffer, fmt, argptr);
        va_end(argptr);
        __android_log_write(prio, tag, buffer);
    }
    return 0;
}

int _ijk_print_error(int prio, const char *tag, const char *fmt, ...) {
    if (mLog == 1) {
        char buffer[1000];
        va_list argptr;
        va_start(argptr, fmt);
        vsprintf(buffer, fmt, argptr);
        va_end(argptr);
        __android_log_write(prio, tag, buffer);

//        va_list ap;
//        va_start(ap, fmt);
//        va_end(ap);
//        __android_log_print(prio, tag, tag, fmt, ap);

    }
    return 0;
}