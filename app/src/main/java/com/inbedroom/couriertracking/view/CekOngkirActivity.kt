package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.connectNetwork
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.data.entity.CekOngkirSetupValidation
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_cek_ongkir.*
import javax.inject.Inject

class CekOngkirActivity : BaseActivity() {

    companion object {
        private const val REQUEST_DATA = "data"
        const val ORIGIN_STRING = "origin"
        const val DESTINATION_STRING = "destination"

        const val STATUS_LOADING = 0
        const val STATUS_FINISHED = 1
        const val STATUS_ERROR = 2

        fun callIntent(
            context: Context,
            data: CekOngkirSetupValidation
        ): Intent {
            val intent = Intent(context, CekOngkirActivity::class.java)
            intent.putExtra(REQUEST_DATA, data)
            return intent
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: OngkirViewModel
    private lateinit var ongkirSetupData: CekOngkirSetupValidation
    private var interstitialAd: InterstitialAd? = null

    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {
        ongkirSetupData = intent.getParcelableExtra(REQUEST_DATA)!!

        (application as CourierTrackingApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(OngkirViewModel::class.java)

        if (this.connectNetwork()) {
            viewModel.checkTariff(
                ongkirSetupData
            )
            viewModel.onRequestStatus.observe(this, loadingRequest)

        } else {
            displayNotConnected()
        }

        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            ServiceData.CEK_ONGKIR_INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    interstitialAd = p0
                }
            })

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
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

    private val loadingRequest = Observer<Int> {
        when (it) {
            STATUS_LOADING -> {
                cekOngkirLoading.visible()
            }
            STATUS_FINISHED -> {
                cekOngkirLoading.invisible()
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                val prev = supportFragmentManager.findFragmentByTag("ongkirList")
                if (prev != null) {
                    fragmentTransaction.remove(prev)
                }
                fragmentTransaction
                    .add(
                        R.id.cekOngkirMainFragmentRoot,
                        OngkirDetailFragment.newInstance(
                            ongkirSetupData.origin!!.name,
                            ongkirSetupData.destination!!.name,
                            ongkirSetupData.weight
                        ),
                        "ongkirList"
                    )
                    .commit()
            }
            STATUS_ERROR -> {
                cekOngkirLoading.invisible()
                Message.alert(this, "Something unexpected happen",
                    DialogInterface.OnClickListener { _, _ -> finish() })
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (interstitialAd != null) {
            interstitialAd?.show(this)
        } else {
            finish()
        }
    }
}