package com.example.autopieces.logic.combat.record

import com.example.autopieces.logic.map.MapRole

/**
 * 角色发起攻击记录
 */
class AttackRecord(
    val attackMapRole: MapRole,
    val beAttackedMapRoles : List<MapRole>
):CombatRecord() {
}