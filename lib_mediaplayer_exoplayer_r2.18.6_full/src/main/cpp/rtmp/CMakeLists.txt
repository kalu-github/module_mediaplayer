# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
add_definitions(-DNDEBUG)


add_library( # Sets the name of the library.
        exoplayer-rtmp

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

add_definitions(-DNO_CRYPTO)


target_link_libraries( # Specifies the target library.
        exoplayer-rtmp

        # Links the target library to the log library
        # included in the NDK.
        log)