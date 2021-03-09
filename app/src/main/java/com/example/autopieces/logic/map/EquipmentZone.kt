package com.example.autopieces.logic.map

class EquipmentZone(zoneNum:Int) : OneDimensionalZone(zoneNum) {

    override fun zoneBelongWhere(): String {
        return Position.POSITION_EQUIPMENT
    }

//    fun roleMove(mapRole: MapRole, targetPosition: Position): GameMap.MoveResult{
//
//    }
}