#define IJKJ4A_LOG_H

#include <stdio.h>

#include <android/log.h>
#include "log_base.h"

#define LOG_TAG "MP_IJK_J4A"
#define J4A_VLOGV(...)  J4A_VLOG(IJK_LOG_VERBOSE,   LOG_TAG, __VA_ARGS__)
#define J4A_VLOGD(...)  J4A_VLOG(IJK_LOG_DEBUG,     LOG_TAG, __VA_ARGS__)
#define J4A_VLOGI(...)  J4A_VLOG(IJK_LOG_INFO,      LOG_TAG, __VA_ARGS__)
#define J4A_VLOGW(...)  J4A_VLOG(IJK_LOG_WARN,      LOG_TAG, __VA_ARGS__)
#define J4A_VLOGE(...)  J4A_VLOG(IJK_LOG_ERROR,     LOG_TAG, __VA_ARGS__)
#define J4A_ALOGV(...)  J4A_ALOG(IJK_LOG_VERBOSE,   LOG_TAG, __VA_ARGS__)
#define J4A_ALOGD(...)  J4A_ALOG(IJK_LOG_DEBUG,     LOG_TAG, __VA_ARGS__)
#define J4A_ALOGI(...)  J4A_ALOG(IJK_LOG_INFO,      LOG_TAG, __VA_ARGS__)
#define J4A_ALOGW(...)  J4A_ALOG(IJK_LOG_WARN,      LOG_TAG, __VA_ARGS__)
#define J4A_ALOGE(...)  J4A_ALOG(IJK_LOG_ERROR,     LOG_TAG, __VA_ARGS__)
#define J4A_VLOG(level, TAG, ...)    ((void)ffp_log_extra_vprint(level, TAG, __VA_ARGS__))
#define J4A_ALOG(level, TAG, ...)    ((void)ffp_log_extra_print(level, TAG, __VA_ARGS__))
#define J4A_LOG_ALWAYS_FATAL(...)   do { J4A_ALOGE(__VA_ARGS__); exit(1); } while (0)
//#define J4A_LOG_TAG "MP_IJK_J4A"
//#define J4A_VLOGV(...)  __android_log_vprint(ANDROID_LOG_VERBOSE,   J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_VLOGD(...)  __android_log_vprint(ANDROID_LOG_DEBUG,     J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_VLOGI(...)  __android_log_vprint(ANDROID_LOG_INFO,      J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_VLOGW(...)  __android_log_vprint(ANDROID_LOG_WARN,      J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_VLOGE(...)  __android_log_vprint(ANDROID_LOG_ERROR,     J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_ALOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,    J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_ALOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,      J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_ALOGI(...)  __android_log_print(ANDROID_LOG_INFO,       J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_ALOGW(...)  __android_log_print(ANDROID_LOG_WARN,       J4A_LOG_TAG, __VA_ARGS__)
//#define J4A_ALOGE(...)  __android_log_print(ANDROID_LOG_ERROR,      J4A_LOG_TAG, __VA_ARGS__)