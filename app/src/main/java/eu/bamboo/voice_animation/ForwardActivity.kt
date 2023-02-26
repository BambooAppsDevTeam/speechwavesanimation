package eu.bamboo.voice_animation

import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eu.bamboo.voice_animation.databinding.ActivityForwardBinding


class ForwardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForwardBinding

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForwardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mediaPlayer = MediaPlayer.create(this, R.raw.voice_main_feature_5)
    }

    override fun onStart() {
        super.onStart()

        mediaPlayer.setOnPreparedListener {
            it.start()

            val id = it.audioSessionId
            if (id != -1) {
                setAudioSessionId(id)
            }
        }

    }

    override fun onStop() {
        mediaPlayer.stop()
        visualizer?.release()

        super.onStop()
    }

    private var visualizer: Visualizer? = null

    private fun setAudioSessionId(audioSessionId: Int) {
        if (visualizer != null) {
            visualizer?.release()
        }
        val visualizer = Visualizer(audioSessionId)
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1])
        visualizer.setDataCaptureListener(object : OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer, bytes: ByteArray,
                samplingRate: Int
            ) {
                binding.musicWave.updateVisualizer(bytes)
                binding.musicWave.invalidate()
            }

            override fun onFftDataCapture(
                visualizer: Visualizer, bytes: ByteArray,
                samplingRate: Int
            ) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false)
        visualizer.setEnabled(true)
        this.visualizer = visualizer
    }

}