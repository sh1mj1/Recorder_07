package com.example.recorder_07

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    // Mark -Properties/////////////////////////////////////////
    private val recordBtn: RecordBtn by lazy { findViewById(R.id.record_Btn) }
    private var state = State.BEFORE_RECORDING


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

    }


    // Mark -Help/////////////////////////////////////////
    private fun initViews(){
        recordBtn.updateIconWithState(state)
    }

}