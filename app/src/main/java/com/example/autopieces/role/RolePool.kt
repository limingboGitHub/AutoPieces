package com.example.autopieces.role

import kotlin.math.cos

object RolePool {

    private var costPool = arrayOf(
            ArrayList<Pair>(),
            ArrayList<Pair>(),
            ArrayList<Pair>(),
            ArrayList<Pair>(),
            ArrayList<Pair>()
    )

    private var roleMaxAmountArray = arrayOf(29,22,18,12,10)

    fun init(){
        //初始化卡池中棋子对应的数量
        costPool.forEachIndexed { index, arrayList ->
          val rolesArray = RoleName.rolesArray[index]
          rolesArray.forEach {
              arrayList.add(Pair(it, roleMaxAmountArray[index]))
          }
        }
    }

    /**
     * 从卡池中取棋子
     */
    fun getRole(roleName:String):Role?{
        val rolePair = findRole(roleName)
        return if (rolePair.amount > 0){
            rolePair.amount--
            Role(roleName, roleCost(roleName))
        }else
            null
    }

    fun getRoleAmount(roleName: String):Int{
        return findRole(roleName).amount
    }

    private fun findRole(roleName:String):Pair{
        val cost = roleCost(roleName)

        costPool[cost-1].forEach {
            if (it.name == roleName){
                return it
            }
        }
        return Pair(roleName,-1)
    }

    class Pair(val name:String,var amount:Int)
}