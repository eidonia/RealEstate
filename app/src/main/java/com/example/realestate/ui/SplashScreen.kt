package com.example.realestate.ui

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.example.realestate.R
import com.example.realestate.ui.mainActivity.MainActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.firebase.auth.FirebaseAuth
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class SplashScreen : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private val RC_CAMERA_AND_LOCATION = 123
    private val RC_SIGN_IN = 123
    private lateinit var prefs: SharedPreferences
    private val perms = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, RC_CAMERA_AND_LOCATION, *perms)
                    .setRationale(R.string.camera_and_location_rationale)
                    .setPositiveButtonText(R.string.rationale_ask_ok)
                    .setNegativeButtonText(R.string.rationale_ask_cancel)
                    .build()
            )
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            connectFirebase()
        }

    }

    @Suppress("DEPRECATION")
    private fun connectFirebase() {
        val providers = listOf(
            GoogleBuilder().build(),
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo_real)
                .build(),
            RC_SIGN_IN
        )
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //TODO
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }
}