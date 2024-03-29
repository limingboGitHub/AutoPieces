package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName
import org.junit.Assert.*
import org.junit.Assert
import org.junit.Test

class CombatTest {

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

        val combat = Combat(combatZone)
        //行动
        combat.action(0)

        Assert.assertEquals(MapRole.STATE_MOVING, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_MOVING, teamTwoRole.state)

        //模拟过了300ms
        combat.action(teamOneRole.role.moveSpeed)

        Assert.assertEquals(0, teamOneRole.position.x)
        Assert.assertEquals(3, teamOneRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)

        Assert.assertEquals(0, teamTwoRole.position.x)
        Assert.assertEquals(4, teamTwoRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        //已经相遇，再次行动，应该开始相互攻击
        combat.action(0)

        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamTwoRole.state)

        //模拟经过250ms
        combat.action(250)
        //相互受到伤害
        Assert.assertEquals(70, teamOneRole.role.curHP)
        Assert.assertEquals(70, teamTwoRole.role.curHP)

        Assert.assertEquals(MapRole.STATE_AFTER_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_AFTER_ATTACK, teamTwoRole.state)
    }

    /**
     * 两个角色坐标[2,6],[4,1]，战斗测试
     */
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

        //开启一场战斗
        val combat = Combat(combatZone)
        //各个角色行动一次
        combat.action(0)
        //模拟经过了500ms，再看看行动结果
        combat.action(teamOneRole.role.moveSpeed)
        Assert.assertEquals(2, teamOneRole.position.x)
        Assert.assertEquals(5, teamOneRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)

        Assert.assertEquals(4, teamTwoRole.position.x)
        Assert.assertEquals(2, teamTwoRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        //再次行动，并经过了500ms
        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)

        Assert.assertEquals(2, teamOneRole.position.x)
        Assert.assertEquals(4, teamOneRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)

        Assert.assertEquals(4, teamTwoRole.position.x)
        Assert.assertEquals(3, teamTwoRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        //再次行动，并经过了500ms,这次两个目标应该是相遇了
        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)
        Assert.assertEquals(3, teamOneRole.position.x)
        Assert.assertEquals(4, teamOneRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)

        Assert.assertEquals(3, teamTwoRole.position.x)
        Assert.assertEquals(3, teamTwoRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        //再次行动，两个角色准备开始相互攻击
        combat.action(0)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamTwoRole.state)

        //模拟经过了攻击前摇所需的时间，角色应该受伤了
        combat.action(teamOneRole.role.beforeAttackTime)
        Assert.assertEquals(70, teamOneRole.role.curHP)
        Assert.assertEquals(70, teamTwoRole.role.curHP)
        Assert.assertEquals(MapRole.STATE_AFTER_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_AFTER_ATTACK, teamTwoRole.state)

        //模拟经过了攻击后摇所需时间
        combat.action(teamOneRole.role.afterAttackTime)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        combat.action(0)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamTwoRole.state)
        combat.action(teamOneRole.role.beforeAttackTime)
        Assert.assertEquals(40, teamOneRole.role.curHP)
        Assert.assertEquals(40, teamTwoRole.role.curHP)

        combat.action(teamOneRole.role.afterAttackTime)
        combat.action(0)
        combat.action(teamOneRole.role.beforeAttackTime)
        Assert.assertEquals(10, teamOneRole.role.curHP)
        Assert.assertEquals(10, teamTwoRole.role.curHP)

        combat.action(teamOneRole.role.afterAttackTime)
        combat.action(0)
        combat.action(teamOneRole.role.beforeAttackTime)
        assert(!teamOneRole.isAlive)
        assert(teamTwoRole.isAlive)
        Assert.assertEquals(0, teamOneRole.role.curHP)
        Assert.assertEquals(10, teamTwoRole.role.curHP)

        //战斗结束
        assert(combatZone.isCombatEnd())
    }

    /**
     * 两个角色坐标[2,5],[4,1]，战斗测试
     */
    @Test
    fun combat3Test(){
        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM,GameMap.COMBAT_COL_NUM)

        val teamOneRole = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)
        val teamTwoRole = MapRole(
            Role(RoleName.ZUO_ZU),
            belongTeam = 2)

        //两个阵营分别添加两个角色
        combatZone.addRole(teamOneRole,2,5)
        combatZone.addRole(teamTwoRole,4,1)

        //开启一场战斗
        val combat = Combat(combatZone)
        //各个角色行动一次
        combat.action(0)
        //模拟经过了500ms，再看看行动结果
        combat.action(teamOneRole.role.moveSpeed)
        Assert.assertEquals(2, teamOneRole.position.x)
        Assert.assertEquals(4, teamOneRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)

        Assert.assertEquals(4, teamTwoRole.position.x)
        Assert.assertEquals(2, teamTwoRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        //再次行动，并经过了500ms
        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)

        Assert.assertEquals(2, teamOneRole.position.x)
        Assert.assertEquals(3, teamOneRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)

        Assert.assertEquals(4, teamTwoRole.position.x)
        Assert.assertEquals(3, teamTwoRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        //再次行动，并经过了500ms,这次两个目标应该是相遇了
        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)
        Assert.assertEquals(3, teamOneRole.position.x)
        Assert.assertEquals(3, teamOneRole.position.y)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)

        Assert.assertEquals(4, teamTwoRole.position.x)
        Assert.assertEquals(3, teamTwoRole.position.y)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamTwoRole.state)

        //再次行动，两个角色准备开始相互攻击
        combat.action(0)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamTwoRole.state)

        //模拟经过了攻击前摇所需的时间，角色应该受伤了
        combat.action(teamOneRole.role.beforeAttackTime)
        Assert.assertEquals(70, teamOneRole.role.curHP)
        Assert.assertEquals(70, teamTwoRole.role.curHP)
        Assert.assertEquals(MapRole.STATE_AFTER_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_AFTER_ATTACK, teamTwoRole.state)

        //模拟经过了攻击后摇所需时间
        combat.action(teamOneRole.role.afterAttackTime)
        Assert.assertEquals(MapRole.STATE_IDLE, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_IDLE, teamTwoRole.state)

        combat.action(0)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamOneRole.state)
        Assert.assertEquals(MapRole.STATE_BEFORE_ATTACK, teamTwoRole.state)
        combat.action(teamOneRole.role.beforeAttackTime)
        Assert.assertEquals(40, teamOneRole.role.curHP)
        Assert.assertEquals(40, teamTwoRole.role.curHP)

        combat.action(teamOneRole.role.afterAttackTime)
        combat.action(0)
        combat.action(teamOneRole.role.beforeAttackTime)
        Assert.assertEquals(10, teamOneRole.role.curHP)
        Assert.assertEquals(10, teamTwoRole.role.curHP)

        combat.action(teamOneRole.role.afterAttackTime)
        combat.action(0)
        combat.action(teamOneRole.role.beforeAttackTime)
        assert(teamOneRole.isAlive)
        assert(!teamTwoRole.isAlive)
        Assert.assertEquals(10, teamOneRole.role.curHP)
        Assert.assertEquals(0, teamTwoRole.role.curHP)

        //战斗结束
        assert(combatZone.isCombatEnd())
    }

    /**
     * 两个阵营1的角色，一个阵营2的角色
     */
    @Test
    fun combat4Test(){
        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM,GameMap.COMBAT_COL_NUM)

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
        combatZone.addRole(teamOneRole,1,5)
        combatZone.addRole(teamOneRole2,2,5)
        combatZone.addRole(teamTwoRole,4,1)

        //开启一场战斗
        val combat = Combat(combatZone)
        //各个角色行动一次
        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)
        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)
        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)

        assertEquals(1,teamOneRole.position.x)
        assertEquals(2,teamOneRole.position.y)

        assertEquals(3,teamOneRole2.position.x)
        assertEquals(3,teamOneRole2.position.y)

        assertEquals(4,teamTwoRole.position.x)
        assertEquals(3,teamTwoRole.position.y)

        combat.action(0)
        combat.action(teamOneRole.role.moveSpeed)


    }
}