package com.example.autopieces.map

/**
 * 准备区
 */
class ReadyZone {

    companion object{
        const val READY_ZONE_FULL = -1
    }

    private val cells = Array<MapRole?>(MapDraw.READY_ZONE_NUM) { null }

    fun forEachCell(doSomeThing:(mapRole:MapRole)->Unit){
        cells.forEachIndexed { index, mapRole ->
            if (mapRole!=null)
                doSomeThing.invoke(mapRole)
        }
    }

    fun addRole(roleToAdd: MapRole):Boolean{
        //放入对应空位置
        cells.forEachIndexed { index, role ->
            if (role==null){
                cells[index] = roleToAdd
                return true
            }
        }
        return false
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
     * 通过index查询对应位置的角色
     */
    fun getRoleByIndex(index:Int):MapRole?{
        return if (index>=0 && index < cells.size)
            cells[index]
        else
            null
    }

    /**
     * 获取第一个空位置的index
     * @see READY_ZONE_FULL 表示没有空位置
     */
    fun getFirstEmptyIndex() : Int{
        cells.forEachIndexed { index, role ->
            if (role == null)
                return index
        }
        return READY_ZONE_FULL
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

    fun getReadyRoleCount() = cells.filterNotNull().sumBy { 1 }


}