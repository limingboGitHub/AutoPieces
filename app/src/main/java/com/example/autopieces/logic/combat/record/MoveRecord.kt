package com.example.autopieces.logic.combat.record

import com.example.autopieces.logic.map.Position

/**
 * 角色移动记录
 */
class MoveRecord(
    val oldPosition:Position,
    val position: Position
) : CombatRecord()