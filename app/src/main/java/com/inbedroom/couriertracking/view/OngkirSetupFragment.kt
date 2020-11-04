package com.inbedroom.couriertracking.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import kotlinx.android.synthetic.main.activity_cek_ongkir.*
import kotlinx.android.synthetic.main.fragment_ongkir_setup.*

class OngkirSetupFragment : Fragment() {

    private val couriers = listOf("JNE", "Pos", "TIKI")
    private val citiesName: MutableMap<String, String> = mutableMapOf()

    private var requestCount = 0

    private lateinit var viewModel: OngkirViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(OngkirViewModel::class.java)
        viewModel.isLoadingData.observe(this, loadingData)
        viewModel.cityList.observe(this, cityList)
        viewModel.failed.observe(this, failed)
        viewModel.ongkirData.observe(this, ongkirData)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ongkir_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        couriers.forEach {
            val chip = Chip(requireContext())
            val chipDrawable = ChipDrawable.createFromAttributes(
                requireContext(),
                null,
                0,
                R.style.Widget_MaterialComponents_Chip_Choice
            )
            chip.setChipDrawable(chipDrawable)
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
                cekOngkirEtOrigin.error = "Empty origin"
                canContinue = false
            } else if (!citiesName.containsKey(origin)) {
                cekOngkirEtOrigin.error = "Unknown city, please type as suggested"
                canContinue = false
            }

            if (destination.isEmpty()) {
                cekOngkirEtDestination.error = "Empty destination"
                canContinue = false
            } else if (!citiesName.containsKey(destination)) {
                cekOngkirEtOrigin.error = "Unknown city, please type as suggested"
                canContinue = false
            }

            if (weight == 0) {
                cekOngkirEtWeight.error = "Empty Weight"
                canContinue = false
            }

            if (canContinue) {
                val courierList = mutableListOf<String>()
                chipGroupCourier.checkedChipIds.forEach {id ->
                    courierList.add(couriers[id - 1])
                    requestCount++
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

    private val ongkirData = Observer<List<OngkirResult>> {
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
}