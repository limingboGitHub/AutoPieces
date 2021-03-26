package com.example.autopieces.logic.map

import com.example.autopieces.logic.equipment.EquipmentName
import com.example.autopieces.logic.Player
import com.example.autopieces.logic.map.GameMap.DragResult.Companion.NO_CHANGE
import com.example.autopieces.logic.map.GameMap.DragResult.Companion.SUCCESS
import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName

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
         * 装备区数量
         */
        const val EQUIPMENT_ZONE_NUM = 10

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
     * 装备区
     */
    val equipmentZone = EquipmentZone(EQUIPMENT_ZONE_NUM)

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
     * 添加装备
     */
    fun addEquipment(role:Role){
        equipmentZone.addRoleToFirstNotNull(MapRole(role,Position(Position.POSITION_EQUIPMENT)))
    }

    /**
     * 添加敌人
     */
    fun addEnemy(){
        val enemy = MapRole(
            Role(RoleName.MINIONS),
            belongTeam = 2
        )
        val enemy2 = MapRole(
            Role(RoleName.MINIONS),
            belongTeam = 2
        )
        combatZone.addRole(enemy,4,1)
        combatZone.addRole(enemy2,3,1)
    }

    /**
     * 拖动移动
     */
    fun dragRole(mapRole: MapRole, targetPosition: Position):DragResult{

        return when(mapRole.position.where){
            Position.POSITION_STORE -> fromStoreZone(mapRole,targetPosition)

            Position.POSITION_READY -> fromReadyZone(mapRole,targetPosition)

            Position.POSITION_COMBAT -> fromCombatZone(mapRole,targetPosition)

            Position.POSITION_EQUIPMENT -> fromEquipmentZone(mapRole,targetPosition)

            else -> DragResult()
        }
    }

    private fun fromEquipmentZone(mapRole: MapRole, targetPosition: Position): DragResult {
        return if (targetPosition.where == Position.POSITION_COMBAT){
            val combatRole = combatZone.getRoleByIndex(targetPosition.x,targetPosition.y)
            if (combatRole != null){
                equipEquipment(combatRole, mapRole)
            }else
                DragResult(NO_CHANGE)
        }else if (targetPosition.where == Position.POSITION_READY){
            val readyRole = readyZone.getRoleByIndex(targetPosition.x)
            if (readyRole != null){
                equipEquipment(readyRole,mapRole)
            }else
                DragResult(NO_CHANGE)
        }else if (targetPosition.where == Position.POSITION_EQUIPMENT){
            val oldPosition = mapRole.position.copy()

            equipmentZone.removeRole(mapRole)

            val oldRole = equipmentZone.addRole(mapRole,targetPosition.x)?.apply {
                equipmentZone.addRole(this,oldPosition.x)
            }
            DragResult(SUCCESS,oldRole,oldPosition = oldPosition)
        }else
             DragResult(NO_CHANGE)
    }

    private fun equipEquipment(role: MapRole, equipmentRole: MapRole) =
            if (role.equipment.size < EquipmentName.EQUIP_MAX_NUM) {
                role.equipment.add(equipmentRole.role)
                DragResult(SUCCESS,oldPosition = role.position.copy()).apply { removeRole.add(equipmentRole) }
            } else
                DragResult(NO_CHANGE)


    private fun fromStoreZone(mapRole: MapRole, targetPosition: Position):DragResult{
        val oldPosition = mapRole.position.copy()

        if (targetPosition.where != Position.POSITION_STORE
            && targetPosition.where != Position.POSITION_STORE_DOWN){
            if (player.money<mapRole.role.cost)
                return DragResult(DragResult.MONEY_NOT_ENOUGH)
            //是否可以合成
            val sampleLevelRolesInReadyZone = readyZone.getSameLevelRoles(mapRole)
            val sampleLevelRolesInCombatZone = combatZone.getSameLevelRoles(mapRole)
            if (sampleLevelRolesInReadyZone.size + sampleLevelRolesInCombatZone.size >= 2){
                //棋子购买
                player.money -= mapRole.role.cost
                storeZone.removeRole(mapRole)
                tempMapRoles.add(mapRole)

                val moveResult = DragResult(SUCCESS, oldPosition = oldPosition)
                    .apply { removeRole.add(mapRole) }
                //升级
                roleLevelUp(mapRole,moveResult.removeRole)
                return moveResult
            }

            if (readyZone.isFull())
                return DragResult(DragResult.READY_ZONE_FULL)

            player.money -= mapRole.role.cost
            storeZone.removeRole(mapRole)
            readyZone.addRoleToFirstNotNull(mapRole)

            return DragResult(SUCCESS,oldPosition = oldPosition)
        }else
            return DragResult()
    }

    private fun fromReadyZone(mapRole: MapRole, targetPosition: Position):DragResult{
        val oldPosition = mapRole.position.copy()
        return when(targetPosition.where){
            Position.POSITION_STORE ->{
                readyZone.removeRole(mapRole)
                DragResult(SUCCESS,oldPosition = oldPosition).apply { removeRole.add(mapRole) }
            }
            Position.POSITION_READY ->{
                readyZone.removeRole(mapRole)

                //放入准备区指定位置，如果有其他角色，则放到原来的位置
                val oldRole = readyZone.addRole(mapRole,targetPosition.x)?.apply {
                    readyZone.addRole(this,oldPosition.x)
                }
                DragResult(SUCCESS,oldRole,oldPosition = oldPosition)
            }
            Position.POSITION_COMBAT ->{
                //非自己的战斗半区无法拖入
                if (combatZone.isNotMyRoleCombatZone(targetPosition))
                    return DragResult()
                //参战棋子已达上限
                if (combatZone.getRoleByIndex(targetPosition.x,targetPosition.y)==null
                    && combatZone.isTeamAmountToMax(player.level)){
                    return DragResult()
                }
                readyZone.removeRole(mapRole)
                val oldRole = combatZone.addRole(mapRole,targetPosition.x,targetPosition.y)?.apply {
                    readyZone.addRole(this,oldPosition.x)
                }
                DragResult(SUCCESS,oldRole,oldPosition = oldPosition)
            }
            else -> DragResult()
        }
    }

    private fun fromCombatZone(mapRole: MapRole, targetPosition: Position):DragResult{
        val oldPosition = mapRole.position.copy()
        return when (targetPosition.where) {
            Position.POSITION_STORE -> {
                //拖入商店 出售
                combatZone.removeRole(mapRole)
                DragResult(SUCCESS,oldPosition = oldPosition).apply { removeRole.add(mapRole) }
            }
            Position.POSITION_READY -> {
                //拖入准备区
                combatZone.removeRole(mapRole)

                val oldMapRole = readyZone.addRole(mapRole,targetPosition.x)
                oldMapRole?.apply {
                    combatZone.addRole(oldMapRole,oldPosition.x,oldPosition.y)
                }
                DragResult(SUCCESS,oldMapRole,oldPosition = oldPosition)
            }
            Position.POSITION_COMBAT -> {
                if (combatZone.isNotMyRoleCombatZone(targetPosition))
                    return DragResult()
                combatZone.removeRole(mapRole)

                val oldMapRole = combatZone.addRole(mapRole,targetPosition.x,targetPosition.y)?.apply {
                    combatZone.addRole(this,oldPosition.x,oldPosition.y)
                }
                DragResult(SUCCESS,oldMapRole,oldPosition = oldPosition)
            }
            else -> DragResult()
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
            DragResult(SUCCESS,oldPosition = mapRole.position.copy())
    }

    /**
     * 棋子拖动的结果
     * 是否成功，是否需要互换，是否需要删除等
     */
    class DragResult(
        var result :Int = NO_CHANGE,
        /**
         * 互换位置的角色
         */
        var exchangeRole: MapRole? = null,

        /**
         * 需要删除的角色
         */
        var removeRole : ArrayList<MapRole> = ArrayList(),

        /**
         * 移动之前的位置
         */
        var oldPosition: Position? = null
    ){
        companion object{
            val NO_CHANGE = 0
            val SUCCESS = 1
            val MONEY_NOT_ENOUGH = 2
            val READY_ZONE_FULL = 3
        }
    }
}