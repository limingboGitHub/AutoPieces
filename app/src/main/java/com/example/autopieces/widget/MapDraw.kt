package com.example.autopieces.widget

import android.graphics.*
import com.example.autopieces.R
import com.example.autopieces.utils.getColor
import com.lmb.lmbkit.MyContext
import com.lmb.lmbkit.utils.getDensity
import com.lmb.lmbkit.utils.getScreenHeight
import com.lmb.lmbkit.utils.getScreenWidth

/**
 * 管理地图的绘制
 */
class MapDraw {

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

    private val mapPaint = Paint()

    /**
     * 预备区域
     */
    private val readyCellNum = 9

    private var readyZoneCellWidth = 0

    private var readyZoneRect = Rect()

    private var readyZoneStartY = 0

    private var readyStartMargin = (5 * density).toInt()
    private var readyBottomMargin = (10 * density).toInt()

    private val readyPaint = Paint()

    /**
     * 商店区域
     */
    private val storeNum = 5

    private var storeCellWidth = 0
    private var storeZoneRect = Rect()

    //margin边距
    private var storeStartMargin = (5 * density).toInt()
    private var storeBottomMargin = (5 * density).toInt()

    private val storePaint = Paint()

    //商店角色宽度
    private var storeItemWidth = 0

    init {
        //地图绘制相关参数
        mapPaint.strokeWidth = 2*density
        mapPaint.color = Color.GRAY
        cellWidth = screenWidth/cellCols

        //商店区域
        storePaint.strokeWidth = 3 * density
        storePaint.color = getColor(R.color.store_stroke_color)
        storeCellWidth = (screenWidth-storeStartMargin*2)/storeNum

        storeZoneRect = Rect(
            storeStartMargin,
            screenHeight-storeBottomMargin-storeCellWidth,
            screenWidth-storeStartMargin,
            screenHeight-storeBottomMargin
        )
        //商店角色宽高
        storeItemWidth = storeCellWidth - 2 * MyContext.context.resources.getDimensionPixelOffset(R.dimen.store_item_padding)

        //准备区域相关参数
        readyPaint.strokeWidth = 1 * density
        readyPaint.color = getColor(R.color.ready_zone_color)
        readyZoneCellWidth = (screenWidth-2*readyStartMargin)/readyCellNum

        readyZoneRect = Rect(
            readyStartMargin,
            storeZoneRect.top - readyBottomMargin - readyZoneCellWidth,
            screenWidth - readyStartMargin,
            storeZoneRect.top - readyBottomMargin
        )
    }

    fun drawStore(canvas: Canvas) {
        val storeZoneRect = RectF(this.storeZoneRect)
        canvas.drawLine(
            storeZoneRect.left,
            storeZoneRect.top,
            storeZoneRect.right,
            storeZoneRect.top,
            storePaint
        )
        canvas.drawLine(
            storeZoneRect.left,
            storeZoneRect.bottom,
            storeZoneRect.right,
            storeZoneRect.bottom,
            storePaint
        )
        repeat(storeNum + 1) {
            canvas.drawLine(
                storeZoneRect.left + storeCellWidth * it,
                storeZoneRect.top,
                storeZoneRect.left + storeCellWidth * it,
                storeZoneRect.bottom,
                storePaint
            )
        }
    }

    fun drawReadyZone(canvas: Canvas) {
        val readyZoneRectF = RectF(readyZoneRect)
        canvas.drawLine(
            readyZoneRectF.left,
            readyZoneRectF.top,
            readyZoneRectF.right,
            readyZoneRectF.top,
            readyPaint
        )
        canvas.drawLine(
            readyZoneRectF.left,
            readyZoneRectF.bottom,
            readyZoneRectF.right,
            readyZoneRectF.bottom,
            readyPaint
        )
        repeat(readyCellNum+1){
            canvas.drawLine(
                readyZoneRectF.left + readyZoneCellWidth*it,
                readyZoneRectF.top,
                readyZoneRectF.left + readyZoneCellWidth*it,
                readyZoneRectF.bottom,
                readyPaint
            )
        }
    }

    fun drawMap(canvas: Canvas) {
        for (index in 1 until cellCols) {
            val startX = (index * cellWidth).toFloat()
            canvas.drawLine(startX, 0f, startX, ((cellRows - 1) * cellWidth).toFloat(), mapPaint)
        }
        for (index in 1 until cellRows) {
            val startY = (index * cellWidth).toFloat()
            canvas.drawLine(0f, startY, screenWidth.toFloat(), startY, mapPaint)
        }
    }
    
    fun getStoreItemWidth() = storeItemWidth

    fun getStoreCellWith() = storeCellWidth

    fun getStoreZone() = storeZoneRect

    fun getMapCellWidth() = cellWidth
}