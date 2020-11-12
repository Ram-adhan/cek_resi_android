package com.inbedroom.couriertracking.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.connectNetwork
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.data.entity.Address
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.utils.AddressItem
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_ongkir_setup.*
import java.util.*

class OngkirSetupFragment : Fragment() {

    companion object {
        const val CITY = "city"
        const val SUBDISTRICT = "subdistrict"
    }

    private val couriers = listOf("JNE", "Pos", "TIKI", "Wahana", "SiCepat", "JNT", "Ninja", "JET")
    private val citiesName: MutableMap<String, Address> = mutableMapOf()
    private val subDistrictOrigin: MutableMap<String, Address> = mutableMapOf()
    private val subDistrictDestination: MutableMap<String, Address> = mutableMapOf()

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadingStatus.observe(this, loadingStatus)
        viewModel.addressList.observe(this, subdistrictList)
        viewModel.cityList.observe(this, cityList)
        viewModel.failedLoadData.observe(this, failed)
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

        var origin: Address? = null
        var destination: Address? = null

        cekOngkirEtOrigin.addTextChangedListener {
            val value = it.toString()
            if (citiesName.containsKey(value)) {
                origin = citiesName[value]
                origin?.id?.let { it1 -> viewModel.getSubDistricts(it1, true) }
            } else {
                origin = null
                cekOngkirEtOriginSub.text.clear()
            }
        }

        cekOngkirEtDestination.addTextChangedListener {
            val value = it.toString()
            if (citiesName.containsKey(value)) {
                destination = citiesName[value]
                destination?.id?.let { it1 -> viewModel.getSubDistricts(it1, false) }
            } else {
                destination = null
                cekOngkirEtDestinationSub.text.clear()
            }
        }

        cekOngkirEtOriginSub.addTextChangedListener {
            val value = it.toString()
            if (value.isNotEmpty()) {
                origin = subDistrictOrigin[value] ?: origin
            }
        }

        cekOngkirEtDestinationSub.addTextChangedListener {
            val value = it.toString()
            if (value.isNotEmpty()) {
                destination = subDistrictDestination[value] ?: destination
            }
        }

        cekOngkirButtonCalculate.setOnClickListener {
            var canContinue = true
            val weight = try {
                cekOngkirEtWeight.text.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }

            if (cekOngkirEtOrigin.text.isEmpty() && origin == null) {
                cekOngkirEtOrigin.error = getString(R.string.empty_origin)
                canContinue = false
            } else if (cekOngkirEtOrigin.text.isNotEmpty() && origin == null) {
                cekOngkirEtOrigin.error = getString(R.string.unknown_city)
                canContinue = false
            } else if (cekOngkirEtOriginSub.text.isNotEmpty() && origin!!.type == CITY) {
                cekOngkirEtOriginSub.error = getString(R.string.unknown_subdistrict)
                canContinue = false
            }


            if (cekOngkirEtDestination.text.isEmpty() && destination == null) {
                cekOngkirEtDestination.error = getString(R.string.empty_destination)
                canContinue = false
            } else if (cekOngkirEtDestination.text.isNotEmpty() && destination == null) {
                cekOngkirEtDestination.error = getString(R.string.unknown_city)
                canContinue = false
            } else if (cekOngkirEtDestinationSub.text.isNotEmpty() && destination!!.type == CITY) {
                cekOngkirEtDestinationSub.error = getString(R.string.unknown_subdistrict)
                canContinue = false
            }

            if (weight == 0) {
                cekOngkirEtWeight.error = getString(R.string.empty_weight)
                canContinue = false
            }
            
            val chipIds = chipGroupCourier.checkedChipIds
            if (chipIds.size == 0) {
                canContinue = false
                Message.alert(requireContext(), getString(R.string.choose_courier), null)
            }

            if (canContinue) {
                var courierToCheck = ""
                chipIds.forEachIndexed { i, chipId ->
                    if (i != 0) {
                        courierToCheck += ":"
                    }
                    val id = chipId - chipIds[0]
                    courierToCheck += couriers[id].toLowerCase(Locale.ROOT)
                }

                val request = CostRequest(
                    origin = origin!!.id,
                    originType = origin!!.type,
                    destination = destination!!.id,
                    destinationType = destination!!.type,
                    weight = weight,
                    courier = courierToCheck
                )

                startActivity(
                    CekOngkirActivity.callIntent(
                        requireContext(), origin!!.name, destination!!.name, request
                    )
                )
            }
        }
    }

    private val cityList = Observer<List<Address>> { data ->
        citiesName.clear()
        data.forEach {
            citiesName[it.name] = it
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            citiesName.keys.toTypedArray()
        )
        cekOngkirEtOrigin.setAdapter(adapter)
        cekOngkirEtDestination.setAdapter(adapter)
    }

    private val subdistrictList = Observer<AddressItem<List<Address>>> { data ->
        when(data){
            is AddressItem.SubOrigin -> {
                subDistrictOrigin.clear()
                data.value.forEach {
                    subDistrictOrigin[it.name] = it
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    subDistrictOrigin.keys.toTypedArray()
                )
                cekOngkirEtOriginSub.setAdapter(adapter)
            }
            is AddressItem.SubDestination -> {
                subDistrictDestination.clear()
                data.value.forEach {
                    subDistrictDestination[it.name] = it
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    subDistrictDestination.keys.toTypedArray()
                )
                cekOngkirEtDestinationSub.setAdapter(adapter)
            }
            else -> {}
        }
    }

    private val loadingStatus = Observer<Int> {
        when (it) {
            MainViewModel.LOADING_CITY -> {
                loadingOrigin.visible()
                loadingDestination.visible()
            }
            MainViewModel.FINISHED -> {
                loadingOrigin.invisible()
                loadingDestination.invisible()
                loadingOriginSub.invisible()
                loadingDestinationSub.invisible()
            }
            MainViewModel.LOADING_SUB_ORIGIN -> loadingOriginSub.visible()
            MainViewModel.LOADING_SUB_DESTINATION -> loadingDestinationSub.visible()
            MainViewModel.ERROR -> {
                loadingOriginSub.invisible()
                loadingDestinationSub.invisible()
                Message.toast(requireContext(), "Error Getting Subdistrict")
            }
            MainViewModel.EMPTY -> {
                loadingOriginSub.invisible()
                loadingDestinationSub.invisible()
                Message.toast(requireContext(), "No available subdistrict")
            }
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
        return when (item.itemId) {
            R.id.refreshList -> {
                cekOngkirEtDestination.error = null
                cekOngkirEtOrigin.error = null
                cekOngkirEtWeight.error = null
                if (requireContext().connectNetwork()) {
                    viewModel.getCityList()
                } else {
                    Message.alert(requireContext(), getString(R.string.no_internet), null)
                }
                true
            }
            else -> false
        }
    }
}