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
     * 地图相关参数
     */
    private var roleCoordinates = HashMap<View,RectF>()

    private val rolesInMap = RoleMatrix(MapDraw.COMBAT_ROW_NUM,MapDraw.COMBAT_COL_NUM)

    /**
     * 商店角色
     */
    private val storeRoles = ArrayList<Role>()

    /**
     * 预备区
     */
    private val readyZone = ReadyZone()


    private val rolesViews = HashMap<View,Role>()

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
            val role = rolesViews[releasedChild]?:return

            val targetRect = mapDraw.calculateLocation(role,releasedChild)

            when(role.location){
                LOCATION_STORE -> {
                    //拖出了商店区域
                    if (!mapDraw.belongStoreZone(targetRect)){
                        //购买，加入预备区
                        storeRoles.remove(role)
                        //新的位置
                        val firstEmptyIndex = readyZone.getFirstEmptyIndex()
                        val readyZoneRect = mapDraw.getReadyItemZone(firstEmptyIndex)
                        roleCoordinates[releasedChild] = readyZoneRect

                        readyZone.addRole(role)

                        role.location = LOCATION_READY
                    }
                }
                LOCATION_READY -> {
                    //拖入商店 出售
                    if (mapDraw.belongStoreZone(targetRect)){
                        readyZone.removeRole(role)
                        removeView(releasedChild)
                    }else if (mapDraw.belongReadyZone(targetRect)){
                        //交换
                    }else if (mapDraw.belongCombatZone(targetRect)){
                        readyZone.removeRole(role)
                        roleCoordinates[releasedChild] = targetRect
                        role.location = LOCATION_COMBAT
                    }
                }
                LOCATION_COMBAT -> {
                    //拖入商店 出售
                    if (releasedChild.top>mapDraw.getStoreZone().top){
                        rolesInMap.removeRole(role)
                        removeView(releasedChild)
                    }
                }
            }

            //归位
            val curLeft = releasedChild.left
            val curTop = releasedChild.top
            val curWith = releasedChild.width
            val curHeight = releasedChild.height

            roleCoordinates[releasedChild]?.apply {
                val targetLeft = left
                val targetTop = top
                val targetWidth = width()
                val targetHeight = height()

                val animation = ValueAnimator.ofFloat(1f,0f)
                animation.addUpdateListener {
                    val scale = it.animatedValue as Float
                    releasedChild.left = (targetLeft + (curLeft - targetLeft)*scale).toInt()
                    releasedChild.top = (targetTop + (curTop - targetTop)*scale).toInt()
                    releasedChild.right = (releasedChild.left + targetWidth + (curWith -targetWidth)*scale).toInt()
                    releasedChild.bottom = (releasedChild.top + targetHeight + (curHeight - targetHeight)*scale).toInt()
                    logE(TAG,"l:${releasedChild.left} r:${releasedChild.right}")
                }
                animation.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        releasedChild.elevation = 3*density
                        //商店的视图删除，由新的角色视图替换
                        val roleView = createRoleView(role)
                        roleCoordinates[roleView] = this@apply
                        rolesViews[roleView] = role
                        addView(roleView)

                        removeView(releasedChild)
                        roleCoordinates.remove(releasedChild)
                        rolesViews.remove(releasedChild)
                    }
                })
                animation.duration = 200
                animation.start()
            }
            logE(TAG,"x:$curLeft y:$curTop")

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
    private val viewDragHelper = ViewDragHelper.create(this,dragCallback)

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount){
            val childView = getChildAt(index)
            roleCoordinates[childView]?.apply {
                childView.layout(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            }

            logD(TAG,"onLayout $childView to:${childView.left}")
        }
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
        val x = mapDraw.getStoreZone().left + mapDraw.getStoreCellWith() * storeRoles.size
        val y = mapDraw.getStoreZone().top

        roleCoordinates[storeBinding.root] = RectF(
                x, y, (x+storeItemWidth), (y+storeItemWidth)
        )

        storeRoles.add(role)
        rolesViews[storeBinding.root] = role
    }

    fun createRoleView(role: Role):View{
        val roleBinding = ItemRoleBinding.inflate(LayoutInflater.from(context),this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = mapDraw.getReadyCellWidth().toInt()
        layoutParams.height = mapDraw.getReadyCellWidth().toInt()
        roleBinding.root.layoutParams = layoutParams

        roleBinding.nameTv.text = role.name
        return roleBinding.root
    }

    fun addView(view:View,point:Point){
//        if (point.x>=cellCols)
//            throw RuntimeException("x must < $cellCols")
//        if (point.y>=cellRows)
//            throw RuntimeException("y must < $cellRows")
        val cellWidth = mapDraw.getCombatCellWidth()
        roleCoordinates[view] = RectF(
                point.x*cellWidth, point.y*cellWidth,
                (point.x+1)*cellWidth, (point.y+1)*cellWidth
        )
        addView(view)

        rolesViews[view] = Role("")
        logD(TAG,"add $view to:${point.x},${point.y}")
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
//        return super.onTouchEvent(event)
        return true
    }



}