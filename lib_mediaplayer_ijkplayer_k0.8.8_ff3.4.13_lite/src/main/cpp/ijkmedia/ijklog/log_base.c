#include <stdio.h>
#include <stdarg.h>
#include <jni.h>
#include <android/log.h>
#include "log_base.h"

#define LOG_BUF_SIZE    1024
int LOG_ENABLE = 0; // 默认关闭

void ffp_log_enable(jboolean enable) {
    if (enable) {
        LOG_ENABLE = 1;
    } else {
        LOG_ENABLE = 0;
    }
}

void ffp_log_extra_print(int level, const char *tag, const char *fmt, ...) {
    if (LOG_ENABLE == 1) {
        va_list ap;
        char log_buffer[LOG_BUF_SIZE] = {0};
        va_start(ap, fmt);
        vsnprintf(log_buffer, LOG_BUF_SIZE, fmt, ap);
        va_end(ap);
        __android_log_write(level, tag, log_buffer);
//        __android_log_print(level, tag, log_buffer);
    }
}

void ffp_log_extra_vprint(int level, const char *tag, const char *fmt, va_list ap) {
    if (LOG_ENABLE == 1) {
        char log_buffer[LOG_BUF_SIZE] = {0};
        vsnprintf(log_buffer, LOG_BUF_SIZE, fmt, ap);
        __android_log_write(level, tag, log_buffer);
//        __android_log_vprint(level, tag, log_buffer);
    }
}