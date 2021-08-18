package com.example.realestate.baseActivity

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
abstract class BaseActivityBuyLoc : AppCompatActivity(), BottomSheetDialog.OpenActivity,
    ListPicAdapter.UpdatePic {

    lateinit var binding: ActivityAddEstateBinding
    private lateinit var listPicAdapter: ListPicAdapter
    private val GET_FROM_GALLERY = 3
    private val GET_FROM_CAMERA = 6
    private lateinit var picEstate: String
    private lateinit var btsPic: BottomSheetDialog
    private lateinit var name: String
    private lateinit var currentPhotoPath: String
    private lateinit var photoUri: Uri
    private var listUriPic = mutableListOf<String>()
    private var check = 0
    private lateinit var viewModel: AddViewModel
    private val CHANNEL_ID = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEstateBinding.inflate(layoutInflater)
        name = getName()
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        binding.toolbar.title = getTitleName()
        binding.priceEstate.hint = getPriceText()

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

    abstract fun getPriceText(): CharSequence?

    abstract fun getTitleName(): CharSequence?

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

                when (PreferenceManager.getDefaultSharedPreferences(this@BaseActivityBuyLoc)
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
            }


            if (listUriPic.isEmpty()) {
                textAddPic.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                textAddPic.text = getString(R.string.add_pic)
                check++
            } else {
                textAddPic.text = ""
            }

            val listCriteria: ArrayList<Criteria> = createCriteriaList()
            val listAround: ArrayList<PointOfInterest> = createPoiList()
            val user = FirebaseAuth.getInstance().currentUser
            val employee = user?.displayName ?: "user"

            if (check == 0) {
                viewModel.setAddress(address)
                viewModel.geocode.observe(this@BaseActivityBuyLoc, {

                    val estate = RealEstate(
                        buyOrLoc = when (name) {
                            "BUY" -> BuyOrLoc.BUY
                            else -> BuyOrLoc.LOCATION
                        },
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
                        listPic = listUriPic,
                        isAvailable = true,
                        dateEntry = System.currentTimeMillis(),
                        employee = employee,
                        oldOrNew = ageEstate,
                        lat = it.latitude,
                        lng = it.longitude
                    )

                    viewModel.staticImage.observe(
                        this@BaseActivityBuyLoc,
                        { staticImageUrl ->
                            estate.staticImage = staticImageUrl
                        })

                    viewModel.insertEstate(estate).observe(this@BaseActivityBuyLoc, { dateEntry ->
                        createNotification()
                        var intent = Intent(
                            this@BaseActivityBuyLoc,
                            EstateAfterAdd::class.java
                        ).apply { putExtra("idAdd", dateEntry) }
                        finish()
                        startActivity(intent)
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

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data ?: photoUri
            try {
                picEstate = imageUri.toString()
                viewModel.uploadPicture(picEstate).observe(this, { picture ->
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

    abstract fun getName(): String

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

    override fun openGallery() {
        resultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI))
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
                listUriPic.remove(picUri)
                listPicAdapter.addList(listUriPic)
            }
            .show()
    }

    private fun createNotification() {
        val message = getString(R.string.add_estate_notif)
        val name = getString(R.string.app_name)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            val nm =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val builder =
                Notification.Builder(applicationContext)
            builder.setContentTitle(name)
                .setContentText(message)
                .setStyle(
                    Notification.InboxStyle()
                        .addLine(message)
                        .setBigContentTitle(name)
                )
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
            val n = builder.build()
            nm.notify(1234, n)

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            val notificationChannel: NotificationChannel = NotificationChannel(
                CHANNEL_ID,
                "RealEstate",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = message
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
            val builder = Notification.Builder(
                applicationContext,
                CHANNEL_ID
            )
                .setContentTitle(name)
                .setContentText(message)
                .setStyle(
                    Notification.InboxStyle()
                        .addLine(message)
                        .setBigContentTitle(name)
                )
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
            notificationManager.notify(1234, builder.build())
        }
    }
}