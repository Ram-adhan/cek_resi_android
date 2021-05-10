package com.inbedroom.couriertracking.view

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.extension.connectNetwork
import com.inbedroom.couriertracking.core.extension.hideKeyboard
import com.inbedroom.couriertracking.core.extension.invisible
import com.inbedroom.couriertracking.core.extension.visible
import com.inbedroom.couriertracking.customview.DialogEditTitle
import com.inbedroom.couriertracking.data.entity.Courier
import com.inbedroom.couriertracking.data.entity.HistoryEntity
import com.inbedroom.couriertracking.data.entity.SpinnerCourier
import com.inbedroom.couriertracking.utils.Message
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.view.adapter.CourierSpinnerAdapter
import com.inbedroom.couriertracking.view.adapter.HistoryAdapter
import com.inbedroom.couriertracking.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_tracking_detail.*
import kotlinx.android.synthetic.main.fragment_courier_track.*

class CourierTrackFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var courierAdapter: CourierSpinnerAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var getBarcodeContent: ActivityResultLauncher<Intent>

    private var courierData: Courier? = null
    private var courierList = mutableListOf<Courier>()

    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_courier_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.historiesData.observe(viewLifecycleOwner, historyObserver)
        viewModel.isChanged.observe(viewLifecycleOwner, onTitleChange)
        viewModel.courierList.observe(viewLifecycleOwner, populateCourier)
        viewModel.updateCouriers.observe(viewLifecycleOwner, onUpdateCourier)

        MobileAds.initialize(requireContext())

        loadAd()

        courierAdapter = CourierSpinnerAdapter(requireContext(), mutableListOf())
        mainCourierList.adapter = courierAdapter

        val llManager = LinearLayoutManager(requireContext())

        historyAdapter = HistoryAdapter(ArrayList())
        mainHistories.apply {
            layoutManager = llManager
            adapter = historyAdapter
        }
            .addItemDecoration(DividerItemDecoration(requireContext(), llManager.orientation))

        getBarcodeContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val result = it.data?.getStringExtra(MainActivity.RESULT_LABEL)
                    mainAWBInput.setText(result ?: "")
                }
            }

        onAction()
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            ServiceData.INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    interstitialAd = p0
                }
            })
    }

    private fun showInterstitial() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadAd()
                getBarcodeContent.launch(BarcodeScanActivity.callIntent(requireContext()))
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
        interstitialAd?.show(requireActivity())
    }

    private fun onAction() {
        mainCourierList.onItemSelectedListener = this

        mainButtonSearch.setOnClickListener {
            val awb = mainAWBInput.text.toString()
            if (awb.isNotEmpty()) {
                if (requireContext().connectNetwork()) {
                    courierData?.let { it1 ->
                        startActivity(
                            TrackingDetailActivity.callIntent(
                                requireContext(),
                                awb,
                                it1
                            )
                        )
                    }
                } else {
                    Message.alert(requireContext(), getString(R.string.no_internet), null)
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
                        requireContext(),
                        historyAdapter.getData(position).awb,
                        historyAdapter.getData(position).courier
                    )
                )
            }

            override fun onDeleteMenuClick(position: Int) {
                val item = historyAdapter.getData(position)
                viewModel.deleteHistory(item.awb)
                Message.notify(
                    requireActivity().mainCoordinatorLayout,
                    "${item.title ?: item.awb} Deleted"
                )
            }

            override fun onEditMenuClick(position: Int) {
                Message.alertEditText(
                    childFragmentManager,
                    object : DialogEditTitle.DialogListener {
                        override fun onPositiveDialog(text: String?) {
                            hideKeyboard()
                            with(historyAdapter.getData(position)) {
                                viewModel.editHistoryTitle(awb, text)
                            }
                        }
                    })
            }

            override fun onCopyAwbClick(position: Int) {
                val item = historyAdapter.getData(position)
                val clipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData =
                    ClipData.newPlainText(requireContext().getString(R.string.awb_hint), item.awb)
                clipboardManager.setPrimaryClip(clip)
                Message.toast(requireContext(), "Copied ${item.awb}")
            }
        })



        mainButtonBarcodeScan.setOnClickListener {
            if (interstitialAd != null) {
                showInterstitial()
            } else {
                getBarcodeContent.launch(BarcodeScanActivity.callIntent(requireContext()))
            }
        }
    }

    private val populateCourier = Observer<List<Courier>> {
        courierList.clear()
        courierList.addAll(it)
        val couriers: MutableList<SpinnerCourier> = mutableListOf()
        it.forEach { value ->
            couriers.add(
                SpinnerCourier(
                    value.name, value.imgUrl, value.code, value.available, getImgId(value.imgUrl)
                )
            )
        }
        courierAdapter.addData(couriers)
    }

    private val onUpdateCourier = Observer<Int> {
        when (it) {
            1 -> {
                if (loadingLayout != null) loadingLayout.visible()
            }
            0 -> Message.toast(requireContext(), "Courier Updated")
            else -> loadingLayout.invisible()
        }
        if (loadingLayout != null) loadingLayout.invisible()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == MainActivity.REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
            val result = data?.getStringExtra(MainActivity.RESULT_LABEL)
            mainAWBInput.setText(result ?: "")
        }
    }

    private val historyObserver = Observer<List<HistoryEntity>> {
        historyAdapter.addList(it.reversed())
    }

    private val onTitleChange = Observer<Boolean> {
        if (it) {
            Message.notify(
                requireActivity().mainCoordinatorLayout,
                getString(R.string.title_changed)
            )
        }
    }

    private fun getImgId(string: String): Int {
        return this.resources.getIdentifier(string, "drawable", context?.packageName)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refreshCourier -> {
                viewModel.getCouriers()
                true
            }
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        courierData = courierList[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

}