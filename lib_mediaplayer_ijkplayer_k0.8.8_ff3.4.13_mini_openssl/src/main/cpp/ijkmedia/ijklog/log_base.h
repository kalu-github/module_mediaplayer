#ifndef LOG_BASE_H
#define LOG_BASE_H

#include <stdio.h>
#include <stdarg.h>
#include <jni.h>
#include <android/log.h>

#define IJK_LOG_UNKNOWN     ANDROID_LOG_UNKNOWN
#define IJK_LOG_DEFAULT     ANDROID_LOG_DEFAULT
#define IJK_LOG_VERBOSE     ANDROID_LOG_VERBOSE
#define IJK_LOG_DEBUG       ANDROID_LOG_DEBUG
#define IJK_LOG_INFO        ANDROID_LOG_INFO
#define IJK_LOG_WARN        ANDROID_LOG_WARN
#define IJK_LOG_ERROR       ANDROID_LOG_ERROR
#define IJK_LOG_FATAL       ANDROID_LOG_FATAL
#define IJK_LOG_SILENT      ANDROID_LOG_SILENT

void ffp_log_enable(jboolean enable);

void ffp_log_extra_print(int level, const char *tag, const char *fmt, ...);

void ffp_log_extra_vprint(int level, const char *tag, const char *fmt, va_list ap);

#endif
