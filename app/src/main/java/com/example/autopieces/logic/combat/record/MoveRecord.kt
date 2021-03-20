package com.example.autopieces.logic.combat.record

import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.Position

/**
 * 角色移动记录
 */
class MoveRecord(
    val mapRole: MapRole,
    val oldPosition:Position,
    val targetPosition: Position
) : CombatRecord()