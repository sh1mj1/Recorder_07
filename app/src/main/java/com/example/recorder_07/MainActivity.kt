package com.example.recorder_07

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    // Mark -Properties/////////////////////////////////////////
    private val soundVisualizerView: SoundVisualizerView by lazy { findViewById(R.id.soundVisualizer_V) }
    private val resetBtn: Button by lazy { findViewById(R.id.reset_Btn) }
    private val recordBtn: RecordBtn by lazy { findViewById(R.id.record_Btn) }
    private val recordTimeView: CountUpView by lazy { findViewById(R.id.recordTime_Tv) }


    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var state = State.BEFORE_RECORDING
    set(value) {
        field = value
        resetBtn.isEnabled = (value == State.AFTER_RECORDING) ||
                (value == State.ON_PLAYING)
        recordBtn.updateIconWithState(value)
    }

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private var player: MediaPlayer? = null
    private var recorder: MediaRecorder? = null



    // Mark -LifeCycle/////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initViews()
        bindViews()
        initVariables()

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

    private fun initVariables() {
        state = State.BEFORE_RECORDING
    }

    private fun startRecording(){
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            // 지금 구현하는 녹음기 같은 경우 녹음을 하고 확인하고 따로 저장하지 않는 형태를 띄고 있기 때문에
            // 앱에서만 접근할 수 있는 스토리지는 범위가 한정되어 있음 scope Storage. 내부 & 외부
            // 인터널 같은 경우는 용량이 한정되어 있음. (녹음 파일을 저장하기 에는)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        soundVisualizerView.startVisualizing(false)
        recordTimeView.startCountUp()
        state = State.ON_RECORDING
        Log.d("AudioState", "AudioState: $state")
    }
    private fun stopRecording(){
        recorder?.run{
            stop()
            release()
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeView.stopCountUp()
        state = State.AFTER_RECORDING
        Log.d("AudioState", "AudioState: $state")
    }

    private fun startPlaying(){
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            // 로딩할 때 시간이 오래 걸린다거나 스트리밍 같은 경우 서버에서 데이터를 가져오는 것이므로 aync 을 사용함.
            //  또 로딩 중 로딩인디케이터같은 UI 를 사용. 하지만 우리는 단지 방금 녹음된 거승ㄹ 가져오는 것 뿐이기 때문에 async 하지 않으 ㄱ서임.
            prepare()
        }
        soundVisualizerView.startVisualizing(true)
        recordTimeView.startCountUp()
        player?.start()
        state = State.ON_PLAYING
        Log.d("AudioState", "AudioState: $state")
    }

    private fun stopPlaying(){
        player?.release()
        player = null
        recordTimeView.stopCountUp()
        soundVisualizerView.stopVisualizing()
        state = State.AFTER_RECORDING
        Log.d("AudioState", "AudioState: $state")
    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        recordBtn.updateIconWithState(state)
    }

    private fun bindViews(){
        // 사운드비주얼라이저뷰 클래스에서 visualizeRepeatAction 이 반복 동작할 때
        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }

        resetBtn.setOnClickListener {
            stopPlaying()
            state = State.BEFORE_RECORDING
        }

        recordBtn.setOnClickListener {
            when(state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }

            }
        }
    }


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}