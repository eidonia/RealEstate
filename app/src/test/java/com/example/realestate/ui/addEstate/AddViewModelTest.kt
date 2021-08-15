package com.example.realestate.ui.addEstate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.realestate.FakeRepo
import com.example.realestate.MainCoroutineRule
import com.example.realestate.models.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AddViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var addViewModel: AddViewModel

    private val estate = RealEstate(BuyOrLoc.LOCATION, EstateType.APARTMENT, OldOrNew.NEW, "11111", "111111", "70", "5", "5", "rue auguste mounié", "92160", "Antony", "France", "5+rue auguste mounié+92160+Antony+France", mutableListOf(), mutableListOf(), mutableListOf(), true, 251, "user", "lorem ipsum", 1.0, 1.0)



    @Before
    fun setup() {
        addViewModel = AddViewModel(FakeRepo())
    }

    @Test
    fun `insert estate`() {
        addViewModel.insertEstate(estate).observeForever {
            assertEquals(251, it)
        }
    }

    @Test
    fun `get estate`() {
        addViewModel.getEstateRoom(251).observeForever {
            assertEquals(estate, it)
        }
    }

    @Test
    fun `get address`() {
        estate.address?.let { addViewModel.setAddress(it) }
        addViewModel.staticImage.observeForever {
            assertEquals(estate.address, it)
        }
    }

    @Test
    fun `upload Pic`() {
        addViewModel.uploadPicture("urlPic").observeForever {
            assertEquals("urlPic", "urlPic")
        }
    }
}