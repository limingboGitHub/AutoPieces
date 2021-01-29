package com.example.autopieces.map

import android.view.View
import com.example.autopieces.utils.logE


abstract class Zone(val cellNum:Int) {
    val TAG = "Zone"

    companion object{
        const val INDEX_NONE = -1
    }

    protected val cells = Array<MapRole?>(cellNum) { null }

    abstract fun zoneBelongWhere():String

    /**
     * 遍历每个格子
     */
    fun forEachCell(doSomeThing:(mapRole:MapRole)->Unit){
        cells.forEachIndexed { index, mapRole ->
            if (mapRole!=null)
                doSomeThing.invoke(mapRole)
        }
    }

    /**
     * 添加角色，如果指定的位置有其他角色，则返回该角色
     */
    fun addRole(mapRole: MapRole,index:Int):MapRole?{
        val oldRole = if (cells[index]==null)
            null
        else
            cells[index]

        cells[index] = mapRole.apply {
            position.where = zoneBelongWhere()
            position.x = index
            logE(TAG,"${zoneBelongWhere()}区域 x:$index 添加了角色:${role.name}")
        }
        return oldRole
    }

    fun addRoleToFirstNotNull(mapRoleToAdd: MapRole){
        val index = getFirstEmptyIndex()
        if (index!= INDEX_NONE){
            addRole(mapRoleToAdd,index)
        }
    }

    fun removeRole(index: Int){
        cells[index] = null
    }

    fun removeRole(roleToRemove: MapRole) {
        cells.forEachIndexed { index, role ->
            if (role == roleToRemove){
                cells[index] = null
                return
            }
        }
    }

    /**
     * 获取第一个空位置的index
     * @see INDEX_NONE 表示没有空位置
     */
    fun getFirstEmptyIndex() : Int{
        cells.forEachIndexed { index, role ->
            if (role == null)
                return index
        }
        return INDEX_NONE
    }

    /**
     * 通过index查询对应位置的角色
     */
    fun getRoleByIndex(index:Int):MapRole?{
        return if (index>=0 && index < cells.size)
            cells[index]
        else
            null
    }

    /**
     * 找指定角色的位置
     */
    fun getRoleIndex(roleToFind:MapRole):Int{
        cells.forEachIndexed { index, role ->
            if (roleToFind == role)
                return index
        }
        return -1
    }

    fun getMapRoleByView(view:View):MapRole?{
        cells.filterNotNull().forEach {
            if (it.roleView == view)
                return it
        }
        return null
    }

    /**
     * 角色数量
     */
    fun roleNum():Int = cells.filterNotNull().sumBy { 1 }
}