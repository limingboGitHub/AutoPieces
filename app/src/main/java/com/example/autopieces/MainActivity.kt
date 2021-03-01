package com.example.autopieces

import android.content.Intent
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

class MainActivity : BaseActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        binding.startBt.setOnClickListener {
            startActivity(Intent(this,GameActivity::class.java))
//            finish()
        }
    }
}