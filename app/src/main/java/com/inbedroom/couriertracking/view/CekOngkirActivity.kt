package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.data.entity.CityEntity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.data.network.response.RajaOngkirBaseResponse
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_cek_ongkir.*
import java.lang.NumberFormatException
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
    val couriers = listOf("JNE", "Pos Indonesia", "Tiki")
    private val citiesName: MutableMap<String, String> = mutableMapOf()

    private val request = CostRequest()

    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {
        (application as CourierTrackingApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(OngkirViewModel::class.java)

        viewModel.cityList.observe(this, cityList)
    }

    override fun initView() {
        supportActionBar?.title = getString(R.string.tariff_check)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            } else if (!citiesName.containsKey(origin)){
                cekOngkirEtOrigin.error = "Unknown city, please type as suggested"
                canContinue = false
            }

            if (destination.isEmpty()) {
                cekOngkirEtDestination.error = "Empty destination"
                canContinue = false
            } else if (!citiesName.containsKey(destination)){
                cekOngkirEtOrigin.error = "Unknown city, please type as suggested"
                canContinue = false
            }

            if (weight == 0) {
                cekOngkirEtWeight.error = "Empty Weight"
                canContinue = false
            } else {
                request.weight = weight
            }

            if (canContinue) {
                val originString = citiesName[origin] ?: "-1"
                val destinationString = citiesName[destination] ?: "-1"
                viewModel.checkTariff(originString, destinationString, weight)
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}