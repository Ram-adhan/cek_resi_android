package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import javax.inject.Inject

class CekOngkirActivity : BaseActivity() {

    val couriers = listOf("JNE", "Pos Indonesia", "Tiki")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: OngkirViewModel

    companion object{
        fun callIntent(context: Context): Intent {
            val intent = Intent(context, CekOngkirActivity::class.java)
            return intent
        }
    }

    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {
        (application as CourierTrackingApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(OngkirViewModel::class.java)
    }

    override fun initView() {
        supportActionBar?.title = getString(R.string.tariff_check)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onAction() {
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