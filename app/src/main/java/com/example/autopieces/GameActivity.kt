package com.example.autopieces

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import com.example.autopieces.databinding.ActivityGameBinding
import com.example.autopieces.logic.Equipment
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.MapViewInterface
import com.example.autopieces.logic.role.*
import com.example.autopieces.utils.logE
import com.example.autopieces.view.window.RoleInfoWindow
import com.example.autopieces.viewmodel.GameViewModel
import com.lmb.lmbkit.extend.toast
import java.util.*

class GameActivity : BaseActivity() {
    val TAG = javaClass.name

    lateinit var binding : ActivityGameBinding

    val viewModel : GameViewModel by viewModels()

    var timer = Timer()

    var equipmentView:View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        initPlayer()

        initMapView()

        RolePool.init()
        //商店进货 5个角色
//        binding.mapView.updateStore(randomCreateRoles(player.level))
        binding.mapView.updateStore(createSameRole(RoleName.MING_REN))

        initUIListener()

        initEquipment()

        setTimer()
    }


    private fun initEquipment() {
        binding.mapView.postDelayed({
            val equipments = listOf(
                Role(Equipment.KUWU),
                Role(Equipment.SHOULIJIAN)
            )
            binding.mapView.addEquipment(equipments)
        },200)
    }

    private var restTime = 10*1000
    private var lastTime = 0L
    private fun setTimer() {
        val handler = Handler(Looper.getMainLooper())
        restTime = 10*1000
        lastTime = System.currentTimeMillis()

        binding.readyTimeProgress.max = restTime
        binding.readyTimeProgress.progress = restTime

        timer.schedule(object: TimerTask(){
            override fun run() {
                val curTime = System.currentTimeMillis()
                val timeSub = curTime - lastTime
                lastTime = curTime
                restTime = (restTime-timeSub.toInt())
                    .coerceAtLeast(0)
                handler.post {
                    binding.readyTimeProgress.progress = restTime
                }
                if (restTime == 0){
                    timer.cancel()
                    timer.purge()
                }
            }
        },0,16)
    }


    private fun initMapView() {
        val gameMap = GameMap()
        gameMap.player = viewModel.getPlayer()

        binding.mapView.setGameMap(gameMap)

        binding.mapView.setInterface(object : MapViewInterface {

            override fun update(gameMap: GameMap) {
                viewModel.player.value = gameMap.player
            }

            override fun roleClick(mapRole: MapRole) {
                RoleInfoWindow(binding.root as ViewGroup).show(mapRole)
            }
        })
    }

    private fun initPlayer() {
        binding.player = viewModel.player
        viewModel.addMoney(1000)
    }

    private fun initUIListener() {
        //点击刷新
        binding.refreshStoreCl.setOnClickListener {
            if (viewModel.useMoney(2)){
                binding.mapView.updateStore(randomCreateRoles(viewModel.getPlayer().level))
            }else{
                toast(R.string.your_money_is_not_enough)
            }
        }
        //购买经验
        binding.buyExpCl.setOnClickListener {
            val level = viewModel.getPlayer().level
            if (level>=9)
                return@setOnClickListener
            if (viewModel.useMoney(4)){
                viewModel.addExp(4)
            }else{
                toast(R.string.your_money_is_not_enough)
            }
        }
    }
}