package com.example.autopieces.map

import android.graphics.*
import android.view.View
import com.example.autopieces.R
import com.example.autopieces.extend.belongRect
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
    private val TAG = "MapDraw"

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
        combatZonePaint.color = getColor(R.color.map_draw_combat_zone_border)

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
            if (it == (COMBAT_ROW_NUM)/2){
                combatZonePaint.color = getColor(R.color.map_draw_combat_zone_center_line)
                combatZonePaint.strokeWidth = 3*density
            } else {
                combatZonePaint.color = getColor(R.color.map_draw_combat_zone_border)
                combatZonePaint.strokeWidth = 2*density
            }
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
     * 通过Position来获取地图中的物理位置
     */
    fun getPhysicalRectByPosition(position: Position):RectF{
        return when(position.where){
            Position.POSITION_STORE -> {
                RectF(
                        storeZoneRect.left + storeCellWidth*position.x,
                        storeZoneRect.top,
                        storeZoneRect.left + storeCellWidth*(position.x+1),
                        storeZoneRect.bottom
                )
            }
            Position.POSITION_READY -> {
                RectF(
                        readyZoneRect.left + readyZoneCellWidth*position.x,
                        readyZoneRect.top,
                        readyZoneRect.left + readyZoneCellWidth*(position.x+1),
                        readyZoneRect.bottom
                )
            }
            Position.POSITION_COMBAT -> {
                RectF(
                        combatZoneRect.left + combatCellWidth * position.x,
                        combatZoneRect.top + combatCellWidth * position.y,
                        combatZoneRect.left + combatCellWidth*(position.x+1),
                        combatZoneRect.top + combatCellWidth * (position.y+1)
                )
            }
            else -> RectF()
        }
    }

    /**
     * 判断区域是否属于商店
     */
    fun belongStoreZone(zone:RectF) = zone.belongRect(storeZoneRect)

    fun belongReadyZone(zone:RectF) = zone.belongRect(readyZoneRect)

    fun belongCombatZone(zone: RectF) = zone.belongRect(combatZoneRect)

    /**
     * 计算视图的位置
     */
    fun calculatePosition(roleView:View):Position{
        val roleViewCenterX = roleView.left + (roleView.right-roleView.left)/2
        val roleViewCenterY = roleView.top + (roleView.bottom - roleView.top)/2

        //判断是否进入各区域
        if (roleViewCenterY>storeZoneRect.top && roleViewCenterY<storeZoneRect.bottom){
            logE(TAG, "商店区")
            return Position(Position.POSITION_STORE)
        }else if (roleViewCenterY>readyZoneRect.top && roleViewCenterY<readyZoneRect.bottom){
            //判断进入哪一个格子
            var index = 0
            var centerX = roleViewCenterX - readyZoneRect.left
            while(centerX>=readyZoneCellWidth){
                index++.coerceAtMost(READY_ZONE_NUM-1)
                centerX -=readyZoneCellWidth
            }
            logE(TAG, "准备区第${index+1}格")

//            val left = readyZoneRect.left+readyZoneCellWidth*index
//            val rect = RectF(
//                    left,
//                    readyZoneRect.top,
//                    left+readyZoneCellWidth,
//                    readyZoneRect.bottom
//            )

            return Position(Position.POSITION_READY,index)
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

            return Position(Position.POSITION_COMBAT,colIndex,rowIndex)
        }else{
            logE(TAG, "其他区域")
        }
//        val rect = RectF(
//                roleView.left.toFloat(),
//                roleView.top.toFloat(),
//                roleView.right.toFloat(),
//                roleView.bottom.toFloat()
//        )
        return Position(Position.POSITION_OTHER)
    }
}