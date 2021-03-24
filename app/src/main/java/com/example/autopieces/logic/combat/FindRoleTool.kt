package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.calculateAttackScopeAll
import com.example.autopieces.logic.map.calculateAttackScopeN
import kotlin.math.abs

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
 * 寻找最近的目标，进行移动路径规划
 */
//fun CombatZone.findRoleToMove(roleX:Int,roleY:Int):Pair<Int,Int>?{
//    val closestPoint = searchClosestRole(roleX,roleY)?:return null
//    //角色只能朝上下左右4个方向移动，计算那种移动距离目标最近
//    listOf(
//        Pair(0,-1),
//        Pair(0,1),
//        Pair(-1,0),
//        Pair(1,0)
//    ).forEach{
//        val toMoveX = roleX+it.first
//        val toMoveY = roleY+it.second
//        if (toMoveX in 0 until col &&
//            toMoveY in 0 until row &&
//            getRoleByIndex(toMoveX,toMoveY)==null){
//            //计算到目标点的轨迹
//            val movePath = ArrayList<Pair<Int,Int>>()
//            movePath.add(Pair(toMoveX,toMoveY))
//            var pathStartX = toMoveX
//            var pathStartY = toMoveY
//            while (pathStartX!=closestPoint.first && pathStartY!=closestPoint.second){
//                pathStartX
//            }
//        }
//
//    }
//}

/**
 * 找到最近的目标
 */
private fun CombatZone.searchClosestRole(roleX: Int, roleY: Int): Pair<Int, Int>? {
    val offsetList = ArrayList<Pair<Int,Int>>()
    var scope = 1
    while (scope<row+col){
        calculateAttackScopeN(scope,offsetList)
        offsetList.forEach {
            //在地图范围内
            val x = roleX + it.first
            val y = roleY + it.second
            if (x in 0 until col
                && y in 0 until row){
                //有目标，且是一个棋子角色
                val targetMapRole = getRoleByIndex(x,y)
                if (targetMapRole!=null
                    && targetMapRole.flag == MapRole.FLAG_ROLE)
                    return Pair(x,y)
            }
        }
        scope++
    }
    return null
}

/**
 * 寻找攻击目标，并添加至攻击列表
 * @return 寻找结果
 * @see FindRoleTool.RESULT_SUCCESS
 */
fun CombatZone.findRoleToAttack(mapRole: MapRole):Int{
    val roleX = mapRole.position.x
    val roleY = mapRole.position.y

    var result = FindRoleTool.RESULT_FAILED
    //在攻击范围内寻找目标
    val offsetList = calculateAttackScopeAll(mapRole.role.attackScope)
    calculateCellIndex(roleX,roleY,offsetList).forEach {
        val toBeAttackedMapRole = getRoleByIndex(it.first,it.second)
        if (toBeAttackedMapRole!=null && toBeAttackedMapRole.belongTeam!= mapRole.belongTeam){
            if (toBeAttackedMapRole.flag == MapRole.FLAG_MOVE_PLACEHOLDER)
                result =  FindRoleTool.RESULT_WAIT
            //攻击目标超过攻击数量上限，则寻找过程结束
            if (mapRole.beAttackedRoles.size>=mapRole.role.attackAmount)
                return FindRoleTool.RESULT_SUCCESS
            mapRole.beAttackedRoles.add(toBeAttackedMapRole)
            if (mapRole.beAttackedRoles.size>=mapRole.role.attackAmount)
                return FindRoleTool.RESULT_SUCCESS
        }
    }
    return result
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

