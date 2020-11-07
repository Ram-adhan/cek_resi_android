package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_cek_ongkir.*
import javax.inject.Inject

class CekOngkirActivity : BaseActivity() {

    companion object {
        const val ORIGIN_STRING = "origin"
        const val DESTINATION_STRING = "destination"
        const val REQUEST = "request"
        const val COURIER_LIST = "couriers"

        fun callIntent(
            context: Context,
            origin: String,
            destination: String,
            request: CostRequest,
            courierList: ArrayList<String>
        ): Intent {
            val intent = Intent(context, CekOngkirActivity::class.java)
            intent.putExtra(ORIGIN_STRING, origin)
            intent.putExtra(DESTINATION_STRING, destination)
            intent.putExtra(REQUEST, request)
            intent.putStringArrayListExtra(COURIER_LIST, courierList)
            return intent
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: OngkirViewModel
    private lateinit var origin: String
    private lateinit var destination: String

    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {
        origin = intent.getStringExtra(ORIGIN_STRING).toString()
        destination = intent.getStringExtra(DESTINATION_STRING).toString()
        val request = intent.getParcelableExtra(REQUEST) ?: CostRequest()
        val courierList = intent.getStringArrayListExtra(COURIER_LIST)

        (application as CourierTrackingApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(OngkirViewModel::class.java)

        if (courierList != null) {
            viewModel.checkTariff(request.origin, request.destination, request.weight, courierList.toList())
        }

        viewModel.onRequest.observe(this, loadingRequest)
    }

    override fun initView() {
        supportActionBar?.title = getString(R.string.ongkir_title, origin, destination)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction
                .add(R.id.cekOngkirMainFragmentRoot, OngkirDetailFragment.forOngkir(), "ongkirList")
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}