package com.example.autopieces.logic.map

import com.example.autopieces.logic.role.Role

/**
 * 地图中一个角色
 * 包含
 * @param role 角色属性
// * @param roleView 角色视图
 * @param position 角色的位置信息
 */
class MapRole(
    var role:Role,
    var position:Position = Position()
){

}