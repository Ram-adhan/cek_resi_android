package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_cek_ongkir.*
import javax.inject.Inject

class CekOngkirActivity : BaseActivity() {

    companion object {
        fun callIntent(context: Context): Intent {
            val intent = Intent(context, CekOngkirActivity::class.java)
            return intent
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: OngkirViewModel
    private val couriers = listOf("JNE", "Pos", "TIKI")
    private val citiesName: MutableMap<String, String> = mutableMapOf()

    private var requestCount = 0

    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {
        (application as CourierTrackingApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(OngkirViewModel::class.java)

        viewModel.isLoadingData.observe(this, loadingData)
        viewModel.cityList.observe(this, cityList)
        viewModel.failed.observe(this, failed)
        viewModel.ongkirData.observe(this, ongkirData)
        viewModel.onRequest.observe(this, loadingRequest)
    }

    override fun initView() {
        supportActionBar?.title = getString(R.string.tariff_check)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        couriers.forEach {
            val chip = Chip(this)
            val chipDrawable = ChipDrawable.createFromAttributes(
                this,
                null,
                0,
                R.style.Widget_MaterialComponents_Chip_Choice
            )
            chip.setChipDrawable(chipDrawable)
            chip.text = it
            chip.isCheckable = true

            chipGroupCourier.addView(chip)
        }
    }

    override fun onAction() {

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
                chipGroupCourier.checkedChipIds.forEach {
                    courierList.add(couriers[it - 1])
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
            this,
            android.R.layout.simple_list_item_1,
            citiesName.keys.toTypedArray()
        )
        cekOngkirEtOrigin.setAdapter(adapter)
        cekOngkirEtDestination.setAdapter(adapter)
    }

    private val loadingRequest = Observer<Boolean> {
        if (it) {
            cekOngkirLoading.visible()
        } else {
            cekOngkirLoading.invisible()
            supportFragmentManager.beginTransaction()
                .add(R.id.cekOngkirRoot, OngkirDetailFragment.forOngkir(), "ongkirList")
                .commit()
        }
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
        Message.toast(this, it)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}