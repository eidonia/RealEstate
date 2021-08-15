package com.example.realestate.models

import android.content.ContentValues
import android.content.Context
import android.preference.PreferenceManager
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class RealEstate(
    val buyOrLoc: BuyOrLoc = BuyOrLoc.LOCATION,
    val type: EstateType = EstateType.HOUSE,
    val oldOrNew: OldOrNew = OldOrNew.OLD,
    var priceDollar: String? = "",
    var priceEuro: String? = "",
    val size: String? = "",
    val nbRoom: String? = "",
    val numStreet: String? = "",
    val vicinity: String? = "",
    val zipCode: String? = "",
    val city: String? = "",
    val country: String? = "",
    val address: String? = "",
    var listPoi: MutableList<PointOfInterest>? = mutableListOf(),
    var listCriteria: MutableList<Criteria>? = mutableListOf(),
    var listPic: MutableList<String> = mutableListOf(),
    var isAvailable: Boolean = true,
    @PrimaryKey(autoGenerate = false)
    var dateEntry: Long = 1,
    var employee: String? = "",
    var description: String = "",
    var lat: Double = 1.0,
    var lng: Double = 1.0,
) {

    var dateSellOrRent: Long? = null
    var staticImage: String? = null

    fun formatType(): String {
        return when (this.type) {
            EstateType.HOUSE -> "Maison"
            EstateType.APARTMENT -> "Appartement"
            EstateType.DUPLEX -> "Duplex"
            EstateType.MANOR -> "Manoir"
        }
    }

    fun formatBuyOrLoc(): String {
        return when (this.buyOrLoc) {
            BuyOrLoc.LOCATION -> "Location"
            BuyOrLoc.BUY -> "Achat"
        }
    }

    fun formatPointOfInterest(pointOfInterest: PointOfInterest): String {
        return when (pointOfInterest) {
            PointOfInterest.BUSINESS -> "Commerces"
            PointOfInterest.SCHOOLS -> "Ecoles"
            PointOfInterest.TRANSPORTS -> "Transports"
            PointOfInterest.PARK -> "Parc"
            PointOfInterest.MEDICAL -> "Etablissement de soin"

        }
    }

    fun formatCriteria(criteria: Criteria): String {
        return when (criteria) {
            Criteria.BALCONY -> "Balcon"
            Criteria.PARKING -> "Parking"
            Criteria.KITCHEN_AREA -> "Cuisine"
            Criteria.OPENPLANE_KITCHEN -> "Cuisine ouverte"
            Criteria.CAVE -> "Cave"
            Criteria.TERRACE -> "Terrasse"
        }
    }

    fun getDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        return sdf.format(this.dateEntry)
    }

    fun getDateUnavailable(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        return sdf.format(this.dateSellOrRent ?: 0)
    }

    fun getCurrency(context: Context): String {
        return when (PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("getCurrency", false)) {
            true -> {
                "${separatorThousand(priceDollar!!)} $"
            }
            false -> "${separatorThousand(priceEuro!!)} €"
        }
    }

    fun getSymbolCurrency(context: Context): String {
        return when (PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("getCurrency", false)) {
            true -> " $"
            false -> "€"
        }
    }

    fun euroOrDollarLong(context: Context): Long {
        return when (PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("getCurrency", false)) {
            true -> priceDollar!!.toLong()
            false -> priceEuro!!.toLong()
        }
    }

    private fun separatorThousand(number: String): String {
        return NumberFormat.getNumberInstance(Locale.FRANCE).format(number.toLong())
    }

    fun forContentValues(values: ContentValues): RealEstate {
        return RealEstate(
            values.get("buyOrLoc") as BuyOrLoc,
            values.get("type") as EstateType,
            values.get("oldOrNew") as OldOrNew,
            values.getAsString("priceDollar"),
            values.getAsString("priceEuro"),
            values.getAsString("size"),
            values.getAsString("nbRoom"),
            values.getAsString("numStreet"),
            values.getAsString("vicinity"),
            values.getAsString("zipCode"),
            values.getAsString("city"),
            values.getAsString("country"),
            values.getAsString("address"),
            values.get("listPoi") as MutableList<PointOfInterest>?,
            values.get("listCriteria") as MutableList<Criteria>?,
            values.get("listPic") as MutableList<String>,
            values.getAsBoolean("isAvailable"),
            values.getAsLong("dateEntry"),
            values.getAsString("employee"),
            values.getAsString("description"),
            values.getAsDouble("lat"),
            values.getAsDouble("lng")
        )
    }

}

enum class BuyOrLoc {
    BUY, LOCATION
}

enum class OldOrNew {
    OLD, NEW
}

enum class EstateType {
    HOUSE, APARTMENT, DUPLEX, MANOR
}

enum class PointOfInterest {
    SCHOOLS, BUSINESS, TRANSPORTS, PARK, MEDICAL
}

enum class Criteria {
    CAVE, PARKING, KITCHEN_AREA, OPENPLANE_KITCHEN, TERRACE, BALCONY
}
