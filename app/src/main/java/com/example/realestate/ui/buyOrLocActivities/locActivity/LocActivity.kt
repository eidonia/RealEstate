package com.example.realestate.ui.buyOrLocActivities.locActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.realestate.R
import com.example.realestate.databinding.ActivityLocBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.loc_nav_frag) as NavHostFragment
        val navController = navHostFragment.navController


    }
}