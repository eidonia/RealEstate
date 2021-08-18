package com.example.realestate.ui.buyOrLocActivities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.adapter.ListPicAdapter
import com.example.realestate.databinding.ActivityModificationEstateBinding
import com.example.realestate.models.*
import com.example.realestate.utils.BottomSheetDialog
import com.example.realestate.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ModificationEstate : AppCompatActivity(), ListPicAdapter.UpdatePic,
    BottomSheetDialog.OpenActivity {

    private lateinit var binding: ActivityModificationEstateBinding
    private lateinit var viewModel: BuyAndLocViewModel
    private lateinit var adapterPic: ListPicAdapter
    private lateinit var btsPic: BottomSheetDialog
    private val GET_FROM_GALLERY = 3
    private val GET_FROM_CAMERA = 6
    private lateinit var name: String
    private lateinit var estate: RealEstate
    private lateinit var currentPhotoPath: String
    private lateinit var photoUri: Uri
    private lateinit var picEstate: String
    private var check = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModificationEstateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(BuyAndLocViewModel::class.java)
        adapterPic = ListPicAdapter(this, this)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        btsPic = BottomSheetDialog(this, this)

        binding.listPic.apply {
            layoutManager =
                LinearLayoutManager(this@ModificationEstate, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterPic
        }

        viewModel.getEstateRoom(intent.getLongExtra("idModif", 0)).observe(this, { estate ->
            this.estate = estate
            createUI(estate)
        })
    }

    private fun createUI(estate: RealEstate?) {
        when (estate?.isAvailable) {
            true -> binding.radioAvailable.isChecked = true
            false -> {
                binding.radioNotAvailable.isChecked = true
                binding.radioAvailable.isClickable = false
                binding.radioAvailable.isFocusable = false
            }
        }

        when (estate?.buyOrLoc) {
            BuyOrLoc.LOCATION -> {
                binding.radioLoc.isChecked = true
                name = "LOC"
            }
            BuyOrLoc.BUY -> {
                binding.radioBuy.isChecked = true
                name = "BUY"
            }
        }

        when (estate?.type) {
            EstateType.HOUSE -> binding.radioHouse.isChecked = true
            EstateType.APARTMENT -> binding.radioApartment.isChecked = true
            EstateType.MANOR -> binding.radioManor.isChecked = true
            EstateType.DUPLEX -> binding.radioDuplex.isChecked = true
        }

        when (estate?.oldOrNew) {
            OldOrNew.OLD -> binding.radioOld.isChecked = true
            OldOrNew.NEW -> binding.radioNew.isChecked = true
        }

        binding.editNumStreet.setText(estate?.numStreet)
        binding.editVicinity.setText(estate?.vicinity)
        binding.editZipCode.setText(estate?.zipCode)
        binding.editCity.setText(estate?.city)
        binding.editCountry.setText(estate?.country)
        binding.editnumRoom.setText(estate?.nbRoom)
        binding.editSizeEstate.setText(estate?.size)
        binding.editDescription.setText(estate?.description)
        binding.editPriceEstate.setText(estate?.euroOrDollarLong(this).toString())
        estate?.listPic?.let { adapterPic.addList(it) }


        binding.btnAddPic.setOnClickListener {
            btsPic.openBTS()
        }

        binding.btnAdd.setOnClickListener { modifyEstate() }

        checkPoi(estate)
        checkCriteria(estate)

    }

    private fun checkPoi(estate: RealEstate?) {
        if (estate?.listPoi != null) {
            estate.listPoi?.let {
                for (poi in it) {
                if (poi == PointOfInterest.MEDICAL) {
                    binding.checkMedical.isChecked = true
                }

                if (poi == PointOfInterest.PARK) {
                    binding.checkPark.isChecked = true
                }

                if (poi == PointOfInterest.TRANSPORTS) {
                    binding.checkTransports.isChecked = true
                }

                if (poi == PointOfInterest.SCHOOLS) {
                    binding.checkSchools.isChecked = true
                }

                if (poi == PointOfInterest.BUSINESS) {
                    binding.checkBusiness.isChecked = true
                }
            } }
        }
    }

    private fun checkCriteria(estate: RealEstate?) {
        if (estate?.listCriteria != null) {
            estate.listCriteria?.let {
                for (criteria in it) {
                    if (criteria == Criteria.CAVE) {
                        binding.checkCave.isChecked = true
                    }

                    if (criteria == Criteria.PARKING) {
                        binding.checkParking.isChecked = true
                    }

                    if (criteria == Criteria.KITCHEN_AREA) {
                        binding.checkKitchArea.isChecked = true
                    }

                    if (criteria == Criteria.OPENPLANE_KITCHEN) {
                        binding.checkOpenKitch.isChecked = true
                    }

                    if (criteria == Criteria.TERRACE) {
                        binding.checkTerrace.isChecked = true
                    }

                    if (criteria == Criteria.BALCONY) {
                        binding.checkBalcony.isChecked = true
                    }
                }
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data ?: photoUri
            try {
                picEstate = imageUri.toString()
                viewModel.putPicOnFirebase(picEstate).observe(this, { picture ->
                    estate.listPic.add(picture)
                    adapterPic.addToList(picture)
                })
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun openCamera() {
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
                    resultLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun modifyEstate() {
        with(binding) {

            val typeEstate = when (radioGroup.checkedRadioButtonId) {
                R.id.radioHouse -> EstateType.HOUSE
                R.id.radioApartment -> EstateType.APARTMENT
                R.id.radioDuplex -> EstateType.DUPLEX
                else -> EstateType.MANOR
            }

            val buyOrLoc = when (radioGroupBuyOrLoc.checkedRadioButtonId) {
                R.id.radioBuy -> BuyOrLoc.BUY
                else -> BuyOrLoc.LOCATION
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
                editnumRoom.error = getString(R.string.empty_field)
                ""
            }

            val sizeEstate = if (editSizeEstate.text.toString() != "") {
                editSizeEstate.text.toString()
            } else {
                check++
                editSizeEstate.error = getString(R.string.empty_field)
                ""
            }

            val description = if (editDescription.text.toString() != "") {
                editDescription.text.toString()
            } else {
                check++
                editDescription.error = getString(R.string.empty_field)
                ""
            }

            var priceEuro = ""
            var priceDollar = ""

            if (editPriceEstate.text.toString() != "") {
                editPriceEstate.text.toString()

                when (PreferenceManager.getDefaultSharedPreferences(this@ModificationEstate)
                    .getBoolean("getCurrency", false)) {
                    true -> {
                        priceDollar = editPriceEstate.text.toString()
                        priceEuro =
                            Utils.convertDollarToEuro(editPriceEstate.text.toString().toInt())
                                .toString()
                    }
                    false -> {
                        priceEuro = editPriceEstate.text.toString()
                        priceDollar =
                            Utils.convertEuroToDollar(editPriceEstate.text.toString().toInt())
                                .toString()
                    }
                }


            } else {
                check++
                editPriceEstate.error = getString(R.string.empty_field)
                ""
            }

            if (estate.listPic.isEmpty()) {
                textAddPic.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                textAddPic.text = getString(R.string.add_pic)
                check++
            } else {
                textAddPic.text = ""
            }

            val listCriteria: ArrayList<Criteria> = createCriteriaList()
            val listAround: ArrayList<PointOfInterest> = createPoiList()
            val employee = FirebaseAuth.getInstance().currentUser?.displayName

            if (check == 0) {
                viewModel.setAddress(address)
                viewModel.geocode.observe(this@ModificationEstate, { it ->

                    val estate = RealEstate(
                        buyOrLoc = buyOrLoc,
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
                        priceDollar = priceDollar,
                        priceEuro = priceEuro,
                        listCriteria = listCriteria,
                        listPoi = listAround,
                        listPic = this@ModificationEstate.estate.listPic,
                        isAvailable = true,
                        dateEntry = this@ModificationEstate.estate.dateEntry,
                        employee = employee,
                        oldOrNew = ageEstate,
                        lat = it.latitude,
                        lng = it.longitude
                    )

                    if (this@ModificationEstate.estate.isAvailable && radioNotAvailable.isChecked) {
                        estate.isAvailable = false
                        estate.dateSellOrRent = System.currentTimeMillis()
                    } else if(!this@ModificationEstate.estate.isAvailable && radioNotAvailable.isChecked) {
                        estate.isAvailable = false
                    }

                    viewModel.staticImage.observe(
                        this@ModificationEstate,
                        { staticImageUrl ->
                            estate.staticImage = staticImageUrl
                        })

                    viewModel.updateEstate(estate).observe(this@ModificationEstate, {
                        finish()
                    })

                    check = 0

                })
            } else {
                textValidate.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                textValidate.text = getString(R.string.error_field)
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
                editNumStreet.error = getString(R.string.empty_field)
                ""
            }

            val vicinity = if (editVicinity.text.toString() != "") {
                editVicinity.text.toString()
            } else {
                check++
                editVicinity.error = getString(R.string.empty_field)
                ""
            }

            val zipCode = if (editZipCode.text.toString() != "") {
                editZipCode.text.toString()
            } else {
                check++
                editZipCode.error = getString(R.string.empty_field)
                ""
            }

            val city = if (editCity.text.toString() != "") {
                editCity.text.toString()
            } else {
                check++
                editCity.error = getString(R.string.empty_field)
                ""
            }

            val country = if (editCountry.text.toString() != "") {
                editCountry.text.toString()
            } else {
                check++
                editCountry.error = getString(R.string.empty_field)
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

    override fun openGallery() {
        resultLauncher.launch(
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            ))
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

        Snackbar.make(contextView, getString(R.string.del_pic), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.validate)) {
                estate.listPic.remove(picUri)
                adapterPic.addList(estate.listPic)
            }
            .show()
    }
}