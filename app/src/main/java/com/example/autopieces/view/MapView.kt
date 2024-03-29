package com.example.autopieces.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.example.autopieces.R
import com.example.autopieces.databinding.ItemEquipmentBinding
import com.example.autopieces.databinding.ItemReadyRoleBinding
import com.example.autopieces.databinding.ItemStoreBinding
import com.example.autopieces.extend.attackAni
import com.example.autopieces.extend.damageAni
import com.example.autopieces.extend.deadAni
import com.example.autopieces.extend.transAni
import com.example.autopieces.logic.combat.Damage
import com.example.autopieces.logic.combat.record.*
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.MapViewInterface
import com.example.autopieces.logic.map.Position
import com.example.autopieces.logic.role.Role
import com.example.autopieces.utils.*
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.lmb.lmbkit.extend.toast
import com.lmb.lmbkit.utils.getDensity
import kotlin.text.StringBuilder

class MapView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
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
        val dragResult = gameMap.dragRole(mapRole, targetPosition)
        //分析移动结果
        when (dragResult.result) {
            GameMap.DragResult.MONEY_NOT_ENOUGH -> context.toast(R.string.your_money_is_not_enough)
        }
        //需要删除的角色
        dragResult.removeRole.forEach {
            removeRoleView(mapRoleViews.remove(it))
        }
        //归位
        moveRoleViewAni(mapRole) {
            dragResult.oldPosition?.apply {
                if (where == Position.POSITION_STORE &&
                    mapRole.position.where != Position.POSITION_STORE){
                    val oldView = mapRoleViews[mapRole]?:return@apply
                    createReadyRoleView(mapRole)
                    removeView(oldView)
                }
            }
            updateRoleView()
        }
        dragResult.exchangeRole?.apply { moveRoleViewAni(this) }
    }

    private fun updateRoleView() {
        mapRoleViews.forEach {
            it.value.findViewById<TextView>(R.id.star_tv)?.text = roleLevelStar(it.key.role)
            logE(TAG,"role level:${it.key.role.level}")
        }

        mapViewInterface.update(gameMap)
    }

    private fun moveRoleViewAni(mapRole: MapRole,endFun:()->Unit = {}) {
        if (mapRoleViews[mapRole]==null){
            endFun.invoke()
            return
        }
        val releasedChild = mapRoleViews[mapRole]?:return

        releasedChild.tag = MapRole.STATE_MOVING

        val targetRect = mapDraw.getPhysicalRectByPosition(mapRole.position)
        releasedChild.transAni(targetRect){
            releasedChild.elevation = 3 * density
            endFun.invoke()
            releasedChild.tag = MapRole.STATE_IDLE
        }
    }

    private val viewDragHelper = ViewDragHelper.create(this,2.0f,dragCallback)

    fun setGameMap(gameMap: GameMap){
        this.gameMap = gameMap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mapDraw.initLayout(width,height)

        gameMap.combatZone.forEachCell {
            layoutRoleView(it)
        }
        gameMap.readyZone.forEachCell{
            layoutRoleView(it)
        }
        gameMap.equipmentZone.forEachCell {
            layoutRoleView(it)
        }
        gameMap.storeZone.forEachCell {
            layoutRoleView(it)
        }
    }

    private fun layoutRoleView(mapRole: MapRole){
        val rectF = mapDraw.getPhysicalRectByPosition(mapRole.position)
        mapRoleViews[mapRole]?.apply {
            ViewCompat.offsetLeftAndRight(this, rectF.left.toInt())
            ViewCompat.offsetTopAndBottom(this, rectF.top.toInt())
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
            createRoleView(it)
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
            createReadyRoleView(it)
        }
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
            root.setOnClickListener {
                it.damageAni(this@MapView, Damage(10))
            }
            addView(root)
        }
        equipBinding.root.tag = MapRole.STATE_IDLE
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
                val clickMapRole = getMapRoleByView(it)?:return@setOnClickListener
                val targetPosition = Position(Position.POSITION_READY,0)
                roleMove(clickMapRole,targetPosition)
            }
            addView(root)
            mapRoleViews[mapRole] = root
        }
        layoutRoleView(mapRole)
        return storeBinding.root
    }

    fun createReadyRoleView(mapRole: MapRole):View{
        val roleBinding = ItemReadyRoleBinding.inflate(LayoutInflater.from(context),this,false)

        val layoutParams = LayoutParams(mapDraw.getMapRoleWidth().toInt(),mapDraw.getMapRoleWidth().toInt())
        roleBinding.root.layoutParams = layoutParams

        roleBinding.nameTv.text = mapRole.role.name
        //角色星级
        roleBinding.starTv.text = roleLevelStar(mapRole.role)
        //显示背景
        roleBinding.root.setBackgroundResource(backgroundRes(mapRole.role.cost))
        roleBinding.root.tag = MapRole.STATE_IDLE
        //血量
        roleBinding.hpProgress.max = mapRole.role.maxHP
        roleBinding.hpProgress.progress = mapRole.role.maxHP
        roleBinding.mpProgress.max = mapRole.role.maxMP
        roleBinding.mpProgress.progress = mapRole.role.maxMP

        roleBinding.root.setOnClickListener {
            mapViewInterface.roleClick(mapRole)
        }

        addView(roleBinding.root)
        mapRoleViews[mapRole] = roleBinding.root

        layoutRoleView(mapRole)

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
        return viewDragHelper.shouldInterceptTouchEvent(ev)
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

    /**
     * 战斗状态更新
     */
    fun update(combatRecord: CombatRecord) {
        when{
            combatRecord is MoveRecord -> {
                mapRoleViews[combatRecord.mapRole]?.transAni(mapDraw.getPhysicalRectByPosition(combatRecord.targetPosition))
            }
            combatRecord is AttackRecord ->{
                mapRoleViews[combatRecord.attackMapRole]?.attackAni(mapRoleViews[combatRecord.beAttackedMapRoles[0]])
            }
            combatRecord is DeadRecord ->{
                mapRoleViews[combatRecord.mapRole]?.deadAni{
                    //死亡动画播完后删除视图
                    removeRoleView(mapRoleViews.remove(combatRecord.mapRole))
                }
            }
            combatRecord is HurtRecord ->{
                mapRoleViews[combatRecord.beHurtMapRole]?.apply {
                    //血量更新
                    val hpProgressTV = findViewById<LinearProgressIndicator>(R.id.hp_progress)
                    hpProgressTV.progress = combatRecord.beHurtMapRole.role.curHP
                    //伤害飘血动画
                    this.damageAni(this@MapView,combatRecord.damage)
                }
            }
            else ->{}
        }
    }
}