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
import androidx.customview.widget.ViewDragHelper
import com.example.autopieces.databinding.ItemRoleBinding
import com.example.autopieces.databinding.ItemStoreBinding
import com.example.autopieces.role.Role
import com.example.autopieces.utils.*
import com.lmb.lmbkit.utils.getDensity

class MapView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val TAG = "MapView"

    companion object{
        val TYPE_STORE = "store"
        val TYPE_ROLE = "role"

        /**
         * 角色的位置
         */
        val LOCATION_STORE = "store"
        val LOCATION_READY = "ready"
        val LOCATION_COMBAT = "map"
    }
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
            val targetCell = mapDraw.calculateLocation(releasedChild)

            //目标从一个区域转移到另一个区域的逻辑判断
            val endFun : ()->Unit = when(mapRole.role.location){
                LOCATION_STORE -> fromStoreZone(mapRole,targetCell)

                LOCATION_READY -> fromReadyZone(mapRole,targetCell)

                LOCATION_COMBAT -> fromCombatZone(mapRole,targetCell)

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

        mapRole.cell.rect.apply {
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

        mapRole.cell.rect.apply {
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

    private fun fromCombatZone(mapRole: MapRole, targetCell: Cell): () -> Unit {
        if (mapDraw.belongStoreZone(targetCell.rect)){
            //拖入商店 出售
            combatZone.removeRole(mapRole)
            removeRoleView(mapRole.roleView)
            logE(TAG,"出售了:${mapRole.role.name}")
        }else if (mapDraw.belongReadyZone(targetCell.rect)){
            //拖入准备区
            combatZone.removeRole(mapRole)
            readyZone.addRoleToFirstNotNull(mapRole)

            mapRole.cell = targetCell
            mapRole.role.location = LOCATION_READY
            logE(TAG,"${mapRole.role.name} 进入准备区")
        }else if (mapDraw.belongCombatZone(targetCell.rect)){
            mapRole.cell = targetCell
        }
        return {}
    }

    private fun fromReadyZone(mapRole: MapRole, targetCell: Cell): () -> Unit {
        //拖入商店 出售
        if (mapDraw.belongStoreZone(targetCell.rect)){
            readyZone.removeRole(mapRole)
            removeRoleView(mapRole.roleView)
            logE(TAG,"出售了:${mapRole.role.name}")
        }else if (mapDraw.belongReadyZone(targetCell.rect)){
            val roleCell = mapRole.cell
            logE(TAG, "原来的位置:$roleCell")
            //交换
            mapRole.cell = targetCell
            readyZone.getRoleByIndex(targetCell.x)?.apply {
                cell = roleCell

                roleMove(this)
            }
        }else if (mapDraw.belongCombatZone(targetCell.rect)){
            readyZone.removeRole(mapRole)
            mapRole.cell = targetCell
            mapRole.role.location = LOCATION_COMBAT
            logE(TAG,"${mapRole.role.name} 进入战斗区")
        }
        return {}
    }

    private fun fromStoreZone(mapRole: MapRole, targetCell: Cell) : ()->Unit {
        //拖出了商店区域
        if (!mapDraw.belongStoreZone(targetCell.rect)){
            //购买，加入预备区
//            roles.remove(role)
            //新的位置
            val firstEmptyIndex = readyZone.getFirstEmptyIndex()
            val readyZoneRect = mapDraw.getReadyItemZone(firstEmptyIndex)
            mapRole.cell = Cell(readyZoneRect,firstEmptyIndex)
            readyZone.addRoleToFirstNotNull(mapRole)

            mapRole.role.location = LOCATION_READY
            logE(TAG,"购买了:${mapRole.role.name}")

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

    private val viewDragHelper = ViewDragHelper.create(this,dragCallback)

    init {
        setWillNotDraw(false)
    }

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
        mapRole.cell.rect.apply {
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

    fun addStore(role: Role){
        val storeItemWidth = mapDraw.getStoreItemWidth()

        val storeBinding = ItemStoreBinding.inflate(LayoutInflater.from(context),this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = storeItemWidth.toInt()
        layoutParams.height = storeItemWidth.toInt()

        storeBinding.apply {
            nameTv.text = role.name
            root.layoutParams = layoutParams
            addView(root)
        }

        //计算每个卡片对应在商店区域的坐标
        val x = mapDraw.getStoreZone().left + mapDraw.getStoreCellWith() * storeZone.roleNum()
        val y = mapDraw.getStoreZone().top

        val mapRole = MapRole(
                role,
                storeBinding.root,
                Cell(RectF(x, y, (x+storeItemWidth), (y+storeItemWidth)))
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
        return roleBinding.root
    }

    private fun removeRoleView(roleView: View){
        removeView(roleView)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
//        return super.onTouchEvent(event)
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

    private fun getRoleByView(view:View):Role?{

        return null
    }

    private fun getViewByRole(role: Role):View?{

        return null
    }

}