package com.example.realestate.ui.mainActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.realestate.databinding.ActivityMainBinding
import com.example.realestate.ui.addEstate.addBuyActivity.AddBuyActivity
import com.example.realestate.ui.addEstate.addLocActivity.AddLocActivity
import com.example.realestate.ui.buyOrLocActivities.buyActivity.BuyActivity
import com.example.realestate.ui.buyOrLocActivities.locActivity.LocActivity
import com.example.realestate.ui.loanSimActivity.LoanSimActivity
import com.example.realestate.ui.mapActivity.MapActivity
import com.example.realestate.ui.settings.SettingsActivity
import com.example.realestate.utils.ViewAnimation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isRotate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser

        Toast.makeText(this, "Hi, ${user!!.displayName} !", Toast.LENGTH_LONG).show()
        with(binding) {

            ViewAnimation.init(fabAddBuy)
            ViewAnimation.init(fabAddLoc)
            btnBuy.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        BuyActivity::class.java
                    )
                )
            }
            btnLoc.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        LocActivity::class.java
                    )
                )
            }
            btnSim.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        LoanSimActivity::class.java
                    )
                )
            }
            btnSettings.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        SettingsActivity::class.java
                    )
                )
            }
            btnMap.setOnClickListener {
                Log.d("ClickMapButton", "1st Click")
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this@MainActivity, MapActivity::class.java))
                } else {
                    Toast.makeText(this@MainActivity, "Location not allowed", Toast.LENGTH_LONG)
                        .show()
                }
            }
            fabAdd.setOnClickListener {
                isRotate = ViewAnimation.rotateFab(it, !isRotate)
                if (isRotate) {
                    ViewAnimation.showIn(fabAddLoc)
                    ViewAnimation.showIn(fabAddBuy)
                } else {
                    ViewAnimation.showOut(fabAddLoc)
                    ViewAnimation.showOut(fabAddBuy)
                }
            }
            fabAddBuy.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        AddBuyActivity::class.java
                    )
                )
            }
            fabAddLoc.setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        AddLocActivity::class.java
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRotate)
            isRotate = ViewAnimation.rotateFab(binding.fabAdd, !isRotate)
        ViewAnimation.init(binding.fabAddBuy)
        ViewAnimation.init(binding.fabAddLoc)
    }
}