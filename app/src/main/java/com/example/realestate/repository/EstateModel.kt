package com.example.realestate.repository

import androidx.lifecycle.MutableLiveData
import com.example.realestate.models.BuyOrLoc
import com.example.realestate.models.Geocode
import com.example.realestate.models.RealEstate

interface EstateModel {
    suspend fun getStaticImage(address: String): String
    suspend fun getLatlng(adress: String): Geocode
    suspend fun insertEstate(estate: RealEstate): Long
    suspend fun getEstate(id: Long): RealEstate
    suspend fun getListEstate(): MutableList<RealEstate>
    suspend fun uploadPic(pic: String): MutableLiveData<String>
    suspend fun getListEstate(buyOrLoc: BuyOrLoc): MutableLiveData<MutableList<RealEstate>>
    suspend fun updateEstate(estate: RealEstate): Long
}