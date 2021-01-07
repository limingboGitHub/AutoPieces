package com.example.autopieces.utils

import com.lmb.lmbkit.MyContext

fun getColor(resId:Int):Int{
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        MyContext.context.getColor(resId)
    } else {
        MyContext.context.resources.getColor(resId)
    }
}