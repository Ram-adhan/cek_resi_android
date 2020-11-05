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


    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {
        (application as CourierTrackingApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(OngkirViewModel::class.java)

        viewModel.onRequest.observe(this, loadingRequest)
    }

    override fun initView() {
        supportActionBar?.title = getString(R.string.tariff_check)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
            .add(R.id.cekOngkirMainFragmentRoot, OngkirSetupFragment(), "ongkirSetup")
            .commit()
    }

    override fun onAction() {
    }

    private val loadingRequest = Observer<Boolean> {
        if (it) {
            cekOngkirLoading.visible()
        } else {
            cekOngkirLoading.invisible()
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("ongkirList")
            if (prev != null) {
                supportFragmentManager.popBackStack()
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction
                .addToBackStack("ongkirList")
                .add(R.id.cekOngkirMainFragmentRoot, OngkirDetailFragment.forOngkir(), "ongkirList")
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}