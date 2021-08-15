package com.example.realestate.ui.mapActivity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.realestate.FakeRepo
import com.example.realestate.MainCoroutineRule
import com.example.realestate.models.*
import com.example.realestate.ui.buyOrLocActivities.BuyAndLocViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MapViewModelTest {    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var mapViewModel: MapViewModel

    @Before
    fun setup() {
        mapViewModel = MapViewModel(FakeRepo())
    }

    @Test
    fun `get list estate`() {
        mapViewModel.getListEstate().observeForever {
            assertEquals(5, it.size)
        }
    }
}