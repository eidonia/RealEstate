package com.example.realestate.utils

import androidx.room.TypeConverter
import com.example.realestate.models.Criteria
import com.example.realestate.models.PointOfInterest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {

    private var gson = Gson()

    @TypeConverter
    fun stringToStringList(data: String?): MutableList<String?>? {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson<MutableList<String?>>(data, listType)
    }

    @TypeConverter
    fun stringListToString(someObjects: List<String?>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToPOIList(data: String?): MutableList<PointOfInterest?>? {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<List<PointOfInterest?>?>() {}.type
        return gson.fromJson<MutableList<PointOfInterest?>>(data, listType)
    }

    @TypeConverter
    fun pOIListToString(someObjects: List<PointOfInterest?>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun stringToCriteriaList(data: String?): MutableList<Criteria?>? {
        if (data == null) {
            return mutableListOf()
        }
        val listType: Type = object : TypeToken<List<Criteria?>?>() {}.type
        return gson.fromJson<MutableList<Criteria?>>(data, listType)
    }

    @TypeConverter
    fun criteriaListToString(someObjects: List<Criteria?>?): String? {
        return gson.toJson(someObjects)
    }
}