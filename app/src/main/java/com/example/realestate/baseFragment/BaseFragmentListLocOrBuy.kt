package com.example.realestate.baseFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.adapter.ListLocOrBuyAdapter
import com.example.realestate.databinding.FragmentListLocBuyEstateBinding
import com.example.realestate.models.*
import com.example.realestate.ui.buyOrLocActivities.BuyAndLocViewModel
import com.example.realestate.ui.buyOrLocActivities.buyActivity.BuyActivity
import com.example.realestate.ui.buyOrLocActivities.locActivity.LocActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
abstract class BaseFragmentListLocOrBuy : Fragment() {

    private lateinit var binding: FragmentListLocBuyEstateBinding
    private lateinit var viewModel: BuyAndLocViewModel
    private lateinit var adapterList: ListLocOrBuyAdapter
    private var listEstate = mutableListOf<RealEstate>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListLocBuyEstateBinding.inflate(inflater)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetDialog)
        bottomSheetBehavior.isDraggable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 600

        binding.toolbar.setNavigationOnClickListener { activity?.finish() }
        binding.toolbar.title = getToolbarTitle()

        viewModel = ViewModelProvider(this).get(BuyAndLocViewModel::class.java)



        binding.toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.openFilter -> {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                    return@OnMenuItemClickListener true
                }
                else -> return@OnMenuItemClickListener false
            }
        })

        binding.btnFilter.setOnClickListener {
            filterList(listEstate)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.textPrize.text = getPrizeText()

        binding.sliderPrice.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance(Locale.FRANCE)
            format.format(value)
        }

        binding.sliderPrice.setLabelFormatter { value: Float ->
            val format = NumberFormat.getInstance(Locale.FRANCE)
            format.format(value)
        }

        return binding.root
    }

    abstract fun getBuyOrLoc(): BuyOrLoc

    abstract fun getPrizeText(): CharSequence?

    abstract fun getToolbarTitle(): CharSequence?

    private fun filterList(listEstate: MutableList<RealEstate>) {
        if (binding.radioAvailable.isChecked) {
            filterAvail(listEstate, true)
        } else if (binding.radioNotAvailable.isChecked) {
            filterAvail(listEstate, false)
        }
    }

    private fun filterAvail(listFilter: MutableList<RealEstate>, available: Boolean) {
        val list = mutableListOf<RealEstate>()
        for (realEstate in listFilter) {
            if (realEstate.isAvailable == available) {
                list.add(realEstate)
            }
        }
        filterType(list)
    }

    private fun filterType(list: MutableList<RealEstate>) {
        var listType = mutableListOf<RealEstate>()

        when (binding.radioGroupType.checkedRadioButtonId) {
            R.id.radioHouseBuy -> {
                listType = typeEstate(list, EstateType.HOUSE)
            }
            R.id.radioApartmentBuy -> {
                listType = typeEstate(list, EstateType.APARTMENT)
            }
            R.id.radioManorBuy -> {
                listType = typeEstate(list, EstateType.MANOR)
            }
            R.id.radioDuplexBuy -> {
                listType = typeEstate(list, EstateType.DUPLEX)
            }
        }

        filternumRoom(listType)

    }

    private fun filternumRoom(listType: MutableList<RealEstate>) {
        val listRoom = mutableListOf<RealEstate>()

        for (estate in listType) {
            estate.nbRoom?.toInt()?.let {
                if (it >= binding.sliderRoom.values[0] && estate.nbRoom.toFloat() <= binding.sliderRoom.values[1])
                    listRoom.add(estate)
            }

        }

        filterSize(listRoom)
    }

    private fun filterSize(listRoom: MutableList<RealEstate>) {
        val listSize = mutableListOf<RealEstate>()
        for (estate in listRoom) {
            estate.size?.toFloat()?.let {
                if (it >= binding.sliderSize.values[0] && estate.size.toFloat() <= binding.sliderSize.values[1])
                    listSize.add(estate) }

        }

        filterPrice(listSize)
    }

    private fun filterPrice(listSize: MutableList<RealEstate>) {
        val listPrice = mutableListOf<RealEstate>()
        for (estate in listSize) {
            if (estate.euroOrDollarLong(requireContext()) >= binding.sliderPrice.values[0] && estate.euroOrDollarLong(
                    requireContext()
                ) <= binding.sliderPrice.values[1]
            ) {
                listPrice.add(estate)
            }
        }
        filterCity(listPrice)
    }

    private fun filterCity(listPrice: MutableList<RealEstate>) {
        val listName = mutableListOf<RealEstate>()

        if (binding.editVicinity.text.isNullOrEmpty()) {
            listName.addAll(listPrice)
        } else {
            for (estate in listPrice) {
                estate.city?.let {
                    if (it == binding.editVicinity.text.toString()) listName.add(estate)
                }
            }
        }

        listCriteria(listName)
    }

    private fun listCriteria(listName: MutableList<RealEstate>) {
        val listCriteria = mutableListOf<RealEstate>()
        val list: MutableList<Criteria> = createListCrit()

        if (list.isEmpty()) {
            listCriteria.addAll(listName)
        } else {
            for (estate in listName) {
                estate.listCriteria?.let {
                    if (it.containsAll(list)) {
                        listCriteria.add(estate)
                    }
                }

            }
        }

        listPoi(listCriteria)
    }

    private fun listPoi(listCriteria: MutableList<RealEstate>) {
        val listPoi = mutableListOf<RealEstate>()
        val list: MutableList<PointOfInterest> = createListPoi()

        if (list.isEmpty()) {
            listPoi.addAll(listCriteria)
        } else {
            for (estate in listCriteria) {
                estate.listPoi?.let {
                    if (it.containsAll(list)) {
                        listPoi.add(estate)
                    }
                }

            }
        }

        if (listPoi.isEmpty()) {
            Log.d("testListFilter", "list vide")
        }
        for (estate in listPoi) {
            Log.d("testListFilter", "$estate")
        }

        adapterList.addList(listPoi)
    }

    private fun createListPoi(): MutableList<PointOfInterest> {
        val list = mutableListOf<PointOfInterest>()

        if (binding.checkSchools.isChecked) list.add(PointOfInterest.SCHOOLS)
        if (binding.checkBusiness.isChecked) list.add(PointOfInterest.BUSINESS)
        if (binding.checkTransports.isChecked) list.add(PointOfInterest.TRANSPORTS)
        if (binding.checkPark.isChecked) list.add(PointOfInterest.PARK)
        if (binding.checkMedical.isChecked) list.add(PointOfInterest.MEDICAL)

        return list

    }

    private fun createListCrit(): MutableList<Criteria> {
        val list = mutableListOf<Criteria>()

        if (binding.checkCave.isChecked) list.add(Criteria.CAVE)
        if (binding.checkParking.isChecked) list.add(Criteria.PARKING)
        if (binding.checkKitchArea.isChecked) list.add(Criteria.KITCHEN_AREA)
        if (binding.checkBalcony.isChecked) list.add(Criteria.BALCONY)
        if (binding.checkOpenKitch.isChecked) list.add(Criteria.OPENPLANE_KITCHEN)
        if (binding.checkTerrace.isChecked) list.add(Criteria.TERRACE)

        return list
    }

    private fun typeEstate(
        list: MutableList<RealEstate>,
        type: EstateType
    ): MutableList<RealEstate> {
        val listType = mutableListOf<RealEstate>()
        for (estate in list) {
            if (estate.type == type) listType.add(estate)
        }
        return listType
    }

    override fun onResume() {
        super.onResume()

        adapterList = when (getBuyOrLoc()) {
            BuyOrLoc.BUY -> ListLocOrBuyAdapter(requireContext(), activity as BuyActivity)
            else -> ListLocOrBuyAdapter(requireContext(), activity as LocActivity)
        }

        binding.listEstate.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterList
        }

        viewModel.getEstate(getBuyOrLoc()).observe(viewLifecycleOwner, { listEstate ->
            this.listEstate = listEstate
            adapterList.addList(listEstate)

        })

    }
}