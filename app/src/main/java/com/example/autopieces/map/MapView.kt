package com.example.autopieces.map

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
import com.example.autopieces.Player
import com.example.autopieces.R
import com.example.autopieces.databinding.ItemRoleBinding
import com.example.autopieces.databinding.ItemStoreBinding
import com.example.autopieces.role.Role
import com.example.autopieces.utils.*
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

    /**
     * 战斗区
     */
    private val combatZone = CombatZone(MapDraw.COMBAT_ROW_NUM,MapDraw.COMBAT_COL_NUM)

    /**
     * 预备区
     */
    private val readyZone = ReadyZone()

    /**
     * 商店区
     */
    private val storeZone = StoreZone()

    /**
     * 角色暂时存放区
     * 用于角色合成
     */
    private val tempMapRoles = ArrayList<MapRole>()

    private lateinit var player : Player

    var playerUpdateListenerAdapter : PlayerUpdateListener? = null

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

            //目标从一个区域转移到另一个区域的逻辑判断
            val endFun : ()->Unit = when(mapRole.position.where){
                Position.POSITION_STORE -> fromStoreZone(mapRole,targetPosition)

                Position.POSITION_READY -> fromReadyZone(mapRole,targetPosition)

                Position.POSITION_COMBAT -> fromCombatZone(mapRole,targetPosition)

                else -> {{}}
            }

            //归位
            roleMoveAni(mapRole, endFun)
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

    private fun roleMove(mapRole: MapRole){
        val releasedChild = mapRole.roleView

        val targetRect = mapDraw.getPhysicalRectByPosition(mapRole.position)
        targetRect.apply {
            releasedChild.left = left.toInt()
            releasedChild.top = top.toInt()
            releasedChild.right = (left+width()).toInt()
            releasedChild.bottom = (top + height()).toInt()
        }
    }

    private fun roleMoveAni(mapRole: MapRole, endFun: () -> Unit = {}) {
        val releasedChild = mapRole.roleView
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
                    endFun.invoke()
                }
            })
            animation.duration = 200
            animation.start()
        }
    }

    private fun fromCombatZone(mapRole: MapRole, targetPosition: Position): () -> Unit {
        when (targetPosition.where) {
            Position.POSITION_STORE -> {
                //拖入商店 出售
                combatZone.removeRole(mapRole)
                removeRoleView(mapRole.roleView)
                logE(TAG,"出售了:${mapRole.role.name}")
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
                    roleMove(this)
                }

                logE(TAG,"${mapRole.role.name} 进入准备区")
            }
            Position.POSITION_COMBAT -> {
                if (combatZone.isNotMyRoleCombatZone(targetPosition))
                    return{}
                combatZone.removeRole(mapRole)
                //mapRole原本的位置
                val oldPositionX = mapRole.position.x
                val oldPositionY = mapRole.position.y

                val oldMapRole = combatZone.addRole(mapRole,targetPosition.x,targetPosition.y)?.apply {
                    combatZone.addRole(this,oldPositionX,oldPositionY)
                    roleMove(this)
                }
            }
        }
        return {}
    }

    private fun fromReadyZone(mapRole: MapRole, targetPosition: Position): () -> Unit {
        targetPosition.apply {
            //拖入商店 出售
            when(where){
                Position.POSITION_STORE ->{
                    logE(TAG,"出售了:${mapRole.role.name}")
                    readyZone.removeRole(mapRole)
                    removeRoleView(mapRole.roleView)
                }
                Position.POSITION_READY->{
                    readyZone.removeRole(mapRole)

                    val oldIndex = mapRole.position.x
                    //放入准备区指定位置，如果有其他角色，则放到原来的位置
                    val oldRole = readyZone.addRole(mapRole,x)?.apply {
                        readyZone.addRole(this,oldIndex)
                        roleMove(this)
                    }
                }
                Position.POSITION_COMBAT ->{
                    if (combatZone.isNotMyRoleCombatZone(targetPosition))
                        return@apply
                    readyZone.removeRole(mapRole)
                    val oldIndex = mapRole.position.x
                    combatZone.addRole(mapRole,x,y)?.apply {
                        readyZone.addRole(this,oldIndex)
                        roleMove(this)
                    }
                }
            }
        }

        return {}
    }

    private fun fromStoreZone(mapRole: MapRole, targetPosition: Position) : ()->Unit {
        //拖出了商店区域
        if (targetPosition.where != Position.POSITION_STORE){
            val money = player.money
            if (money < mapRole.role.cost)
                return{}
            //是否可以合成
            val sampleLevelRolesInReadyZone = readyZone.getSampleLevelRoles(mapRole)
            val sampleLevelRolesInCombatZone = combatZone.getSampleLevelRoles(mapRole)
            if (sampleLevelRolesInReadyZone.size + sampleLevelRolesInCombatZone.size >= 2){
                //棋子购买
                player.money = money - mapRole.role.cost
                playerUpdateListenerAdapter?.update(player)
                logE(TAG,"购买了:${mapRole.role.name}")
                storeZone.removeRole(mapRole)

                tempMapRoles.add(mapRole)
                //升级
                roleLevelUp(mapRole)

                return{}
            }

            if (readyZone.isFull())
                return{}
            player.money = money - mapRole.role.cost
            playerUpdateListenerAdapter?.update(player)
            logE(TAG,"购买了:${mapRole.role.name}")
            storeZone.removeRole(mapRole)

            readyZone.addRoleToFirstNotNull(mapRole)

            return {
                //商店的视图删除，由新的角色视图替换
                removeRoleView(mapRole.roleView)

                val roleView = createRoleView(mapRole)
                mapRole.roleView = roleView
                addView(roleView)
            }
        }else
            return{}
    }

    private fun roleLevelUp(mapRole: MapRole){
        val sampleLevelRolesInReadyZone = readyZone.getSampleLevelRoles(mapRole)
        val sampleLevelRolesInCombatZone = combatZone.getSampleLevelRoles(mapRole)
        if (sampleLevelRolesInReadyZone.size + sampleLevelRolesInCombatZone.size + tempMapRoles.size >= 3){
            //升级
            val toLevelUpRole = if (sampleLevelRolesInCombatZone.isNotEmpty())
                sampleLevelRolesInCombatZone.removeAt(0)
            else
                sampleLevelRolesInReadyZone.removeAt(0)
            toLevelUpRole.role.levelUp()
            toLevelUpRole.roleView.findViewById<TextView>(R.id.star_tv)?.apply {
                text = roleLevelStar(toLevelUpRole.role)
            }
            //装备整合
            //其他棋子移除
            sampleLevelRolesInCombatZone.forEach {
                combatZone.removeRole(it)
                removeRoleView(it.roleView)
            }
            sampleLevelRolesInReadyZone.forEach {
                readyZone.removeRole(it)
                removeRoleView(it.roleView)
            }
            tempMapRoles.clear()
            //商店的视图删除
            removeRoleView(mapRole.roleView)
            //继续升级判定
            roleLevelUp(toLevelUpRole)
        }
    }

    private val viewDragHelper = ViewDragHelper.create(this,dragCallback)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        combatZone.forEachCell {
            layoutChildView(it)
        }
        readyZone.forEachCell{
            layoutChildView(it)
        }
        storeZone.forEachCell {
            layoutChildView(it)
        }
    }

    private fun layoutChildView(mapRole: MapRole){
        mapDraw.getPhysicalRectByPosition(mapRole.position)
                .apply {
                    mapRole.roleView.layout(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                }
        logD(TAG,"onLayout ${mapRole.roleView} to:${mapRole.roleView.left}")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制地图
        mapDraw.drawCombat(canvas)
        //绘制棋子准备区域
        mapDraw.drawReadyZone(canvas)
        //绘制商店区域
        mapDraw.drawStore(canvas)
    }

    fun setPlayer(player: Player){
        this.player = player
    }

    fun updateStore(roles:List<Role>){
        storeZone.forEachCell {
            removeRoleView(it.roleView)
        }
        storeZone.clear()

        roles.forEach {
            addStore(it)
        }
    }

    fun addStore(role: Role){
        val storeItemWidth = mapDraw.getStoreItemWidth()

        val storeBinding = ItemStoreBinding.inflate(LayoutInflater.from(context),this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = storeItemWidth.toInt()
        layoutParams.height = storeItemWidth.toInt()
        //背景
        storeBinding.root.setBackgroundResource(backgroundRes(role.cost))

        storeBinding.apply {
            nameTv.text = role.name
            costTv.text = role.cost.toString()
            root.layoutParams = layoutParams
            root.setOnClickListener {

            }
            addView(root)
        }

        val mapRole = MapRole(
                role,
                storeBinding.root
        )

        storeZone.addRoleToFirstNotNull(mapRole)
    }

    fun createRoleView(mapRole: MapRole):View{
        val roleBinding = ItemRoleBinding.inflate(LayoutInflater.from(context),this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = mapDraw.getReadyCellWidth().toInt()
        layoutParams.height = mapDraw.getReadyCellWidth().toInt()
        roleBinding.root.layoutParams = layoutParams

        roleBinding.nameTv.text = mapRole.role.name
        //角色星级
        roleBinding.starTv.text = roleLevelStar(mapRole.role)
        //显示背景
        roleBinding.root.setBackgroundResource(backgroundRes(mapRole.role.cost))
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

    private fun removeRoleView(roleView: View){
        removeView(roleView)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
        return true
    }

    private fun getMapRoleByView(view: View):MapRole?{
        var mapRole = storeZone.getMapRoleByView(view)
        if (mapRole!=null)
            return mapRole
        mapRole = readyZone.getMapRoleByView(view)
        if (mapRole!=null)
            return mapRole
        mapRole = combatZone.getMapRoleByView(view)
        if (mapRole!=null)
            return mapRole
        return null
    }

    interface PlayerUpdateListener{
        fun update(player: Player)
    }
}