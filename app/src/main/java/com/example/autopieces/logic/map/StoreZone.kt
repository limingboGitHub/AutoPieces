package com.example.autopieces.logic.map

/**
 * 商店区域
 */
class StoreZone(zoneNum:Int) : OneDimensionalZone(zoneNum){

    override fun zoneBelongWhere(): String {
        return Position.POSITION_STORE
    }
}