package com.example.autopieces.logic.role

import com.example.autopieces.logic.map.MapRole

//import com.example.autopieces.utils.logD
//import com.example.autopieces.utils.logE

fun createSameRole(roleName: String):List<Role>{
    return ArrayList<Role>().apply {
        repeat(5){
            add(Role(roleName))
        }
    }
}

fun createRandomRoles(level:Int):List<Role>{
    val roles = ArrayList<Role>()
    repeat(5){
        roles.add(randomCreateRole(level))
    }
    return roles
}

private fun randomCreateRole(level:Int):Role{
    //获取棋子的刷新概率表
    val roleRefreshRate = RoleRefreshRate.refreshRate[level-1]

    //先决定卡的费用
    val randomRateNum = (1..100).random()
    var roleCost = 0
    var rateSum = 0
    roleRefreshRate.forEachIndexed { index, rateNum ->
        if (randomRateNum in rateSum+1 .. rateSum+rateNum)
            roleCost = index+1
        rateSum +=rateNum
    }

    //从对应费用卡组中随机选卡
    val rolesToSelect = RoleName.rolesArray[roleCost-1]
    val randomIndex = rolesToSelect.indices.random()
    val roleNameToSelect = rolesToSelect[randomIndex]
    //从卡池中取出对应棋子，如果卡池取完了，则重新随机
    val roleFromPool = RolePool.getRole(roleNameToSelect)
    return roleFromPool ?: randomCreateRole(level)
}

fun createMovePlaceholder():MapRole{
    return MapRole(Role(""),flag = MapRole.FLAG_MOVE_PLACEHOLDER)
}