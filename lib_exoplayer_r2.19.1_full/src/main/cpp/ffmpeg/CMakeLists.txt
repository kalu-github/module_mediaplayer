cmake_minimum_required(VERSION 3.4.1)
add_definitions(-DNDEBUG)


# ffmpeg-avcodec-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/ffmpeg3/${ANDROID_ABI}/lib/libavcodec.a ${Project_BINARY_DIR}/libavcodec.a COPYONLY)
add_library(ffmpeg-avcodec-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avcodec-lib
        PROPERTIES IMPORTED_LOCATION
        libavcodec.a)

# ffmpeg-avformat-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/ffmpeg3/${ANDROID_ABI}/lib/libavformat.a ${Project_BINARY_DIR}/libavformat.a COPYONLY)
add_library(ffmpeg-avformat-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avformat-lib
        PROPERTIES IMPORTED_LOCATION
        libavformat.a)

# ffmpeg-avutil-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/ffmpeg3/${ANDROID_ABI}/lib/libavutil.a ${Project_BINARY_DIR}/libavutil.a COPYONLY)
add_library(ffmpeg-avutil-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-avutil-lib
        PROPERTIES IMPORTED_LOCATION
        libavutil.a)

# ffmpeg-swresample-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/ffmpeg3/${ANDROID_ABI}/lib/libswresample.a ${Project_BINARY_DIR}/libswresample.a COPYONLY)
add_library(ffmpeg-swresample-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-swresample-lib
        PROPERTIES IMPORTED_LOCATION
        libswresample.a)

# ffmpeg-swscale-lib
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/ffmpeg3/${ANDROID_ABI}/lib/libswscale.a ${Project_BINARY_DIR}/libswscale.a COPYONLY)
add_library(ffmpeg-swscale-lib
        STATIC
        IMPORTED)
set_target_properties(ffmpeg-swscale-lib
        PROPERTIES IMPORTED_LOCATION
        libswscale.a)

include_directories(
        ffmpeg3/${ANDROID_ABI}/include
        logging.h
)

add_library(
        exoplayer2-ffmpeg
        SHARED
        logging.c
        ffmpeg_jni.cc)

target_link_libraries(
        exoplayer2-ffmpeg

        # ffmpeg
        ffmpeg-avcodec-lib
        ffmpeg-avformat-lib
        ffmpeg-avutil-lib
        ffmpeg-swresample-lib
        ffmpeg-swscale-lib

        m
        z
        dl
        OpenSLES
        GLESv1_CM
        GLESv2
        jnigraphics
        log
        android)