package com.example.realestate.ui.buyOrLocActivities.buyActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.example.realestate.R
import com.example.realestate.adapter.ListLocOrBuyAdapter
import com.example.realestate.databinding.ActivityBuyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyActivity : AppCompatActivity(), ListLocOrBuyAdapter.FragCallBacks {

    private lateinit var binding: ActivityBuyBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.listFragEstate) as NavHostFragment
    }


    override fun onClickList(id: Long) {
        val isTablet = resources.getBoolean(R.bool.isTablet)
        when {
            isTablet -> {
                val bundle = bundleOf("idBuy" to id)
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.infoEstate) as NavHostFragment

                navHostFragment.navController.navigate(R.id.buyEstateFrag2, bundle)
            }
            else -> {
                val action = ListBuyEstateFragDirections.actionListBuyEstateFragToBuyEstateFrag(id)
                navHostFragment.navController.navigate(action)
            }
        }

    }
}