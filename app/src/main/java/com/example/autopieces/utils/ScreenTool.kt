package com.example.autopieces.utils

import android.content.Context
import com.example.autopieces.MyApplication

fun dp2px(dp:Int) = MyApplication.context.resources.displayMetrics.density * dp

fun getDensity():Float{
    return MyApplication.context.resources.displayMetrics.density
}

fun getScreenWidth():Int{
    return MyApplication.context.resources.displayMetrics.widthPixels
}

fun getScreenHeight():Int{
    return MyApplication.context.resources.displayMetrics.heightPixels
}