package com.example.realestate.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.realestate.database.dao.EstateDao
import com.example.realestate.models.BuyOrLoc
import com.example.realestate.models.Geocode
import com.example.realestate.models.RealEstate
import com.example.realestate.network.GeocodeMapsService
import com.example.realestate.utils.Constante.API_KEY
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import javax.inject.Inject

class EstateRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val geocodeMapsService: GeocodeMapsService,
    private val estateDao: EstateDao,
    private val storage: StorageReference
) : EstateModel {


    override suspend fun getStaticImage(address: String): String {
        return "http://maps.google.com/maps/api/staticmap?center=$address&zoom=19&size=1000x800&sensor=false&markers=color:blue%7C$address&key=$API_KEY"
    }

    override suspend fun getLatlng(adress: String): Geocode {
        return geocodeMapsService.getGeocode(address = adress, key = API_KEY)
    }

    override suspend fun insertEstate(estate: RealEstate): Long {
        insertEstateFirebase(estate)
        return estateDao.insertEstate(estate)
    }

    private fun insertEstateFirebase(estate: RealEstate) {
        db.collection(estate.buyOrLoc.name)
            .document(estate.dateEntry.toString())
            .set(estate)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Log.d("errorInsertFirebase", "error : ${it.message}")
            }
    }

    override suspend fun getEstate(id: Long): RealEstate {
        return estateDao.getEstatewithId(id)
    }


    override suspend fun getListEstate(): MutableList<RealEstate> {
        return estateDao.getEstate()
    }

    override suspend fun uploadPic(pic: String): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        val picUri = Uri.parse(pic)

        val picRef: StorageReference = storage.child(picUri.lastPathSegment!!)
        val uploadTask: UploadTask = picRef.putFile(picUri)

        uploadTask.addOnFailureListener {
            Log.d("uploadPic", "error : ${it.message}")
        }.addOnSuccessListener {
            Log.d("uploadPic", "done")
        }

        var urlTask: Task<Uri> = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw  task.exception!!
            }
            return@continueWithTask picRef.downloadUrl
        }.addOnCompleteListener { task ->
            val downloadUri: Uri = task.result
            mutableLiveData.postValue(downloadUri.toString())
        }
        return mutableLiveData
    }

    override suspend fun getListEstate(buyOrLoc: BuyOrLoc): MutableLiveData<MutableList<RealEstate>> {
        val mutableLiveData = MutableLiveData<MutableList<RealEstate>>()

        db.collection(buyOrLoc.name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val estate = document.toObject(RealEstate::class.java)
                    estateDao.insertEstate(estate)
                }
                mutableLiveData.value = estateDao.getLocOrBuyEstate(buyOrLoc)
            }
            .addOnFailureListener {
                Log.d("errorGetFirebase", "error: ${it.message}")
            }

        return mutableLiveData
    }

    override suspend fun updateEstate(estate: RealEstate): Long {
        updateEstateFirebase(estate)
        return estateDao.updateRealEstate(estate)
    }

    private fun updateEstateFirebase(estate: RealEstate) {
        db.collection(estate.buyOrLoc.name)
            .document(estate.dateEntry.toString())
            .set(estate, SetOptions.merge())
    }
}