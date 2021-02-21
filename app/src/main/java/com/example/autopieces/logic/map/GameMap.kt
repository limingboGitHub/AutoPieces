package com.example.autopieces.logic.map

import com.example.autopieces.Player
import com.example.autopieces.logic.map.GameMap.MoveResult.Companion.SUCCESS
import com.example.autopieces.logic.role.Role
import com.lmb.lmbkit.utils.getString

class GameMap {

    companion object{
        /**
         * 战斗区域行列数
         */
        const val COMBAT_ROW_NUM = 8
        const val COMBAT_COL_NUM = 7

        /**
         * 准备区数量
         */
        const val READY_ZONE_NUM = 9

        /**
         * 商店区数量
         */
        const val STORE_ZONE_NUM = 5
    }

    /**
     * 战斗区
     */
    val combatZone = CombatZone(COMBAT_ROW_NUM,COMBAT_COL_NUM)

    /**
     * 预备区
     */
    val readyZone = ReadyZone(READY_ZONE_NUM)

    /**
     * 商店区
     */
    val storeZone = StoreZone(STORE_ZONE_NUM)

    /**
     * 角色暂时存放区
     * 用于角色合成
     */
    val tempMapRoles = ArrayList<MapRole>()

    var player = Player()

    /**
     * 商店刷新
     */
    fun updateStore(roles:List<Role>){
        storeZone.clear()

        roles.forEach {
            storeZone.addRoleToFirstNotNull(MapRole(it))
        }
    }


    /**
     * 棋子移动
     */
    fun roleMove(mapRole: MapRole,targetPosition: Position):MoveResult{

        return when(mapRole.position.where){
            Position.POSITION_STORE -> fromStoreZone(mapRole,targetPosition)

            Position.POSITION_READY -> fromReadyZone(mapRole,targetPosition)

            Position.POSITION_COMBAT -> fromCombatZone(mapRole,targetPosition)

            else -> MoveResult()
        }
    }

    private fun fromStoreZone(mapRole: MapRole, targetPosition: Position):MoveResult{
        if (targetPosition.where != Position.POSITION_STORE){
            if (player.money<mapRole.role.cost)
                return MoveResult(MoveResult.MONEY_NOT_ENOUGH)
            //是否可以合成
            val sampleLevelRolesInReadyZone = readyZone.getSameLevelRoles(mapRole)
            val sampleLevelRolesInCombatZone = combatZone.getSameLevelRoles(mapRole)
            if (sampleLevelRolesInReadyZone.size + sampleLevelRolesInCombatZone.size >= 2){
                //棋子购买
                player.money -= mapRole.role.cost
                storeZone.removeRole(mapRole)
                tempMapRoles.add(mapRole)

                val moveResult = MoveResult(SUCCESS).apply { removeRole.add(mapRole) }
                //升级
                roleLevelUp(mapRole,moveResult.removeRole)
                return moveResult
            }

            if (readyZone.isFull())
                return MoveResult(MoveResult.READY_ZONE_FULL)

            player.money -= mapRole.role.cost
            storeZone.removeRole(mapRole)
            readyZone.addRoleToFirstNotNull(mapRole)

            return MoveResult(MoveResult.SUCCESS)
        }else
            return MoveResult()
    }

    private fun fromReadyZone(mapRole: MapRole, targetPosition: Position):MoveResult{
        return when(targetPosition.where){
            Position.POSITION_STORE ->{
                readyZone.removeRole(mapRole)
                MoveResult(SUCCESS).apply { removeRole.add(mapRole) }
            }
            Position.POSITION_READY ->{
                readyZone.removeRole(mapRole)

                val oldIndex = mapRole.position.x
                //放入准备区指定位置，如果有其他角色，则放到原来的位置
                val oldRole = readyZone.addRole(mapRole,targetPosition.x)?.apply {
                    readyZone.addRole(this,oldIndex)
                }
                MoveResult(SUCCESS,oldRole)
            }
            Position.POSITION_COMBAT ->{
                //非自己的战斗半区无法拖入
                if (combatZone.isNotMyRoleCombatZone(targetPosition))
                    return MoveResult()
                //参战棋子已达上限
                if (combatZone.getRoleByIndex(targetPosition.x,targetPosition.y)==null
                    && combatZone.isRoleAmountToMax(player.level)){
                    return MoveResult()
                }
                readyZone.removeRole(mapRole)
                val oldIndex = mapRole.position.x
                val oldRole = combatZone.addRole(mapRole,targetPosition.x,targetPosition.y)?.apply {
                    readyZone.addRole(this,oldIndex)
                }
                MoveResult(SUCCESS,oldRole)
            }
            else -> MoveResult()
        }
    }

    private fun fromCombatZone(mapRole: MapRole, targetPosition: Position):MoveResult{
        return when (targetPosition.where) {
            Position.POSITION_STORE -> {
                //拖入商店 出售
                combatZone.removeRole(mapRole)
                MoveResult(SUCCESS).apply { removeRole.add(mapRole) }
            }
            Position.POSITION_READY -> {
                //拖入准备区
                combatZone.removeRole(mapRole)
                //mapRole原本的位置
                val oldPositionX = mapRole.position.x
                val oldPositionY = mapRole.position.y

                val oldMapRole = readyZone.addRole(mapRole,targetPosition.x)
                oldMapRole?.apply {
                    combatZone.addRole(oldMapRole,oldPositionX,oldPositionY)
                }
                MoveResult(SUCCESS,oldMapRole)
            }
            Position.POSITION_COMBAT -> {
                if (combatZone.isNotMyRoleCombatZone(targetPosition))
                    return MoveResult()
                combatZone.removeRole(mapRole)
                //mapRole原本的位置
                val oldPositionX = mapRole.position.x
                val oldPositionY = mapRole.position.y

                val oldMapRole = combatZone.addRole(mapRole,targetPosition.x,targetPosition.y)?.apply {
                    combatZone.addRole(this,oldPositionX,oldPositionY)
                }
                MoveResult(SUCCESS,oldMapRole)
            }
            else -> MoveResult()
        }
    }

    private fun roleLevelUp(mapRole: MapRole,removeRole: ArrayList<MapRole>){
        val sampleLevelRolesInReadyZone = readyZone.getSameLevelRoles(mapRole)
        val sampleLevelRolesInCombatZone = combatZone.getSameLevelRoles(mapRole)
        if (sampleLevelRolesInReadyZone.size + sampleLevelRolesInCombatZone.size + tempMapRoles.size >= 3){
            //升级
            val toLevelUpRole = if (sampleLevelRolesInCombatZone.isNotEmpty())
                sampleLevelRolesInCombatZone.removeAt(0)
            else
                sampleLevelRolesInReadyZone.removeAt(0)
            toLevelUpRole.role.levelUp()
            //装备整合
            //其他棋子移除
            sampleLevelRolesInCombatZone.forEach {
                combatZone.removeRole(it)
                removeRole.add(it)
            }
            sampleLevelRolesInReadyZone.forEach {
                readyZone.removeRole(it)
                removeRole.add(it)
            }
            tempMapRoles.clear()
            //继续升级判定
            roleLevelUp(toLevelUpRole,removeRole)
        }else
            MoveResult(SUCCESS)
    }

    class MoveResult(
        var result :Int = NO_CHANGE,
        var exchangeRole: MapRole? = null,
        var removeRole : ArrayList<MapRole> = ArrayList()
    ){
        companion object{
            val NO_CHANGE = 0
            val SUCCESS = 1
            val MONEY_NOT_ENOUGH = 2
            val READY_ZONE_FULL = 3
        }
    }
}