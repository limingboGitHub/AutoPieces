//
// Created by Lenovo on 2021/3/25.
//
#include "com_example_autopieces_cpp_MoveMethod.h"

JNIEXPORT jintArray JNICALL Java_com_example_autopieces_cpp_MoveMethod_calculateMovePath
  (JNIEnv *env, jclass obj, jint startX, jint startY, jint endX, jint endY){

    int a[4];
    a[0] = 0;
    a[1] = 1;
    a[2] = 2;
    a[3] = 3;

    jintArray movePath = (*env)->NewIntArray(env,4);

    (*env)->SetIntArrayRegion(env,movePath,0,4,a);

    return movePath;
}

