package com.inbedroom.couriertracking.view

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import kotlinx.android.synthetic.main.fragment_ongkir_setup.*

class OngkirSetupFragment : Fragment() {

    private val couriers = listOf("JNE", "Pos", "TIKI")
    private val citiesName: MutableMap<String, String> = mutableMapOf()

    private lateinit var viewModel: OngkirViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(OngkirViewModel::class.java)
        viewModel.isLoadingData.observe(this, loadingData)
        viewModel.cityList.observe(this, cityList)
        viewModel.failed.observe(this, failed)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ongkir_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colorState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.getColorStateList(R.color.chip_text_state)
        } else {
            AppCompatResources.getColorStateList(requireContext(), R.color.chip_text_state)
        }
        couriers.forEach {
            val chip = Chip(requireContext())
            val chipDrawable = ChipDrawable.createFromAttributes(
                requireContext(),
                null,
                0,
                R.style.CustomChipStyle
            )
            chip.setChipDrawable(chipDrawable)
            chip.setTextColor(colorState)
            chip.text = it
            chip.isCheckable = true
            chipGroupCourier.addView(chip)
        }

        onActionListener()
    }

    private fun onActionListener() {
        cekOngkirButtonCalculate.setOnClickListener {
            var canContinue = true
            val origin = cekOngkirEtOrigin.text.toString()
            val destination = cekOngkirEtDestination.text.toString()
            val weight = try {
                cekOngkirEtWeight.text.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }

            if (origin.isEmpty()) {
                cekOngkirEtOrigin.error = getString(R.string.empty_origin)
                canContinue = false
            } else if (!citiesName.containsKey(origin)) {
                cekOngkirEtOrigin.error = getString(R.string.unknown_city)
                canContinue = false
            }

            if (destination.isEmpty()) {
                cekOngkirEtDestination.error = getString(R.string.empty_destination)
                canContinue = false
            } else if (!citiesName.containsKey(destination)) {
                cekOngkirEtOrigin.error = getString(R.string.unknown_city)
                canContinue = false
            }

            if (weight == 0) {
                cekOngkirEtWeight.error = getString(R.string.empty_weight)
                canContinue = false
            }
            val chipIds = chipGroupCourier.checkedChipIds
            if (chipIds.size == 0){
                canContinue = false
                Message.alert(requireContext(), getString(R.string.choose_courier), null)
            }

            if (canContinue) {
                val courierList = mutableListOf<String>()
                chipIds.forEach { id ->
                    courierList.add(couriers[id - 1])
                }
                val originString = citiesName[origin] ?: "-1"
                val destinationString = citiesName[destination] ?: "-1"
                viewModel.checkTariff(originString, destinationString, weight, courierList)
            }
        }
    }

    private val cityList = Observer<List<CityEntity>> { data ->
        data.forEach {
            val prefix = if (it.type.equals("kabupaten", true)) it.type + " " else ""
            citiesName[prefix + it.cityName] = it.cityId
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            citiesName.keys.toTypedArray()
        )
        cekOngkirEtOrigin.setAdapter(adapter)
        cekOngkirEtDestination.setAdapter(adapter)
    }

    private val loadingData = Observer<Boolean> {
        if (it) {
            loadingOrigin.visible()
            loadingDestination.visible()
        } else {
            loadingOrigin.invisible()
            loadingDestination.invisible()
        }
    }

    private val failed = Observer<String> {
        Message.toast(requireContext(), it)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.cek_ongkir_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.refreshList -> {
                viewModel.getCityList()
                true
            }
            else -> false
        }
    }
}