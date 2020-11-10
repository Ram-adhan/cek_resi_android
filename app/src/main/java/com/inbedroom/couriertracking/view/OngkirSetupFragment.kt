package com.inbedroom.couriertracking.view

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.connectNetwork
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.data.entity.Address
import com.inbedroom.couriertracking.data.entity.AddressEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_ongkir_setup.*
import java.util.*

class OngkirSetupFragment : Fragment() {

    private val couriers = listOf("JNE", "Pos", "TIKI", "Wahana", "SiCepat", "JNT", "Ninja", "JET")
    private val citiesName: MutableMap<String, Address> = mutableMapOf()
    private val subDistrictOrigin: MutableMap<String, Address> = mutableMapOf()
    private val subDistrictDestination: MutableMap<String, Address> = mutableMapOf()

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.isLoadingData.observe(this, loadingData)
        viewModel.cityList.observe(this, cityList)
        viewModel.failedLoadData.observe(this, failed)
        viewModel.noNetwork.observe(this, noNetwork)
        viewModel.isLoadingSubDistrict.observe(this, subdistrictStatus)
        viewModel.subDistrictListOrigin.observe(this, subdistrictListOrigin)
        viewModel.subDistrictListDestination.observe(this, subdistrictListDestination)
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

        cekOngkirEtOrigin.addTextChangedListener {
            val value = it.toString()
            if (citiesName.containsKey(value)) {
                viewModel.getSubDistricts(citiesName[value]?.id.toString(), true)
            } else {
                cekOngkirEtOriginSub.text.clear()
            }
        }

        cekOngkirEtDestination.addTextChangedListener {
            val value = it.toString()
            if (citiesName.containsKey(value)) {
                viewModel.getSubDistricts(citiesName[value]?.id.toString(), false)
            } else {
                cekOngkirEtDestinationSub.text.clear()
            }
        }

        cekOngkirButtonCalculate.setOnClickListener {
            var canContinue = true
            var origin = cekOngkirEtOrigin.text.toString()
            var destination = cekOngkirEtDestination.text.toString()
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
            if (chipIds.size == 0) {
                canContinue = false
                Message.alert(requireContext(), getString(R.string.choose_courier), null)
            }

            if (canContinue) {
                if (requireContext().connectNetwork()) {
                    var courierToCheck = ""
                    chipIds.forEachIndexed { i, chipId ->
                        if (i != 0) {
                            courierToCheck += ":"
                        }
                        val id = chipId - chipIds[0]
                        courierToCheck += couriers[id].toLowerCase(Locale.ROOT)
                    }
                    val originIdVal: String
                    val originTypeVal: String
                    val destIdVal: String
                    val destTypeVal: String

                    if (cekOngkirEtOriginSub.text.isNotEmpty()) {
                        origin = cekOngkirEtOriginSub.text.toString()
                        originIdVal =
                            subDistrictOrigin[origin]?.id ?: "-1"
                        originTypeVal =
                            subDistrictOrigin[origin]?.type ?: "-1"
                    } else {
                        originIdVal = citiesName[origin]?.id ?: "-1"
                        originTypeVal = citiesName[origin]?.type ?: "-1"
                    }

                    if (cekOngkirEtDestinationSub.text.isNotEmpty()) {
                        destination = cekOngkirEtDestinationSub.text.toString()
                        destIdVal =
                            subDistrictDestination[destination]?.id
                                ?: "-1"
                        destTypeVal =
                            subDistrictDestination[destination]?.type
                                ?: "-1"
                    } else {
                        destIdVal = citiesName[destination]?.id ?: "-1"
                        destTypeVal = citiesName[destination]?.type ?: "-1"
                    }

                    val request = CostRequest(
                        origin = originIdVal,
                        originType = originTypeVal,
                        destination = destIdVal,
                        destinationType = destTypeVal,
                        weight = weight,
                        courier = courierToCheck
                    )

                    startActivity(
                        CekOngkirActivity.callIntent(
                            requireContext(), origin, destination, request
                        )
                    )
                } else {
                    Message.alert(requireContext(), getString(R.string.no_internet), null)
                }
            }
        }
    }

    private val cityList = Observer<List<AddressEntity>> { data ->
        data.forEach {
            val prefix = if (it.type.equals("kabupaten", true)) "Kab. " else ""
            citiesName[prefix + it.name] =
                Address(it.name, it.addressId, if (it.isCity) "city" else "subdistrict")
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            citiesName.keys.toTypedArray()
        )
        cekOngkirEtOrigin.setAdapter(adapter)
        cekOngkirEtDestination.setAdapter(adapter)
    }

    private val subdistrictListOrigin = Observer<List<AddressEntity>> { data ->
        subDistrictOrigin.clear()
        data.forEach {
            subDistrictOrigin[it.name] = it.toAddress()
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            subDistrictOrigin.keys.toTypedArray()
        )

        cekOngkirEtOriginSub.setAdapter(adapter)
    }

    private val subdistrictListDestination = Observer<List<AddressEntity>> { data ->
        subDistrictDestination.clear()
        data.forEach {
            subDistrictDestination[it.name] = it.toAddress()
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            subDistrictDestination.keys.toTypedArray()
        )

        cekOngkirEtDestinationSub.setAdapter(adapter)
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

    private val noNetwork = Observer<Boolean> {
        if (it) {
            Message.alert(requireContext(), getString(R.string.no_internet), null)
        }
    }

    private val subdistrictStatus = Observer<Int> {
        when (it) {
            MainViewModel.LOADING_ORIGIN -> loadingOriginSub.visible()
            MainViewModel.LOADING_DESTINATION -> loadingDestinationSub.visible()
            MainViewModel.ERROR -> {
                loadingOriginSub.invisible()
                loadingDestinationSub.invisible()
                Message.toast(requireContext(), "Error Getting Subdistrict")
            }
            MainViewModel.FINISHED -> {
                loadingOriginSub.invisible()
                loadingDestinationSub.invisible()
            }
            MainViewModel.EMPTY -> {
                loadingOriginSub.invisible()
                loadingDestinationSub.invisible()
                Message.toast(requireContext(), "No available subdistrict")
            }
        }
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
                viewModel.getCityList(requireContext())
                true
            }
            else -> false
        }
    }
}