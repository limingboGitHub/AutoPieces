package com.example.autopieces.view

import android.graphics.*
import android.view.View
import com.example.autopieces.R
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.Position
import com.example.autopieces.utils.getColor
import com.example.autopieces.utils.logE
import com.lmb.lmbkit.MyContext
import com.lmb.lmbkit.utils.getDensity

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
    private var screenWidth = 0
    private var screenHeight = 0
    private val density = getDensity()

    /**
     * 战斗区域相关参数
     */
    private var combatZoneRect = RectF()

    private val combatStartMargin = 10*density

    private var combatCellWidth = 0f

    private var combatCellPadding = 2*density

    private val combatZonePaint = Paint()

    /**
     * 预备区域
     */

    private var readyZoneCellWidth = 0f

    private var readyZoneRect = RectF()

    private var readyStartMargin = 5 * density
    private var readyBottomMargin = 10 * density

    private val readyCellPadding = 2*density

    private val readyPaint = Paint()

    //棋子宽度
    private var mapRoleWidth = 0f

    /**
     * 商店区域
     */
    private val storeNum = 5

    private var storeCellWidth = 0f
    private var storeZoneRect = RectF()

    //margin边距
    private var storeStartMargin = 10 * density
    private var storeEndMargin = 100*density
    private var storeBottomMargin = 10 * density

    private val storeCellPadding = 3*density

    private val storePaint = Paint()

    //商店角色宽度
    private var storeItemWidth = 0f

    /**
     * 装备区域
     */
    private var equipmentCellWidth = 0f
    private var equipmentZoneRect = RectF()

    private var equipmentStartMargin = 5*density
    private var equipmentEndMargin = 5*density
    private var equipmentBottomMargin = 10*density

    private var equipmentCellPadding = 2*density
    private var equipmentItemWidth = 0f

    private val equipmentPaint = Paint()

    fun initLayout(screenWidth:Int, screenHeight:Int) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        //商店区域
        initStoreZoneParam(screenWidth,screenHeight)

        //装备区域
        initEquipmentZoneParam(screenWidth, storeZoneRect.top.toInt())

        //准备区域相关参数
        initReadyZoneParam(screenWidth,equipmentZoneRect.top.toInt())

        //战斗区域绘制相关参数
        initCombatZoneParam(screenWidth,readyZoneRect.top.toInt())
    }

    private fun initCombatZoneParam(zoneWidth: Int, zoneBottom: Int) {
        combatZonePaint.strokeWidth = 2 * density
        combatZonePaint.color = getColor(R.color.map_draw_combat_zone_border)

        val combatWidth = zoneWidth - 2 * combatStartMargin
        combatCellWidth = combatWidth / COMBAT_COL_NUM
        val combatZoneBottom = zoneBottom - 10 * density

        combatZoneRect = RectF(
            combatStartMargin,
            combatZoneBottom - combatCellWidth * COMBAT_ROW_NUM,
            combatStartMargin + combatWidth,
            combatZoneBottom
        )
    }

    private fun initReadyZoneParam(zoneWidth: Int, zoneBottom: Int) {
        readyPaint.strokeWidth = 1 * density
        readyPaint.color = getColor(R.color.ready_zone_color)
        readyZoneCellWidth = (zoneWidth - 2 * readyStartMargin) / READY_ZONE_NUM
        mapRoleWidth = readyZoneCellWidth - readyCellPadding * 2

        readyZoneRect = RectF(
            readyStartMargin,
            zoneBottom - readyBottomMargin - readyZoneCellWidth,
            zoneWidth - readyStartMargin,
            zoneBottom - readyBottomMargin
        )
    }

    private fun initEquipmentZoneParam(zoneWidth: Int,zoneBottom:Int) {
        equipmentPaint.apply {
            strokeWidth = 1 * density
            color = getColor(R.color.equipment_stroke_color)
        }
        equipmentCellWidth =
            (zoneWidth - equipmentStartMargin - equipmentEndMargin) / GameMap.EQUIPMENT_ZONE_NUM

        equipmentZoneRect = RectF(
            equipmentStartMargin,
            zoneBottom - equipmentBottomMargin - equipmentCellWidth,
            zoneWidth - equipmentEndMargin,
            zoneBottom - equipmentBottomMargin
        )
        equipmentItemWidth = equipmentCellWidth - equipmentCellPadding * 2
    }

    private fun initStoreZoneParam(zoneWidth: Int,zoneBottom:Int) {
        storePaint.strokeWidth = 3 * density
        storePaint.color = getColor(R.color.store_stroke_color)
        storeCellWidth = (zoneWidth - storeStartMargin - storeEndMargin) / storeNum

        storeZoneRect = RectF(
            storeStartMargin,
            zoneBottom - storeBottomMargin - storeCellWidth,
            zoneWidth - storeEndMargin,
            zoneBottom - storeBottomMargin
        )
        //商店角色宽高
        storeItemWidth =
            storeCellWidth - 2 * storeCellPadding
    }

    fun drawAll(canvas: Canvas){
        drawStore(canvas)
        drawEquipment(canvas)
        drawReadyZone(canvas)
        drawCombat(canvas)
    }

    private fun drawEquipment(canvas: Canvas){
        equipmentZoneRect.apply {
            canvas.drawLine(left, top, right, top, equipmentPaint)
            canvas.drawLine(left, bottom, right, bottom, equipmentPaint)
            repeat(GameMap.EQUIPMENT_ZONE_NUM+1){
                canvas.drawLine(
                    left + equipmentCellWidth * it,
                    top,
                    left + equipmentCellWidth * it,
                    bottom,
                    equipmentPaint
                )
            }
        }
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
        repeat(READY_ZONE_NUM +1){
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
        repeat(COMBAT_COL_NUM +1){
            val startX = combatStartMargin + it*combatCellWidth
            canvas.drawLine(
                startX,
                combatZoneRect.top,
                startX,
                combatZoneRect.bottom,
                combatZonePaint)
        }

        repeat(COMBAT_ROW_NUM +1){
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

    fun getEquipmentItemWidth() = equipmentItemWidth

    fun getStoreItemWidth() = storeItemWidth

    fun getMapRoleWidth() = mapRoleWidth

    fun getPhysicalPointByPosition(position: Position):Pair<Float,Float>{
        return when(position.where){
            Position.POSITION_STORE -> {
                getZoneItemPoint(storeZoneRect,storeCellWidth,position)
            }
            Position.POSITION_READY -> {
                getZoneItemPoint(readyZoneRect,readyZoneCellWidth,position)
            }
            Position.POSITION_EQUIPMENT ->{
                getZoneItemPoint(equipmentZoneRect,equipmentCellWidth,position)
            }
            Position.POSITION_COMBAT -> {
                getZoneItemPoint(combatZoneRect,combatCellWidth,position)
            }

            else -> Pair(0f,0f)
        }
    }

    /**
     * 通过Position来获取地图中的物理位置
     */
    fun getPhysicalRectByPosition(position: Position):RectF{
        return when(position.where){
            Position.POSITION_STORE -> {
                RectF(
                        storeZoneRect.left + storeCellWidth*position.x + storeCellPadding,
                        storeZoneRect.top + storeCellPadding,
                        storeZoneRect.left + storeCellWidth*(position.x+1) - storeCellPadding,
                        storeZoneRect.bottom - storeCellPadding
                )
            }
            Position.POSITION_READY -> {
                RectF(
                        readyZoneRect.left + readyZoneCellWidth*position.x + readyCellPadding,
                        readyZoneRect.top + readyCellPadding,
                        readyZoneRect.left + readyZoneCellWidth*(position.x+1) - readyCellPadding,
                        readyZoneRect.bottom - readyCellPadding
                )
            }
            Position.POSITION_EQUIPMENT ->{
                getZoneItemRectF(equipmentZoneRect,equipmentCellWidth,equipmentCellPadding,position)
            }
            Position.POSITION_COMBAT -> {
                val padding = (combatCellWidth - mapRoleWidth)/2
                RectF(
                        combatZoneRect.left + combatCellWidth * position.x + padding,
                        combatZoneRect.top + combatCellWidth * position.y + padding,
                        combatZoneRect.left + combatCellWidth*(position.x+1) - padding,
                        combatZoneRect.top + combatCellWidth * (position.y+1) - padding
                )
            }

            else -> RectF()
        }
    }

    private fun getZoneItemPoint(zone:RectF,cellWidth: Float,position: Position):Pair<Float,Float>{
        return Pair(
            zone.left + cellWidth*position.x + cellWidth/2,
            zone.top + cellWidth*position.y + cellWidth/2
        )
    }

    private fun getZoneItemRectF(zone:RectF,cellWidth:Float,padding:Float,position: Position):RectF{
        return  RectF(
            zone.left + cellWidth * position.x + padding,
            zone.top + padding,
            zone.left + cellWidth * (position.x+1) - padding,
            zone.bottom - padding
        )
    }

    /**
     * 计算视图的位置
     */
    fun calculatePosition(roleView:View): Position {
        val roleViewCenterX = roleView.left + (roleView.right-roleView.left)/2
        val roleViewCenterY = roleView.top + (roleView.bottom - roleView.top)/2

        //判断是否进入各区域
        if (roleViewCenterY>storeZoneRect.bottom){
            return Position(Position.POSITION_STORE_DOWN)
        }else if (roleViewCenterY>equipmentZoneRect.top && roleViewCenterY<equipmentZoneRect.bottom){
            //判断列
            val index = calculateColIndex(roleViewCenterX - equipmentZoneRect.left,equipmentCellWidth,GameMap.EQUIPMENT_ZONE_NUM)
            return Position(Position.POSITION_EQUIPMENT,index)
        }else if (roleViewCenterY>storeZoneRect.top && roleViewCenterY<storeZoneRect.bottom){
            logE(TAG, "商店区")
            return Position(Position.POSITION_STORE)
        }else if (roleViewCenterY>readyZoneRect.top && roleViewCenterY<readyZoneRect.bottom){
            //判断进入哪一个格子
            val index = calculateColIndex(roleViewCenterX-readyZoneRect.left,readyZoneCellWidth, READY_ZONE_NUM)
            logE(TAG, "准备区第${index+1}格")

            return Position(Position.POSITION_READY,index)
        }else if (roleViewCenterY>combatZoneRect.top && roleViewCenterY<combatZoneRect.bottom){
            val rowIndex = calculateColIndex(roleViewCenterY-combatZoneRect.top,combatCellWidth, READY_ZONE_NUM)
            logE(TAG, "战斗区第${rowIndex+1}行")

            val colIndex = calculateColIndex(roleViewCenterX-combatZoneRect.left,combatCellWidth, READY_ZONE_NUM)
            logE(TAG, "战斗区第${colIndex+1}列")

            return Position(Position.POSITION_COMBAT,colIndex,rowIndex)
        }else{
            logE(TAG, "其他区域")
        }
        return Position(Position.POSITION_OTHER)
    }

    private fun calculateColIndex(centerX:Float,cellWidth:Float,cellNum:Int):Int{
        //判断进入哪一个格子
        var index = 0
        var curCenterX = centerX
        while(curCenterX>=cellWidth){
            index++.coerceAtMost(cellNum -1)
            curCenterX -=cellWidth
        }
        return index
    }
}