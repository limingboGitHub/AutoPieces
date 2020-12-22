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

class MapView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    /**
     * 地图相关参数
     */
    private val cellRows = 10
    private var cellCols = 0

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

        cellWidth = screenWidth/cellRows
        cellCols = screenHeight/cellWidth

        setWillNotDraw(false)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount){
            val childView = getChildAt(index)
            childView.layout(3*cellWidth,0,4*cellWidth,cellWidth)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (index in 1 until cellRows){
            val startX = (index*cellWidth).toFloat()
            canvas.drawLine(startX,0f,startX,screenHeight.toFloat(),paint)
        }
        for (index in 1 until cellCols){
            val startY = (index * cellWidth).toFloat()
            canvas.drawLine(0f,startY,screenWidth.toFloat(),startY,paint)
        }
    }

}