package com.example.autopieces.role

import com.example.autopieces.utils.logE
import kotlin.random.Random

fun randomCreateRoles(count:Int):List<Role>{
    val roles = ArrayList<Role>()

    val rolesToSelect = cost1Roles()
    repeat(count){
        val randomIndex = (0 until rolesToSelect.size).random()
        val role = Role(rolesToSelect[randomIndex],(1..5).random())
        roles.add(role)
        logE("RoleCreator","index:${role.name}")
    }
    return roles
}