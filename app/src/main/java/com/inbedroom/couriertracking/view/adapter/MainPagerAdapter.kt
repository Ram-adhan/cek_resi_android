package com.inbedroom.couriertracking.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.inbedroom.couriertracking.view.CourierTrackFragment
import com.inbedroom.couriertracking.view.OngkirSetupFragment

class MainPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val list: Map<Int, String>) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        return when (position){
            1 -> OngkirSetupFragment()
            else -> CourierTrackFragment()
        }
    }
}