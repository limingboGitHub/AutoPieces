package com.example.autopieces.logic.map


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
    fun isRoleAmountToMax(roleLevel:Int):Boolean = roleAmount()>=roleLevel

    /**
     * 开始行动
     */
    fun action(){
        forEachCell {
            //当前攻击状态
            when(it.state){
                MapRole.STATE_IDLE->{
                    val isFindRole = findRoleToAttack(it)
                    if (isFindRole){
                        //进入前摇状态
                        it.changeState(MapRole.STATE_BEFORE_ATTACK)
                    }else{
                        //进入移动状态
                        findRoleToMove(it)
                        it.changeState(MapRole.STATE_MOVING)
                    }
                }
                MapRole.STATE_BEFORE_ATTACK->{
                    if (it.stateRestTime<=0){
                        //造成伤害
                        it.hurtRole()
                        //进入后摇
                        it.changeState(MapRole.STATE_AFTER_ATTACK)
                    }
                }
                MapRole.STATE_AFTER_ATTACK->{
                    if (it.stateRestTime<=0)
                        it.changeState(MapRole.STATE_IDLE)
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
            val toBeAttackedMapRole = cells[it.first][it.second]
            if (toBeAttackedMapRole!=null){
                mapRole.attackRoles.add(toBeAttackedMapRole)
                //攻击目标超过攻击数量上限，则寻找过程结束
                if (mapRole.attackRoles.size>=mapRole.role.attackAmount)
                    return true
            }
        }
        return false
    }

    private fun findRoleToMove(mapRole: MapRole){
        val roleX = mapRole.position.x
        val roleY = mapRole.position.y
        val offsetList = ArrayList<Pair<Int,Int>>().apply {
            var scope = mapRole.role.attackScope+1
            while (scope<row+col){
                calculateAttackScopeN(scope,this)
                if (this.isNotEmpty()){

                }

                scope++
            }
        }
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