package com.example.realestate.ui.addEstate

import androidx.lifecycle.*
import com.example.realestate.models.RealEstate
import com.example.realestate.repository.EstateModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(private val repo: EstateModel) : ViewModel() {

    private val addressGeocode: LiveData<String> = MutableLiveData()
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

    fun setAddress(address: String) {
        addressGeocode as MutableLiveData<String>
        addressGeocode.value = address
    }

    val staticImage: LiveData<String> = addressGeocode.switchMap { address ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(repo.getStaticImage(address))
        }

    }

    fun uploadPicture(pic: String): MutableLiveData<String> {
        var mutableLiveData = MutableLiveData<String>()
        viewModelScope.launch { mutableLiveData = repo.uploadPic(pic) }
        return mutableLiveData
    }

    fun getEstateRoom(idEstate: Long): MutableLiveData<RealEstate> {
        var mutableLiveData = MutableLiveData<RealEstate>()
        viewModelScope.launch { mutableLiveData.value = repo.getEstate(idEstate) }
        return mutableLiveData
    }

    fun insertEstate(estate: RealEstate): MutableLiveData<Long> {
        var mutableLiveData = MutableLiveData<Long>()
        viewModelScope.launch { mutableLiveData.value = repo.insertEstate(estate) }
        return mutableLiveData
    }

}