package com.inbedroom.couriertracking.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.data.entity.CekOngkirSetupValidation
import com.inbedroom.couriertracking.data.entity.SimpleLocation
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_ongkir_setup.*
import java.util.*

class OngkirSetupFragment : Fragment() {

    private val couriers =
        mapOf(
            Pair("JNE", "jne"),
            Pair("Pos Indonesia", "pos"),
            Pair("TIKI", "tiki"),
            Pair("Wahana", "wahana"),
            Pair("SiCepat", "sicepat"),
            Pair("JNT", "jnt"),
            Pair("Ninja", "ninja"),
            Pair("JET", "jet"),
            Pair("AnterAja", "anteraja"),
            Pair("First Logistics", "first"),
            Pair("ID Express", "ide"),
            Pair("Lion", "lion"),
            Pair("Rex", "rex")
        )
    private val locationList: MutableList<SimpleLocation> = mutableListOf()
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private val status = CekOngkirSetupValidation()
    private var statusLiveData: MutableLiveData<CekOngkirSetupValidation> = MutableLiveData(status)

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadingStatus.observe(this, loadingStatus)
        viewModel.failedLoadData.observe(this, failed)
        viewModel.locationOriginData.observe(this, populateOriginLocation)
        viewModel.locationDestinationData.observe(this, populateDestinationLocation)

        statusLiveData.observe(this, validateInput)
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

        var counter = 0
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
            chip.text = it.key
            chip.isCheckable = true
            chip.id = counter
            chipGroupCourier.addView(chip)
            counter++
        }

        autoCompleteAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            locationList.map { it.name }
        )

        cekOngkirEtOrigin.setAdapter(autoCompleteAdapter)
        cekOngkirEtDestination.setAdapter(autoCompleteAdapter)

        onActionListener()
    }

    private fun onActionListener() {

        chipGroupCourier.forEach {
            (it as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                val value = couriers[buttonView.text.toString()] ?: ""
                if (isChecked) {
                    status.couriers.add(value)
                } else {
                    status.couriers.remove(value)
                }
                statusLiveData.postValue(status)
            }
        }

        cekOngkirEtOrigin.addTextChangedListener {
            val value = it.toString()
            if (value.isNotEmpty() && value.length >= 3 && !cekOngkirEtOrigin.isPerformingCompletion) {
                viewModel.getLocations(value, true)
                status.origin = null
            }

            if (value.isEmpty()) {
                status.origin = null
                deleteInputTextOrigin.invisible()
            } else {
                deleteInputTextOrigin.visible()
            }

            statusLiveData.postValue(status)
        }

        cekOngkirEtOrigin.setOnItemClickListener { _, _, position, _ ->
            val adapterValue = cekOngkirEtOrigin.adapter.getItem(position)
            status.origin = locationList.find {
                it.name == adapterValue
            }
            statusLiveData.postValue(status)
        }

        cekOngkirEtDestination.addTextChangedListener {
            val value = it.toString()
            if (value.isNotEmpty() && value.length >= 3 && !cekOngkirEtDestination.isPerformingCompletion) {
                viewModel.getLocations(value, false)
                status.destination = null
            }

            if (value.isEmpty()) {
                status.destination = null
                deleteInputTextDestination.invisible()
            } else {
                deleteInputTextDestination.visible()
            }

            statusLiveData.postValue(status)
        }

        cekOngkirEtDestination.setOnItemClickListener { _, _, position, _ ->
            val adapterValue = cekOngkirEtDestination.adapter.getItem(position)
            status.destination = locationList.find {
                it.name == adapterValue
            }
            statusLiveData.postValue(status)
        }

        cekOngkirEtWeight.addTextChangedListener {
            status.weight = try {
                it.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }

            if (it.toString().isEmpty()) {
                deleteInputTextWeight.invisible()
            } else {
                deleteInputTextWeight.visible()
            }

            statusLiveData.postValue(status)
        }

        hint_origin.setOnClickListener {
            Message.customDialog(
                requireContext(),
                "Pilih lokasi Asal dari daftar yang tersedia.\nGaris tepi akan berwarna hijau saat lokasi dipilih dari daftar",
                "Bantuan",
                null
            )
        }

        hint_destination.setOnClickListener {
            Message.customDialog(
                requireContext(),
                "Pilih lokasi Tujuan dari daftar yang tersedia.\n" +
                        "Garis tepi akan berwarna hijau saat lokasi dipilih dari daftar",
                "Bantuan",
                null
            )
        }

        deleteInputTextOrigin.setOnClickListener {
            cekOngkirEtOrigin.text.clear()
        }

        deleteInputTextDestination.setOnClickListener {
            cekOngkirEtDestination.text.clear()
        }

        deleteInputTextWeight.setOnClickListener {
            cekOngkirEtWeight.text.clear()
        }

        cekOngkirButtonCalculate.setOnClickListener {

            startActivity(
                CekOngkirActivity.callIntent(
                    requireContext(), status
                )
            )
        }
    }

    private val validateInput = Observer<CekOngkirSetupValidation> {
        if (it.origin != null) {
            cekOngkirEtOrigin.setBackgroundResource(R.drawable.rounded_rectangle_ok)
        } else {
            cekOngkirEtOrigin.setBackgroundResource(R.drawable.edittext_state)
        }

        if (it.destination != null) {
            cekOngkirEtDestination.setBackgroundResource(R.drawable.rounded_rectangle_ok)
        } else {
            cekOngkirEtDestination.setBackgroundResource(R.drawable.edittext_state)
        }

        if (it.weight > 0) {
            cekOngkirEtWeight.setBackgroundResource(R.drawable.rounded_rectangle_ok)
        } else {
            cekOngkirEtWeight.setBackgroundResource(R.drawable.edittext_state)
        }

        cekOngkirButtonCalculate.isEnabled = it.isValid()
        if (it.isValid()) {
            status.formattedCourier = formatCourier(it.couriers)
        }
    }

    private fun formatCourier(list: MutableList<String>): String {
        var result = ""
        list.forEachIndexed { i, value ->
            if (i != 0) {
                result += ":"
            }
            result += value.toLowerCase(Locale.ROOT)
        }

        return result
    }

    private val loadingStatus = Observer<Int> {
        when (it) {
            MainViewModel.LOADING_CITY_ORIGIN_START -> {
                loadingOrigin.visible()
            }
            MainViewModel.LOADING_CITY_DESTINATION_START -> {
                loadingDestination.visible()
            }
            MainViewModel.LOADING_CITY_ORIGIN_FINISHED -> {
                loadingOrigin.invisible()
            }
            MainViewModel.LOADING_CITY_DESTINATION_FINISHED -> {
                loadingDestination.invisible()
            }
            MainViewModel.ERROR -> {
                loadingOrigin.invisible()
                loadingDestination.invisible()
            }
            MainViewModel.EMPTY -> {
                loadingOrigin.invisible()
                loadingDestination.invisible()
            }
        }
    }

    private val failed = Observer<String> {
        Message.toast(requireContext(), it)
    }

    private val populateOriginLocation = Observer<List<SimpleLocation>> { locations ->
        locationList.clear()
        locationList.addAll(locations)

        autoCompleteAdapter.clear()

        autoCompleteAdapter.addAll(locationList.map { it.name })

        autoCompleteAdapter.notifyDataSetChanged()

    }

    private val populateDestinationLocation = Observer<List<SimpleLocation>> { locations ->
        locationList.clear()
        locationList.addAll(locations)

        autoCompleteAdapter.clear()

        autoCompleteAdapter.addAll(locationList.map { it.name })

        autoCompleteAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.helper -> {
                true
            }
            else -> false
        }
    }
}