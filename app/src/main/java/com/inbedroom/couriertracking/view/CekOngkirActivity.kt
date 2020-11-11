package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.connectNetwork
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.data.entity.CostRequest
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_cek_ongkir.*
import javax.inject.Inject

class CekOngkirActivity : BaseActivity() {

    companion object {
        const val ORIGIN_STRING = "origin"
        const val DESTINATION_STRING = "destination"
        const val REQUEST = "request"

        fun callIntent(
            context: Context,
            origin: String,
            destination: String,
            request: CostRequest
        ): Intent {
            val intent = Intent(context, CekOngkirActivity::class.java)
            intent.putExtra(ORIGIN_STRING, origin)
            intent.putExtra(DESTINATION_STRING, destination)
            intent.putExtra(REQUEST, request)
            return intent
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: OngkirViewModel
    private lateinit var origin: String
    private lateinit var destination: String
    private var weight: Int = 0
    private lateinit var interstitialAd: InterstitialAd

    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {
        origin = intent.getStringExtra(ORIGIN_STRING).toString()
        destination = intent.getStringExtra(DESTINATION_STRING).toString()
        val request = intent.getParcelableExtra(REQUEST) ?: CostRequest()
        weight = request.weight

        (application as CourierTrackingApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(OngkirViewModel::class.java)

        if (this.connectNetwork()){
            viewModel.checkTariff(request)
            viewModel.onRequest.observe(this, loadingRequest)

        }else{
            displayNotConnected()
        }

        MobileAds.initialize(this)
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = ServiceData.INTERSTITIAL_AD_ID
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                finish()
            }
        }
    }

    private fun displayNotConnected() {
        cekOngkirNoInternet.visible()
    }

    override fun initView() {
        supportActionBar?.title = getString(R.string.tariff_check)
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
                .add(
                    R.id.cekOngkirMainFragmentRoot,
                    OngkirDetailFragment.newInstance(origin, destination, weight),
                    "ongkirList"
                )
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (interstitialAd.isLoaded) {
            interstitialAd.show()
        } else {
            finish()
        }
    }
}