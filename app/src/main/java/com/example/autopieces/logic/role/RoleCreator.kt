package com.example.autopieces.logic.role

import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.PlaceholderMapRole

//import com.example.autopieces.utils.logD
//import com.example.autopieces.utils.logE

fun createSameRole(roleName: String):List<Role>{
    return ArrayList<Role>().apply {
        repeat(5){
            add(Role(roleName))
        }
    }
}

fun randomCreateRoles(level:Int):List<Role>{
    val roles = ArrayList<Role>()



    repeat(5){
        roles.add(randomCreateRole(level))
    }
    return roles
}

private fun randomCreateRole(level:Int):Role{
    val roleRefreshRate = RoleRefreshRate.refreshRate[level-1]

//    logD("refreshRole","lv ${player.getLevel()} 刷新概率:")
    roleRefreshRate.forEachIndexed { index, i ->
//        logD("refreshRole","${index+1} 费:$i %")
    }
    //先决定卡的费用
    val randomRateNum = (1..100).random()
//        logD("refreshRole","随机数:$randomRateNum")
    var roleCost = 0
    var rateSum = 0
    roleRefreshRate.forEachIndexed { index, rateNum ->
        if (randomRateNum in rateSum+1 .. rateSum+rateNum)
            roleCost = index+1
        rateSum +=rateNum
    }
//        logD("refreshRole","此次费率:${roleCost+1}")

    //从对应费用卡组中随机选卡
    val rolesToSelect = RoleName.rolesArray[roleCost-1]
    val randomIndex = rolesToSelect.indices.random()
    val roleNameToSelect = rolesToSelect[randomIndex]
    //从卡池中取出对应棋子，如果卡池取完了，则重新随机
    val roleFromPool = RolePool.getRole(roleNameToSelect)
    return roleFromPool ?: randomCreateRole(level)
}

fun placeholderMapRole():PlaceholderMapRole{
    return PlaceholderMapRole(Role(""))
}