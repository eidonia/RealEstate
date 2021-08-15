package com.example.realestate.ui.buyOrLocActivities.locActivity

import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.example.realestate.R
import com.example.realestate.baseFragment.BaseFragmentBuyOrLoc
import com.example.realestate.databinding.FragmentLocEstateBinding
import com.example.realestate.models.RealEstate
import com.example.realestate.ui.buyOrLocActivities.BuyAndLocViewModel
import com.example.realestate.ui.buyOrLocActivities.ModificationEstate
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.DefaultSliderView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocEstateFrag : BaseFragmentBuyOrLoc<FragmentLocEstateBinding>() {

    override fun createUI(estate: RealEstate) {
        super.createUI(estate)
        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)
        if (!isTablet) {
            binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
            binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        }

        val requestOption = RequestOptions()
        requestOption.centerCrop()

        for (string in estate.listPic) {
            val sliderVew = DefaultSliderView(context)

            sliderVew
                .image(string)
                .setRequestOption(RequestOptions.centerCropTransform())
                .setProgressBarVisible(true)

            binding.sliderImg.addSlider(sliderVew)
        }

        with(binding.sliderImg) {
            setPresetTransformer(SliderLayout.Transformer.Default)
            setPresetIndicator(SliderLayout.PresetIndicators.Left_Bottom)
        }

        with(binding) {
            toolbar.title = "${estate.formatType()}, ${estate.size} m², ${
                estate.getCurrency(requireContext())
            }"
            sizeEstate.text = "${estate.size} m²"
            roomEstate.text = "${estate.nbRoom} pièces"
            descriptionEstate.text = estate.description
            dateEstate.text = estate.getDate()

            listCriteria.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            listCriteria.adapter = adapterCriteria
            listPoi.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            listPoi.adapter = adapterPoi
            if (estate.listPoi != null) adapterPoi.addList(listPoiToString(estate))
            if (estate.listCriteria != null) adapterCriteria.addList(listCriteriaToString(estate))

            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.openModify -> {
                        if (sameUser(estate.employee)) {
                            var intent = Intent(requireContext(), ModificationEstate::class.java)
                            intent.putExtra("idModif", estate.dateEntry)
                            startActivity(intent)
                        }

                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }

        }
    }

    private fun sameUser(employee: String?): Boolean {
        val user = FirebaseAuth.getInstance().currentUser?.displayName
        return employee == user
    }

    override fun onStop() {
        binding.sliderImg.stopAutoCycle()
        super.onStop()
    }

    override fun setupVM() {
        viewModel = ViewModelProvider(this).get(BuyAndLocViewModel::class.java)
    }

    override fun getViewBinding() = FragmentLocEstateBinding.inflate(layoutInflater)

    override fun getActivNav(): Long {
        Log.d("BlopId", "IDREceived")
        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)

        return when {
            isTablet -> {
                arguments?.getLong("idLoc") ?: 0
            }
            else -> {
                val args: LocEstateFragArgs by navArgs()
                args.idLocation
            }
        }
    }

    override fun getImage(): ImageView = binding.staticImage
    override fun getSellOrRent(): TextView = binding.dateSellOrRentEstate
    override fun getTextSellOrRent(): TextView = binding.dateSellOrRentText
    override fun setTextSellOrRent(): CharSequence? = "Loué le"
}