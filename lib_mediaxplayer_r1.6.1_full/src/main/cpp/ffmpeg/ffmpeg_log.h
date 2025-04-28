/*
 * copyright (c) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * This file is part of jni4android.
 *
 * jni4android is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * jni4android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with jni4android; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#ifndef LOG_BASE_H
#define LOG_BASE_H

#include <stdio.h>
#include <stdarg.h>
#include <jni.h>
#include <android/log.h>

//日志开关，1为开，其它为关
void _exo_logger_enable(jboolean enable);

//void _exo_log_print(int prio, const char *tag, const char *fmt, va_list ap);

void _exo_logger_print(int prio, const char *tag, const char *fmt, ...);

#endif//LOG_BASE_INTERNAL_H
