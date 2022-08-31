package com.example.recorder_07

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    // Mark -Properties/////////////////////////////////////////
    private val recordBtn: RecordBtn by lazy { findViewById(R.id.record_Btn) }
    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var state = State.BEFORE_RECORDING


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        requestAudioPermission()
        initViews()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        Log.d("MainActivity", "audioRecordPermissionGranted : $audioRecordPermissionGranted")
        if (!audioRecordPermissionGranted){
            finish()
        }
    }


    // Mark -Help/////////////////////////////////////////

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        recordBtn.updateIconWithState(state)
    }


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}