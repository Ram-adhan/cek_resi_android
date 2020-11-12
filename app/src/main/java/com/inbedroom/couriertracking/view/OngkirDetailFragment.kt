package com.inbedroom.couriertracking.view

import android.os.Bundle
import android.util.Log
import android.view.*
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
import kotlinx.android.synthetic.main.fragment_ongkir_detail.*

class OngkirDetailFragment : Fragment() {

    companion object {
        const val WEIGHT = "weight"
        fun newInstance(origin: String, destination: String, weight: Int): OngkirDetailFragment {
            val args = Bundle()
            args.putString(CekOngkirActivity.ORIGIN_STRING, origin)
            args.putString(CekOngkirActivity.DESTINATION_STRING, destination)
            args.putInt(WEIGHT, weight)

            val fragment = OngkirDetailFragment()
            fragment.arguments = args
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
        viewModel.ongkirListData.observe(this, onLoad)
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

        val origin = arguments?.getString(CekOngkirActivity.ORIGIN_STRING, "")
        val destination = arguments?.getString(CekOngkirActivity.DESTINATION_STRING, "")
        val weight = arguments?.getInt(WEIGHT, 0)

        detailTextOngkirCity.text = getString(R.string.ongkir_title, origin, destination)
        detailTextWeight.text = getString(R.string.weight_text, weight)

        val llManager = LinearLayoutManager(requireContext())
        ongkirDetailAdapter = OngkirDetailAdapter()
        detailOngkirRecycler.apply {
            layoutManager = llManager
            adapter = ongkirDetailAdapter
        }
            .addItemDecoration(DividerItemDecoration(requireContext(), llManager.orientation))

        initAds()
    }

    @ExperimentalStdlibApi
    private val onLoad = Observer<List<OngkirResult>> { result ->

        result.forEach { courData ->
            if (courData.costs.isNullOrEmpty()) {
                val data = Ongkir()
                data.courier = courData.name
                listOngkir.add(data)
            } else {
                courData.costs.forEach { costs ->
                    val data = Ongkir()
                    data.courier = courData.name
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
            }
        }

        ongkirDetailAdapter.setData(listOngkir)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initAds() {
        adView = AdView(requireContext())
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = ServiceData.BANNER_AD_ID
        cekOngkirAdsRoot.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}