package com.example.autopieces.role

import com.example.autopieces.Player
import com.example.autopieces.utils.logD
import com.example.autopieces.utils.logE

fun randomCreateRoles(player: Player):List<Role>{
    val roles = ArrayList<Role>()


    val roleRefreshRate = RoleRefreshRate.refreshRate[player.getLevel()-1]

    logD("refreshRole","lv ${player.getLevel()} 刷新概率:")
    roleRefreshRate.forEachIndexed { index, i ->
        logD("refreshRole","${index+1} 费:$i %")
    }

    repeat(5){
        //先决定卡的费用
        val randomRateNum = (1..100).random()
        logD("refreshRole","随机数:$randomRateNum")
        var roleCost = 0
        var rateSum = 0
        roleRefreshRate.forEachIndexed { index, rateNum ->
            if (randomRateNum in rateSum+1 .. rateSum+rateNum)
                roleCost = index
            rateSum +=rateNum
        }
        logD("refreshRole","此次费率:${roleCost+1}")

        //从对应费用卡组中随机选卡
        val rolesToSelect = when(roleCost){
            0 -> cost1Roles()
            1 -> cost2Roles()
            2 -> cost3Roles()
            3 -> cost4Roles()
            else -> cost5Roles()
        }
        val randomIndex = (0 until rolesToSelect.size).random()
        val role = Role(rolesToSelect[randomIndex],roleCost+1)
        roles.add(role)
        logE("RoleCreator","index:${role.name}")
    }
    return roles
}