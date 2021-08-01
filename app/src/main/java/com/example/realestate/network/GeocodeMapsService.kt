package com.example.realestate.network

import com.example.realestate.models.Geocode
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeMapsService {

    @GET("api/geocode/json?")
    suspend fun getGeocode(@Query("address") address: String, @Query("key") key: String): Geocode

}