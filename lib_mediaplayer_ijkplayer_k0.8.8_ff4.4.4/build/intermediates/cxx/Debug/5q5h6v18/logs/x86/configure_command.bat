@echo off
"C:\\Users\\kalu\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HD:\\Android\\Huan_Component\\module_mediaplayer\\lib_mediaplayer_ijkplayer_k0.8.8_ff4.4.4\\src\\main\\cpp" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=19" ^
  "-DANDROID_PLATFORM=android-19" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\kalu\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\kalu\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\kalu\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\kalu\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_CXX_FLAGS=-std=c++11 -Wl,-Bsymbolic" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=D:\\Android\\Huan_Component\\module_mediaplayer\\lib_mediaplayer_ijkplayer_k0.8.8_ff4.4.4\\build\\intermediates\\cxx\\Debug\\5q5h6v18\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=D:\\Android\\Huan_Component\\module_mediaplayer\\lib_mediaplayer_ijkplayer_k0.8.8_ff4.4.4\\build\\intermediates\\cxx\\Debug\\5q5h6v18\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BD:\\Android\\Huan_Component\\module_mediaplayer\\lib_mediaplayer_ijkplayer_k0.8.8_ff4.4.4\\.cxx\\Debug\\5q5h6v18\\x86" ^
  -GNinja
