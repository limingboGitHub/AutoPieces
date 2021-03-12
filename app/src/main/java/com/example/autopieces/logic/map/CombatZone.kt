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
            when(it.attackState){
                MapRole.ATTACK_STATE_IDLE->{
                    val isFindRole = findRoleAndAttack(it)
                    if (isFindRole){
                        //进入前摇状态
                        it.changeAttackState(MapRole.ATTACK_STATE_BEFORE)
                    }else{
                        //进入移动状态
                    }
                }
                MapRole.ATTACK_STATE_BEFORE->{
                    if (it.attackStateRestTime<=0){
                        //造成伤害
                        it.hurtRole()
                        //进入后摇
                        it.changeAttackState(MapRole.ATTACK_STATE_AFTER)
                    }
                }
                MapRole.ATTACK_STATE_AFTER->{
                    if (it.attackStateRestTime<=0)
                        it.changeAttackState(MapRole.ATTACK_STATE_IDLE)
                }
            }
        }
    }


    /**
     * 寻找攻击目标，并添加至攻击列表
     * @return true 找到目标 false 未找到
     */
    private fun findRoleAndAttack(mapRole: MapRole):Boolean{
        val roleX = mapRole.position.x
        val roleY = mapRole.position.y
        //在攻击范围内寻找目标
        val offsetList = calculateAttackScope(mapRole.role.attackScope)
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
fun calculateAttackScope(n:Int):List<Pair<Int,Int>>{
    val solution = ArrayList<Pair<Int,Int>>()

    var x = 0
    while(x<=n){
        var y = 0
        while(y<=n-x){
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

            y++
        }
        x++
    }

    return solution
}