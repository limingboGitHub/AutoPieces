package com.example.autopieces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.autopieces.databinding.ActivityMainBinding
import com.example.autopieces.role.randomCreateRoles
import com.lmb.lmbkit.extend.transNavAndStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transNavAndStatus()

        //商店进货 5个角色
        binding.mapView.updateStore(randomCreateRoles(5))

        //点击刷新
        binding.refreshStoreBt.setOnClickListener {
            binding.mapView.updateStore(randomCreateRoles(5))
        }
    }

}