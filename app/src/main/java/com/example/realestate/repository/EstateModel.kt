package com.example.realestate.repository

import androidx.lifecycle.MutableLiveData
import com.example.realestate.models.BuyOrLoc
import com.example.realestate.models.Geocode
import com.example.realestate.models.RealEstate

interface EstateModel {
    suspend fun getStaticImage(address: String): String
    suspend fun getLatlng(adress: String): Geocode
    suspend fun insertEstateDao(estate: RealEstate): Long
    suspend fun insertEstateFirebase(estate: RealEstate)
    suspend fun getEstateWithIdRoom(id: Long): RealEstate
    suspend fun getEstate(): MutableList<RealEstate>
    suspend fun uploadPicOnFirebase(pic: String): MutableLiveData<String>
    suspend fun getBuyOrLocEstate(buyOrLoc: BuyOrLoc): MutableLiveData<MutableList<RealEstate>>
    suspend fun updateEstate(estate: RealEstate): Long
    suspend fun deleteEstate(estate: RealEstate)
    suspend fun updateEstateFirebase(estate: RealEstate)
    suspend fun deleteEstateFirebase(estate: RealEstate)

}