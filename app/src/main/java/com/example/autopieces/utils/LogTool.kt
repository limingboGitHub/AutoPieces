package com.example.autopieces.utils

import android.util.Log
import com.example.autopieces.BuildConfig

fun logD(tag:String,msg:String){
    if (!BuildConfig.DEBUG)
        return
    Log.d(tag, msg)
}

fun logE(tag:String,msg:String){
    if (!BuildConfig.DEBUG)
        return
    Log.e(tag, msg)
}