package com.inbedroom.couriertracking.view

import android.content.Context
import android.content.Intent
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.platform.BaseActivity

class CekOngkirActivity : BaseActivity() {

    companion object{
        fun callIntent(context: Context): Intent {
            val intent = Intent(context, CekOngkirActivity::class.java)
            return intent
        }
    }

    override fun layoutId(): Int = R.layout.activity_cek_ongkir

    override fun setupLib() {

    }

    override fun initView() {
        supportActionBar?.title = getString(R.string.tariff_check)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onAction() {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}