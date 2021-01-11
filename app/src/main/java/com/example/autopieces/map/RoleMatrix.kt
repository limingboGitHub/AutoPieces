package com.example.autopieces.map

import com.example.autopieces.role.Role

class RoleMatrix(row:Int,col:Int) {


    private val rolesInMap : Array<Array<Role?>> = Array(row) {
        arrayOfNulls<Role?>(col)
    }

    fun removeRole(roleToRemove: Role) {
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