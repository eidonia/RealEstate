package com.example.realestate.ui.buyOrLocActivities.locActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.example.realestate.R
import com.example.realestate.adapter.ListLocOrBuyAdapter
import com.example.realestate.databinding.ActivityLocBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocActivity : AppCompatActivity(), ListLocOrBuyAdapter.FragCallBacks {

    private lateinit var binding: ActivityLocBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.listFragEstate) as NavHostFragment
    }

    override fun onClickList(id: Long) {
        val isTablet = resources.getBoolean(R.bool.isTablet)

        when {
            isTablet -> {
                val bundle = bundleOf("idLoc" to id)
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.infoEstate) as NavHostFragment

                navHostFragment.navController.navigate(R.id.locEstateFrag2, bundle)
            }
            else -> {
                val action = ListLocEstateFragDirections.actionListLocEstateFragToLocEstateFrag(id)
                navHostFragment.navController.navigate(action)
            }
        }

    }
}