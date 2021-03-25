LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := MoveMethod
LOCAL_SRC_FILES := movemethod.c

include $(BUILD_SHARED_LIBRARY)