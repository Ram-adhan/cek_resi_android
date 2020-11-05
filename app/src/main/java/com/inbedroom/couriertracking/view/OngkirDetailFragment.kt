package com.inbedroom.couriertracking.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.data.entity.Ongkir
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.utils.ServiceData
import com.inbedroom.couriertracking.view.adapter.OngkirDetailAdapter
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ongkir_detail.*
import java.util.*
import kotlin.collections.ArrayList

class OngkirDetailFragment : Fragment() {

    companion object {
        fun forOngkir(): OngkirDetailFragment {
            val fragment = OngkirDetailFragment()
            return fragment
        }
    }

    private lateinit var ongkirDetailAdapter: OngkirDetailAdapter
    private lateinit var viewModel: OngkirViewModel
    private var listOngkir = mutableListOf<Ongkir>()
    private lateinit var adView: AdView

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(requireActivity()).get(OngkirViewModel::class.java)
        viewModel.ongkirData.observe(this, onLoad)
        MobileAds.initialize(requireContext()) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ongkir_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailOngkirTitle.text = getString(R.string.ongkir_title, viewModel.from, viewModel.to)

        val llManager = LinearLayoutManager(requireContext())
        ongkirDetailAdapter = OngkirDetailAdapter(ArrayList())
        detailOngkirRecycler.apply {
            layoutManager = llManager
            adapter = ongkirDetailAdapter
        }
            .addItemDecoration(DividerItemDecoration(requireContext(), llManager.orientation))

        initAds()
    }

    @ExperimentalStdlibApi
    private val onLoad = Observer<OngkirResult> { result ->

        val courier = result.name

        result.costs.forEach { costs ->
            val data = Ongkir()
            data.courier = courier
            data.service = costs.service
            with(costs.cost[0]) {
                data.cost = value
                data.etd = if (etd.contains("hari", true)) {
                    etd.replace(
                        "hari",
                        "",
                        true
                    )
                } else etd
            }
            listOngkir.add(data)
        }
        ongkirDetailAdapter.replaceData(listOngkir)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initAds(){
        adView = AdView(requireContext())
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = ServiceData.BANNER_AD_ID
        cekOngkirAdsRoot.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}