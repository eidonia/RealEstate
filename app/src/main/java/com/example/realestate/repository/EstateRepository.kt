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
        Log.d("address", "address + $address")
        return "http://maps.google.com/maps/api/staticmap?center=$address&zoom=19&size=1000x800&sensor=false&markers=color:blue%7C$address&key=$API_KEY"
    }

    override suspend fun getLatlng(adress: String): Geocode {
        return geocodeMapsService.getGeocode(address = adress, key = API_KEY)
    }

    override suspend fun insertEstateDao(estate: RealEstate): Long {
        insertEstateFirebase(estate)
        return estateDao.insertEstate(estate)
    }

    override suspend fun insertEstateFirebase(estate: RealEstate) {
        db.collection(estate.buyOrLoc.name)
            .document(estate.dateEntry.toString())
            .set(estate)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                Log.d("errorInsertFirebase", "error : ${it.message}")
            }
    }

    override suspend fun getEstateWithIdRoom(id: Long): RealEstate {
        Log.d("testRepo", "id: $id")
        return estateDao.getEstatewithId(id)
    }


    override suspend fun getEstate(): MutableList<RealEstate> {
        return estateDao.getEstate()
    }

    override suspend fun uploadPicOnFirebase(pic: String): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        var picUri = Uri.parse(pic)
        var picRef: StorageReference = storage.child(picUri.lastPathSegment!!)
        var uploadTask: UploadTask = picRef.putFile(picUri)

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
            var downloadUri: Uri = task.result
            mutableLiveData.postValue(downloadUri.toString())
        }
        return mutableLiveData
    }

    override suspend fun getBuyOrLocEstate(buyOrLoc: BuyOrLoc): MutableLiveData<MutableList<RealEstate>> {
        var mutableLiveData = MutableLiveData<MutableList<RealEstate>>()
        //val listFireStore = getBuyOrLocFromFirebase(buyOrLoc)

        db.collection(buyOrLoc.name)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("test", "listPicFirebase : ${document["listPic"]}")
                    val estate = document.toObject(RealEstate::class.java)
                    Log.d("test", "listPicFirebase : ${estate.listPic}")
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

    override suspend fun updateEstateFirebase(estate: RealEstate) {
        db.collection(estate.buyOrLoc.name)
            .document(estate.dateEntry.toString())
            .set(estate, SetOptions.merge())
    }

    override suspend fun deleteEstateFirebase(estate: RealEstate) {
        db.collection(estate.buyOrLoc.name)
            .document(estate.dateEntry.toString())
            .delete()
    }

    override suspend fun deleteEstate(estate: RealEstate) {
        deleteEstateFirebase(estate)
        return estateDao.deleteEstate(estate)
    }
}