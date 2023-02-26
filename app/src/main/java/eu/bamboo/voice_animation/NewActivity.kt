package eu.bamboo.voice_animation

import android.content.Intent
import android.media.AudioFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.squti.androidwaverecorder.WaveRecorder
import eu.bamboo.voice_animation.databinding.ActivityNewBinding


class NewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewBinding
    private lateinit var waveRecorder: WaveRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.forward.setOnClickListener {
            startActivity(Intent(this, ForwardActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        /**
         * This path points to application cache directory.
         * you could change it based on your usage
         */
        val filePath:String = externalCacheDir?.absolutePath + "/audioFile.wav"

        waveRecorder = WaveRecorder(filePath)
        waveRecorder.waveConfig.sampleRate = 44100
        waveRecorder.waveConfig.channels = AudioFormat.CHANNEL_IN_STEREO
        waveRecorder.waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_8BIT
        waveRecorder.startRecording()
        waveRecorder.onAmplitudeListener = {
            Log.i("Ololo", "Amplitude : $it")
        }
        waveRecorder.onStateChangeListener = {
            Log.i("Ololo", "onStateChangeListener RecorderState: $it")
//            when (it) {
//                RecorderState.RECORDING -> TODO()
//                RecorderState.STOP -> TODO()
//                RecorderState.PAUSE -> TODO()
//            }
        }
    }

    override fun onStop() {
        waveRecorder.stopRecording()

        super.onStop()
    }

}