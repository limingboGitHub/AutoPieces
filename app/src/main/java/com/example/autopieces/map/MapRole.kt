package com.example.autopieces.map

import android.view.View
import com.example.autopieces.role.Role

/**
 * 地图中一个角色
 * 包含
 * @param role 角色属性
 * @param roleView 角色视图
 * @param cell 角色的位置信息
 */
class MapRole(
    var role:Role,
    var roleView:View,
    var cell:Cell
){

}