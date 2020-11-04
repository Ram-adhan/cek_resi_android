package com.inbedroom.couriertracking.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.data.entity.Ongkir
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.view.adapter.OngkirDetailAdapter
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import kotlinx.android.synthetic.main.fragment_ongkir_detail.*

class OngkirDetailFragment : Fragment() {

    companion object {
        fun forOngkir(): OngkirDetailFragment {
            val fragment = OngkirDetailFragment()
            return fragment
        }
    }

    private lateinit var ongkirDetailAdapter: OngkirDetailAdapter
    private var listOngkir = mutableListOf<Ongkir>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val viewModel = ViewModelProvider(requireActivity()).get(OngkirViewModel::class.java)
        viewModel.ongkirData.observe(this, onLoad)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ongkir_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val llManager = LinearLayoutManager(requireContext())
        ongkirDetailAdapter = OngkirDetailAdapter(ArrayList())
        detailOngkirRecycler.apply {
            layoutManager = llManager
            adapter = ongkirDetailAdapter
        }
            .addItemDecoration(DividerItemDecoration(requireContext(), llManager.orientation))
    }

    private val onLoad = Observer<OngkirResult> { result ->

        val courier = result.name

        result.costs.forEach { costs ->
            val data = Ongkir()
            data.courier = courier
            data.service = costs.service
            with(costs.cost[0]){
                data.cost = getString(R.string.price, value)
                data.etd = getString(R.string.etd, etd)
            }
            listOngkir.add(data)
        }
        ongkirDetailAdapter.replaceData(listOngkir)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
}