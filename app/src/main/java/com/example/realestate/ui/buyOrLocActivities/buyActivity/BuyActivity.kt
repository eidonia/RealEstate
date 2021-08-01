package com.example.realestate.ui.buyOrLocActivities.buyActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.realestate.databinding.ActivityBuyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}