#define IJKPLAYER_LOG_H

#include <stdio.h>
#include "log_base.h"

#define LOG_TAG "MP_IJK_PLAYER"
#define PLAYER_VLOGV(...)  PLAYER_VLOG(IJK_LOG_VERBOSE,   LOG_TAG, __VA_ARGS__)
#define PLAYER_VLOGD(...)  PLAYER_VLOG(IJK_LOG_DEBUG,     LOG_TAG, __VA_ARGS__)
#define PLAYER_VLOGI(...)  PLAYER_VLOG(IJK_LOG_INFO,      LOG_TAG, __VA_ARGS__)
#define PLAYER_VLOGW(...)  PLAYER_VLOG(IJK_LOG_WARN,      LOG_TAG, __VA_ARGS__)
#define PLAYER_VLOGE(...)  PLAYER_VLOG(IJK_LOG_ERROR,     LOG_TAG, __VA_ARGS__)
#define PLAYER_ALOGV(...)  PLAYER_ALOG(IJK_LOG_VERBOSE,   LOG_TAG, __VA_ARGS__)
#define PLAYER_ALOGD(...)  PLAYER_ALOG(IJK_LOG_DEBUG,     LOG_TAG, __VA_ARGS__)
#define PLAYER_ALOGI(...)  PLAYER_ALOG(IJK_LOG_INFO,      LOG_TAG, __VA_ARGS__)
#define PLAYER_ALOGW(...)  PLAYER_ALOG(IJK_LOG_WARN,      LOG_TAG, __VA_ARGS__)
#define PLAYER_ALOGE(...)  PLAYER_ALOG(IJK_LOG_ERROR,     LOG_TAG, __VA_ARGS__)
#define PLAYER_VLOG(level, TAG, ...)    ((void)ffp_log_extra_vprint(level, TAG, __VA_ARGS__))
#define PLAYER_ALOG(level, TAG, ...)    ((void)ffp_log_extra_print(level, TAG, __VA_ARGS__))
#define PLAYER_LOG_ALWAYS_FATAL(...)   do { PLAYER_ALOGE(__VA_ARGS__); exit(1); } while (0)