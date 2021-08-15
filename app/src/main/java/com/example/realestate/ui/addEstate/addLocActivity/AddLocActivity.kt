package com.example.realestate.ui.addEstate.addLocActivity

import com.example.realestate.baseActivity.BaseActivityBuyLoc

class AddLocActivity : BaseActivityBuyLoc() {
    override fun getPriceText(): CharSequence = "Prix par mois"
    override fun getTitleName(): CharSequence = "Rajouter un bien en location"
    override fun getName(): String = "LOC"
}