package com.example.autopieces.map

import android.graphics.*
import android.view.View
import com.example.autopieces.R
import com.example.autopieces.role.Role
import com.example.autopieces.utils.getColor
import com.example.autopieces.utils.logE
import com.lmb.lmbkit.MyContext
import com.lmb.lmbkit.utils.getDensity
import com.lmb.lmbkit.utils.getScreenHeight
import com.lmb.lmbkit.utils.getScreenWidth

/**
 * 管理地图的绘制
 */
class MapDraw {
    val TAG = "MapDraw"

    companion object{
        /**
         * 战斗区域行列数
         */
        const val COMBAT_ROW_NUM = 8
        const val COMBAT_COL_NUM = 7

        /**
         * 准备区数量
         */
        const val READY_ZONE_NUM = 9
    }

    /**
     * 系统参数
     */
    private val screenWidth = getScreenWidth()
    private val screenHeight = getScreenHeight()
    private val density = getDensity()

    /**
     * 战斗区域相关参数
     */
    private var combatZoneRect = RectF()

    private val combatStartMargin = 5*density

    private var combatCellWidth = 0f

    private val combatZonePaint = Paint()

    /**
     * 预备区域
     */

    private var readyZoneCellWidth = 0f

    private var readyZoneRect = RectF()

    private var readyZoneStartY = 0

    private var readyStartMargin = 5 * density
    private var readyBottomMargin = 10 * density

    private val readyPaint = Paint()

    /**
     * 商店区域
     */
    private val storeNum = 5

    private var storeCellWidth = 0f
    private var storeZoneRect = RectF()

    //margin边距
    private var storeStartMargin = 5 * density
    private var storeBottomMargin = 5 * density

    private val storePaint = Paint()

    //商店角色宽度
    private var storeItemWidth = 0f

    init {
        //商店区域
        storePaint.strokeWidth = 3 * density
        storePaint.color = getColor(R.color.store_stroke_color)
        storeCellWidth = (screenWidth-storeStartMargin*2)/storeNum

        storeZoneRect = RectF(
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
        readyZoneCellWidth = (screenWidth-2*readyStartMargin)/READY_ZONE_NUM

        readyZoneRect = RectF(
            readyStartMargin,
            storeZoneRect.top - readyBottomMargin - readyZoneCellWidth,
            screenWidth - readyStartMargin,
            storeZoneRect.top - readyBottomMargin
        )

        //战斗区域绘制相关参数
        combatZonePaint.strokeWidth = 2*density
        combatZonePaint.color = Color.GRAY

        val combatWidth = screenWidth-2*combatStartMargin
        combatCellWidth = combatWidth/COMBAT_COL_NUM
        val combatZoneBottom = readyZoneRect.top - 10*density

        combatZoneRect = RectF(
            combatStartMargin,
            combatZoneBottom - combatCellWidth* COMBAT_ROW_NUM,
            combatStartMargin+combatWidth,
            combatZoneBottom
        )
    }

    fun drawStore(canvas: Canvas) {
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
        canvas.drawLine(
            readyZoneRect.left,
            readyZoneRect.top,
            readyZoneRect.right,
            readyZoneRect.top,
            readyPaint
        )
        canvas.drawLine(
            readyZoneRect.left,
            readyZoneRect.bottom,
            readyZoneRect.right,
            readyZoneRect.bottom,
            readyPaint
        )
        repeat(READY_ZONE_NUM+1){
            canvas.drawLine(
                readyZoneRect.left + readyZoneCellWidth*it,
                readyZoneRect.top,
                readyZoneRect.left + readyZoneCellWidth*it,
                readyZoneRect.bottom,
                readyPaint
            )
        }
    }

    fun drawCombat(canvas: Canvas) {
        repeat(COMBAT_COL_NUM+1){
            val startX = combatStartMargin + it*combatCellWidth
            canvas.drawLine(
                startX,
                combatZoneRect.top,
                startX,
                combatZoneRect.bottom,
                combatZonePaint)
        }

        repeat(COMBAT_ROW_NUM+1){
            val startY = combatZoneRect.bottom - it*combatCellWidth
            canvas.drawLine(
                combatZoneRect.left,
                startY,
                combatZoneRect.right,
                startY,
                combatZonePaint)
        }
    }


    fun getStoreItemWidth() = storeItemWidth

    fun getStoreCellWith() = storeCellWidth

    fun getStoreZone() = storeZoneRect

    fun getCombatCellWidth() = combatCellWidth

    fun getReadyCellWidth() = readyZoneCellWidth

    fun getReadyItemZone(index:Int) = RectF(
            readyZoneRect.left + readyZoneCellWidth*index,
            readyZoneRect.top,
            readyZoneRect.left + readyZoneCellWidth*(index+1),
            readyZoneRect.bottom
    )

    /**
     * 判断区域是否属于商店
     */
    fun belongStoreZone(zone:RectF):Boolean{
        return false
    }

    fun belongReadyZone(zone:RectF):Boolean{
        return false
    }

    fun belongCombatZone(zone: RectF):Boolean{
        return true
    }


    /**
     * 计算视图的位置
     */
    fun calculateLocation(role:Role, roleView:View):RectF{
        val roleViewCenterX = roleView.left + (roleView.right-roleView.left)/2
        val roleViewCenterY = roleView.top + (roleView.bottom - roleView.top)/2

        //判断是否进入各区域
        if (roleViewCenterY>storeZoneRect.top && roleViewCenterY<storeZoneRect.bottom){
            logE(TAG, "商店区")
            role.location = MapView.LOCATION_STORE
        }else if (roleViewCenterY>readyZoneRect.top && roleViewCenterY<readyZoneRect.bottom){
            //判断进入哪一个格子
            var index = 0
            var centerX = roleViewCenterX - readyZoneRect.left
            while(centerX>=readyZoneCellWidth){
                index++.coerceAtMost(READY_ZONE_NUM-1)
                centerX -=readyZoneCellWidth
            }
            logE(TAG, "准备区第${index+1}格")

            val left = readyZoneRect.left+readyZoneCellWidth*index
            return RectF(
                    left,
                    readyZoneRect.top,
                    left+readyZoneCellWidth,
                    readyZoneRect.bottom
            )
        }else if (roleViewCenterY>combatZoneRect.top && roleViewCenterY<combatZoneRect.bottom){
            var rowIndex = 0
            var centerY = roleViewCenterY - combatZoneRect.top
            while(centerY>=combatCellWidth){
                rowIndex++.coerceAtMost(READY_ZONE_NUM-1)
                centerY -=combatCellWidth
            }
            logE(TAG, "战斗区第${rowIndex+1}行")

            var colIndex = 0
            var centerX = roleViewCenterX - combatZoneRect.left
            while(centerX>=combatCellWidth){
                colIndex++.coerceAtMost(READY_ZONE_NUM-1)
                centerX -=combatCellWidth
            }
            logE(TAG, "战斗区第${colIndex+1}列")

            val left = combatZoneRect.left+combatCellWidth*colIndex
            val top = combatZoneRect.top + combatCellWidth * rowIndex
            return RectF(
                left,
                top,
                left+combatCellWidth,
                top+combatCellWidth
            )
        }else{
            logE(TAG, "其他区域")
        }
        return RectF(
            roleView.left.toFloat(),
            roleView.top.toFloat(),
            roleView.right.toFloat(),
            roleView.bottom.toFloat()
        )
    }
}