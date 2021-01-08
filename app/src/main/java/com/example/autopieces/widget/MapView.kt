package com.example.autopieces.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper
import com.example.autopieces.R
import com.example.autopieces.role.Role
import com.example.autopieces.utils.*
import com.lmb.lmbkit.utils.getDensity
import com.lmb.lmbkit.utils.getScreenHeight
import com.lmb.lmbkit.utils.getScreenWidth
import java.lang.RuntimeException

class MapView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val TAG = "MapView"

    /**
     * 系统参数
     */
    private val screenWidth = getScreenWidth()
    private val screenHeight = getScreenHeight()
    private val density = getDensity()

    /**
     * 地图相关参数
     */
    private val cellCols = 7
    private var cellRows = 8

    private var cellWidth = 0

    private var roleCoordinates = HashMap<View,Rect>()

    private val mapPaint = Paint()

    /**
     * 预备区域
     */
    private val readyCellNum = 9

    private var readyZoneCellWidth = 0

    private var readyZoneStartY = 0

    private val readyPaint = Paint()

    /**
     * 商店区域
     */
    private val storeNum = 5

    private var storeCellWidth = 0

    //margin边距
    private var storeStartMargin = (5 * density).toInt()
    private var storeBottomMargin = (5 * density).toInt()

    private val storePaint = Paint()

    /**
     * 商店角色
     */
    private val storeRoles = ArrayList<Role>()

    /**
     * 子View拖动
     */
    private val dragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(view: View, i: Int): Boolean {
            return true
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            //归位
            val curX = releasedChild.x
            val curY = releasedChild.y

            roleCoordinates[releasedChild]?.apply {
                val targetX = left
                val targetY = top

                val animation = ValueAnimator.ofFloat(1f,0f)
                animation.addUpdateListener {
                    val scale = it.animatedValue as Float
                    releasedChild.x = targetX + (curX - targetX)*scale
                    releasedChild.y = targetY + (curY - targetY)*scale
                }
                animation.duration = 200
                animation.start()
            }
            logE(TAG,"x:$curX y:$curY")

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
        //地图绘制相关参数
        mapPaint.strokeWidth = 2*density
        mapPaint.color = Color.GRAY

        cellWidth = screenWidth/cellCols

        //准备区域相关参数
        readyPaint.strokeWidth = 1 * density
        readyPaint.color = getColor(R.color.ready_zone_color)

        readyZoneCellWidth = screenWidth/readyCellNum

        readyZoneStartY = (screenWidth + 10 * density).toInt()

        //商店区域
        storePaint.strokeWidth = 3 * density
        storePaint.color = getColor(R.color.store_stroke_color)

        storeCellWidth = (screenWidth-storeStartMargin*2)/storeNum

        setWillNotDraw(false)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount){
            val childView = getChildAt(index)
            roleCoordinates[childView]?.apply {
                childView.layout(left,top,right,bottom)
            }

            logD(TAG,"onLayout $childView to:${childView.left}")
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制地图
        drawMap(canvas)
        //绘制棋子准备区域
        drawReadyZone(canvas)
        //绘制商店区域
        drawStore(canvas)
    }

    private fun drawStore(canvas: Canvas) {
        canvas.drawLine(
            storeStartMargin.toFloat(),
            (screenHeight - storeBottomMargin - storeCellWidth).toFloat(),
            (screenWidth - storeStartMargin).toFloat(),
            (screenHeight - storeBottomMargin - storeCellWidth).toFloat(),
            storePaint
        )
        canvas.drawLine(
            storeStartMargin.toFloat(),
            (screenHeight - storeBottomMargin).toFloat(),
            (screenWidth - storeStartMargin).toFloat(),
            (screenHeight - storeBottomMargin).toFloat(),
            storePaint
        )
        repeat(storeNum + 1) {
            canvas.drawLine(
                storeStartMargin.toFloat() + storeCellWidth * it,
                (screenHeight - storeBottomMargin - storeCellWidth).toFloat(),
                storeStartMargin.toFloat() + storeCellWidth * it,
                (screenHeight - storeBottomMargin).toFloat(),
                storePaint
            )
        }
    }

    private fun drawReadyZone(canvas: Canvas) {
        canvas.drawLine(
            0f, readyZoneStartY.toFloat(),
            screenWidth.toFloat(), readyZoneStartY.toFloat(),
            readyPaint
        )
        canvas.drawLine(
            0f, (readyZoneStartY + readyZoneCellWidth).toFloat(),
            screenWidth.toFloat(), (readyZoneStartY + readyZoneCellWidth).toFloat(),
            readyPaint
        )
        repeat(readyCellNum-1){
            canvas.drawLine(
                (readyZoneCellWidth*(it+1)).toFloat(), readyZoneStartY.toFloat(),
                (readyZoneCellWidth*(it+1)).toFloat(), (readyZoneStartY + readyZoneCellWidth).toFloat(),
                readyPaint
            )
        }
    }

    private fun drawMap(canvas: Canvas) {
        for (index in 1 until cellCols) {
            val startX = (index * cellWidth).toFloat()
            canvas.drawLine(startX, 0f, startX, ((cellRows - 1) * cellWidth).toFloat(), mapPaint)
        }
        for (index in 1 until cellRows) {
            val startY = (index * cellWidth).toFloat()
            canvas.drawLine(0f, startY, screenWidth.toFloat(), startY, mapPaint)
        }
    }

    fun addStore(role: Role){
        val padding = 4 *density
        val cardWidth = (storeCellWidth - 2*padding).toInt()

        val cardView = LayoutInflater.from(context).inflate(R.layout.item_store,this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = cardWidth
        layoutParams.height = cardWidth
        cardView.layoutParams = layoutParams

        addView(cardView)
        //计算每个卡片对应在商店区域的坐标
        val x = storeStartMargin + padding + (cardWidth + 2*padding)*storeRoles.size
        val y = screenHeight - storeBottomMargin - storeCellWidth + padding

        roleCoordinates[cardView] = Rect(
                x.toInt(), y.toInt(), (x+cardWidth).toInt(), (y+cardWidth).toInt()
        )

        storeRoles.add(role)
    }

    fun addView(view:View,point:Point){
        if (point.x>=cellCols)
            throw RuntimeException("x must < $cellCols")
        if (point.y>=cellRows)
            throw RuntimeException("y must < $cellRows")
        roleCoordinates[view] = Rect(
                point.x*cellWidth, point.y*cellWidth,
                (point.x+1)*cellWidth, (point.y+1)*cellWidth
        )
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