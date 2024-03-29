package com.example.autopieces.logic.combat

import com.example.autopieces.cpp.MoveMethod
import com.example.autopieces.logic.combat.search.*
import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName
import org.junit.Test
import org.junit.Assert.*

class SearchPathTest {

    @Test
    fun searchPathTest(){

        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM, GameMap.COMBAT_COL_NUM)

        val teamOneRole = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)
        val teamOneRole2 = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)

        val teamTwoRole = MapRole(
            Role(RoleName.ZUO_ZU),
            belongTeam = 2)

        //两个阵营分别添加两个角色
        combatZone.addRole(teamOneRole,1,6)
        combatZone.addRole(teamOneRole2,2,7)
        combatZone.addRole(teamTwoRole,4,1)

        val pathList = combatZone.getMovePath(teamOneRole.position,teamTwoRole.position)
        val pathList2 = combatZone.getMovePath(teamOneRole2.position,teamTwoRole.position)
        val pathList3 = combatZone.getMovePath(teamTwoRole.position,teamOneRole.position)
        assertEquals(2,pathList)
//        assertEquals(3,pathList[0].first)
//        assertEquals(2,pathList[0].second)
//        assertEquals(2,pathList[1].first)
//        assertEquals(2,pathList[1].second)
    }
}