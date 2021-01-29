package com.example.autopieces.map

/**
 * 准备区
 */
class ReadyZone : Zone(MapDraw.READY_ZONE_NUM) {

    override fun zoneBelongWhere(): String {
        return Position.POSITION_READY
    }
}