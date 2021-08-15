package com.example.realestate.baseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.realestate.adapter.CriteriaAdapter
import com.example.realestate.adapter.PoiAdapter
import com.example.realestate.models.RealEstate
import com.example.realestate.ui.buyOrLocActivities.BuyAndLocViewModel

abstract class BaseFragmentBuyOrLoc<B : ViewBinding> : Fragment() {

    lateinit var binding: B
    lateinit var adapterCriteria: CriteriaAdapter
    lateinit var adapterPoi: PoiAdapter
    lateinit var viewModel: BuyAndLocViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        setupVM()
        return binding.root
    }

    abstract fun setupVM()

    abstract fun getViewBinding(): B

    override fun onResume() {
        super.onResume()

        adapterCriteria = CriteriaAdapter()
        adapterPoi = PoiAdapter()


        viewModel.getEstateRoom(getActivNav()).observe(viewLifecycleOwner, { estate ->
            createUI(estate)
        })
    }

    open fun listCriteriaToString(estate: RealEstate): MutableList<String> {
        val list = mutableListOf<String>()
        estate.listCriteria?.let {
            for (criteria in it) {
                list.add(estate.formatCriteria(criteria))
            }
        }

        return list
    }

    open fun listPoiToString(estate: RealEstate): MutableList<String> {
        val list = mutableListOf<String>()
        estate.listPoi?.let {
            for (poi in it) {
                list.add(estate.formatPointOfInterest(poi))
            }
        }
        return list
    }

    abstract fun getActivNav(): Long

    open fun createUI(estate: RealEstate) {
        estate.address?.let { viewModel.setAddress(it) }
        viewModel.staticImage.observe(viewLifecycleOwner, { address ->
            Glide.with(requireContext())
                .load(address)
                .into(getImage())

        })

        if (estate.isAvailable) {
            getTextSellOrRent().visibility = View.INVISIBLE
            getSellOrRent().visibility = View.INVISIBLE
        } else {
            getTextSellOrRent().visibility = View.VISIBLE
            getTextSellOrRent().text = setTextSellOrRent()
            getSellOrRent().visibility = View.VISIBLE
            getSellOrRent().text = estate.getDateUnavailable()
        }
    }

    abstract fun setTextSellOrRent(): CharSequence?

    abstract fun getSellOrRent(): TextView

    abstract fun getTextSellOrRent(): TextView

    abstract fun getImage(): ImageView

}