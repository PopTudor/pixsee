cmake_minimum_required(VERSION 3.6)

set(CMAKE_VERBOSE_MAKEFILE on)
set(libs "${CMAKE_SOURCE_DIR}/../jniLibs")

# jniLibs opencv libraries should be moved to openCVLibrary310 module but I'm not sure if
# the includes will work after that
#--------------------------------------------------- OpenCV ---------------------------------------------------#
include_directories(include)

# this library is missing from some ABIs but if you need it, uncomment the lines
# and add libopencv_ts to target_link_libraries below
#add_library(libopencv_ts STATIC IMPORTED )
#set_target_properties(libopencv_ts PROPERTIES
#    IMPORTED_LOCATION "${libs}/${ANDROID_ABI}/libopencv_ts.a")

########################### Detection Based Tracker ###########################
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -fexceptions -frtti")
add_library(detectionBasedTracker SHARED DetectionBasedTracker_jni.cpp)

# add lib dependencies
target_link_libraries(detectionBasedTracker android log)
