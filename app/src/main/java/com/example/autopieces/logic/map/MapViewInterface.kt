package com.example.autopieces.logic.map

/**
 * MapView不直接依赖Player类，通过此接口来从外部获取player相关信息和修改player的信息
 */
interface MapViewInterface {

    fun update(gameMap: GameMap)
}