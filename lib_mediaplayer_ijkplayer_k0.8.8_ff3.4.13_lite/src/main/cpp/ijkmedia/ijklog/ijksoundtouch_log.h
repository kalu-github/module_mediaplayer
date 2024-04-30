
#define IJKSOUNDTOUCH_LOG_H

#include <stdio.h>
#include <android/log.h>
#include "log_base.h"

#include <stdio.h>

#include <android/log.h>
#include "log_base.h"

#define SOUNDTOUCH_LOG_TAG "MP_IJK_SOUNDTOUCH"
#define SOUNDTOUCH_VLOGV(...)  VLOG(IJK_LOG_VERBOSE,   SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_VLOGD(...)  VLOG(IJK_LOG_DEBUG,     SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_VLOGI(...)  VLOG(IJK_LOG_INFO,      SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_VLOGW(...)  VLOG(IJK_LOG_WARN,      SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_VLOGE(...)  VLOG(IJK_LOG_ERROR,     SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_ALOGV(...)  ALOG(IJK_LOG_VERBOSE,   SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_ALOGD(...)  ALOG(IJK_LOG_DEBUG,     SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_ALOGI(...)  ALOG(IJK_LOG_INFO,      SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_ALOGW(...)  ALOG(IJK_LOG_WARN,      SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_ALOGE(...)  ALOG(IJK_LOG_ERROR,     SOUNDTOUCH_LOG_TAG, __VA_ARGS__)
#define SOUNDTOUCH_VLOG(level, TAG, ...)    ((void)ffp_log_extra_vprint(level, TAG, __VA_ARGS__))
#define SOUNDTOUCH_ALOG(level, TAG, ...)    ((void)ffp_log_extra_print(level, TAG, __VA_ARGS__))
#define LOG_ALWAYS_FATAL(...)   do { ALOGE(__VA_ARGS__); exit(1); } while (0)