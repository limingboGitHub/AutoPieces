package com.example.autopieces.role

object RolePool {

    private var cost1Pool = ArrayList<Pair>()
    private var cost2Pool = ArrayList<Pair>()
    private var cost3Pool = ArrayList<Pair>()
    private var cost4Pool = ArrayList<Pair>()
    private var cost5Pool = ArrayList<Pair>()

    fun init(){
        cost1Pool.clear()
        cost1Roles().forEach {
            cost1Pool.add(Pair(it,29))
        }

        cost2Pool.clear()
        cost2Roles().forEach {
            cost2Pool.add(Pair(it,22))
        }
        cost3Pool.clear()
        cost3Roles().forEach {
            cost3Pool.add(Pair(it,18))
        }
        cost4Pool.clear()
        cost4Roles().forEach {
            cost4Pool.add(Pair(it,12))
        }
        cost5Pool.clear()
        cost5Roles().forEach {
            cost5Pool.add(Pair(it,10))
        }
    }

    /**
     * 卡池中是否还有该棋子
     */
    fun haveRole(roleName:String,cost:Int):Boolean{
        val costPool = when(cost){
            1 -> cost1Pool
            2 -> cost2Pool
            3 -> cost3Pool
            4 -> cost4Pool
            else -> cost5Pool
        }
        costPool.forEach {
            if (it.name == roleName){
                return it.amount > 0
            }
        }
        return false
    }

    class Pair(val name:String,var amount:Int)
}