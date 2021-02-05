package com.example.autopieces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.example.autopieces.databinding.ActivityMainBinding
import com.example.autopieces.role.randomCreateRoles
import com.lmb.lmbkit.extend.toast
import com.lmb.lmbkit.extend.transNavAndStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    /**
     * 玩家
     */
    private val player = Player()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        transNavAndStatus()

        binding.player = player
        player.money.value = 5
        //商店进货 5个角色
        binding.mapView.updateStore(randomCreateRoles(5))

        //点击刷新
        binding.refreshStoreCl.setOnClickListener {
            val money = player.money.value?:0
            if (money>=2){
                player.money.value = money-2
                binding.mapView.updateStore(randomCreateRoles(5))
            }else{
                toast(R.string.your_money_is_not_enough)
            }
        }
        //购买经验
        binding.buyExpCl.setOnClickListener {
            val money = player.money.value?:0
            if (money>=4){
                player.money.value = money-4
                player.addExp(4)
            }else{
                toast(R.string.your_money_is_not_enough)
            }
        }
    }

}