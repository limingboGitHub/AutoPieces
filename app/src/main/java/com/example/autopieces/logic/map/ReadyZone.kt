package com.example.autopieces.logic.map

/**
 * 准备区
 */
class ReadyZone(zoneNum:Int) : OneDimensionalZone(zoneNum) {

    override fun zoneBelongWhere(): String {
        return Position.POSITION_READY
    }



}