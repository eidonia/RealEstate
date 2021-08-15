package com.example.realestate

import com.example.realestate.utils.Utils
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class UtilsTests {

    @Test
    fun convertDollarToEuros() {
        val euros = Utils.convertDollarToEuro(150)
        assertEquals(126, euros)
    }

    @Test
    fun convertEurosToDollars() {
        val euros = Utils.convertEuroToDollar(150)
        assertEquals(179, euros)
    }

    @Test
    fun getTodayDate() {
        val date = SimpleDateFormat("dd/MM/yyyy").format(Date())
        val testDate = Utils.getTodayDate()
        assertEquals(date, testDate)
    }
}