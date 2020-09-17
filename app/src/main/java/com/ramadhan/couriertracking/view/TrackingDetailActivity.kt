package com.ramadhan.couriertracking.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ramadhan.couriertracking.R
import com.ramadhan.couriertracking.data.response.entity.Courier
import com.ramadhan.couriertracking.data.response.entity.Track
import com.ramadhan.couriertracking.data.response.entity.Tracking
import com.ramadhan.couriertracking.utils.Injector
import com.ramadhan.couriertracking.viewmodel.TrackingViewModel
import kotlinx.android.synthetic.main.activity_tracking_detail.*

class TrackingDetailActivity : AppCompatActivity() {
    companion object {
        const val COURIER_NAME = "courier"
        const val AWB_NUMBER = "resi"
    }

    private lateinit var viewModel: TrackingViewModel
    private var courierData: Courier? = null
    private var awbData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_detail)

        setupLib()
        if (savedInstanceState == null) {
            if (courierData != null && awbData != null) {
                viewModel.getTrackingData(awbData!!, courierData!!.code)
            }
        }
        setupUI()
    }

    private fun setupLib() {
        viewModel = ViewModelProvider(
            this,
            Injector.provideViewModelFactory()
        ).get(TrackingViewModel::class.java)

        courierData = intent.getParcelableExtra(COURIER_NAME)
        awbData = intent.getStringExtra(AWB_NUMBER)

    }

    private fun setupUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Tracking from ${courierData?.name}"

        viewModel.trackingData.observe(this, trackingObserver)
        viewModel.isViewLoading.observe(this, loadingObserver)
        viewModel.isNoData.observe(this, noDataObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
    }

    private val trackingObserver = Observer<Track<List<Tracking>>> {
        trackingDetailCourierName.setValueText(it.courier)
        trackingDetailAwb.setValueText(it.waybill)
        trackingDetailStatus.setValueText(it.status)
        trackingDetailSender.setValueText("${it.shipped.name}\n${it.shipped.addr}")
        trackingDetailDestination.setValueText("${it.received.name}\n${it.received.addr}")
    }

    private val loadingObserver = Observer<Boolean> {
        val visibility = if (it) View.VISIBLE else View.GONE
        loadingLayout.visibility = visibility
    }

    private val noDataObserver = Observer<Boolean> {

    }

    private val onMessageErrorObserver = Observer<Any> {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(getString(R.string.alert_title))
        dialogBuilder.setMessage(it.toString())
        dialogBuilder.setCancelable(false)

        dialogBuilder.setPositiveButton(R.string.ok_button) { _, _ ->
            finish()
        }

        dialogBuilder.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}