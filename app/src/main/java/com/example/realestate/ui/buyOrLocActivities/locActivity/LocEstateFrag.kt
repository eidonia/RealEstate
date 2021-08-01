package com.example.realestate.ui.buyOrLocActivities.locActivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.realestate.R
import com.example.realestate.databinding.FragmentLocEstateBinding
import com.example.realestate.models.RealEstate
import com.example.realestate.adapter.CriteriaAdapter
import com.example.realestate.adapter.PoiAdapter
import com.example.realestate.ui.buyOrLocActivities.BuyAndLocViewModel
import com.example.realestate.ui.buyOrLocActivities.ModificationEstate
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.DefaultSliderView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocEstateFrag : Fragment() {

    private lateinit var binding: FragmentLocEstateBinding
    private lateinit var adapterCriteria: CriteriaAdapter
    private lateinit var adapterPoi: PoiAdapter
    private lateinit var viewModel: BuyAndLocViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocEstateBinding.inflate(inflater)

        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        val args: LocEstateFragArgs by navArgs()

        adapterCriteria = CriteriaAdapter()
        adapterPoi = PoiAdapter()

        viewModel = ViewModelProvider(this).get(BuyAndLocViewModel::class.java)

        viewModel.getEstateRoom(args.idLocation).observe(viewLifecycleOwner, { estate ->
            createUI(estate)
        })

        return binding.root
    }


    private fun createUI(estate: RealEstate) {
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
            toolbar.title = "${estate.formatType(requireContext())}, ${estate.size} m², ${
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
            if (estate.listCriteria != null)adapterCriteria.addList(listCriteriaToString(estate))


            viewModel.setAddress(estate.address!!)
            viewModel.staticImage.observe(viewLifecycleOwner, { address ->
                Glide.with(requireContext())
                    .load(address)
                    .into(staticImage)

            })

            toolbar.setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.openModify -> {
                        var intent = Intent(requireContext(), ModificationEstate::class.java)
                        intent.putExtra("idModif", estate.dateEntry)
                        startActivity(intent)

                        return@setOnMenuItemClickListener  true
                    }
                    R.id.delete -> {
                        val action = LocEstateFragDirections.actionLocEstateFragToListLocEstateFrag()
                        findNavController().navigate(action)
                        viewModel.deleteEstate(estate)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }

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