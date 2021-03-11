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
                0->{
                    findRoleAndAttack(it)
                }
                1->{}
                2->{}
            }
            it.attackStateRestTime
        }
    }

    private fun findRoleAndAttack(it: MapRole) {
        val roleX = it.position.x
        val roleY = it.position.y
        //先在攻击范围1内寻找
        //攻击范围1 表示求解 |x|+|y|=1   {x=1,y=0},{x=-1,y=0},{x=0,y=1},{x=0,y=-1}
        //攻击范围2 表示求解 |x|+|y|=2
        //攻击范围n 表示求解 |x|+|y|=n

    }

    private fun answer(n:Int):List<Pair<Int,Int>>{
        val solution = ArrayList<Pair<Int,Int>>()

        var x = 0

        return listOf()
    }
}