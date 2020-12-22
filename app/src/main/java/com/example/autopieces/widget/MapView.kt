package com.example.autopieces.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.autopieces.utils.getDensity
import com.example.autopieces.utils.getScreenHeight
import com.example.autopieces.utils.getScreenWidth
import com.example.autopieces.utils.logD
import java.lang.RuntimeException

class MapView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val TAG = "MapView"

    /**
     * 地图相关参数
     */
    private val cellCols = 7
    private var cellRows = 8

    private var cellWidth = 0

    private var roleCoordinates = HashMap<View,Point>()

    /**
     * 系统参数
     */
    private val screenWidth = getScreenWidth()
    private val screenHeight = getScreenHeight()
    private val density = getDensity()

    private val paint = Paint()

    init {
        paint.strokeWidth = 2*density
        paint.color = Color.GRAY

        cellWidth = screenWidth/cellCols
//        cellCols = screenHeight/cellWidth

        setWillNotDraw(false)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount){
            val childView = getChildAt(index)
            roleCoordinates[childView]?.apply {
                childView.layout(x*cellWidth,y*cellWidth,(x+1)*cellWidth,(y+1)*cellWidth)
            }
            logD(TAG,"onLayout $childView to:${childView.left}")
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (index in 1 until cellCols){
            val startX = (index*cellWidth).toFloat()
            canvas.drawLine(startX,0f,startX, ((cellRows-1)*cellWidth).toFloat(),paint)
        }
        for (index in 1 until cellRows){
            val startY = (index * cellWidth).toFloat()
            canvas.drawLine(0f,startY,screenWidth.toFloat(),startY,paint)
        }
    }

    fun addView(view:View,point:Point){
        if (point.x>=cellCols)
            throw RuntimeException("x must < $cellCols")
        if (point.y>=cellRows)
            throw RuntimeException("y must < $cellRows")
        roleCoordinates[view] = point
        addView(view)
        logD(TAG,"add $view to:${point.x},${point.y}")
    }

}