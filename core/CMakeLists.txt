cmake_minimum_required(VERSION 3.16)
project(YumeMowCore)

set(CMAKE_CXX_STANDARD 23)

# 查找 JDK 获取 JNI 头文件
find_path(JNI_H_PATH jni.h
    PATHS
        "$ENV{JAVA_HOME}/include"
        "$ENV{JAVA_HOME}/../include"
        "[HKEY_LOCAL_MACHINE\\SOFTWARE\\JavaSoft\\Java Development Kit\\1.8;JavaHome]/include"
    PATH_SUFFIXES win32 linux darwin
)

if(NOT JNI_H_PATH)
    message(WARNING "JNI headers not found. JNI functions will be disabled.")
else()
    message(STATUS "Found JNI headers: ${JNI_H_PATH}")
    include_directories(${JNI_H_PATH})

    if(WIN32)
        include_directories(${JNI_H_PATH}/win32)
    elseif(UNIX AND NOT APPLE)
        include_directories(${JNI_H_PATH}/linux)
    elseif(APPLE)
        include_directories(${JNI_H_PATH}/darwin)
    endif()

    add_compile_definitions(ENABLE_JNI=1)
endif()

find_package(Qt6 REQUIRED COMPONENTS Core)

# JNI 共享库 (DLL)
add_library(FolderIconSetter SHARED
    src/FolderIconSetter.cpp
    src/FolderIconJni.cpp
    src/FileDialog.cpp
)

target_include_directories(FolderIconSetter PUBLIC include)
target_link_libraries(FolderIconSetter PRIVATE Qt6::Core)

if(WIN32)
    target_link_libraries(FolderIconSetter PRIVATE shell32)
    target_compile_definitions(FolderIconSetter PRIVATE FOLDERICONSETTER_EXPORTS)
endif()

set_target_properties(FolderIconSetter PROPERTIES
    OUTPUT_NAME "yumemow_core"
    PREFIX ""
)

# 命令行工具
add_executable(YumeMowCLI
    src/main.cpp
    src/FolderIconSetter.cpp
)

target_include_directories(YumeMowCLI PRIVATE include)
target_link_libraries(YumeMowCLI PRIVATE Qt6::Core)

if(WIN32)
    target_link_libraries(YumeMowCLI PRIVATE shell32)
endif()
