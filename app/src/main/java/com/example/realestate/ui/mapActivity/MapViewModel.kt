package com.example.realestate.ui.mapActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realestate.models.RealEstate
import com.example.realestate.repository.EstateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(val repo: EstateModel) : ViewModel() {

    fun getListEstate(): MutableLiveData<MutableList<RealEstate>> {
        var mutableLiveData = MutableLiveData<MutableList<RealEstate>>()
        viewModelScope.launch {
            mutableLiveData.value = repo.getEstate()
        }
        return mutableLiveData
    }
}
