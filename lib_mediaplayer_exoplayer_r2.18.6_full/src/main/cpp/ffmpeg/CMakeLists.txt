#
# Copyright 2021 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
add_definitions(-DNDEBUG)

## Enable C++11 features.
set(CMAKE_CXX_STANDARD 11)
#
project(libexoplayer-ffmpeg-jni C CXX)
##
### Additional flags needed for "arm64-v8a" from NDK 23.1.7779620 and above.
### See https://github.com/google/ExoPlayer/issues/9933#issuecomment-1029775358.
if (${ANDROID_ABI} MATCHES "arm64-v8a")
    set(CMAKE_CXX_FLAGS "-Wl,-Bsymbolic")
endif ()

include_directories(
        ffmpeg_log.h
        include
        include/config/${ANDROID_ABI}
)

#ffmpeg
configure_file(${CMAKE_SOURCE_DIR}/ffmpeg/jniLibs/${ANDROID_ABI}/libexoplayer-ffmpeg.so ${Project_BINARY_DIR}/libexoplayer-ffmpeg.so COPYONLY)
add_library(ffmpeg-lib
        SHARED
        IMPORTED)
set_target_properties(ffmpeg-lib
        PROPERTIES IMPORTED_LOCATION
        libexoplayer-ffmpeg.so)

add_library(exoplayer-ffmpeg-jni
        SHARED
        ffmpeg_log.c
        ffmpeg_jni.cc)

target_link_libraries( # Specifies the target library.
        exoplayer-ffmpeg-jni

        # Links the target library to the log library
        # included in the NDK.
        ffmpeg-lib
        log
        android)
