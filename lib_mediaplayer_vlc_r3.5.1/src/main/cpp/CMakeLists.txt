cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
add_definitions(-DNDEBUG)

include_directories(
        ${CMAKE_SOURCE_DIR}/include/
)

#set(CMAKE_C_STANDARD 99)
#添加动态库，包含源文件路径
#传递三个参数，第一个参数是模块名称，第二个参数是SHARED或者STATIC。如果是源文件模块，第三个参数是源文件列表；
#如果是第三方库，第三个参数是IMPORTED。
add_library(vlc-record
        SHARED
        ${CMAKE_SOURCE_DIR}/yyl_record.c
        ${CMAKE_SOURCE_DIR}/yyl_log.c
        #   src/main/cpp/yyl_fixsurface.c
        )

#libc++_shared.so
configure_file(${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libc++_shared.so ${Project_BINARY_DIR}/libc++_shared.so COPYONLY)
add_library(c++_shared-lib
        SHARED
        IMPORTED)
set_target_properties(c++_shared-lib
        PROPERTIES IMPORTED_LOCATION
        libc++_shared.so)
#libvlc.so
configure_file(${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libvlc.so ${Project_BINARY_DIR}/libvlc.so COPYONLY)
add_library(vlc-lib
        SHARED
        IMPORTED)
set_target_properties(vlc-lib
        PROPERTIES IMPORTED_LOCATION
        libvlc.so)

#libvlcjni.so
configure_file(${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI}/libvlcjni.so ${Project_BINARY_DIR}/libvlcjni.so COPYONLY)
add_library(vlcjni-lib
        SHARED
        IMPORTED)
set_target_properties(vlcjni-lib
        PROPERTIES IMPORTED_LOCATION
        libvlcjni.so)

target_link_libraries(vlc-record
        android
        log
        vlc-lib
        vlcjni-lib
        c++_shared-lib
        )
