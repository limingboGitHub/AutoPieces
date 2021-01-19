package com.example.autopieces.map

import com.example.autopieces.role.Role

/**
 * 准备区
 */
class ReadyZone {

    companion object{
        const val READY_ZONE_FULL = -1
    }

    private val readyRoles = Array<Role?>(MapDraw.READY_ZONE_NUM) { null }

    fun addRole(roleToAdd: Role):Boolean{
        //放入对应空位置
        readyRoles.forEachIndexed { index, role ->
            if (role==null){
                readyRoles[index] = roleToAdd
                return true
            }
        }
        return false
    }

    fun removeRole(roleToRemove: Role) {
        readyRoles.forEachIndexed { index, role ->
            if (role == roleToRemove){
                readyRoles[index] = null
                return
            }
        }
    }

    /**
     * 通过index查询对应位置的角色
     */
    fun getRoleByIndex(index:Int):Role?{
        return if (index>=0 && index < readyRoles.size)
            readyRoles[index]
        else
            null
    }

    /**
     * 获取第一个空位置的index
     * @see READY_ZONE_FULL 表示没有空位置
     */
    fun getFirstEmptyIndex() : Int{
        readyRoles.forEachIndexed { index, role ->
            if (role == null)
                return index
        }
        return READY_ZONE_FULL
    }

    /**
     * 找指定角色的位置
     */
    fun getRoleIndex(roleToFind:Role):Int{
        readyRoles.forEachIndexed { index, role ->
            if (roleToFind == role)
                return index
        }
        return -1
    }

    fun getReadyRoleCount() = readyRoles.filterNotNull().sumBy { 1 }


}