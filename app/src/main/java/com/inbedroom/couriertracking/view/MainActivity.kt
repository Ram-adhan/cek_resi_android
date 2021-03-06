package com.inbedroom.couriertracking.view

import android.app.Activity
import android.content.Intent
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.inbedroom.couriertracking.BuildConfig
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.hideKeyboard
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.customview.DialogEditTitle
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.HistoryEntity
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.view.adapter.CourierSpinnerAdapter
import com.inbedroom.couriertracking.view.adapter.HistoryAdapter
import com.inbedroom.couriertracking.viewmodel.MainViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        const val REQUEST_CODE = 101
        const val RESULT_LABEL = "AWB"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var courierAdapter: CourierSpinnerAdapter
    private lateinit var courierList: List<Courier>
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var spinner: Spinner
    private lateinit var adView: AdView

    private var courierData: Courier? = null

    override fun layoutId(): Int = R.layout.activity_main

    override fun setupLib() {
        (application as CourierTrackingApplication).appComponent.inject(this)
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(MainViewModel::class.java)

        viewModel.historiesData.observe(this, historyObserver)
        viewModel.isChanged.observe(this, onTitleChange)

        MobileAds.initialize(this) {}
    }

    override fun initView() {
        courierList = readFromAsset()
        courierAdapter = CourierSpinnerAdapter(this, courierList)
        mainCourierList.adapter = courierAdapter
        spinner = findViewById(R.id.mainCourierList)
        spinner.onItemSelectedListener = this

        val llManager = LinearLayoutManager(this)

        historyAdapter = HistoryAdapter(ArrayList())
        mainHistories.apply {
            layoutManager = llManager
            adapter = historyAdapter
        }
            .addItemDecoration(DividerItemDecoration(this, llManager.orientation))

        initAds()
    }

    override fun onAction() {
        mainButtonSearch.setOnClickListener {
            val awb = mainAWBInput.text.toString()
            if (awb.isNotEmpty()) {
                courierData?.let { it1 ->
                    startActivity(
                        TrackingDetailActivity.callIntent(
                            this,
                            awb,
                            it1
                        )
                    )
                }
            } else {
                mainAWBInput.error = getString(R.string.empty_awb)
                mainAWBInput.requestFocus()
            }
        }

        historyAdapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                startActivity(
                    TrackingDetailActivity.callIntent(
                        this@MainActivity,
                        historyAdapter.getData(position).awb,
                        historyAdapter.getData(position).courier
                    )
                )
            }

            override fun onDeleteMenuClick(position: Int) {
                val item = historyAdapter.getData(position)
                viewModel.deleteHistory(item.awb)
                Message.notify(mainCoordinatorLayout, "${item.title ?: item.awb} Deleted", mainAdsRoot)
            }

            override fun onEditMenuClick(position: Int) {
                Message.alertEditText(
                    supportFragmentManager,
                    object : DialogEditTitle.DialogListener {
                        override fun onPositiveDialog(text: String?) {
                            hideKeyboard()
                            with(historyAdapter.getData(position)) {
                                viewModel.editHistoryTitle(awb, text)
                            }
                        }
                    })
            }
        })

        mainButtonBarcodeScan.setOnClickListener {
            startActivityForResult(BarcodeScanActivity.callIntent(this), REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
            val result = data?.getStringExtra(RESULT_LABEL)
            mainAWBInput.setText(result ?: "")
        }
    }

    private fun initAds(){
        adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = ServiceData.BANNER_AD_ID
        mainAdsRoot.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteAllHistory -> {
                deleteAllHistory()
                true
            }
            else -> false
        }
    }

    private fun deleteAllHistory() {
        viewModel.clearHistory()
    }

    private fun readFromAsset(): List<Courier> {
        val fileName = "courier_list.json"

        val bufferReader = application.assets.open(fileName).bufferedReader()

        val jsonString = bufferReader.use {
            it.readText()
        }
        val gson = Gson()

        return gson.fromJson(jsonString, Array<Courier>::class.java).toList().filter {
            it.available
        }
    }

    private val historyObserver = Observer<List<HistoryEntity>> {
        historyAdapter.addList(it.reversed())
    }

    private val onTitleChange = Observer<Boolean> {
        if (it) {
            Message.notify(mainCoordinatorLayout, getString(R.string.title_changed), mainAdsRoot)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        courierData = parent?.getItemAtPosition(position) as Courier
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}