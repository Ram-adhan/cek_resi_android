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
    private val cities: MutableList<CityEntity> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>

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
        cekOngkirEtOrigin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("activity", "pos: $position")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
            }

        }

        cekOngkirEtDestination.setOnItemClickListener { parent, view, position, id ->
            request.destination = cities[position].cityId
        }

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
            }

            if (destination.isEmpty()) {
                cekOngkirEtDestination.error = "Empty destination"
                canContinue = false
            }

            if (weight == 0) {
                cekOngkirEtWeight.error = "Empty Weight"
                canContinue = false
            } else {
                request.weight = weight
            }

            if (canContinue) {
                viewModel.checkTariff(request, origin, destination)
            }
        }
    }

    private val cityList = Observer<List<CityEntity>> { data ->
        val citiesName: MutableList<String> = ArrayList()
        data.forEach {
            val prefix = if (it.type.equals("kabupaten", true)) it.type + " " else ""
            citiesName.add(prefix + it.cityName)
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, citiesName)
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