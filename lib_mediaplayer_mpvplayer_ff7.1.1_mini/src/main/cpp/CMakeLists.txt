cmake_minimum_required(VERSION 3.18.1)
project(mpvplayer LANGUAGES C CXX)

# Dependencies are installed by the native build tools. Never link the legacy
# FFmpeg 3.4 static archives, cpp/player, or SDL2 into this target.
set(MPV_LAYOUT "merged" CACHE STRING "Native layout: merged or split")
if(MPV_LAYOUT STREQUAL "merged")
    set(MPV_DEPS "${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}")
    if(NOT EXISTS "${MPV_DEPS}/manifest.json" OR NOT EXISTS "${MPV_DEPS}/link.cmake")
        message(FATAL_ERROR "Run tools/build_merged_native.py --abi ${ANDROID_ABI} --ndk <NDK path> first.")
    endif()
    if(NOT ANDROID_STL STREQUAL "c++_static")
        message(FATAL_ERROR "The two-library layout requires -DANDROID_STL=c++_static")
    endif()
    include("${MPV_DEPS}/link.cmake")
    add_library(ffmpeg SHARED mpv/ffmpeg_anchor.c)
    target_link_options(ffmpeg PRIVATE
        "-Wl,-z,max-page-size=16384" "-Wl,--no-undefined"
        "-Wl,--version-script=${MPV_DEPS}/ffmpeg.exports"
        "@${MPV_DEPS}/ffmpeg-roots.rsp")
    set_property(TARGET ffmpeg APPEND PROPERTY LINK_DEPENDS
        "${MPV_DEPS}/ffmpeg.exports" "${MPV_DEPS}/ffmpeg-roots.rsp")
    target_link_libraries(ffmpeg PRIVATE
        "-Wl,--start-group" ${FFMPEG_STATIC_ARCHIVES} "-Wl,--end-group"
        ${FFMPEG_SYSTEM_LINK_FLAGS})
elseif(MPV_LAYOUT STREQUAL "split")
    set(MPV_DEPS "${CMAKE_CURRENT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/mpv")
    if(NOT EXISTS "${MPV_DEPS}/lib/libmpv.so" OR NOT EXISTS "${MPV_DEPS}/manifest.json")
        message(FATAL_ERROR
            "mpv dependencies missing for ${ANDROID_ABI}. Run tools/build_native.py --abi ${ANDROID_ABI} --ndk <NDK path> first.")
    endif()
    foreach(LIB mpv avcodec)
        add_library(${LIB} SHARED IMPORTED)
        set_target_properties(${LIB} PROPERTIES IMPORTED_LOCATION "${MPV_DEPS}/lib/lib${LIB}.so")
    endforeach()
else()
    message(FATAL_ERROR "Unknown MPV_LAYOUT: ${MPV_LAYOUT}")
endif()
add_library(mpvplayer SHARED mpv/mpv_jni.cpp)
target_compile_features(mpvplayer PRIVATE cxx_std_17)
target_compile_options(mpvplayer PRIVATE -Wall -Wextra -Werror)
target_compile_definitions(mpvplayer PRIVATE _FILE_OFFSET_BITS=64)
target_include_directories(mpvplayer PRIVATE "${MPV_DEPS}/include")
target_link_options(mpvplayer PRIVATE "-Wl,-z,max-page-size=16384" "-Wl,--no-undefined")
if(MPV_LAYOUT STREQUAL "merged")
    target_link_options(mpvplayer PRIVATE
        "-Wl,--version-script=${CMAKE_CURRENT_SOURCE_DIR}/mpv/mpvplayer.exports")
    set_property(TARGET mpvplayer APPEND PROPERTY LINK_DEPENDS
        "${CMAKE_CURRENT_SOURCE_DIR}/mpv/mpvplayer.exports")
    # Keep all mpv client entry points, resolving its static dependency cycles as a group.
    list(REMOVE_ITEM MPV_STATIC_ARCHIVES "${MPV_DEPS}/lib/libmpv.a")
    target_link_libraries(mpvplayer PRIVATE
        "-Wl,--whole-archive" "${MPV_DEPS}/lib/libmpv.a" "-Wl,--no-whole-archive"
        "-Wl,--start-group" ${MPV_STATIC_ARCHIVES} ffmpeg "-Wl,--end-group"
        ${MPV_SYSTEM_LINK_FLAGS} android log)
else()
    target_link_libraries(mpvplayer PRIVATE mpv avcodec android log)
endif()
