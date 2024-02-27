# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.10.0)

project(mmkv_jvm)

IF (APPLE)
    add_compile_definitions(FORCE_POSIX)
ENDIF ()

add_subdirectory(../MMKV/Core Core)

set(JAVA_HOME $ENV{JAVA_HOME})
message("JAVA_HOME is set to: ${JAVA_HOME}")
set(JAVA_INCLUDE_PATH ${JAVA_HOME}/include)
set(JAVA_INCLUDE_PATH2 ${JAVA_HOME}/include/win32)  # Adjust for your platform
#set(JAVA_INCLUDE_PATH2 ${JAVA_HOME}/include/linux)  # Adjust for your platform
include_directories(${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2})

include_directories("../build/generated/sources/headers/java/main")

add_library( # Sets the name of the library.
        mmkv

        # Sets the library as a python module library.
        SHARED
#        STATIC

        # Provides a relative path to your source file(s).
#        cpp/icu_twtool_chat_library_mmkv_MMKV.h
        ../build/generated/sources/headers/java/main/icu_twtool_mmkv_MMKV.h
        icu_twtool_mmkv_MMKV.cpp
)

#target_include_directories(mmkv PUBLIC
#        ${CMAKE_CURRENT_SOURCE_DIR})

set_target_properties(mmkv PROPERTIES
        CXX_STANDARD 17
        CXX_EXTENSIONS OFF
)

target_link_libraries( # Specifies the target library.
        #mmkv PRIVATE
        mmkv
        core
#        pthread
)
