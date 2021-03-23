package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.calculateAttackScopeAll

class FindRoleTool {

    companion object{
        /**
         * 寻找角色进行攻击的结果
         */
        val RESULT_SUCCESS = 0
        val RESULT_WAIT = 1
        val RESULT_FAILED = 2
    }
}

/**
 * 寻找攻击目标，并添加至攻击列表
 * @return 寻找结果
 * @see FindRoleTool.RESULT_SUCCESS
 */
fun CombatZone.findRoleToAttack(mapRole: MapRole):Int{
    val roleX = mapRole.position.x
    val roleY = mapRole.position.y
    //在攻击范围内寻找目标
    val offsetList = calculateAttackScopeAll(mapRole.role.attackScope)
    calculateCellIndex(roleX,roleY,offsetList).forEach {
        val toBeAttackedMapRole = getRoleByIndex(it.first,it.second)
        if (toBeAttackedMapRole!=null){
            if (toBeAttackedMapRole.flag == MapRole.FLAG_MOVE_PLACEHOLDER)
                return FindRoleTool.RESULT_WAIT
            //攻击目标超过攻击数量上限，则寻找过程结束
            if (mapRole.beAttackedRoles.size>=mapRole.role.attackAmount)
                return FindRoleTool.RESULT_SUCCESS
            mapRole.beAttackedRoles.add(toBeAttackedMapRole)
            if (mapRole.beAttackedRoles.size>=mapRole.role.attackAmount)
                return FindRoleTool.RESULT_SUCCESS
        }
    }
    return FindRoleTool.RESULT_FAILED
}

private fun CombatZone.calculateCellIndex(x:Int,y:Int,offsetList: List<Pair<Int, Int>>) =
    ArrayList<Pair<Int,Int>>().apply {
        offsetList.forEach {
            //筛选出地图界内的坐标索引
            if (x+it.first in 0 until col &&
                y+it.second in 0 until row)
                add(Pair(x+it.first,y+it.second))
        }
    }