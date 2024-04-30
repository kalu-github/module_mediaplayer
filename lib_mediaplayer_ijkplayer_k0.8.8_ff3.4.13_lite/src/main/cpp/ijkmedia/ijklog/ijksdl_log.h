#define IJKSDL_LOG_H

#include <stdio.h>
#include "log_base.h"

#define LOG_TAG "MP_IJK_SDL"
#define SDL_VLOGV(...)  SDL_VLOG(IJK_LOG_VERBOSE,   LOG_TAG, __VA_ARGS__)
#define SDL_VLOGD(...)  SDL_VLOG(IJK_LOG_DEBUG,     LOG_TAG, __VA_ARGS__)
#define SDL_VLOGI(...)  SDL_VLOG(IJK_LOG_INFO,      LOG_TAG, __VA_ARGS__)
#define SDL_VLOGW(...)  SDL_VLOG(IJK_LOG_WARN,      LOG_TAG, __VA_ARGS__)
#define SDL_VLOGE(...)  SDL_VLOG(IJK_LOG_ERROR,     LOG_TAG, __VA_ARGS__)
#define SDL_ALOGV(...)  SDL_ALOG(IJK_LOG_VERBOSE,   LOG_TAG, __VA_ARGS__)
#define SDL_ALOGD(...)  SDL_ALOG(IJK_LOG_DEBUG,     LOG_TAG, __VA_ARGS__)
#define SDL_ALOGI(...)  SDL_ALOG(IJK_LOG_INFO,      LOG_TAG, __VA_ARGS__)
#define SDL_ALOGW(...)  SDL_ALOG(IJK_LOG_WARN,      LOG_TAG, __VA_ARGS__)
#define SDL_ALOGE(...)  SDL_ALOG(IJK_LOG_ERROR,     LOG_TAG, __VA_ARGS__)
#define SDL_VLOG(level, TAG, ...)    ((void)ffp_log_extra_vprint(level, TAG, __VA_ARGS__))
#define SDL_ALOG(level, TAG, ...)    ((void)ffp_log_extra_print(level, TAG, __VA_ARGS__))
#define SDL_LOG_ALWAYS_FATAL(...)   do { SDL_ALOGE(__VA_ARGS__); exit(1); } while (0)