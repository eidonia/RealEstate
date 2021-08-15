package com.example.realestate.ui.buyOrLocActivities

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.realestate.FakeRepo
import com.example.realestate.MainCoroutineRule
import com.example.realestate.models.*
import com.example.realestate.ui.addEstate.AddViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BuyAndLocViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var buyAndLocViewModel: BuyAndLocViewModel

    private val estate = RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 251, "user", "lorem ipsum", 1.0, 1.0)



    @Before
    fun setup() {
        buyAndLocViewModel = BuyAndLocViewModel(FakeRepo())
    }

    @Test
    fun `get list Estate`() {
        buyAndLocViewModel.getEstate(estate.buyOrLoc).observeForever {
            assertEquals(3, it.size)
        }
        buyAndLocViewModel.getEstate(BuyOrLoc.BUY).observeForever {
            assertNotEquals(3, it.size)
            assertEquals(2, it.size)
        }
    }





}