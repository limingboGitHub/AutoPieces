package com.example.autopieces.logic.combat.record

import com.example.autopieces.logic.combat.Damage
import com.example.autopieces.logic.map.MapRole

/**
 * 角色受伤记录
 */
class HurtRecord(
    val beHurtMapRole: MapRole,
    val damage: Damage
):CombatRecord() {
}