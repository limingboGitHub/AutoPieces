package com.example.autopieces.logic.map

/**
 * 二维区域
 */
abstract class TwoDimensionalZone(val row:Int, val col:Int) {
    val TAG = "Zone"

    protected val cells : Array<Array<MapRole?>> = Array(row) {
        Array<MapRole?>(col){null}
    }

    abstract fun zoneBelongWhere():String

    /**
     * 遍历每个格子
     */
    fun forEachCell(doSomeThing:(mapRole:MapRole)->Unit){
        cells.forEachIndexed { row, arrayOfRoles ->
            arrayOfRoles.forEachIndexed { col, role ->
                if (role!=null)
                    doSomeThing.invoke(role)
            }
        }
    }

    /**
     * 找到所有角色
     */
    fun getAllRole():List<MapRole>{
        return ArrayList<MapRole>().apply {
            forEachCell {
                if (it.flag == MapRole.FLAG_ROLE)
                    add(it)
            }
        }
    }

    /**
     * 添加角色，如果指定的位置有其他角色，则返回该角色
     */
    fun addRole(mapRole: MapRole,x:Int,y:Int):MapRole?{
        val oldRole = if (cells[y][x] == null){
            null
        }else
            cells[y][x]

        cells[y][x] = mapRole
        mapRole.position.apply {
            where = zoneBelongWhere()
            this.x = x
            this.y = y
        }

        return oldRole
    }

    fun addRoleToFirstNotNull(mapRoleToAdd: MapRole){
        cells.forEachIndexed { row, arrayOfRoles ->
            arrayOfRoles.forEachIndexed { col, role ->
                if (role == null) {
                    addRole(mapRoleToAdd,col,row)
                    return
                }
            }
        }
    }


    /**
     * 找到同名，同星级的角色
     */
    fun getSameLevelRoles(mapRole: MapRole):ArrayList<MapRole>{
        val sameLevelRoles = ArrayList<MapRole>()
        forEachCell {
            if (it.role.name == mapRole.role.name &&
                    it.role.level == mapRole.role.level)
                sameLevelRoles.add(it)
        }
        return sameLevelRoles
    }

    fun clear(){
        cells.forEachIndexed { row, arrayOfRoles ->
            repeat(arrayOfRoles.size){
                arrayOfRoles[it] = null
            }
        }

    }

    fun removeRole(roleToRemove: MapRole) {
        cells.forEachIndexed { row, arrayOfRoles ->
            arrayOfRoles.forEachIndexed { col, role ->
                if (role == roleToRemove){
                    cells[row][col] = null
                    return
                }
            }
        }
    }

    fun removeRoleByIndex(x:Int,y:Int){
        cells[y][x] = null
    }

    /**
     * 通过index查询对应位置的角色
     */
    fun getRoleByIndex(x: Int,y: Int):MapRole?{
        if (y>=0 && y < cells.size) {
            if (x>=0 && x < cells[y].size)
                return cells[y][x]
        }
        return null
    }

    /**
     * 角色数量
     */
    fun roleAmount():Int = cells.sumBy { it -> it.sumBy { if (it==null) 0 else 1 } }

    fun teamOneAmount():Int = cells.sumBy { it -> it.sumBy { if (it==null ||it.belongTeam == 2) 0 else 1 } }
}