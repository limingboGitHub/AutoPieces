package com.example.autopieces.map

/**
 * 商店区域
 */
class StoreZone {

    companion object{
        const val STORE_ITEM_NUM = 5
    }

    private val cells = Array<MapRole?>(STORE_ITEM_NUM) { null }

    fun forEachCell(doSomeThing:(mapRole:MapRole)->Unit){
        cells.forEachIndexed { index, mapRole ->
            if (mapRole!=null)
                doSomeThing.invoke(mapRole)
        }
    }

    fun addRole(mapRoleToAdd: MapRole){
        cells.forEachIndexed { index, mapRole ->
            if (mapRole==null)
                cells[index] = mapRoleToAdd
        }
    }

    fun remove(mapRoleToRemove:MapRole){
        cells.forEachIndexed { index, mapRole ->
            if (mapRole==mapRoleToRemove)
                cells[index] = null
        }
    }

    fun roleCount():Int = cells.sumBy { if (it!=null) 1 else 0 }
}