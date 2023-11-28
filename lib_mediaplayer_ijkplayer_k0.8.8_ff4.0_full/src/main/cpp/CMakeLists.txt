cmake_minimum_required(VERSION 3.4.1)

add_definitions(-DNDEBUG)

set(common_sources
        ijkmedia/ijkj4a/j4a/j4a_allclasses.c
        ijkmedia/ijkj4a/j4a/j4a_base.c
        ijkmedia/ijkj4a/j4a/class/android/media/AudioTrack.c
        ijkmedia/ijkj4a/j4a/class/android/media/MediaCodec.c
        ijkmedia/ijkj4a/j4a/class/android/media/MediaFormat.c
        ijkmedia/ijkj4a/j4a/class/android/media/PlaybackParams.c
        ijkmedia/ijkj4a/j4a/class/android/os/Build.c
        ijkmedia/ijkj4a/j4a/class/android/os/Bundle.c
        ijkmedia/ijkj4a/j4a/class/java/nio/Buffer.c
        ijkmedia/ijkj4a/j4a/class/java/nio/ByteBuffer.c
        ijkmedia/ijkj4a/j4a/class/java/util/ArrayList.c
        ijkmedia/ijkj4a/j4a/class/tv/danmaku/ijk/media/player/misc/IMediaDataSource.c
        ijkmedia/ijkj4a/j4a/class/tv/danmaku/ijk/media/player/misc/IAndroidIO.c
        ijkmedia/ijkj4a/j4a/class/tv/danmaku/ijk/media/player/IjkMediaPlayer.c
        ijkmedia/ijkj4a/j4au/class/android/media/AudioTrack.util.c
        ijkmedia/ijkj4a/j4au/class/java/nio/ByteBuffer.util.c

        #ijkmedia/ijksdl/
        ijkmedia/ijksdl/ijksdl_aout.c
        ijkmedia/ijksdl/ijksdl_audio.c
        ijkmedia/ijksdl/ijksdl_egl.c
        ijkmedia/ijksdl/ijksdl_error.c
        ijkmedia/ijksdl/ijksdl_mutex.c
        ijkmedia/ijksdl/ijksdl_stdinc.c
        ijkmedia/ijksdl/ijksdl_thread.c
        ijkmedia/ijksdl/ijksdl_timer.c
        ijkmedia/ijksdl/ijksdl_vout.c
        ijkmedia/ijksdl/ijksdl_extra_log.c
        ijkmedia/ijksdl/gles2/color.c
        ijkmedia/ijksdl/gles2/common.c
        ijkmedia/ijksdl/gles2/renderer.c
        ijkmedia/ijksdl/gles2/renderer_rgb.c
        ijkmedia/ijksdl/gles2/renderer_yuv420p.c
        ijkmedia/ijksdl/gles2/renderer_yuv444p10le.c
        ijkmedia/ijksdl/gles2/shader.c
        ijkmedia/ijksdl/gles2/fsh/rgb.fsh.c
        ijkmedia/ijksdl/gles2/fsh/yuv420p.fsh.c
        ijkmedia/ijksdl/gles2/fsh/yuv444p10le.fsh.c
        ijkmedia/ijksdl/gles2/vsh/mvp.vsh.c
        ijkmedia/ijksdl/dummy/ijksdl_vout_dummy.c
        ijkmedia/ijksdl/ffmpeg/ijksdl_vout_overlay_ffmpeg.c
        ijkmedia/ijksdl/ffmpeg/abi_all/image_convert.c
        ijkmedia/ijksdl/android/android_audiotrack.c
        ijkmedia/ijksdl/android/android_nativewindow.c
        ijkmedia/ijksdl/android/ijksdl_android_jni.c
        ijkmedia/ijksdl/android/ijksdl_aout_android_audiotrack.c
        ijkmedia/ijksdl/android/ijksdl_aout_android_opensles.c
        ijkmedia/ijksdl/android/ijksdl_codec_android_mediacodec_dummy.c
        ijkmedia/ijksdl/android/ijksdl_codec_android_mediacodec_internal.c
        ijkmedia/ijksdl/android/ijksdl_codec_android_mediacodec_java.c
        ijkmedia/ijksdl/android/ijksdl_codec_android_mediacodec.c
        ijkmedia/ijksdl/android/ijksdl_codec_android_mediadef.c
        ijkmedia/ijksdl/android/ijksdl_codec_android_mediaformat_java.c
        ijkmedia/ijksdl/android/ijksdl_codec_android_mediaformat.c
        ijkmedia/ijksdl/android/ijksdl_vout_android_nativewindow.c
        ijkmedia/ijksdl/android/ijksdl_vout_android_surface.c
        ijkmedia/ijksdl/android/ijksdl_vout_overlay_android_mediacodec.c

        #ijkmedia/ijksoundtouch/
        ijkmedia/ijksoundtouch/source/SoundTouch/AAFilter.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/FIFOSampleBuffer.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/FIRFilter.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/cpu_detect_x86.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/sse_optimized.cpp
        ijkmedia/ijksoundtouch/source/SoundStretch/WavFile.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/RateTransposer.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/SoundTouch.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/InterpolateCubic.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/InterpolateLinear.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/InterpolateShannon.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/TDStretch.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/BPMDetect.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/PeakFinder.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/SoundTouch.cpp
        ijkmedia/ijksoundtouch/source/SoundTouch/mmx_optimized.cpp
        ijkmedia/ijksoundtouch/ijksoundtouch_wrap.cpp

        #ijkmedia/ijkyuv
        ijkmedia/ijkyuv/source/compare.cc
        ijkmedia/ijkyuv/source/compare_common.cc
        ijkmedia/ijkyuv/source/compare_posix.cc
        ijkmedia/ijkyuv/source/convert.cc
        ijkmedia/ijkyuv/source/convert_argb.cc
        ijkmedia/ijkyuv/source/convert_from.cc
        ijkmedia/ijkyuv/source/convert_from_argb.cc
        ijkmedia/ijkyuv/source/convert_to_argb.cc
        ijkmedia/ijkyuv/source/convert_to_i420.cc
        ijkmedia/ijkyuv/source/cpu_id.cc
        ijkmedia/ijkyuv/source/format_conversion.cc
        ijkmedia/ijkyuv/source/planar_functions.cc
        ijkmedia/ijkyuv/source/rotate.cc
        ijkmedia/ijkyuv/source/rotate_argb.cc
        ijkmedia/ijkyuv/source/rotate_mips.cc
        ijkmedia/ijkyuv/source/row_any.cc
        ijkmedia/ijkyuv/source/row_common.cc
        ijkmedia/ijkyuv/source/row_mips.cc
        ijkmedia/ijkyuv/source/row_posix.cc
        ijkmedia/ijkyuv/source/scale.cc
        ijkmedia/ijkyuv/source/scale_argb.cc
        ijkmedia/ijkyuv/source/scale_common.cc
        ijkmedia/ijkyuv/source/scale_mips.cc
        ijkmedia/ijkyuv/source/scale_posix.cc
        ijkmedia/ijkyuv/source/video_common.cc

        #ijkplayer
        ijkmedia/ijkplayer/ff_cmdutils.c
        ijkmedia/ijkplayer/ff_ffplay.c
        ijkmedia/ijkplayer/ff_ffpipeline.c
        ijkmedia/ijkplayer/ff_ffpipenode.c
        ijkmedia/ijkplayer/ijkmeta.c
        ijkmedia/ijkplayer/ijkplayer.c
        ijkmedia/ijkplayer/pipeline/ffpipeline_ffplay.c
        ijkmedia/ijkplayer/pipeline/ffpipenode_ffplay_vdec.c
        ijkmedia/ijkplayer/android/ijkplayer_android.c
        ijkmedia/ijkplayer/android/ijkplayer_jni.c
        ijkmedia/ijkplayer/android/pipeline/ffpipeline_android.c
        ijkmedia/ijkplayer/android/pipeline/ffpipenode_android_mediacodec_vdec.c
        ijkmedia/ijkplayer/ijkavformat/allformats.c
        ijkmedia/ijkplayer/ijkavformat/ijklivehook.c
        ijkmedia/ijkplayer/ijkavformat/ijkmediadatasource.c
        ijkmedia/ijkplayer/ijkavformat/ijkio.c
        ijkmedia/ijkplayer/ijkavformat/ijkiomanager.c
        ijkmedia/ijkplayer/ijkavformat/ijkiocache.c
        ijkmedia/ijkplayer/ijkavformat/ijkioffio.c
        ijkmedia/ijkplayer/ijkavformat/ijkioandroidio.c
        ijkmedia/ijkplayer/ijkavformat/ijkioprotocol.c
        ijkmedia/ijkplayer/ijkavformat/ijkioapplication.c
        ijkmedia/ijkplayer/ijkavformat/ijkiourlhook.c
        ijkmedia/ijkplayer/ijkavformat/ijkasync.c
        ijkmedia/ijkplayer/ijkavformat/ijkurlhook.c
        ijkmedia/ijkplayer/ijkavformat/ijklongurl.c
        ijkmedia/ijkplayer/ijkavformat/ijksegment.c
        ijkmedia/ijkplayer/ijkavutil/ijkdict.c
        ijkmedia/ijkplayer/ijkavutil/ijkutils.c
        ijkmedia/ijkplayer/ijkavutil/ijkthreadpool.c
        ijkmedia/ijkplayer/ijkavutil/ijktree.c
        ijkmedia/ijkplayer/ijkavutil/ijkfifo.c
        ijkmedia/ijkplayer/ijkavutil/ijkstl.cpp

        #ijkprof
        ijkprof/prof.c

        #ijklog
        ijkmedia/ijklog/log_base.c
        )

set(armeabi_v7a_sources
        ijkmedia/ijkyuv/source/compare_neon.cc
        ijkmedia/ijkyuv/source/rotate_neon.cc
        ijkmedia/ijkyuv/source/row_neon.cc
        ijkmedia/ijkyuv/source/scale_neon.cc
        )

set(arm64_v8a_sources
        ijkmedia/ijkyuv/source/compare_neon64.cc
        ijkmedia/ijkyuv/source/rotate_neon64.cc
        ijkmedia/ijkyuv/source/row_neon64.cc
        ijkmedia/ijkyuv/source/scale_neon64.cc
        )

if (${ANDROID_ABI} STREQUAL armeabi-v7a)
    add_library( # Sets the name of the library.
            ijkplayer
            SHARED
            ${common_sources}
            ${armeabi_v7a_sources}
            )
else ()
    add_library( # Sets the name of the library.
            ijkplayer
            SHARED
            ${common_sources}
            ${arm64_v8a_sources}
            )
endif ()


# ffmpeg-avcodec-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libavcodec.a ${Project_BINARY_DIR}/libavcodec.a COPYONLY)
add_library(ffmpeg-avcodec-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avcodec-lib
        PROPERTIES IMPORTED_LOCATION
        libavcodec.a)

# ffmpeg-avformat-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libavformat.a ${Project_BINARY_DIR}/libavformat.a COPYONLY)
add_library(ffmpeg-avformat-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avformat-lib
        PROPERTIES IMPORTED_LOCATION
        libavformat.a)

# ffmpeg-avutil-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libavutil.a ${Project_BINARY_DIR}/libavutil.a COPYONLY)
add_library(ffmpeg-avutil-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avutil-lib
        PROPERTIES IMPORTED_LOCATION
        libavutil.a)

# ffmpeg-swresample-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libswresample.a ${Project_BINARY_DIR}/libswresample.a COPYONLY)
add_library(ffmpeg-swresample-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-swresample-lib
        PROPERTIES IMPORTED_LOCATION
        libswresample.a)

# ffmpeg-swscale-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg4/${ANDROID_ABI}/lib/libswscale.a ${Project_BINARY_DIR}/libswscale.a COPYONLY)
add_library(ffmpeg-swscale-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-swscale-lib
        PROPERTIES IMPORTED_LOCATION
        libswscale.a)

# open-ssl-lib
configure_file(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/lib/libssl.a ${Project_BINARY_DIR}/libssl.a COPYONLY)
add_library(open-ssl-lib STATIC IMPORTED)
set_target_properties(
        open-ssl-lib
        PROPERTIES IMPORTED_LOCATION
        libssl.a)

# open-crypto-lib
configure_file(${CMAKE_SOURCE_DIR}/openssl/${ANDROID_ABI}/lib/libcrypto.a ${Project_BINARY_DIR}/libcrypto.a COPYONLY)
add_library(open-crypto-lib
        STATIC
        IMPORTED)
set_target_properties(
        open-crypto-lib
        PROPERTIES IMPORTED_LOCATION
        libcrypto.a)

include_directories(
        ffmpeg4/${ANDROID_ABI}/include
        openssl/${ANDROID_ABI}/include
        ijkmedia
        ijkmedia/ijkplayer
        ijkmedia/ijklog
        ijkmedia/ijkj4a
        ijkmedia/ijksdl
        ijkmedia/ijksoundtouch
        ijkmedia/ijksoundtouch/include
        ijkmedia/ijkyuv
        ijkmedia/ijkyuv/include
)

target_link_libraries(
        # Specifies the target library.
        ijkplayer

        # ffmpeg
        ffmpeg-avcodec-lib
        ffmpeg-avformat-lib
        ffmpeg-avutil-lib
        ffmpeg-swresample-lib
        ffmpeg-swscale-lib

        # openssl
        open-ssl-lib
        open-crypto-lib

        # Links the target library to the log library
        log
        m
        z
        android
        EGL
        jnigraphics
        GLESv2
        OpenSLES)