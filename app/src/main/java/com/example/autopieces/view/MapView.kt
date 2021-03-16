package com.example.autopieces.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.customview.widget.ViewDragHelper
import com.example.autopieces.R
import com.example.autopieces.databinding.ItemEquipmentBinding
import com.example.autopieces.databinding.ItemReadyRoleBinding
import com.example.autopieces.databinding.ItemStoreBinding
import com.example.autopieces.logic.combat.Combat
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.MapViewInterface
import com.example.autopieces.logic.map.Position
import com.example.autopieces.logic.role.Role
import com.example.autopieces.utils.*
import com.lmb.lmbkit.extend.toast
import com.lmb.lmbkit.utils.getDensity
import kotlin.text.StringBuilder

class MapView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val TAG = "MapView"

    /**
     * 地图绘制
     */
    private val mapDraw = MapDraw()

    /**
     * 系统参数
     */
    private val density = getDensity()

    private lateinit var gameMap : GameMap

    /**
     * 角色对应视图
     */
    private val mapRoleViews = HashMap<MapRole,View>()

    /**
     * 外部需要实现的MapView接口
     */
    private lateinit var mapViewInterface: MapViewInterface

    init {
        setWillNotDraw(false)
    }

    /**
     * 子View拖动
     */
    private val dragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(view: View, i: Int): Boolean {
            return true
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            capturedChild.elevation = 5*density
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val mapRole = getMapRoleByView(releasedChild)?:return
            //松手后，目标的落点区域
            val targetPosition = mapDraw.calculatePosition(releasedChild)

            roleMove(mapRole, targetPosition)
        }

        override fun getViewHorizontalDragRange(view: View): Int {
            return view.width
        }

        override fun getViewVerticalDragRange(view: View): Int {
            return view.height
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

    }

    private fun roleMove(
        mapRole: MapRole,
        targetPosition: Position
    ) {
        val moveResult = gameMap.roleMove(mapRole, targetPosition)
        when (moveResult.result) {
            GameMap.MoveResult.MONEY_NOT_ENOUGH -> context.toast(R.string.your_money_is_not_enough)
        }
        moveResult.removeRole.forEach {
            removeRoleView(mapRoleViews.remove(it))
        }
        //归位
        moveRoleViewAni(mapRole) {
            updateRoleView()
        }
        moveResult.exchangeRole?.apply { moveRoleViewAni(this) }
    }

    private fun updateRoleView() {
        mapRoleViews.forEach {
            it.value.findViewById<TextView>(R.id.star_tv)?.text = roleLevelStar(it.key.role)
            logE(TAG,"role level:${it.key.role.level}")
        }

        mapViewInterface.update(gameMap)
    }

    private fun moveRoleView(mapRole: MapRole){
        val releasedChild = mapRoleViews[mapRole]?:return

        val targetRect = mapDraw.getPhysicalRectByPosition(mapRole.position)
        targetRect.apply {
            releasedChild.left = left.toInt()
            releasedChild.top = top.toInt()
            releasedChild.right = (left+width()).toInt()
            releasedChild.bottom = (top + height()).toInt()
        }
    }

    private fun moveRoleViewAni(mapRole: MapRole, endFun:()->Unit = {}) {
        if (mapRoleViews[mapRole]==null){
            endFun.invoke()
            return
        }
        var releasedChild = mapRoleViews[mapRole]?:return

        val curLeft = releasedChild.left
        val curTop = releasedChild.top
        val curWith = releasedChild.width
        val curHeight = releasedChild.height

        val targetRect = mapDraw.getPhysicalRectByPosition(mapRole.position)

        targetRect.apply {
            val targetLeft = left
            val targetTop = top
            val targetWidth = width()
            val targetHeight = height()

            val animation = ValueAnimator.ofFloat(1f, 0f)
            animation.addUpdateListener {
                val scale = it.animatedValue as Float
                releasedChild.left = (targetLeft + (curLeft - targetLeft) * scale).toInt()
                releasedChild.top = (targetTop + (curTop - targetTop) * scale).toInt()
                releasedChild.right = (releasedChild.left + targetWidth + (curWith - targetWidth) * scale).toInt()
                releasedChild.bottom = (releasedChild.top + targetHeight + (curHeight - targetHeight) * scale).toInt()
    //                    logE(TAG,"l:${releasedChild.left} r:${releasedChild.right}")
            }
            animation.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    releasedChild.elevation = 3 * density

                    if (releasedChild.tag == Position.POSITION_STORE &&
                        mapRole.position.where != Position.POSITION_STORE){

                        releasedChild = createReadyRoleView(mapRole).apply {
                            left = releasedChild.left
                            top = releasedChild.top
                            right = releasedChild.right
                            bottom = releasedChild.bottom
                            addView(this)
                            removeView(releasedChild)
                            mapRoleViews[mapRole] = this
                        }

                        endFun.invoke()
                    }
                }
            })
            animation.duration = 200
            animation.start()
        }
    }

    private val viewDragHelper = ViewDragHelper.create(this,dragCallback)

    fun setGameMap(gameMap: GameMap){
        this.gameMap = gameMap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mapDraw.initLayout(width,height)
        gameMap.combatZone.forEachCell {
            layoutChildView(it)
        }
        gameMap.readyZone.forEachCell{
            layoutChildView(it)
        }
        gameMap.equipmentZone.forEachCell {
            layoutChildView(it)
        }
        gameMap.storeZone.forEachCell {
            layoutChildView(it)
        }
    }

    private fun layoutChildView(mapRole: MapRole){
        mapDraw.getPhysicalRectByPosition(mapRole.position)
                .apply {
                    mapRoleViews[mapRole]?.layout(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mapDraw.drawAll(canvas)
    }

    fun setInterface(mapViewInterface: MapViewInterface){
        this.mapViewInterface = mapViewInterface
    }

    fun updateStore(roles:List<Role>){
        //删除商店未购买的棋子View
        gameMap.storeZone.forEachCell {
            removeRoleView(mapRoleViews.remove(it))
        }
        //刷新商店
        gameMap.updateStore(roles)
        gameMap.storeZone.forEachCell {
            mapRoleViews[it] = createRoleView(it)
        }
    }

    fun addEquipment(roles:List<Role>){
        roles.forEach {
            gameMap.addEquipment(it)
        }
        gameMap.equipmentZone.forEachCell {
            mapRoleViews[it] = createEquipmentView(it)
        }
    }

    fun addEnemy(){
        gameMap.addEnemy()
        gameMap.combatZone.forEachCell {
            mapRoleViews[it] = createReadyRoleView(it).apply {
                val rect = mapDraw.getPhysicalRectByPosition(it.position)
                left = rect.left.toInt()
                top = rect.top.toInt()
                right = rect.right.toInt()
                bottom = rect.bottom.toInt()
                addView(this)
            }
        }
    }

    fun startCombat(){
        val combat = Combat(gameMap.combatZone)
        combat.start()
    }

    private fun createEquipmentView(mapRole: MapRole): View {
        val equipmentItemWidth = mapDraw.getEquipmentItemWidth()

        val equipBinding = ItemEquipmentBinding.inflate(LayoutInflater.from(context),this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = equipmentItemWidth.toInt()
        layoutParams.height = equipmentItemWidth.toInt()

        equipBinding.apply {
            nameTv.text = mapRole.role.name
            root.layoutParams = layoutParams
//            root.setOnClickListener {
//
//            }
            addView(root)
        }
        equipBinding.root.tag = mapRole.position.where
        return equipBinding.root
    }

    private fun createRoleView(mapRole: MapRole):View{
        val storeItemWidth = mapDraw.getStoreItemWidth()

        val storeBinding = ItemStoreBinding.inflate(LayoutInflater.from(context),this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = storeItemWidth.toInt()
        layoutParams.height = storeItemWidth.toInt()
        //背景
        storeBinding.root.setBackgroundResource(backgroundRes(mapRole.role.cost))

        storeBinding.apply {
            nameTv.text = mapRole.role.name
            costTv.text = mapRole.role.cost.toString()
            root.layoutParams = layoutParams
            root.setOnClickListener {
                val mapRole = getMapRoleByView(it)?:return@setOnClickListener
                val targetPosition = Position(Position.POSITION_READY,0)
                roleMove(mapRole,targetPosition)
            }
            addView(root)
        }
        storeBinding.root.tag = mapRole.position.where
        return storeBinding.root
    }

    fun createReadyRoleView(mapRole: MapRole):View{
        val roleBinding = ItemReadyRoleBinding.inflate(LayoutInflater.from(context),this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = mapDraw.getReadyCellWidth().toInt()
        layoutParams.height = mapDraw.getReadyCellWidth().toInt()
        roleBinding.root.layoutParams = layoutParams

        roleBinding.nameTv.text = mapRole.role.name
        //角色星级
        roleBinding.starTv.text = roleLevelStar(mapRole.role)
        //显示背景
        roleBinding.root.setBackgroundResource(backgroundRes(mapRole.role.cost))
        roleBinding.root.tag = mapRole.position.where

        roleBinding.root.setOnClickListener {
            mapViewInterface.roleClick(mapRole)
        }
        return roleBinding.root
    }

    private fun roleLevelStar(role: Role):String = StringBuilder().apply { repeat(role.level){append("★")} }.toString()

    private fun backgroundRes(cost:Int):Int{
        return when(cost){
            2 -> R.drawable.store_role_cost2_bg
            3 -> R.drawable.store_role_cost3_bg
            4 -> R.drawable.store_role_cost4_bg
            5 -> R.drawable.store_role_cost5_bg
            else -> R.drawable.store_role_cost1_bg
        }
    }

    private fun removeRoleView(roleView: View?){
        if (roleView==null)
            return
        removeView(roleView)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }

    private fun getMapRoleByView(view: View): MapRole?{
        mapRoleViews.forEach {
            if (it.value == view)
                return it.key
        }
        return null
    }
}