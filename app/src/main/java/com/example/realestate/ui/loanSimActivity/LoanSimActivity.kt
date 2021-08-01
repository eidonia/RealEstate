package com.example.realestate.ui.loanSimActivity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.realestate.R
import com.example.realestate.databinding.ActivityLoanSimBinding
import com.github.mikephil.charting.animation.Easing.EaseInOutQuad
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlin.math.pow

class LoanSimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoanSimBinding
    private var notaryCharges: Long = 0
    private var monthly: Long = 0
    private var benefit: Long = 0
    private var loanAndBenefit: Long = 0
    private var loan: Long = 0
    private var pieData: Array<String> = arrayOf("Emprunt", "Intérêt")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanSimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAutoComplete()

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.priceText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    val radioButtonChecked = binding.radioGroup.checkedRadioButtonId
                    val number: Long = s.toString().toLong()
                    if (radioButtonChecked == R.id.oldCheck) {
                        calculatesNotaryCharges(number, 8)

                    } else if (radioButtonChecked == R.id.newCheck) {
                        calculatesNotaryCharges(number, 3)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.oldCheck && !binding.priceText.text.isNullOrEmpty()) {
                calculatesNotaryCharges(binding.priceText.text.toString().toLong(), 8)
            } else if (checkedId == R.id.newCheck && !binding.priceText.text.isNullOrEmpty()) {
                calculatesNotaryCharges(binding.priceText.text.toString().toLong(), 3)
            }
        }

        binding.calculateLoan.setOnClickListener {

            var number =
                binding.priceText.text.toString().toLong() + binding.notaryText.text.toString()
                    .toLong()
            var deposit =
                if (binding.depositText.text.isNullOrEmpty()) 0 else binding.depositText.text.toString()
                    .toLong()
            loan = number - deposit
            binding.pieChart.invalidate()

            when (binding.lengthchoice.text.toString()) {
                "7 ans - Taux d'intérêt 0,96%" -> totalPrice(7, 0.0096, loan)
                "10 ans - Taux d'intérêt 1,01%" -> totalPrice(10, 0.0101, loan)
                "15 ans - Taux d'intérêt 1,17%" -> totalPrice(15, 0.0117, loan)
                "20 ans - Taux d'intérêt 1,32%" -> totalPrice(20, 0.0132, loan)
                "25 ans - Taux d'intérêt 1,65%" -> totalPrice(25, 0.0165, loan)
            }
        }
    }

    private fun createAutoComplete() {
        val items = listOf(
            "7 ans - Taux d'intérêt 0,96%",
            "10 ans - Taux d'intérêt 1,01%",
            "15 ans - Taux d'intérêt 1,17%",
            "20 ans - Taux d'intérêt 1,32%",
            "25 ans - Taux d'intérêt 1,65%"
        )

        val adapter = ArrayAdapter(this, R.layout.list_items, items)
        binding.lengthchoice.setAdapter(adapter)
    }

    private fun calculatesNotaryCharges(price: Long, charges: Int) {
        notaryCharges = (price * charges) / 100
        binding.notaryText.setText(notaryCharges.toString())
    }

    private fun totalPrice(year: Int, percent: Double, loan: Long) {
        monthly = ((loan * (percent / 12)) / (1 - ((1 + (percent / 12)).pow(-12 * year)))).toLong()
        benefit = (12 * year * monthly - loan)
        loanAndBenefit = monthly + benefit

        createPieChart()
        setData()
    }

    private fun createPieChart() {
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
            pieChart.animateY(1400, EaseInOutQuad)
        }

    }

    private fun setData() {
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
}