package com.example.autopieces.logic.map

import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName
import org.junit.Test
import org.junit.Assert.*

class CombatZoneTest {

    @Test
    fun calculateAttackScope(){

        val result1 = calculateAttackScopeAll(1)
        assertEquals(4,result1.size)

        val result2 = calculateAttackScopeAll(2)
        assertEquals(12,result2.size)

        val result3 = calculateAttackScopeAll(3)
        assertEquals(24,result3.size)

        val result4 = calculateAttackScopeAll(4)
        assertEquals(40,result4.size)
    }


    @Test
    fun moveTest(){
        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM,GameMap.COMBAT_COL_NUM)

        val mapRole = MapRole(
            Role(RoleName.MING_REN))
        val mapRole2 = MapRole(
            Role(RoleName.ZUO_ZU))

        combatZone.addRole(mapRole,0,0)
        combatZone.addRole(mapRole2,6,0)

        assert(combatZone.getRoleByIndex(0,0)!=null)
        assert(combatZone.getRoleByIndex(6,0)!=null)

        var toMovePosition = combatZone.findClosetTarget(mapRole)
        assert(toMovePosition!=null)
        assertEquals(1,toMovePosition!!.x)
        assertEquals(0,toMovePosition.y)

        combatZone.removeRole(mapRole2)
        combatZone.addRole(mapRole2,0,7)

        toMovePosition = combatZone.findClosetTarget(mapRole)
        assertEquals(0,toMovePosition!!.x)
        assertEquals(1,toMovePosition.y)
    }
}