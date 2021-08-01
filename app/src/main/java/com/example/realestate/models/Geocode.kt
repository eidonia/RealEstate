package com.example.realestate.models

import com.google.gson.annotations.SerializedName

data class Geocode(

    @field:SerializedName("results")
    val results: List<ResultsItem?>? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class Southwest(

    @field:SerializedName("lng")
    val lng: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)

data class Geometry(

    @field:SerializedName("viewport")
    val viewport: Viewport? = null,

    @field:SerializedName("location")
    val location: Location? = null,

    @field:SerializedName("location_type")
    val locationType: String? = null
)

data class Viewport(

    @field:SerializedName("southwest")
    val southwest: Southwest? = null,

    @field:SerializedName("northeast")
    val northeast: Northeast? = null
)

data class Northeast(

    @field:SerializedName("lng")
    val lng: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)

data class PlusCode(

    @field:SerializedName("compound_code")
    val compoundCode: String? = null,

    @field:SerializedName("global_code")
    val globalCode: String? = null
)

data class ResultsItem(

    @field:SerializedName("formatted_address")
    val formattedAddress: String? = null,

    @field:SerializedName("types")
    val types: List<String?>? = null,

    @field:SerializedName("geometry")
    val geometry: Geometry? = null,

    @field:SerializedName("address_components")
    val addressComponents: List<AddressComponentsItem?>? = null,

    @field:SerializedName("plus_code")
    val plusCode: PlusCode? = null,

    @field:SerializedName("place_id")
    val placeId: String? = null
)

data class AddressComponentsItem(

    @field:SerializedName("types")
    val types: List<String?>? = null,

    @field:SerializedName("short_name")
    val shortName: String? = null,

    @field:SerializedName("long_name")
    val longName: String? = null
)

data class Location(

    @field:SerializedName("lng")
    val lng: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)
