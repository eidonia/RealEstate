package com.example.realestate.ui.buyOrLocActivities.buyActivity

import com.example.realestate.R
import com.example.realestate.baseFragment.BaseFragmentListLocOrBuy
import com.example.realestate.models.BuyOrLoc

class ListBuyEstateFrag : BaseFragmentListLocOrBuy() {
    override fun getBuyOrLoc(): BuyOrLoc = BuyOrLoc.BUY
    override fun getPrizeText(): CharSequence = getString(R.string.estate_price)
    override fun getToolbarTitle(): CharSequence = getString(R.string.sell)

}