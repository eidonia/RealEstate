package com.example.realestate.ui.settings

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.realestate.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("getCurrency", false)) {
            Log.d("testCurrency", "Blop")
            binding.switchDevise.isChecked = true
        }

        binding.switchDevise.setOnCheckedChangeListener { buttonView, isChecked ->
            val pref = PreferenceManager.getDefaultSharedPreferences(this)

            if (isChecked) {
                pref.edit()
                    .putBoolean("getCurrency", true)
                    .apply()
            } else {
                pref.edit()
                    .putBoolean("getCurrency", false)
                    .apply()
            }
        }
    }
}