package com.inbedroom.couriertracking.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.zxing.ResultPoint
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.platform.BaseActivity
import com.inbedroom.couriertracking.utils.ServiceData
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import kotlinx.android.synthetic.main.activity_barcode_scan.*

class BarcodeScanActivity : BaseActivity() {

    companion object {
        fun callIntent(context: Context): Intent {
            return Intent(context, BarcodeScanActivity::class.java)
        }

        private const val BARCODE_PATTERN = "^[a-zA-Z0-9]*\$"
    }

    private lateinit var captureManager: CaptureManager
    private lateinit var interstitialAd: InterstitialAd
    private val permission = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun setupPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission[0]) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(permission[1]) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    permission,
                    101
                )
            }
        }
    }

    override fun processBundle(savedInstanceState: Bundle?) {
        captureManager = CaptureManager(this, barcodeScanner)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        barcodeScanner.decodeContinuous(barcodeCallback)
    }

    override fun layoutId(): Int = R.layout.activity_barcode_scan

    override fun setupLib() {
        MobileAds.initialize(this)
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = ServiceData.INTERSTITIAL_AD_ID
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                onFinishResult()
            }
        }
    }

    override fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Scan AWB"
    }

    override fun onAction() {
        barcodeScanButtonApply.setOnClickListener {
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            } else {
                onFinishResult()
            }
        }

        btnGallery.setOnClickListener {
            val pickImage = Intent()
            pickImage.type = "image/**"
            pickImage.action = Intent.ACTION_PICK

            startActivityForResult(Intent.createChooser(pickImage, "Select Image"), 111)
        }
    }

    private fun onFinishResult() {
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(MainActivity.RESULT_LABEL, barcodeScanTextResult.text)
        )
        finish()
    }

    private var barcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.let { scanned ->
                barcodeScanTextResult.text = scanned.text
                if (scanned.text.matches(BARCODE_PATTERN.toRegex())) {
                    barcodeScanTextResult.setBackgroundResource(R.color.emerald)
                    barcodeScanButtonApply.setBackgroundResource(R.drawable.rounded_button)
                    barcodeScanButtonApply.isEnabled = true
                } else {
                    emptyResult()
                }
            }
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        }

    }

    private fun emptyResult() {
        barcodeScanTextResult.setBackgroundColor(Color.TRANSPARENT)
        barcodeScanButtonApply.setBackgroundResource(R.drawable.rounded_button_disabled)
        barcodeScanButtonApply.isEnabled = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }
}