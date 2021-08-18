package com.example.realestate.ui.buyOrLocActivities.locActivity

import com.example.realestate.R
import com.example.realestate.baseFragment.BaseFragmentListLocOrBuy
import com.example.realestate.models.BuyOrLoc
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListLocEstateFrag : BaseFragmentListLocOrBuy() {
    override fun getBuyOrLoc(): BuyOrLoc = BuyOrLoc.LOCATION

    override fun getPrizeText(): CharSequence? = getString(R.string.price_per_month)

    override fun getToolbarTitle(): CharSequence? = getString(R.string.location)
}