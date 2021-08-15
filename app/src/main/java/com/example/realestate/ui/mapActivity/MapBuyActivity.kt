package com.example.realestate.ui.mapActivity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.realestate.R
import com.example.realestate.adapter.CriteriaAdapter
import com.example.realestate.adapter.PoiAdapter
import com.example.realestate.databinding.ActivityMapBuyBinding
import com.example.realestate.models.OldOrNew
import com.example.realestate.models.RealEstate
import com.example.realestate.ui.buyOrLocActivities.ModificationEstate
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.DefaultSliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.pow

@AndroidEntryPoint
class MapBuyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBuyBinding
    private lateinit var adapterCriteria: CriteriaAdapter
    private lateinit var adapterPoi: PoiAdapter
    private lateinit var viewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        adapterCriteria = CriteriaAdapter()
        adapterPoi = PoiAdapter()

        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        viewModel.getEstateRoom(intent.getLongExtra("idMarker", 0)).observe(this, { estate ->
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
                estate.getCurrency(this@MapBuyActivity)
            }"
            sizeEstate.text = "${estate.size} m²"
            roomEstate.text = "${estate.nbRoom} pièces"
            descriptionEstate.text = estate.description
            dateEstate.text = estate.getDate()

            listCriteria.layoutManager =
                LinearLayoutManager(this@MapBuyActivity, LinearLayoutManager.VERTICAL, false)
            listCriteria.adapter = adapterCriteria
            listPoi.layoutManager =
                LinearLayoutManager(this@MapBuyActivity, LinearLayoutManager.VERTICAL, false)
            listPoi.adapter = adapterPoi
            if (estate.listPoi != null) adapterPoi.addList(listPoiToString(estate))
            if (estate.listCriteria != null) adapterCriteria.addList(listCriteriaToString(estate))


            estate.address?.let { viewModel.setAddress(it) }
            viewModel.staticImage.observe(this@MapBuyActivity, { address ->
                Glide.with(this@MapBuyActivity)
                    .load(address)
                    .into(staticImage)

            })

            fabModify.setOnClickListener {
                Log.d("testChangePage", "blopi")
                var intent = Intent(this@MapBuyActivity, ModificationEstate::class.java)
                intent.putExtra("idModif", estate.dateEntry)
                startActivity(intent)
            }

            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.openModify -> {
                        Log.d("testChangePage", "blopi")
                        var intent = Intent(this@MapBuyActivity, ModificationEstate::class.java)
                        intent.putExtra("idModif", estate.dateEntry)
                        startActivity(intent)

                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }

        }

        estimateLoan(estate)
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

    private fun estimateLoan(estate: RealEstate) {
        var notaryCharges = when (estate.oldOrNew) {
            OldOrNew.OLD -> notaryCharges(estate.euroOrDollarLong(this@MapBuyActivity), 8)
            OldOrNew.NEW -> notaryCharges(estate.euroOrDollarLong(this@MapBuyActivity), 3)
        }

        var deposit = (estate.euroOrDollarLong(this@MapBuyActivity) * 20) / 100
        var loan = (estate.euroOrDollarLong(this@MapBuyActivity) + notaryCharges) - deposit


        binding.textExample.text =
            "Exemple de prêt possible pour une durée de 25 ans avec un taux d'intérêt de 1,65% et un apport égal à 20% du prix (soit $deposit ${
                estate.getSymbolCurrency(this@MapBuyActivity)
            })"
        binding.pieChart.invalidate()

        totalPrice(loan, 25, 0.0165)
    }

    private fun totalPrice(loan: Long, year: Int, percent: Double) {
        var monthly =
            ((loan * (percent / 12)) / (1 - ((1 + (percent / 12)).pow(-12 * year)))).toLong()
        var benefit = (12 * year * monthly - loan)
        var loanAndBenefit = monthly + benefit

        createPieChart(monthly)
        setData(loan, benefit)
    }

    private fun createPieChart(monthly: Long) {
        with(binding) {
            pieChart.visibility = View.VISIBLE
            pieChart.isDrawHoleEnabled = true
            pieChart.setHoleColor(Color.TRANSPARENT)
            pieChart.holeRadius = 58f
            pieChart.setDrawCenterText(true)
            pieChart.setEntryLabelTextSize(10f)
            pieChart.rotationAngle = 0f
            pieChart.isRotationEnabled = true
            pieChart.isHighlightPerTapEnabled = true
            pieChart.setCenterTextSize(20f)
            pieChart.centerText = "$monthly €/mois"
            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = false
            pieChart.animateY(1400, Easing.EaseInOutQuad)
        }

    }

    private fun setData(loan: Long, benefit: Long) {
        val entries = ArrayList<PieEntry>()

        entries.add(
            PieEntry(
                loan.toFloat(),
                "Emprunt"
            )
        )

        entries.add(
            PieEntry(
                benefit.toFloat(),
                "Intérêt"
            )
        )


        val dataSet = PieDataSet(entries, "Résultat simulation")

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = arrayListOf()
        colors.add(Color.rgb(66, 64, 80))
        colors.add(Color.rgb(90, 75, 79))

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        binding.pieChart.data = data

        binding.pieChart.highlightValues(null)

        binding.pieChart.invalidate()

    }

    private fun notaryCharges(price: Long, percent: Int): Long {
        return (price * percent) / 100
    }

    override fun onStop() {
        binding.sliderImg.stopAutoCycle()
        super.onStop()
    }
}