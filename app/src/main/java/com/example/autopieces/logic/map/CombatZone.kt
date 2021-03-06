package com.example.autopieces.logic.map

import com.example.autopieces.utils.logE
import kotlin.math.abs
import kotlin.math.max


class CombatZone(row:Int,col:Int) : TwoDimensionalZone(row,col){

    override fun zoneBelongWhere(): String {
        return Position.POSITION_COMBAT
    }

    /**
     * 判断是否是自己的战斗半区
     */
    fun isNotMyRoleCombatZone(position: Position):Boolean{
        return position.y < row/2
    }

    /**
     * 角色数量是否达到上限
     */
    fun isTeamAmountToMax(roleLevel:Int):Boolean = teamOneAmount()>=roleLevel

    /**
     * 开始行动
     */
    fun action(time:Int){
        getAllRole().forEach {
            if (!it.isAlive)
                return@forEach

            it.stateRestTime = (it.stateRestTime-time).coerceAtLeast(0)
            //当前攻击状态
            when(it.state){
                MapRole.STATE_IDLE->{
                    val isFindRole = findRoleToAttack(it)
                    if (isFindRole){
                        //进入前摇状态
                        it.changeState(MapRole.STATE_BEFORE_ATTACK)
                    }else{
                        //进入移动状态
                        findRoleToMove(it)?.apply {
//                            addRole(placeholderMapRole(),first,second)
                            it.moveTarget = this
                            it.changeState(MapRole.STATE_MOVING)
                        }
                    }
                }
                MapRole.STATE_BEFORE_ATTACK->{
                    if (it.stateRestTime<=0){
                        //造成伤害
                        it.hurtRole()
                        //判定攻击者或者被攻击者是否死亡
                        if (!it.isAlive)
                            removeRole(it)
                        it.attackRoles.forEach { attackRole->
                            if (!attackRole.isAlive)
                                removeRole(attackRole)
                        }
                        //进入后摇
                        it.changeState(MapRole.STATE_AFTER_ATTACK)
                    }
                }
                MapRole.STATE_AFTER_ATTACK->{
                    if (it.stateRestTime<=0)
                        it.changeState(MapRole.STATE_IDLE)
                }
                MapRole.STATE_MOVING ->{
                    if (it.stateRestTime<=0){
                        it.moveTarget?.apply {
                            //从区域中旧位置删除，添加到新位置
                            removeRole(it)
                            addRole(it,this.first,this.second)
                        }
                        it.moveTarget = null
                        it.changeState(MapRole.STATE_IDLE)
                        logE(TAG,"${it.role.name}移动到了(${it.position.x},${it.position.y})")
                    }
                }
            }
        }
    }


    /**
     * 寻找攻击目标，并添加至攻击列表
     * @return true 找到目标 false 未找到
     */
    private fun findRoleToAttack(mapRole: MapRole):Boolean{
        val roleX = mapRole.position.x
        val roleY = mapRole.position.y
        //在攻击范围内寻找目标
        val offsetList = calculateAttackScopeAll(mapRole.role.attackScope)
        calculateCellIndex(roleX,roleY,offsetList).forEach {
            val toBeAttackedMapRole = cells[it.second][it.first]
            if (toBeAttackedMapRole!=null){
                //攻击目标超过攻击数量上限，则寻找过程结束
                if (mapRole.attackRoles.size>=mapRole.role.attackAmount)
                    return true
                mapRole.attackRoles.add(toBeAttackedMapRole)
                if (mapRole.attackRoles.size>=mapRole.role.attackAmount)
                    return true
            }
        }
        return false
    }

    fun findRoleToMove(mapRole: MapRole):Pair<Int,Int>?{
        val targetPosition = searchClosetTarget(mapRole)
        if (targetPosition!=null){
            var toMovePosition : Pair<Int,Int>? = null
            var minDistance = 0;
            var minLongerSide = 0;
            //角色只能朝上下左右4个方向移动，计算那种移动距离目标最近
            listOf(
                Pair(0,-1),
                Pair(0,1),
                Pair(-1,0),
                Pair(1,0)
            ).forEach {
                val toMoveX = mapRole.position.x+it.first
                val toMoveY = mapRole.position.y+it.second
                if (toMoveX in 0 until col &&
                    toMoveY in 0 until row &&
                    cells[toMoveY][toMoveX]==null){
                    //计算移动后的坐标到目标的距离
                    val xDistance = abs(targetPosition.first-toMoveX)
                    val yDistance = abs(targetPosition.second-toMoveY)
                    val distance = xDistance + yDistance
                    val longerSide = max(xDistance,yDistance)
                    //找到距离目标最近的移动方式
                    if (toMovePosition==null){
                        toMovePosition = Pair(toMoveX,toMoveY)
                        minDistance = distance
                        minLongerSide = longerSide
                    }
                    if (distance<minDistance
                        || (distance == minDistance && longerSide < minLongerSide)
                    ){
                        toMovePosition = Pair(toMoveX,toMoveY)
                        minDistance = distance
                        minLongerSide = longerSide
                    }
                }
            }
            return toMovePosition
        }
        return null
    }

    /**
     * 寻找距离最近的目标
     */
    private fun searchClosetTarget(mapRole: MapRole):Pair<Int,Int>?{
        val roleX = mapRole.position.x
        val roleY = mapRole.position.y
        val offsetList = ArrayList<Pair<Int,Int>>()
        var scope = mapRole.role.attackScope+1
        while (scope<row+col){
            calculateAttackScopeN(scope,offsetList)
            offsetList.forEach {
                //在地图范围内,且有目标
                val x = roleX + it.first
                val y = roleY + it.second
                if (x in 0 until col &&
                    y in 0 until row &&
                    cells[y][x]!=null){
                    return Pair(x,y)
                }
            }
            scope++
        }
        return null
    }

    private fun calculateCellIndex(x:Int,y:Int,offsetList: List<Pair<Int, Int>>) =
        ArrayList<Pair<Int,Int>>().apply {
            offsetList.forEach {
                //筛选出地图界内的坐标索引
                if (x+it.first in 0 until col &&
                    y+it.second in 0 until row)
                    add(Pair(x+it.first,y+it.second))
            }
        }

    /**
     * 判断战斗是否结束
     */
    fun isCombatEnd():Boolean {
        var team = 0
        getAllRole()
            .filter { it.isAlive }
            .forEach {
            if (team == 0)
                team = it.belongTeam
            else if (team != it.belongTeam){
                return false
            }
        }
        return true
    }

}

/**
 * 计算攻击范围
 * 攻击范围1 表示求解 |x|+|y|<=1   {x=1,y=0},{x=-1,y=0},{x=0,y=1},{x=0,y=-1}
 * 攻击范围2 表示求解 |x|+|y|<=2
 * 攻击范围n 表示求解 |x|+|y|<=n
 */
fun calculateAttackScopeAll(n:Int):List<Pair<Int,Int>>{
    val solution = ArrayList<Pair<Int,Int>>()
    var scope = 1
    while (scope<=n){
        calculateAttackScopeN(scope,solution)
        scope++
    }
    return solution
}

/**
 * 计算攻击距离刚好为n时的坐标偏移值
 */
fun calculateAttackScopeN(n:Int,solution:ArrayList<Pair<Int,Int>>){
    val xyPairList = solveXSumYEqualN(n)
    xyPairList.forEach {
        solveAbsoluteValue(it.first,it.second,solution)
    }
}

/**
 * 求解 x+y=n 解的各种组合(x>=0,y>=0)
 */
private fun solveXSumYEqualN(n: Int):ArrayList<Pair<Int,Int>>{
    val solution = ArrayList<Pair<Int,Int>>()
    var x = 0
    while (x<=n){
        solution.add(Pair(x,n-x))
        x++
    }
    return solution
}

/**
 * 求解x,y绝对值
 * {|0|,|1|} -> {0,1},{0,-1}
 * {|1|,|1|} -> {1,1},{1,-1},{-1,1},{-1,-1}
 */
private fun solveAbsoluteValue(x:Int, y:Int, solution:ArrayList<Pair<Int,Int>>){
    //+x
    if (x!=0 || y!=0)
        solution.add(Pair(x,y))
    if (y!=0)
        solution.add(Pair(x,-y))
    //-x
    if (x!=0){
        solution.add(Pair(-x,y))
        if (y!=0)
            solution.add(Pair(-x,-y))
    }
}