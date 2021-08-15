package com.example.realestate.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.realestate.models.BuyOrLoc
import com.example.realestate.models.RealEstate

@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEstate(estate: RealEstate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateRealEstate(estate: RealEstate): Long

    @Query("SELECT * FROM realestate WHERE dateEntry = :id")
    suspend fun getEstatewithId(id: Long): RealEstate

    @Query("SELECT * FROM realestate")
    suspend fun getEstate(): MutableList<RealEstate>

    @Query("SELECT * FROM realestate WHERE buyOrLoc = :buyOrLoc")
    fun getLocOrBuyEstate(buyOrLoc: BuyOrLoc): MutableList<RealEstate>

    @Query("SELECT * FROM realestate")
    fun getEstateCursor(): Cursor
}

