cmake_minimum_required(VERSION 3.4.1)
add_definitions(-DNDEBUG)
add_definitions(-DNO_CRYPTO)

add_library( # Sets the name of the library.
        exoplayer2-rtmp

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        rtmpmuxer.c
        librtmp-jni.h
        librtmp-jni.c
        librtmp/amf.c
        librtmp/amf.h
        librtmp/bytes.h
        librtmp/dh.h
        librtmp/dhgroups.h
        librtmp/handshake.h
        librtmp/hashswf.c
        librtmp/http.h
        librtmp/log.c
        librtmp/log.h
        librtmp/parseurl.c
        librtmp/rtmp.c
        librtmp/rtmp.h
        librtmp/rtmp_sys.h
        flvmuxer/xiecc_rtmp.c
        flvmuxer/xiecc_rtmp.h)

include_directories(librtmp)
include_directories(flvmuxer)


target_link_libraries(
        # Specifies the target library.
        exoplayer2-rtmp

        # Links the target library to the log library
        # included in the NDK.
        log)