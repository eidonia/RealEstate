package com.example.realestate

import androidx.lifecycle.MutableLiveData
import com.example.realestate.models.*
import com.example.realestate.repository.EstateModel

class FakeRepo: EstateModel {
    override suspend fun getStaticImage(address: String): String {
        return address
    }

    override suspend fun getLatlng(adress: String): Geocode {
        return Geocode(null, "success")
    }

    override suspend fun insertEstate(estate: RealEstate): Long {
        return estate.dateEntry
    }

    override suspend fun getEstate(id: Long): RealEstate {
        return RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 251, "user", "lorem ipsum", 1.0, 1.0)
    }

    override suspend fun getListEstate(): MutableList<RealEstate> {
        val listEstate = mutableListOf<RealEstate>()
        listEstate.add(RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 251, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11121", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 252, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11131", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 253, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.BUY, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 254, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.BUY, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 255, "user", "lorem ipsum", 1.0, 1.0))
        return listEstate
    }

    override suspend fun uploadPic(pic: String): MutableLiveData<String> {
        return MutableLiveData<String>("urlPic")
    }

    override suspend fun getListEstate(buyOrLoc: BuyOrLoc): MutableLiveData<MutableList<RealEstate>> {
        val listEstate = mutableListOf<RealEstate>()
        listEstate.add(RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 251, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11121", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 252, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11131", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 253, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.BUY, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 254, "user", "lorem ipsum", 1.0, 1.0))
        listEstate.add(RealEstate(BuyOrLoc.BUY, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 255, "user", "lorem ipsum", 1.0, 1.0))
        val list = mutableListOf<RealEstate>()
        for (estate in listEstate) {
            if (estate.buyOrLoc == buyOrLoc) list.add(estate)
        }
        return MutableLiveData(list)
    }

    override suspend fun updateEstate(estate: RealEstate): Long {
        TODO("Not yet implemented")
    }
}