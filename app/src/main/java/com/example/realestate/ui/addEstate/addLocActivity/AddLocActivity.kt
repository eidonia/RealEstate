package com.example.realestate.ui.addEstate.addLocActivity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.adapter.ListPicAdapter
import com.example.realestate.databinding.ActivityAddEstateBinding
import com.example.realestate.models.*
import com.example.realestate.ui.addEstate.AddViewModel
import com.example.realestate.ui.addEstate.EstateAfterAdd
import com.example.realestate.utils.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddLocActivity : AppCompatActivity(), BottomSheetDialog.OpenActivity,
    ListPicAdapter.UpdatePic {

    private lateinit var binding: ActivityAddEstateBinding
    private lateinit var listPicAdapter: ListPicAdapter
    var listPicEstate = mutableListOf<String>()
    private val GET_FROM_GALLERY = 3
    private val GET_FROM_CAMERA = 6
    private lateinit var picEstate: String
    private lateinit var btsPic: BottomSheetDialog
    private val name = "LOC"
    private lateinit var currentPhotoPath: String
    private lateinit var photoUri: Uri
    private var listUriPic = mutableListOf<String>()
    private var check = 0
    private lateinit var viewModel: AddViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEstateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        binding.toolbar.title = "Rajouter un bien en location"
        binding.priceEstate.hint = "Prix par mois"

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("getCurrency", false)) {
            binding.priceEstate.suffixText = "$"
        } else {
            binding.priceEstate.suffixText = "€"
        }

        btsPic = BottomSheetDialog(this, this)
        listPicAdapter = ListPicAdapter(this, this)
        binding.btnAddPic.setOnClickListener {
            btsPic.openBTS()
        }

        binding.listPic.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.listPic.adapter = listPicAdapter

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }


        binding.btnAdd.setOnClickListener { addEstate() }

    }

    private fun addEstate() {
        with(binding) {

            val typeEstate = when (radioGroup.checkedRadioButtonId) {
                R.id.radioHouse -> EstateType.HOUSE
                R.id.radioApartment -> EstateType.APARTMENT
                R.id.radioDuplex -> EstateType.DUPLEX
                else -> EstateType.MANOR
            }

            val ageEstate = when (radioGroupAge.checkedRadioButtonId) {
                R.id.radioOld -> OldOrNew.OLD
                else -> OldOrNew.NEW
            }

            val address: String = createAddress()

            val nbRoom = if (editnumRoom.text.toString() != "") {
                editnumRoom.text.toString()
            } else {
                check++
                editnumRoom.error = "Champ requis"
                ""
            }

            val sizeEstate = if (editSizeEstate.text.toString() != "") {
                editSizeEstate.text.toString()
            } else {
                check++
                editSizeEstate.error = "Champ requis"
                ""
            }

            val description = if (editDescription.text.toString() != "") {
                editDescription.text.toString()
            } else {
                check++
                editDescription.error = "Champ requis"
                ""
            }

            val price = if (editPriceEstate.text.toString() != "") {
                editPriceEstate.text.toString()
            } else {
                check++
                editPriceEstate.error = "Champ requis"
                ""
            }

            if (listUriPic.isEmpty()) {
                textAddPic.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                textAddPic.text = "Ajouter au moins une photo"
                check++
            } else {
                textAddPic.text = ""
            }

            val listCriteria: ArrayList<Criteria> = createCriteriaList()
            val listAround: ArrayList<PointOfInterest> = createPoiList()
            val user = FirebaseAuth.getInstance().currentUser
            val employee = user!!.displayName

            Log.d("resultAdd", "typeEstate : ${typeEstate.name}")
            Log.d("resultAdd", address)
            Log.d("resultAdd", "nbRoom : $nbRoom")
            Log.d("resultAdd", "size : $sizeEstate m²")
            Log.d("resultAdd", "description : $description")
            Log.d("resultAdd", "price : $price €")
            for (criteria: Criteria in listCriteria) {
                Log.d("resultAdd", "criteria : ${criteria.name}")
            }
            for (poi: PointOfInterest in listAround) {
                Log.d("resultAdd", "criteria : ${poi.name}")
            }
            if (check == 0) {
                viewModel.setAddress(address)
                viewModel.geocode.observe(this@AddLocActivity, { it ->

                    Log.d("blop", "fini l'observe")

                    val estate = RealEstate(
                        buyOrLoc = BuyOrLoc.LOCATION,
                        type = typeEstate,
                        numStreet = editNumStreet.text.toString(),
                        vicinity = editVicinity.text.toString(),
                        zipCode = editZipCode.text.toString(),
                        city = editCity.text.toString(),
                        country = editCountry.text.toString(),
                        address = address,
                        nbRoom = nbRoom,
                        size = sizeEstate,
                        description = description,
                        priceDollar = price,
                        priceEuro = price,
                        listCriteria = listCriteria,
                        listPoi = listAround,
                        listPic = listUriPic,
                        isAvailable = true,
                        dateEntry = System.currentTimeMillis(),
                        employee = employee,
                        oldOrNew = ageEstate,
                        lat = it.latitude,
                        lng = it.longitude
                    )

                    viewModel.staticImage.observe(
                        this@AddLocActivity,
                        { staticImageUrl ->
                            estate.staticImage = staticImageUrl
                        })

                    viewModel.insertDao(estate).observe(this@AddLocActivity, { dateEntry ->
                        var intent = Intent(
                            this@AddLocActivity,
                            EstateAfterAdd::class.java
                        ).apply { putExtra("idAdd", dateEntry) }
                        finish()
                        startActivity(intent)
                    })

                    check = 0

                })
            } else {
                textValidate.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                textValidate.text = "Erreur, vérifiez les champs"
                check = 0
            }
        }
    }

    private fun createAddress(): String {
        with(binding) {
            val numStreet = if (editNumStreet.text.toString() != "") {
                editNumStreet.text.toString()
            } else {
                check++
                editNumStreet.error = "Champ requis"
                ""
            }

            val vicinity = if (editVicinity.text.toString() != "") {
                editVicinity.text.toString()
            } else {
                check++
                editVicinity.error = "Champ requis"
                ""
            }

            val zipCode = if (editZipCode.text.toString() != "") {
                editZipCode.text.toString()
            } else {
                check++
                editZipCode.error = "Champ requis"
                ""
            }

            val city = if (editCity.text.toString() != "") {
                editCity.text.toString()
            } else {
                check++
                editCity.error = "Champ requis"
                ""
            }

            val country = if (editCountry.text.toString() != "") {
                editCountry.text.toString()
            } else {
                check++
                editCountry.error = "Champ requis"
                ""
            }

            return "$numStreet+$vicinity+$zipCode+$city+$country"
        }
    }

    private fun createPoiList(): ArrayList<PointOfInterest> {
        val list = arrayListOf<PointOfInterest>()
        with(binding) {
            if (checkSchools.isChecked) list.add(PointOfInterest.SCHOOLS)
            if (checkBusiness.isChecked) list.add(PointOfInterest.BUSINESS)
            if (checkTransports.isChecked) list.add(PointOfInterest.TRANSPORTS)
            if (checkPark.isChecked) list.add(PointOfInterest.PARK)
            if (checkMedical.isChecked) list.add(PointOfInterest.MEDICAL)
        }
        return list

    }

    private fun createCriteriaList(): ArrayList<Criteria> {
        val list = arrayListOf<Criteria>()
        with(binding) {
            if (checkCave.isChecked) list.add(Criteria.CAVE)
            if (checkParking.isChecked) list.add(Criteria.PARKING)
            if (checkKitchArea.isChecked) list.add(Criteria.KITCHEN_AREA)
            if (checkOpenKitch.isChecked) list.add(Criteria.OPENPLANE_KITCHEN)
            if (checkTerrace.isChecked) list.add(Criteria.TERRACE)
            if (checkBalcony.isChecked) list.add(Criteria.BALCONY)
        }
        return list
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_FROM_GALLERY || requestCode == GET_FROM_CAMERA && resultCode == RESULT_OK && null != data) {
            val imageUri = data!!.data
            var imgProfileBitmap: Bitmap
            try {
                if (requestCode == GET_FROM_GALLERY) {
                    picEstate = imageUri.toString()
                } else {
                    picEstate = photoUri.toString()
                }
                viewModel.putPicOnFirebase(picEstate).observe(this, { picture ->
                    listUriPic.add(picture)
                    listPicAdapter.addToList(picture)
                })
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun openCamera() {
        Toast.makeText(this, "Open Camera", Toast.LENGTH_SHORT).show()
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(this, "com.exemple.realEstate.photo", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, GET_FROM_CAMERA)
                }
            }
        }
    }

    override fun openGallery() {
        Toast.makeText(this, "Open Gallery", Toast.LENGTH_SHORT).show()
        startActivityForResult(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            ), GET_FROM_GALLERY
        )
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val imageFileName = "PIC_" + name + "_" + timestamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply { currentPhotoPath = absolutePath }
    }

    override fun deletePic(picUri: String) {
        val contextView = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)

        Snackbar.make(contextView, "Voulez-vous supprimer la photo ? ", Snackbar.LENGTH_LONG)
            .setAction("valider") {
                listUriPic.remove(picUri)
                listPicAdapter.addList(listUriPic)
            }
            .show()
    }


}