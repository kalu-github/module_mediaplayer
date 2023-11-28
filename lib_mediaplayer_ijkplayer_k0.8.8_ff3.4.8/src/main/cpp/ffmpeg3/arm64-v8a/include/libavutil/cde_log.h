/*#########################################################################
# * @author     zhouwg2000@gmail.com
# * @date    	2015-10  ~ present
# * @note
# * @history
# *             2015-10, create
#
#	Copyright (C) zhou.weiguo, 2015-2021  All rights reserved.
#
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# *
# *      http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
#
#########################################################################
*/

#ifndef _CDE_LOG_H_
#define _CDE_LOG_H_

#ifndef __KERNEL__

#include <stdio.h>
#include <stdarg.h>
#include <unistd.h>

#endif

#ifdef __cplusplus
extern "C" {
#endif

/*
 *fetch from AOSP, Android log priority values, in ascending priority order.
 */
typedef enum cde_android_LogPriority {
    CDE_LOG_UNKNOWN = 0,
    CDE_LOG_DEFAULT,    /* only for setGlobalLogLevel */
    CDE_LOG_VERBOSE,
    CDE_LOG_DEBUG,
    CDE_LOG_INFO,
    CDE_LOG_WARN,
    CDE_LOG_ERROR,
    CDE_LOG_FATAL,
    CDE_LOG_SILENT,     /* only for setGlobalLogLevel() */
} cde_android_LogPriority;


#ifndef LOG_NDEBUG
#ifdef NDEBUG
#define LOG_NDEBUG 1
#else
#define LOG_NDEBUG 0
#endif
#endif

#ifndef LOG_TAG
#define LOG_TAG NULL
#endif


#ifdef __GNUC__
#define DO_NOT_INSTRUMENT __attribute__ ((no_instrument_function))
#else
#define DO_NOT_INSTRUMENT
#endif


#if LOG_NDEBUG

#define LOG_PRI(file, func, line, priority, tag, ...) ((void)0)

#else

#define LOG_PRI(file, func, line, priority, tag, ...) \
           LOG_PRI_ORIG_IMPL(file, func, line, priority, tag, __VA_ARGS__)

#endif /*LOG_NDEBUG*/


#ifdef __KERNEL__
asmlinkage  void  LOG_PRI_ORIG_IMPL(const char *file, const char *func, unsigned int line,  int priority, const char *tag, const char *format,...);
#else

void LOG_PRI_ORIG_IMPL(const char *file, const char *func, unsigned int line, int priority,
                       const char *tag, const char *format, ...);

#endif

extern void setGlobalLogLevel(int);

extern int validateLogLevel(int);

void setGlobalLogEnabled(int bEnable);

void setGlobalLogModule(const char *moduleName, int bEnabled);


#ifndef LOGV
#define LOGV(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOG_VERBOSE(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#endif

#ifndef LOGD
#define LOGD(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOG_DEBUG(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#endif

#ifndef LOGI
#define LOGI(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOG_INFO(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_INFO, LOG_TAG, __VA_ARGS__)
#endif

#ifndef LOGW
#define LOGW(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOG_WARN(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_WARN, LOG_TAG, __VA_ARGS__)
#endif

#ifndef LOGE
#define LOGE(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOG_ERROR(...) LOG_PRI(__FILE__, __FUNCTION__, __LINE__, CDE_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif


#ifdef __cplusplus
}
#endif

#endif //_CDE_LOG_H_
