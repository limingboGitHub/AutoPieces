package com.example.autopieces.map

import android.graphics.RectF
import java.lang.StringBuilder

class Cell(
        var rect:RectF,
        val x:Int = 0,
        val y:Int = 0){

        override fun toString(): String {
                val sb = StringBuilder()
                sb.append("l:${rect.left} ")
                        .append("t:${rect.top} ")
                        .append("r:${rect.right} ")
                        .append("b:${rect.bottom} ")
                        .append("x:$x")
                        .append("y:$y")
                return sb.toString()
        }
}