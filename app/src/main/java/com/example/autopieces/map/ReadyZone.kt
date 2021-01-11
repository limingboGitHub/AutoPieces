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

    fun getReadyRoleCount() = readyRoles.filterNotNull().sumBy { 1 }


}