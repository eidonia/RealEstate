package com.example.realestate.utils

import android.content.Context
import android.view.View
import com.example.realestate.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetDialog(val context: Context, private val openActivity: OpenActivity) {

    fun openBTS() {
        val bottomSheetDialog = BottomSheetDialog(context)

        bottomSheetDialog.setContentView(R.layout.bts_pic_gallery)

        val btnCam = bottomSheetDialog.findViewById<View>(R.id.btnCamera)
        val btnGallery = bottomSheetDialog.findViewById<View>(R.id.btnGallery)

        btnCam?.setOnClickListener {
            bottomSheetDialog.dismiss()
            openActivity.openCamera()
        }
        btnGallery?.setOnClickListener {
            bottomSheetDialog.dismiss()
            openActivity.openGallery()
        }

        bottomSheetDialog.show()
    }

    interface OpenActivity {
        fun openCamera()
        fun openGallery()
    }
}