package com.example.autopieces.logic.map

abstract class OneDimensionalZone(x:Int) : TwoDimensionalZone(1,x){

    fun addRole(mapRole: MapRole,x: Int):MapRole?{
        return super.addRole(mapRole,x,0)
    }

    fun isFull():Boolean{
        return cells[0].filterNotNull().size == col
    }

    fun isNotFull():Boolean = cells[0].filterNotNull().size < col

    fun getRoleByIndex(x: Int):MapRole?{
        return super.getRoleByIndex(x,0)
    }

    /**
     * 找指定角色的位置
     */
    fun getRoleIndex(roleToFind:MapRole):Int{
        cells[0].forEachIndexed { index, role ->
            if (roleToFind == role)
                return index
        }
        return -1
    }
}