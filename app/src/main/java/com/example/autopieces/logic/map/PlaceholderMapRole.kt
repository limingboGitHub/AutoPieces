package com.example.autopieces.logic.map

import com.example.autopieces.logic.role.Role

/**
 * 占位符角色
 * 角色移动时，会先在目标位置创建一个占位符占据位置
 */
class PlaceholderMapRole(role:Role) : MapRole(role) {

}