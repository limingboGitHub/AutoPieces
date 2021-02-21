package com.example.autopieces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.autopieces.databinding.ActivityMainBinding
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapViewInterface
import com.example.autopieces.logic.role.RoleName
import com.example.autopieces.logic.role.RolePool
import com.example.autopieces.logic.role.createSameRole
import com.example.autopieces.logic.role.randomCreateRoles
import com.lmb.lmbkit.extend.toast
import com.lmb.lmbkit.extend.transNavAndStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        transNavAndStatus()

        initPlayer()

        val player = viewModel.player.value?:return

        initMapView()

        RolePool.init()
        //商店进货 5个角色
//        binding.mapView.updateStore(randomCreateRoles(player.level))
        binding.mapView.updateStore(createSameRole(RoleName.MING_REN))

        initUIListener()
    }

    private fun initMapView() {
        val gameMap = GameMap()
        gameMap.player = viewModel.getPlayer()

        binding.mapView.setGameMap(gameMap)

        binding.mapView.setInterface(object : MapViewInterface{
            override fun playerLevel(): Int {
                return viewModel.getPlayer().level
            }

            override fun isMoneyEnough(toUseMoney: Int): Boolean {
                return viewModel.getPlayer().money >= toUseMoney
            }

            override fun useMoney(toUseMoney: Int) {
                viewModel.useMoney(toUseMoney)
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