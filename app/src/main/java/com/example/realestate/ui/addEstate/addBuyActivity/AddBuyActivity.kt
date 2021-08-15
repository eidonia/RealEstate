package com.example.realestate.ui.addEstate.addBuyActivity

import com.example.realestate.baseActivity.BaseActivityBuyLoc

class AddBuyActivity : BaseActivityBuyLoc() {
    override fun getPriceText(): CharSequence = "Prix"
    override fun getTitleName(): CharSequence = "Rajouter un bien en vente"
    override fun getName(): String = "BUY"
}