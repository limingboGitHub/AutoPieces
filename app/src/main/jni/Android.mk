LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := MoveMethod

LOCAL_SRC_FILES := MoveMethod.cpp

#LOCAL_C_INCLUDES += ${NDKROOT}/sources/cxx-stl/llvm-libc++/include

include $(BUILD_SHARED_LIBRARY)