package com.example.autopieces.map


class RoleMatrix(row:Int,col:Int) {


    private val rolesInMap : Array<Array<MapRole?>> = Array(row) {
        arrayOfNulls<MapRole?>(col)
    }

    fun removeRole(roleToRemove: MapRole) {
        rolesInMap.forEachIndexed { col, arrayOfRoles ->
            arrayOfRoles.forEachIndexed { row, role ->
                if (role == roleToRemove){
                    rolesInMap[col][row] = null
                    return
                }
            }
        }
    }
}