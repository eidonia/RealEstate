package com.example.realestate.ui.buyOrLocActivities.buyActivity

import com.example.realestate.baseFragment.BaseFragmentListLocOrBuy
import com.example.realestate.models.BuyOrLoc

class ListBuyEstateFrag : BaseFragmentListLocOrBuy() {
    override fun getBuyOrLoc(): BuyOrLoc = BuyOrLoc.BUY
    override fun getPrizeText(): CharSequence = "Prix du bien"
    override fun getToolbarTitle(): CharSequence = "Vente"

}