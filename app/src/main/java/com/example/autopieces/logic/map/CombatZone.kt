package com.example.autopieces.logic.map

import com.example.autopieces.cpp.MoveMethod

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
     * 寻找能够达到的，距离最近的目标
     */
    fun findClosetTarget(mapRole: MapRole):Pair<Int,Int>?{
        var minMovePath : IntArray? = null
        for (role in getAllOtherTeamRole(mapRole)){
            val movePath = MoveMethod.calculateMovePath(
                mapRole.position.x,mapRole.position.y,
                role.position.x,role.position.y,
                toIntArrayMap(),row,col)
            if (minMovePath==null || movePath.size< minMovePath.size){
                minMovePath = movePath
            }
        }
        if (minMovePath!=null){
            return Pair(minMovePath[minMovePath.size-2],minMovePath[minMovePath.size-1])
        }else
            return null
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

    fun toIntArrayMap():IntArray{
        val map = IntArray(row*col)
        for (y in 0 until row){
            for (x in 0 until col){
                map[y*col + x] = if (cells[y][x]==null) 0 else 1
            }
        }
        return map
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