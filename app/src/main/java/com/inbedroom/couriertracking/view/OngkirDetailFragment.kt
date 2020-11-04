package com.inbedroom.couriertracking.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.data.entity.OngkirResult
import com.inbedroom.couriertracking.viewmodel.OngkirViewModel
import kotlinx.android.synthetic.main.fragment_ongkir_detail.*

class OngkirDetailFragment : Fragment() {

    companion object {
        fun forOngkir(): OngkirDetailFragment{
            val fragment = OngkirDetailFragment()
            return fragment
        }
    }

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

    private val onLoad = Observer<List<OngkirResult>>{
        defaultText.text = it.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
}