package com.example.realestate.ui.addEstate.addLocActivity

import com.example.realestate.R
import com.example.realestate.baseActivity.BaseActivityBuyLoc

class AddLocActivity : BaseActivityBuyLoc() {
    override fun getPriceText(): CharSequence = getString(R.string.price_per_month)
    override fun getTitleName(): CharSequence = getString(R.string.add_estate_loc)
    override fun getName(): String = "LOC"
}