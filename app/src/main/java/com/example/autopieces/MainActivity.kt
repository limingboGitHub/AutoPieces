package com.example.autopieces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.autopieces.databinding.ActivityMainBinding
import com.example.autopieces.map.MapView
import com.example.autopieces.role.RolePool
import com.example.autopieces.role.randomCreateRoles
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
        binding.mapView.updateStore(randomCreateRoles(player.level))

        initUIListener()
    }

    private fun initMapView() {
        viewModel.player.value?.apply {
            binding.mapView.setPlayer(this)
            binding.mapView.playerUpdateListenerAdapter = object : MapView.PlayerUpdateListener{
                override fun update(player: Player) {
                    viewModel.player.value = player
                }
            }
        }
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