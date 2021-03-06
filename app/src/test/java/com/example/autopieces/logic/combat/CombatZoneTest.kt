package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName
import org.junit.Test
import org.junit.Assert.*

class CombatZoneTest {


    @Test
    fun combatTest(){
        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM,GameMap.COMBAT_COL_NUM)

        val teamOneRole = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)
        val teamTwoRole = MapRole(
            Role(RoleName.ZUO_ZU),
            belongTeam = 2)

        //两个阵营分别添加两个角色
        combatZone.addRole(teamOneRole,0,2)
        combatZone.addRole(teamTwoRole,0,5)

        //行动
        combatZone.action(0)

        assertEquals(MapRole.STATE_MOVING,teamOneRole.state)
        assertEquals(MapRole.STATE_MOVING,teamTwoRole.state)

        //模拟过了300ms
//        teamOneRole.stateRestTime = 0
//        teamTwoRole.stateRestTime = 0

        combatZone.action(300)

        assertEquals(0,teamOneRole.position.x)
        assertEquals(3,teamOneRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamOneRole.state)

        assertEquals(0,teamTwoRole.position.x)
        assertEquals(4,teamTwoRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamTwoRole.state)

        //已经相遇，再次行动，应该开始相互攻击
        combatZone.action(0)

        assertEquals(MapRole.STATE_BEFORE_ATTACK,teamOneRole.state)
        assertEquals(MapRole.STATE_BEFORE_ATTACK,teamTwoRole.state)

        //模拟经过250ms
//        teamOneRole.stateRestTime = 0
//        teamTwoRole.stateRestTime = 0

        combatZone.action(250)
        //相互受到伤害
        assertEquals(70,teamOneRole.role.curHP)
        assertEquals(70,teamTwoRole.role.curHP)

        assertEquals(MapRole.STATE_AFTER_ATTACK,teamOneRole.state)
        assertEquals(MapRole.STATE_AFTER_ATTACK,teamTwoRole.state)
    }

    @Test
    fun combat2Test(){
        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM,GameMap.COMBAT_COL_NUM)

        val teamOneRole = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)
        val teamTwoRole = MapRole(
            Role(RoleName.ZUO_ZU),
            belongTeam = 2)

        //两个阵营分别添加两个角色
        combatZone.addRole(teamOneRole,2,6)
        combatZone.addRole(teamTwoRole,4,1)

        combatZone.action(0)
        combatZone.action(teamOneRole.role.moveSpeed)
        assertEquals(2,teamOneRole.position.x)
        assertEquals(5,teamOneRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamOneRole.state)

        assertEquals(4,teamTwoRole.position.x)
        assertEquals(2,teamTwoRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamTwoRole.state)

        combatZone.action(0)
        combatZone.action(teamOneRole.role.moveSpeed)
        assertEquals(2,teamOneRole.position.x)
        assertEquals(4,teamOneRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamOneRole.state)

        assertEquals(4,teamTwoRole.position.x)
        assertEquals(3,teamTwoRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamTwoRole.state)

        combatZone.action(0)

        combatZone.action(teamOneRole.role.moveSpeed)
        assertEquals(3,teamOneRole.position.x)
        assertEquals(4,teamOneRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamOneRole.state)

        assertEquals(3,teamTwoRole.position.x)
        assertEquals(3,teamTwoRole.position.y)
        assertEquals(MapRole.STATE_IDLE,teamTwoRole.state)

        combatZone.action(0)
        //两个角色开始相互攻击
        assertEquals(MapRole.STATE_BEFORE_ATTACK,teamOneRole.state)
        assertEquals(MapRole.STATE_BEFORE_ATTACK,teamTwoRole.state)
        combatZone.action(teamOneRole.role.beforeAttackTime)
        assertEquals(70,teamOneRole.role.curHP)
        assertEquals(70,teamTwoRole.role.curHP)
        assertEquals(MapRole.STATE_AFTER_ATTACK,teamOneRole.state)
        assertEquals(MapRole.STATE_AFTER_ATTACK,teamTwoRole.state)

        combatZone.action(teamOneRole.role.afterAttackTime)
        assertEquals(MapRole.STATE_IDLE,teamOneRole.state)
        assertEquals(MapRole.STATE_IDLE,teamTwoRole.state)

        combatZone.action(0)
        assertEquals(MapRole.STATE_BEFORE_ATTACK,teamOneRole.state)
        assertEquals(MapRole.STATE_BEFORE_ATTACK,teamTwoRole.state)
        combatZone.action(teamOneRole.role.beforeAttackTime)
        assertEquals(40,teamOneRole.role.curHP)
        assertEquals(40,teamTwoRole.role.curHP)

        combatZone.action(teamOneRole.role.afterAttackTime)
        combatZone.action(0)
        combatZone.action(teamOneRole.role.beforeAttackTime)
        assertEquals(10,teamOneRole.role.curHP)
        assertEquals(10,teamTwoRole.role.curHP)

        combatZone.action(teamOneRole.role.afterAttackTime)
        combatZone.action(0)
        combatZone.action(teamOneRole.role.beforeAttackTime)
        assert(!teamOneRole.isAlive)
        assert(teamTwoRole.isAlive)
        assertEquals(0,teamOneRole.role.curHP)
        assertEquals(10,teamTwoRole.role.curHP)

        assert(combatZone.isCombatEnd())
    }
}