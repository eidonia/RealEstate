package com.example.realestate.ui.buyOrLocActivities.buyActivity

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.example.realestate.R
import com.example.realestate.baseFragment.BaseFragmentBuyOrLoc
import com.example.realestate.databinding.FragmentBuyEstateBinding
import com.example.realestate.models.OldOrNew
import com.example.realestate.models.RealEstate
import com.example.realestate.ui.buyOrLocActivities.BuyAndLocViewModel
import com.example.realestate.ui.buyOrLocActivities.ModificationEstate
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.DefaultSliderView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.pow

@AndroidEntryPoint
class BuyEstateFrag : BaseFragmentBuyOrLoc<FragmentBuyEstateBinding>() {

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
                            val intent = Intent(requireContext(), ModificationEstate::class.java)
                            intent.putExtra("idModif", estate.dateEntry)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Vous ne pouvez pas modifier ce bien",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }

        }

        estimateLoan(estate)
    }

    private fun sameUser(employee: String?): Boolean {
        val user = FirebaseAuth.getInstance().currentUser?.displayName
        return employee == user
    }


    private fun estimateLoan(estate: RealEstate) {
        var notaryCharges = when (estate.oldOrNew) {
            OldOrNew.OLD -> notaryCharges(estate.euroOrDollarLong(requireContext()), 8)
            OldOrNew.NEW -> notaryCharges(estate.euroOrDollarLong(requireContext()), 3)
        }

        var deposit = (estate.euroOrDollarLong(requireContext()) * 20) / 100
        var loan = (estate.euroOrDollarLong(requireContext()) + notaryCharges) - deposit


        binding.textExample.text =
            "Exemple de prêt possible pour une durée de 25 ans avec un taux d'intérêt de 1,65% et un apport égal à 20% du prix (soit $deposit ${
                estate.getSymbolCurrency(requireContext())
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

    override fun getViewBinding() = FragmentBuyEstateBinding.inflate(layoutInflater)

    override fun getActivNav(): Long {
        Log.d("BlopId", "IDREceived")
        val isTablet = requireContext().resources.getBoolean(R.bool.isTablet)

        return when {
            isTablet -> {
                arguments?.getLong("idBuy") ?: 0
            }
            else -> {
                val args: BuyEstateFragArgs by navArgs()
                args.idBuy
            }
        }
    }

    override fun getImage(): ImageView = binding.staticImage

    override fun onStop() {
        binding.sliderImg.stopAutoCycle()
        super.onStop()
    }

    override fun setupVM() {
        viewModel = ViewModelProvider(this).get(BuyAndLocViewModel::class.java)
    }

    override fun getSellOrRent(): TextView = binding.dateSellOrRentEstate
    override fun getTextSellOrRent(): TextView = binding.dateSellOrRentText
    override fun setTextSellOrRent(): CharSequence? = "Vendu le"
}