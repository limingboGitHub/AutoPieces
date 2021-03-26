package com.example.autopieces.view

import android.content.Intent
import android.os.Bundle
import android.text.method.MovementMethod
import com.example.autopieces.cpp.MoveMethod
import com.example.autopieces.databinding.ActivityMainBinding
import com.example.autopieces.logic.combat.search.getMovePath
import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName
import com.example.autopieces.utils.logE

class MainActivity : BaseActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)


        binding.startBt.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
//            finish()
        }

        test()
    }

    fun test(){
        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM, GameMap.COMBAT_COL_NUM)

        val teamOneRole = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)
        val teamOneRole2 = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)

        val teamTwoRole = MapRole(
            Role(RoleName.ZUO_ZU),
            belongTeam = 2)

        //两个阵营分别添加两个角色
        combatZone.addRole(teamOneRole,1,2)
        combatZone.addRole(teamOneRole2,2,3)
        combatZone.addRole(teamTwoRole,3,3)

        val map = combatZone.toIntArrayMap()
        val startTime = System.nanoTime()
        val result = MoveMethod.calculateMovePath(0,0,6,7,map,combatZone.row,combatZone.col)
        val time = System.nanoTime()-startTime
        logE("calculateMovePath",time.toString())
    }
}