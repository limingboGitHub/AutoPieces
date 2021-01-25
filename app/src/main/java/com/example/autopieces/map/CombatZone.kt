package com.example.autopieces.map

import android.view.View


class CombatZone(row:Int, col:Int){

    private val cells : Array<Array<MapRole?>> = Array(row) {
        arrayOfNulls<MapRole?>(col)
    }

    fun forEachCell(doSomeThing:(mapRole:MapRole)->Unit){
        cells.forEachIndexed { col, arrayOfRoles ->
            arrayOfRoles.forEachIndexed { row, role ->
                if (role!=null)
                    doSomeThing.invoke(role)
            }
        }
    }

    /**
     * 战斗区域增加角色时，如果该区域有其他角色，则返回该角色
     */
    fun addRole(mapRole: MapRole,x:Int,y:Int):MapRole?{
        val oldRole = if (cells[x][y] == null){
            null
        }else
            cells[x][y]

        cells[x][y] = mapRole

        return oldRole
    }

    fun removeRole(roleToRemove: MapRole) {
        cells.forEachIndexed { col, arrayOfRoles ->
            arrayOfRoles.forEachIndexed { row, role ->
                if (role == roleToRemove){
                    cells[col][row] = null
                    return
                }
            }
        }
    }

    fun getMapRoleByView(view:View):MapRole?{
        cells.forEach { array->
            array.filterNotNull().forEach {
                if (it.roleView == view)
                    return it
            }
        }
        return null
    }
}