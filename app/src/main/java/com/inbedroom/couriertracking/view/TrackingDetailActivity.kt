package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.TrackDataEntity
import com.inbedroom.couriertracking.data.entity.Tracking
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.utils.Utils
import com.inbedroom.couriertracking.view.adapter.TrackingRecyclerViewAdapter
import com.inbedroom.couriertracking.viewmodel.TrackingViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_tracking_detail.*
import javax.inject.Inject

class TrackingDetailActivity : BaseActivity() {
    companion object {
        const val COURIER_NAME = "courier"
        const val AWB_NUMBER = "resi"

        fun callIntent(context: Context, awb: String, courier: Courier): Intent {
            val intent = Intent(context, TrackingDetailActivity::class.java)
            intent.putExtra(COURIER_NAME, courier)
            intent.putExtra(AWB_NUMBER, awb)

            return intent
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TrackingViewModel
    private lateinit var trackingListAdapter: TrackingRecyclerViewAdapter
    private lateinit var adView: AdView
    private var courierData: Courier? = null
    private var awbData: String? = null

    private var interstitialAd: InterstitialAd? = null

    override fun layoutId(): Int = R.layout.activity_tracking_detail

    override fun setupLib() {
        (application as CourierTrackingApplication).appComponent.inject(this)
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(TrackingViewModel::class.java)
        courierData = intent.getParcelableExtra(COURIER_NAME)
        awbData = intent.getStringExtra(AWB_NUMBER)

        if (courierData != null && awbData != null) {
            viewModel.getTrackingData(awbData!!, courierData!!.code)
        }

        viewModel.trackingDataEntity.observe(this, trackingObserver)
        viewModel.isViewLoading.observe(this, loadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)

        MobileAds.initialize(this)
    }

    override fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tracking from ${courierData?.name}"

        val llManager = LinearLayoutManager(this)
        trackingListAdapter = TrackingRecyclerViewAdapter(this, ArrayList())

        trackingDetailInfo.apply {
            layoutManager = llManager
            adapter = trackingListAdapter
            setHasFixedSize(true)
        }

    }

    override fun onAction() {

    }

    private val trackingObserver = Observer<TrackDataEntity> { data ->
        initAds()

        val trackingList: List<Tracking> = data.track.filter { it.desc.isNotEmpty() }
        trackingListAdapter.updateItem(trackingList.sortedByDescending { Utils.stringToTime(it.date) })
        trackingDetailAwb.setValueText(data.summary.awb)
        val courierDetail =
            getString(R.string.courier_value, data.summary.courier, data.summary.service)
        val detailStatus = data.summary.status
        val detailSender = if (data.info.shipper.isNullOrEmpty()) {
            ""
        } else {
            "${data.info.shipper}\n${data.info.origin}"
        }
        val detailReceiver = "${data.info.receiver}\n${data.info.destination}"

        trackingDetailCourierName.setValueText(courierDetail)
        trackingDetailStatus.setValueText(detailStatus)
        if (detailStatus.equals("delivered", true)) {
            trackingDetailStatus.setValueColor(R.color.greenSea)
        }
        trackingDetailSender.setValueText(detailSender)
        trackingDetailDestination.setValueText(detailReceiver)

        if (awbData != null && courierData != null) {
            viewModel.saveAsHistory(awbData!!, courierData!!)
        }

    }

    private val loadingObserver = Observer<Boolean> {
        if (it) loadingLayout.visible() else loadingLayout.invisible()
    }

    private val onMessageErrorObserver = Observer<String> {
        var message = it
        if (it.contains("account", false)) {
            message = getString(R.string.generic_error)
        }
        Message.alert(this, message,
            DialogInterface.OnClickListener { _, _ -> onBackPressed() })
    }

    private fun initAds() {
        adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = ServiceData.BANNER_AD_ID
        trackingDetailAdRoot.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        InterstitialAd.load(
            this,
            ServiceData.TRACKING_INTERSTITIAL_AD_ID,
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (interstitialAd != null) {
            interstitialAd?.show(this)
        } else {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}