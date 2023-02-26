package eu.bamboo.voice_animation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import eu.bamboo.voice_animation.databinding.ActivitySecondBinding
import kotlin.random.Random


class SecondActivity : AppCompatActivity(), Visualizer.OnDataCaptureListener {

    private lateinit var binding: ActivitySecondBinding

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.toNextScreen.setOnClickListener {
            startActivity(Intent(this, NextActivity::class.java))
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.voice_main_feature_5)

        launchVisualiserIfPermitted()

        binding.lottieVoice.addLottieOnCompositionLoadedListener {
            for (i in 1..10000) {
                val fl = Random.nextInt(1, 100).toFloat() / 100 % 1
                binding.lottieVoice.progress = fl
                Log.d("ololo", "progress = $fl")
            }
        }
        mediaPlayer.setOnCompletionListener {
//            val id = it.audioSessionId
//            if (id != -1) {
//                binding.wave.setAudioSessionId(id)
//            }
        }

        val mediaController = MediaController(this)
        mediaController.setMediaPlayer(object : MediaController.MediaPlayerControl {
            override fun start() {
                TODO("Not yet implemented")
            }

            override fun pause() {
                TODO("Not yet implemented")
            }

            override fun getDuration(): Int {
                TODO("Not yet implemented")
            }

            override fun getCurrentPosition(): Int {
                TODO("Not yet implemented")
            }

            override fun seekTo(pos: Int) {
                TODO("Not yet implemented")
            }

            override fun isPlaying(): Boolean {
                TODO("Not yet implemented")
            }

            override fun getBufferPercentage(): Int {
                TODO("Not yet implemented")
            }

            override fun canPause(): Boolean {
                TODO("Not yet implemented")
            }

            override fun canSeekBackward(): Boolean {
                TODO("Not yet implemented")
            }

            override fun canSeekForward(): Boolean {
                TODO("Not yet implemented")
            }

            override fun getAudioSessionId(): Int {
                TODO("Not yet implemented")
            }

        })
        val fff: Visualizer
//        Visualizer()

//        binding.seekbar.max = mediaPlayer.duration
//
        binding.lottieVoice.speed = 1f
    }

    private var visualiser: Visualizer? = null

    override fun onWaveFormDataCapture(
        visualizer: Visualizer,
        waveform: ByteArray,
        samplingRate: Int
    ) {
        Log.d("olololo", "onWaveFormDataCapture")
//        if (binding.waveformView != null) {
//            binding.waveformView.setWaveform(waveform)
//        }
    }

    override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
        Log.d("olololo", "onFftDataCapture")
        TODO("Not yet implemented")
    }

    private val REQUEST_CODE = 0
    val PERMISSIONS =
        arrayOf<String>(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.MODIFY_AUDIO_SETTINGS)

    private val CAPTURE_SIZE = 256

    override fun onStart() {
        super.onStart()
        mediaPlayer.setOnPreparedListener {
            it.start()
            val id = it.audioSessionId
            if (id != -1) {
                binding.wave.setAudioSessionId(id)
            }
        }

        launchVisualiserIfPermitted()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        if (visualiser != null) {
            visualiser?.setEnabled(false)
            visualiser?.release()
            visualiser?.setDataCaptureListener(null, 0, false, false)
        }
        mediaPlayer.stop()
        super.onStop()
    }

    private fun startVisualiser() {
        visualiser = Visualizer(0)
        visualiser?.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, false)
        visualiser?.setCaptureSize(CAPTURE_SIZE)
        visualiser?.setEnabled(true)
    }

    private val neededPermissionsArray = listOfNotNull(
        Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    private fun launchVisualiserIfPermitted() {
        if (appHasPermissions()) {
//            startVisualiser()
        } else {
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultMap ->
                neededPermissionsArray
                    .all { resultMap[it] ?: false }
            }.launch(neededPermissionsArray)
        }
    }

    private fun appHasPermissions() = neededPermissionsArray.all { hasPermission(it) }

    fun AppCompatActivity.hasPermission(permission: String) = ActivityCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}