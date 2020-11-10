package com.inbedroom.couriertracking.view

import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.inbedroom.couriertracking.CourierTrackingApplication
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.view.adapter.MainPagerAdapter
import com.inbedroom.couriertracking.viewmodel.MainViewModel
import com.inbedroom.couriertracking.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        const val REQUEST_CODE = 101
        const val RESULT_LABEL = "AWB"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var pagerAdapter: MainPagerAdapter


    private val pageList: MutableMap<Int, String> = mutableMapOf()

    override fun layoutId(): Int = R.layout.activity_main

    override fun setupLib() {
        (application as CourierTrackingApplication).appComponent.inject(this)
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(MainViewModel::class.java)

        pageList.clear()
        pageList.putAll(
            mutableMapOf(
                Pair(0, getString(R.string.main_title)),
                Pair(1, getString(R.string.tariff_check))
            )
        )
    }

    override fun initView() {
        supportActionBar?.elevation = 0F

        pagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle, pageList)
        mainViewPager.adapter = pagerAdapter
        mainViewPager.requestDisallowInterceptTouchEvent(true)

        TabLayoutMediator(mainTabLayout, mainViewPager) { tab, position ->
            tab.text = pageList[position]
        }.attach()
    }

    override fun onAction() {}

}