package com.example.autopieces.map

import android.view.View
import com.example.autopieces.utils.logE


class CombatZone(private val row:Int,private val col:Int){
    val TAG = "CombatZone"

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
        val oldRole = if (cells[y][x] == null){
            null
        }else
            cells[y][x]

        cells[y][x] = mapRole
        mapRole.position.apply {
            where = Position.POSITION_COMBAT
            this.x = x
            this.y = y
        }

        logE(TAG,"${Position.POSITION_COMBAT}区域 x:$x y:$y 添加了角色:${mapRole.role.name}")
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

    fun getSampleLevelRoles(mapRole: MapRole):ArrayList<MapRole>{
        val sampleLevelRoles = ArrayList<MapRole>()
        forEachCell {
            if (it.role.name == mapRole.role.name &&
                    it.role.level == mapRole.role.level)
                sampleLevelRoles.add(it)
        }
        return sampleLevelRoles
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

    fun isNotMyRoleCombatZone(position: Position):Boolean{
        return position.y < row/2
    }
}