package com.example.autopieces

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import com.example.autopieces.databinding.ActivityGameBinding
import com.example.autopieces.logic.Equipment
import com.example.autopieces.logic.combat.Combat
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.MapViewInterface
import com.example.autopieces.logic.role.*
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

        initUIListener()

        initElement()

        binding.startGameBt.setOnClickListener {
            val combat = Combat(viewModel.gameMap.combatZone)
            combat.start()
            //开启一个定时器，每隔33ms获取一次战斗状况，刷新UI
            val handler = Handler(Looper.getMainLooper())

            binding.readyTimeProgress.max = combat.combatTime.toInt()
            binding.readyTimeProgress.progress = combat.combatTime.toInt()
            timer.schedule(object: TimerTask(){
                override fun run() {

                    handler.post {
                        binding.readyTimeProgress.progress = combat.combatTime.toInt()

                        binding.mapView.update()
                    }
                    if (combat.isEnd()){
                        timer.cancel()
                        timer.purge()
                    }
                }
            },0,33)
        }
    }


    private fun initElement() {
        binding.mapView.postDelayed({

            //商店进货 5个角色
            binding.mapView.updateStore(createSameRole(RoleName.MING_REN))

            val equipments = listOf(
                Role(Equipment.KUWU),
                Role(Equipment.SHOULIJIAN)
            )
            binding.mapView.addEquipment(equipments)

            binding.mapView.addEnemy()
        },200)
    }


    private fun initMapView() {
        viewModel.gameMap.player = viewModel.getPlayer()

        binding.mapView.setGameMap(viewModel.gameMap)
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