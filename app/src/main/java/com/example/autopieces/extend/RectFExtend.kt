package com.example.autopieces.extend

import android.graphics.RectF

fun RectF.belongRect(outRectF: RectF):Boolean{
    val centerX = left + (right - left)/2
    val centerY = top + (bottom - top)/2
    return centerX in outRectF.left..outRectF.right &&
            centerY in outRectF.top..outRectF.bottom
}