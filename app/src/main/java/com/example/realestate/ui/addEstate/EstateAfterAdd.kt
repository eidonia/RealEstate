package com.example.realestate.ui.addEstate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.realestate.adapter.CriteriaAdapter
import com.example.realestate.adapter.PoiAdapter
import com.example.realestate.databinding.ActivityEstateAfterAddBinding
import com.example.realestate.models.RealEstate
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.DefaultSliderView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EstateAfterAdd : AppCompatActivity() {

    private lateinit var binding: ActivityEstateAfterAddBinding
    private lateinit var viewModel: AddViewModel
    private lateinit var adapterCriteria: CriteriaAdapter
    private lateinit var adapterPoi: PoiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEstateAfterAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterCriteria = CriteriaAdapter()
        adapterPoi = PoiAdapter()

        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        viewModel.getEstateRoom(intent.getLongExtra("idAdd", 0)).observe(this, { estate ->
            createUI(estate)
        })
    }

    private fun createUI(estate: RealEstate) {
        val requestOption = RequestOptions()
        requestOption.centerCrop()

        for (string in estate.listPic) {
            val sliderVew = DefaultSliderView(this)

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
            toolbar.title = "${estate.formatType(this@EstateAfterAdd)}, ${estate.size} m², ${
                estate.getCurrency(this@EstateAfterAdd)
            }"
            sizeEstate.text = "${estate.size} m²"
            roomEstate.text = "${estate.nbRoom} pièces"
            descriptionEstate.text = estate.description
            dateEstate.text = estate.getDate()

            listCriteria.layoutManager =
                LinearLayoutManager(this@EstateAfterAdd, LinearLayoutManager.VERTICAL, false)
            listCriteria.adapter = adapterCriteria
            listPoi.layoutManager =
                LinearLayoutManager(this@EstateAfterAdd, LinearLayoutManager.VERTICAL, false)
            listPoi.adapter = adapterPoi
            if (estate.listPoi != null) adapterPoi.addList(listPoiToString(estate))
            if (estate.listCriteria != null)adapterCriteria.addList(listCriteriaToString(estate))


            viewModel.setAddress(estate.address!!)
            viewModel.staticImage.observe(this@EstateAfterAdd, { address ->
                Glide.with(this@EstateAfterAdd)
                    .load(address)
                    .into(staticImage)

            })
        }

    }

    private fun listCriteriaToString(estate: RealEstate): MutableList<String> {
        val list = mutableListOf<String>()
        for (criteria in estate.listCriteria!!) {
            list.add(estate.formatCriteria(criteria))
        }
        return list
    }

    private fun listPoiToString(estate: RealEstate): MutableList<String> {
        val list = mutableListOf<String>()
        for (poi in estate.listPoi!!) {
            list.add(estate.formatPointOfInterest(poi))
        }
        return list
    }

    override fun onStop() {
        binding.sliderImg.stopAutoCycle()
        super.onStop()
    }
}