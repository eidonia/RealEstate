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
            mutableLiveData = repo.getBuyOrLocEstate(buyOrLoc)
        }
        return mutableLiveData
    }

    fun getEstateRoom(idEstate: Long): MutableLiveData<RealEstate> {
        var mutableLiveData = MutableLiveData<RealEstate>()
        viewModelScope.launch { mutableLiveData.value = repo.getEstateWithIdRoom(idEstate) }
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
            emit(
                LatLng(
                    geocode.results!![0]?.geometry!!.location!!.lat!!.toDouble(),
                    geocode.results[0]?.geometry?.location?.lng!!.toDouble()
                )
            )
        }
    }

    fun putPicOnFirebase(pic: String): MutableLiveData<String> {
        var mutableLiveData = MutableLiveData<String>()
        viewModelScope.launch { mutableLiveData = repo.uploadPicOnFirebase(pic) }

        return mutableLiveData
    }

    fun updateEstate(estate: RealEstate): MutableLiveData<Long> {
        var mutableLiveData = MutableLiveData<Long>()
        viewModelScope.launch { mutableLiveData.value = repo.updateEstate(estate) }
        return mutableLiveData
    }


    fun deleteEstate(estate: RealEstate) {
        viewModelScope.launch { repo.deleteEstate(estate) }
    }

}