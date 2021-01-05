package com.example.autopieces.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.customview.widget.ViewDragHelper
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

    /**
     * 子View拖动
     */
    private val dragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(view: View, i: Int): Boolean {
            return true
        }

        override fun getViewHorizontalDragRange(view: View): Int {
            return view.width
        }

        override fun getViewVerticalDragRange(view: View): Int {
            return view.height
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

    }
    private val viewDragHelper = ViewDragHelper.create(this,dragCallback)

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

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
//        return super.onTouchEvent(event)
        return true
    }



}