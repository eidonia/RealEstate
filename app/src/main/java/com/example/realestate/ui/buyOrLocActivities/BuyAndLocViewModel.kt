package com.example.realestate.ui.buyOrLocActivities

import androidx.lifecycle.*
import com.example.realestate.models.BuyOrLoc
import com.example.realestate.models.RealEstate
import com.example.realestate.repository.EstateModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyAndLocViewModel @Inject constructor(private val repo: EstateModel) : ViewModel() {

    fun getEstate(buyOrLoc: BuyOrLoc): MutableLiveData<MutableList<RealEstate>> {
        var mutableLiveData = MutableLiveData<MutableList<RealEstate>>()
        viewModelScope.launch {
            mutableLiveData = repo.getListEstate(buyOrLoc)
        }
        return mutableLiveData
    }

    fun getEstateRoom(idEstate: Long): MutableLiveData<RealEstate> {
        var mutableLiveData = MutableLiveData<RealEstate>()
        viewModelScope.launch { mutableLiveData.value = repo.getEstate(idEstate) }
        return mutableLiveData
    }

    fun setAddress(address: String) {
        addressGeocode as MutableLiveData<String>
        addressGeocode.value = address
    }

    private val addressGeocode: LiveData<String> = MutableLiveData()
    val staticImage: LiveData<String> = addressGeocode.switchMap { address ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(repo.getStaticImage(address))
        }
    }

    val geocode: LiveData<LatLng> = addressGeocode.switchMap { address ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val geocode = repo.getLatlng(address)
            geocode.results?.get(0)?.geometry?.location?.let { location ->
                if (location.lat != null && location.lng != null) {
                    emit(
                        LatLng(
                            location.lat.toDouble(),
                            location.lng.toDouble()
                        )
                    )
                }
            }

        }
    }

    fun putPicOnFirebase(pic: String): MutableLiveData<String> {
        var mutableLiveData = MutableLiveData<String>()
        viewModelScope.launch { mutableLiveData = repo.uploadPic(pic) }

        return mutableLiveData
    }

    fun updateEstate(estate: RealEstate): MutableLiveData<Long> {
        var mutableLiveData = MutableLiveData<Long>()
        viewModelScope.launch { mutableLiveData.value = repo.updateEstate(estate) }
        return mutableLiveData
    }
}