package com.example.realestate.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.realestate.database.dao.EstateDao
import com.example.realestate.models.RealEstate
import com.example.realestate.utils.Converters

@Database(
    entities = [RealEstate::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EstateDatabase : RoomDatabase() {
    abstract fun estateDao(): EstateDao
}