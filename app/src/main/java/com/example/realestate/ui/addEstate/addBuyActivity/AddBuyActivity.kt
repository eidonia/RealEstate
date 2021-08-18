package com.example.realestate.ui.addEstate.addBuyActivity

import com.example.realestate.R
import com.example.realestate.baseActivity.BaseActivityBuyLoc

class AddBuyActivity : BaseActivityBuyLoc() {
    override fun getPriceText(): CharSequence = getString(R.string.price_buy)
    override fun getTitleName(): CharSequence = getString(R.string.add_estate_buy)
    override fun getName(): String = "BUY"
}