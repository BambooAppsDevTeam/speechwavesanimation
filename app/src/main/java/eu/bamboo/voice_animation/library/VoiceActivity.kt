package eu.bamboo.voice_animation.library

import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import eu.bamboo.voice_animation.R
import eu.bamboo.voice_animation.databinding.ActivityVoiceBinding

class VoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceBinding

    private lateinit var mediaPlayer: MediaPlayer

    private var visualizer: Visualizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.seekBarDensity.setOnSeekBarChangeListener(object: OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val density = progress.min(1) / 100f
                binding.titleDensity.text = "Change Density: $density"
                binding.musicWave.density = density
            }
        })

        binding.seekBarSpeed.setOnSeekBarChangeListener(object: OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val speed = progress.toAnimationSpeed()
                binding.titleSpeed.text = "Change Speed: $speed"
                binding.musicWave.speed = speed
            }
        })

        binding.seekBarLineCount.setOnSeekBarChangeListener(object: OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val lineCount = progress + 1
                binding.titleLineCount.text = "Change Line Count: $lineCount"
                binding.musicWave.pathCount = lineCount
            }
        })

        binding.seekBarPadding.setOnSeekBarChangeListener(object: OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val padding = progress / 100f / 2
                binding.titlePadding.text = "Change Padding: $padding"
                binding.musicWave.windowPadding = padding
            }
        })

        binding.playAgain.setOnClickListener {
            mediaPlayer.start()
        }

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
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.start()
        }

    }

    override fun onStop() {
        mediaPlayer.stop()
        mediaPlayer.setOnCompletionListener(null)
        visualizer?.release()

        super.onStop()
    }

    private fun setAudioSessionId(audioSessionId: Int) {
        visualizer?.release()

        val visualizer = object : VoiceVisualizer(audioSessionId) {
            override fun onWaveUpdates(bytes: ByteArray) {
                binding.musicWave.updateVisualizer(bytes)
            }
        }
        this.visualizer = visualizer
    }

}