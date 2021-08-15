package com.example.realestate.ui.buyOrLocActivities.locActivity

import com.example.realestate.baseFragment.BaseFragmentListLocOrBuy
import com.example.realestate.models.BuyOrLoc
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListLocEstateFrag : BaseFragmentListLocOrBuy() {
    override fun getBuyOrLoc(): BuyOrLoc = BuyOrLoc.LOCATION

    override fun getPrizeText(): CharSequence? = "Prix par mois"

    override fun getToolbarTitle(): CharSequence? = "Location"
}