package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName

fun createSimpleCombatZone():CombatZone{
    val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM, GameMap.COMBAT_COL_NUM)

    val teamOneRole = MapRole(
        Role(RoleName.MING_REN),
        belongTeam = 1)
    val teamTwoRole = MapRole(
        Role(RoleName.ZUO_ZU),
        belongTeam = 2)

    //两个阵营分别添加两个角色
    combatZone.addRole(teamOneRole,0,2)
    combatZone.addRole(teamTwoRole,0,5)
    return combatZone
}