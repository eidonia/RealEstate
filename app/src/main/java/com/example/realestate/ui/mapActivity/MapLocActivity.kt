package com.example.realestate.ui.mapActivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.realestate.R
import com.example.realestate.adapter.CriteriaAdapter
import com.example.realestate.adapter.PoiAdapter
import com.example.realestate.databinding.ActivityMapLocBinding
import com.example.realestate.models.RealEstate
import com.example.realestate.ui.buyOrLocActivities.BuyAndLocViewModel
import com.example.realestate.ui.buyOrLocActivities.ModificationEstate
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.DefaultSliderView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapLocActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapLocBinding
    private lateinit var adapterCriteria: CriteriaAdapter
    private lateinit var adapterPoi: PoiAdapter
    private lateinit var viewModel: BuyAndLocViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapLocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        adapterCriteria = CriteriaAdapter()
        adapterPoi = PoiAdapter()

        viewModel = ViewModelProvider(this).get(BuyAndLocViewModel::class.java)

        viewModel.getEstateRoom(intent.getLongExtra("idMarker", 0))
            .observe(this@MapLocActivity, { estate ->
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
            toolbar.title = "${estate.formatType()}, ${estate.size} m², ${
                estate.getCurrency(this@MapLocActivity)
            }"
            sizeEstate.text = "${estate.size} m²"
            roomEstate.text = "${estate.nbRoom} pièces"
            descriptionEstate.text = estate.description
            dateEstate.text = estate.getDate()

            listCriteria.layoutManager =
                LinearLayoutManager(this@MapLocActivity, LinearLayoutManager.VERTICAL, false)
            listCriteria.adapter = adapterCriteria
            listPoi.layoutManager =
                LinearLayoutManager(this@MapLocActivity, LinearLayoutManager.VERTICAL, false)
            listPoi.adapter = adapterPoi
            if (estate.listPoi != null) adapterPoi.addList(listPoiToString(estate))
            if (estate.listCriteria != null) adapterCriteria.addList(listCriteriaToString(estate))


            estate.address?.let { viewModel.setAddress(it) }
            viewModel.staticImage.observe(this@MapLocActivity, { address ->
                Glide.with(this@MapLocActivity)
                    .load(address)
                    .into(staticImage)

            })

            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.openModify -> {
                        var intent = Intent(this@MapLocActivity, ModificationEstate::class.java)
                        intent.putExtra("idModif", estate.dateEntry)
                        startActivity(intent)

                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }

        }
    }

    private fun listCriteriaToString(estate: RealEstate): MutableList<String> {
        val list = mutableListOf<String>()
        estate.listCriteria?.let {
            for (criteria in it) {
                list.add(estate.formatCriteria(criteria))
            }
        }

        return list
    }

    private fun listPoiToString(estate: RealEstate): MutableList<String> {
        val list = mutableListOf<String>()
        estate.listPoi?.let {
            for (poi in it) {
                list.add(estate.formatPointOfInterest(poi))
            }
        }
        return list
    }

    override fun onStop() {
        binding.sliderImg.stopAutoCycle()
        super.onStop()
    }
}