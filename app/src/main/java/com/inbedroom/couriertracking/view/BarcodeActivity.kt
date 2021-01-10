package com.inbedroom.couriertracking.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.inbedroom.couriertracking.R
import com.inbedroom.couriertracking.core.platform.BaseActivity
import kotlinx.android.synthetic.main.activity_barcode.*
import java.lang.Exception

class BarcodeActivity : BaseActivity() {

    companion object{
        fun callIntent(context: Context): Intent {
            return Intent(context, BarcodeActivity::class.java)
        }

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        private var previewUseCase: Preview? = null
    }


    override fun layoutId(): Int = R.layout.activity_barcode

    override fun setupLib() {
        if (allPermissionsGranted()){
            startCamera()
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 10)
        }
    }

    override fun initView() {
    }

    override fun onAction() {
    }

    private fun startCamera(){
        val cameraFuture = ProcessCameraProvider.getInstance(this)

        cameraFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,cameraSelector,preview
                )
            }catch (e: Exception){
                Log.e("CameraActivity", "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
    }
}