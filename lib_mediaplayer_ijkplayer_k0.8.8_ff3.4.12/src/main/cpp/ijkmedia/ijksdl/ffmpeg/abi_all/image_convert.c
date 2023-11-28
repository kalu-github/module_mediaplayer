/*****************************************************************************
 * yuv_rgb.c : ARM NEONv1 YUV to RGB32 chroma conversion for VLC
 *****************************************************************************
 * Copyright (C) 2011 Bilibili
 * Copyright (C) 2011 Sébastien Toque
 *                    Rémi Denis-Courmont
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

#include "../ijksdl_image_convert.h"

#if defined(__ANDROID__)
#include "libyuv.h"
#endif

int ijk_image_convert(int width, int height,
                      enum AVPixelFormat dst_format, uint8_t **dst_data, int *dst_linesize,
                      enum AVPixelFormat src_format, const uint8_t **src_data,
                      const int *src_linesize) {
    int ret = -2;
#if defined(__ANDROID__)
    switch (src_format) {
        case AV_PIX_FMT_NV12:{
            switch (dst_format) {
            case AV_PIX_FMT_RGB565:
                ret = NV12ToRGB565(
                    src_data[0], src_linesize[0],
                    src_data[1], src_linesize[1],
                    dst_data[0], dst_linesize[0],
                    width, height);
                break;
            case AV_PIX_FMT_0BGR32:
                ret = NV12ToARGB(
                    src_data[0], src_linesize[0],
                    src_data[1], src_linesize[1],
                    dst_data[0], dst_linesize[0],
                    width, height);
                if(ret == 0){
                    ret = ARGBToABGR(dst_data[0], dst_linesize[0],dst_data[0], dst_linesize[0],width, height);
                } 
                break;       
            default:
                break;
            }
            break;
        }
        case AV_PIX_FMT_YUV420P:
        case AV_PIX_FMT_YUVJ420P: // FIXME: 9 not equal to AV_PIX_FMT_YUV420P, but a workaround
            switch (dst_format) {
            case AV_PIX_FMT_RGB565:
                ret = I420ToRGB565(
                    src_data[0], src_linesize[0],
                    src_data[1], src_linesize[1],
                    src_data[2], src_linesize[2],
                    dst_data[0], dst_linesize[0],
                    width, height);
                break;
            case AV_PIX_FMT_0BGR32:
                ret = I420ToABGR(
                    src_data[0], src_linesize[0],
                    src_data[1], src_linesize[1],
                    src_data[2], src_linesize[2],
                    dst_data[0], dst_linesize[0],
                    width, height);
                break;
            case AV_PIX_FMT_RGB24 :
                ret = I420ToRGB24(
                    src_data[0], src_linesize[0],
                    src_data[1], src_linesize[1],
                    src_data[2], src_linesize[2],
                    dst_data[0], dst_linesize[0],
                    width, height); 
                break;        
            default:
                break;
            }
            break;
        default:
            break;
    }
#endif

    if (ret == 0) {
        return 0;
    }

    if (ret == -2) {
        SDL_ALOGE("ijk_image_convert can not convert %s(%d) -> %s(%d)",
                  av_get_pix_fmt_name(src_format),
                  (int) src_format,
                  av_get_pix_fmt_name(dst_format),
                  (int) dst_format);
    } else if (ret == -1) {
        SDL_ALOGE("ijk_image_convert convert failed %s(%d) -> %s(%d)",
                  av_get_pix_fmt_name(src_format),
                  (int) src_format,
                  av_get_pix_fmt_name(dst_format),
                  (int) dst_format);
    }
    return -1;
}

