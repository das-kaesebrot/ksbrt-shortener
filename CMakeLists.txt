cmake_minimum_required (VERSION 3.8)

project("ksbrt-shortener" CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_EXTENSIONS OFF)

include(${CMAKE_CURRENT_SOURCE_DIR}/build/conanbuildinfo.cmake)
conan_basic_setup()

file(GLOB src
     "*.h"
     "*.cpp"
)

add_executable(${PROJECT_NAME} ${src})