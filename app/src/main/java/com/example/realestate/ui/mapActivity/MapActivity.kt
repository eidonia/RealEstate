package com.example.realestate.ui.mapActivity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.example.realestate.R
import com.example.realestate.databinding.ActivityMapBinding
import com.example.realestate.models.BuyOrLoc
import com.example.realestate.models.RealEstate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapActivity : AppCompatActivity(), LocationListener, OnMapReadyCallback {

    private lateinit var location: Location
    private lateinit var gMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var viewModel: MapViewModel
    private var listEstate = mutableListOf<RealEstate>()
    private var hashMap = hashMapOf<Marker, RealEstate>()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        viewModel.getListEstate().observe(this, { estateList ->
            listEstate.addAll(estateList)
        })

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 150f, this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }


    override fun onLocationChanged(location: Location) {
        this.location = location
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        this.gMap = map
        map.isMyLocationEnabled = true
        map.clear()

        val update =
            CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 17.0f)
        map.animateCamera(update)

        setMarkers(listEstate)
    }

    private fun setMarkers(list: MutableList<RealEstate>) {
        for (estate in list) {
            val marker = when (estate.buyOrLoc) {
                BuyOrLoc.LOCATION -> ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_round_place_rent_24
                )?.toBitmap()
                BuyOrLoc.BUY -> ContextCompat.getDrawable(this, R.drawable.ic_round_place_buy_24)
                    ?.toBitmap()
            }
            hashMap.put(
                gMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(estate.lat, estate.lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(marker!!, 150, 150)))
                        .zIndex(3.0f)
                )!!,
                estate
            )
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap? {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

}