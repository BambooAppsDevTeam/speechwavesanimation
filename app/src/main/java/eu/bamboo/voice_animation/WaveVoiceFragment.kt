package eu.bamboo.voice_animation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import eu.bamboo.speech_waves_animation.toAnimationSpeed
import eu.bamboo.voice_animation.databinding.FragmentWaveVoiceBinding

class WaveVoiceFragment : Fragment(R.layout.fragment_wave_voice) {

    private var _binding: FragmentWaveVoiceBinding? = null
    private val binding get() = _binding!!

    private val player = StreamPlayer.initPlayer(44100)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaveVoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.applyColors.setOnClickListener {
            applyColors()
        }

        setBarListeners()
        setDefault()
    }

    override fun onStart() {
        super.onStart()

        Thread {
            startMediaPlayer()
        }.start()
    }

    override fun onStop() {
        player.interrupt()

        super.onStop()
    }

    private fun startMediaPlayer() {
        player.playStream(requireContext().resources.openRawResource(R.raw.pcm1644m)) { bytes ->
            binding.musicWave.updateVisualizer(bytes)
        }
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
                val thickness = progress + 1f
                binding.titleThickness.text = "Thickness of Main Line: $thickness"
                binding.musicWave.wavePaintConfig.thickness = thickness
            }
        })

        binding.seekBarMiddleThickness.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val thickness = progress + 1f
                binding.titleMiddleThickness.text = "Thickness of Middle Line: $thickness"
                binding.musicWave.wavePaintConfig.thicknessMiddle = thickness
            }
        })

        binding.seekBarColorGradient.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val colorGradient = progress / 100f / 2
                binding.titleColorGradient.text = "Color Gradient Offset: $colorGradient"
                binding.musicWave.wavePaintConfig.colorGradientPositionOffset = colorGradient
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

        binding.musicWave.wavePaintConfig.startColor = startColor
        binding.musicWave.wavePaintConfig.endColor = endColor
        binding.musicWave.wavePaintConfig.middleColor = middleColor
    }

}