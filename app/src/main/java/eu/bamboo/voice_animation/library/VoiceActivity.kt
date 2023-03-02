package eu.bamboo.voice_animation.library

import android.Manifest
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import eu.bamboo.voice_animation.R
import eu.bamboo.voice_animation.databinding.ActivityVoiceBinding

class VoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceBinding

    private lateinit var mediaPlayer: MediaPlayer

    private var visualizer: Visualizer? = null

    private val neededPermissionsArray = listOfNotNull(
        Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.applyColors.setOnClickListener {
            applyColors()
        }

        setBarListeners()
        setDefault()

        mediaPlayer = MediaPlayer.create(this, R.raw.voice_main_feature_5)
    }

    override fun onStart() {
        super.onStart()

        startMediaPlayerIfPermitted()
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

    private fun setBarListeners() {
        binding.seekBarDensity.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val density = progress.min(1) / 100f
                binding.titleDensity.text = "Change Density: $density"
                binding.musicWave.density = density
            }
        })

        binding.seekBarSpeed.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val speed = progress.toAnimationSpeed()
                binding.titleSpeed.text = "Change Speed: $speed"
                binding.musicWave.speed = speed
            }
        })

        binding.seekBarLineCount.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val lineCount = progress + 1
                binding.titleLineCount.text = "Change Line Count: $lineCount"
                binding.musicWave.pathCount = lineCount
            }
        })

        binding.seekBarPadding.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val padding = progress / 100f / 2
                binding.titlePadding.text = "Change Padding: $padding"
                binding.musicWave.windowPadding = padding
            }
        })

        binding.seekBarThickness.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val thickness = progress + 1
                binding.titleThickness.text = "Thickness of Main Line: $thickness"
                binding.musicWave.thickness = thickness
            }
        })

        binding.seekBarMiddleThickness.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val thickness = progress + 1
                binding.titleMiddleThickness.text = "Thickness of Middle Line: $thickness"
                binding.musicWave.thicknessMiddle = thickness
            }
        })

        binding.seekBarColorGradient.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val colorGradient = progress / 100f / 2
                binding.titleColorGradient.text = "Color Gradient Offset: $colorGradient"
                binding.musicWave.config.colorGradientPositionOffset = colorGradient
            }
        })
    }

    private fun setDefault() {
        binding.seekBarDensity.progress = 20
        binding.seekBarSpeed.progress = 1
        binding.seekBarLineCount.progress = 3
        binding.seekBarPadding.progress = 48
        binding.seekBarThickness.progress = 5
        binding.seekBarMiddleThickness.progress = 2
        binding.seekBarColorGradient.progress = 20
        binding.colorStart.setText("#0000ff")
        binding.colorEnd.setText("#ffff00")
        binding.colorMiddle.setText("#ff0000")
        applyColors()
    }

    private fun applyColors() {
        val startColor = binding.colorStart.toColorInt()
        val endColor = binding.colorEnd.toColorInt()
        val middleColor = binding.colorMiddle.toColorInt()

        startColor ?: return
        endColor ?: return
        middleColor ?: return

        binding.musicWave.config.startColor = startColor
        binding.musicWave.config.endColor = endColor
        binding.musicWave.config.middleColor = middleColor
    }

    private fun startMediaPlayerIfPermitted() {
        if (appHasAudioRecordPermissions()) {
            startMediaPlayer()
        } else {
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultMap ->
                neededPermissionsArray
                    .all { resultMap[it] ?: false }
                    .then { startMediaPlayer() }
            }.launch(neededPermissionsArray)
        }
    }

    private fun appHasAudioRecordPermissions() = neededPermissionsArray.all { hasPermission(it) }

    private fun startMediaPlayer() {
        mediaPlayer.start()

        val id = mediaPlayer.audioSessionId
        if (id != -1) {
            setAudioSessionId(id)
        }

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.start()
        }
    }

}